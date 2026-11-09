package com.restrictedcells.me.cells;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.restrictedcells.api.storage.cells.CellRestriction;
import com.restrictedcells.api.storage.cells.IRestrictedCellItem;
import com.restrictedcells.util.SaturatedMath;

import lombok.Getter;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.IncludeExclude;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.StickyClaim;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.core.AELog;
import appeng.core.definitions.AEItems;
import appeng.util.ConfigInventory;
import appeng.util.prioritylist.FuzzyPriorityList;
import appeng.util.prioritylist.IPartitionList;
import appeng.util.prioritylist.PrecisePriorityList;

public class RestrictedCellInventory implements StorageCell {

    private static final int MAX_ITEM_TYPES = 63;
    private static final String ITEM_COUNT_TAG = "ic";
    private static final String STACK_KEYS = "keys";
    private static final String STACK_AMOUNTS = "amts";

    private final ISaveProvider container;
    private final int amountPerByte;
    private final IPartitionList partitionList;
    @Getter
    private final IncludeExclude partitionListMode;
    private final ItemStack cellStack;
    private final IRestrictedCellItem cellType;
    private final int maxItemTypes;
    private final long totalAmountLimit;
    private final long maxItemsPerType;
    @Getter
    private final boolean isEqualDistribution;
    @Getter
    private final int whitelistSize;
    private final boolean hasVoidUpgrade;
    private final boolean sticky;
    private short storedItems;
    @Getter
    private long storedItemCount;
    private Object2LongMap<AEKey> storedAmounts;
    private boolean isPersisted = true;

    private RestrictedCellInventory(IRestrictedCellItem cellType, ItemStack cellStack, ISaveProvider container) {
        this.cellStack = cellStack;
        this.cellType = cellType;
        this.container = container;
        this.amountPerByte = cellType.getAmountPerByte();
        this.maxItemTypes = resolveMaxItemTypes(cellType, cellStack);
        this.storedItems = (short) this.getTag().getLongArray(STACK_AMOUNTS).length;
        this.storedItemCount = this.getTag().getLong(ITEM_COUNT_TAG);

        var builder = IPartitionList.builder();
        var upgrades = this.getUpgradesInventory();
        var config = this.getConfigInventory();
        var hasInverter = upgrades.isInstalled(AEItems.INVERTER_CARD);
        var isFuzzy = upgrades.isInstalled(AEItems.FUZZY_CARD);
        long restrictedAmount = cellType.getRestriction(cellStack).amount();

        if (isFuzzy) builder.fuzzyMode(this.getFuzzyMode());

        builder.addAll(config.keySet());

        this.partitionListMode = hasInverter ? IncludeExclude.BLACKLIST : IncludeExclude.WHITELIST;
        this.partitionList = builder.build();
        this.isEqualDistribution = upgrades.isInstalled(AEItems.EQUAL_DISTRIBUTION_CARD);
        this.whitelistSize = !isFuzzy && this.partitionListMode == IncludeExclude.WHITELIST ? config.keySet().size() : 0;
        this.totalAmountLimit = this.isEqualDistribution ? 0 : restrictedAmount;

        if (this.isEqualDistribution) {
            var distributionTypes = CellRestriction.distributionTypes(this.maxItemTypes, 0, this.whitelistSize);
            var capacityShare = CellRestriction.maxAmountPerType(this.getTotalBytes(), this.getBytesPerType(), distributionTypes, this.amountPerByte);

            this.maxItemsPerType = restrictedAmount > 0 ? Math.min(restrictedAmount, capacityShare) : capacityShare;
        } else {
            this.maxItemsPerType = Long.MAX_VALUE;
        }

        this.hasVoidUpgrade = upgrades.isInstalled(AEItems.VOID_CARD);
        this.sticky = upgrades.isInstalled(AEItems.STICKY_CARD) && !this.partitionList.isEmpty();
    }

    public static RestrictedCellInventory createInventory(ItemStack cellStack, @Nullable ISaveProvider container) {
        Objects.requireNonNull(cellStack, "Cannot create cell inventory for null itemstack");

        if (!(cellStack.getItem() instanceof IRestrictedCellItem cellType)) return null;

        return new RestrictedCellInventory(cellType, cellStack, container);
    }

    private static int clampTotalTypes(IRestrictedCellItem cellType, ItemStack cellStack) {
        return Math.max(1, Math.min(cellType.getTotalTypes(cellStack), MAX_ITEM_TYPES));
    }

    private static int resolveMaxItemTypes(IRestrictedCellItem cellType, ItemStack cellStack) {
        var totalTypes = clampTotalTypes(cellType, cellStack);
        var restrictedTypes = cellType.getRestriction(cellStack).types();

        return restrictedTypes > 0 ? Math.min(restrictedTypes, totalTypes) : totalTypes;
    }

    public long getMaxRestrictedAmount(int types) {
        var totalTypes = clampTotalTypes(this.cellType, this.cellStack);

        if (!this.isEqualDistribution) return CellRestriction.maxAmount(this.getTotalBytes(), this.getBytesPerType(), totalTypes, types, this.amountPerByte);

        var distributionTypes = CellRestriction.distributionTypes(totalTypes, types, this.whitelistSize);

        return CellRestriction.maxAmountPerType(this.getTotalBytes(), this.getBytesPerType(), distributionTypes, this.amountPerByte);
    }

    public boolean isNotPreformatted() {
        return this.partitionList.isEmpty();
    }

    public boolean isFuzzy() {
        return this.partitionList instanceof FuzzyPriorityList;
    }

    private CompoundTag getTag() {
        return this.cellStack.getOrCreateTag();
    }

    @Override
    public boolean canFitInsideCell() {
        return this.getAvailableStacks().isEmpty();
    }

    protected Object2LongMap<AEKey> getCellItems() {
        if (this.storedAmounts == null) {
            this.storedAmounts = new Object2LongOpenHashMap<>();

            this.loadCellItems();
        }

        return this.storedAmounts;
    }

    @Override
    public void persist() {
        if (this.isPersisted) return;

        var amounts = new LongArrayList(this.storedAmounts.size());
        var keys = new ListTag();
        long itemCount = 0;

        for (var entry : this.storedAmounts.object2LongEntrySet()) {
            var amount = entry.getLongValue();

            if (amount > 0) {
                itemCount += amount;

                keys.add(entry.getKey().toTagGeneric());
                amounts.add(amount);
            }
        }

        if (keys.isEmpty()) {
            this.getTag().remove(STACK_KEYS);
            this.getTag().remove(STACK_AMOUNTS);
        } else {
            this.getTag().put(STACK_KEYS, keys);
            this.getTag().putLongArray(STACK_AMOUNTS, amounts.toArray(new long[0]));
        }

        this.storedItems = (short) this.storedAmounts.size();
        this.storedItemCount = itemCount;

        if (itemCount == 0) {
            this.getTag().remove(ITEM_COUNT_TAG);
        } else {
            this.getTag().putLong(ITEM_COUNT_TAG, itemCount);
        }

        this.isPersisted = true;
    }

    protected void saveChanges() {
        this.storedItems = (short) this.storedAmounts.size();
        this.storedItemCount = 0;

        for (var storedAmount : this.storedAmounts.values()) {
            this.storedItemCount += storedAmount;
        }

        this.isPersisted = false;

        if (this.container != null) this.container.saveChanges();
        else
            this.persist();
    }

    private void loadCellItems() {
        var isCorrupted = false;
        var amounts = this.getTag().getLongArray(STACK_AMOUNTS);
        var tags = this.getTag().getList(STACK_KEYS, Tag.TAG_COMPOUND);

        if (amounts.length != tags.size()) AELog.warn("Loading storage cell with mismatched amounts/tags: %d != %d", amounts.length, tags.size());

        for (var index = 0; index < amounts.length; index++) {
            var amount = amounts[index];
            var key = AEKey.fromTagGeneric(tags.getCompound(index));

            if (amount <= 0 || key == null) isCorrupted = true;
            else
                this.storedAmounts.put(key, amount);
        }

        if (isCorrupted) this.saveChanges();
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        for (var entry : Object2LongMaps.fastIterable(this.getCellItems())) {
            out.add(entry.getKey(), entry.getLongValue());
        }
    }

    @Override
    public double getIdleDrain() {
        return this.cellType.getIdleDrain();
    }

    public FuzzyMode getFuzzyMode() {
        return this.cellType.getFuzzyMode(this.cellStack);
    }

    public ConfigInventory getConfigInventory() {
        return this.cellType.getConfigInventory(this.cellStack);
    }

    public IUpgradeInventory getUpgradesInventory() {
        return this.cellType.getUpgrades(this.cellStack);
    }

    public long getBytesPerType() {
        return this.cellType.getBytesPerType(this.cellStack);
    }

    public boolean canHoldNewItem() {
        var bytesFree = this.getFreeBytes();

        return (bytesFree > this.getBytesPerType() || bytesFree == this.getBytesPerType() && this.getUnusedItemCount() > 0) && (this.totalAmountLimit <= 0 || this.totalAmountLimit > this.getStoredItemCount()) && this.getRemainingItemTypes() > 0;
    }

    public long getTotalBytes() {
        return this.cellType.getBytes(this.cellStack);
    }

    public long getFreeBytes() {
        return this.getTotalBytes() - this.getUsedBytes();
    }

    public long getTotalItemTypes() {
        return this.maxItemTypes;
    }

    public long getStoredItemTypes() {
        return this.storedItems;
    }

    public long getRemainingItemTypes() {
        var basedOnStorage = this.getFreeBytes() / this.getBytesPerType();
        var basedOnTotal = this.getTotalItemTypes() - this.getStoredItemTypes();

        return Math.min(basedOnStorage, basedOnTotal);
    }

    public long getUsedBytes() {
        var bytesForItemCount = (this.getStoredItemCount() + this.getUnusedItemCount()) / this.amountPerByte;

        return this.getStoredItemTypes() * this.getBytesPerType() + bytesForItemCount;
    }

    public long getRemainingItemCount() {
        var remaining = SaturatedMath.add(SaturatedMath.multiply(this.getFreeBytes(), this.amountPerByte), this.getUnusedItemCount());

        if (this.totalAmountLimit > 0) remaining = Math.min(remaining, this.totalAmountLimit - this.getStoredItemCount());

        return Math.max(0, remaining);
    }

    public int getUnusedItemCount() {
        var remainder = (int) (this.getStoredItemCount() % this.amountPerByte);

        if (remainder == 0) return 0;

        return this.amountPerByte - remainder;
    }

    @Override
    public CellState getStatus() {
        if (this.getStoredItemTypes() == 0) return CellState.EMPTY;
        if (this.canHoldNewItem()) return CellState.NOT_EMPTY;
        if (this.getRemainingItemCount() > 0) return CellState.TYPES_FULL;

        return CellState.FULL;
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (amount == 0 || !this.cellType.acceptsKey(what) || this.cellType.isBlackListed(this.cellStack, what)) return 0;
        if (!this.partitionList.matchesFilter(what, this.partitionListMode)) return 0;

        var inserted = this.innerInsert(what, amount, mode);

        if (this.isNotPreformatted() && this.hasVoidUpgrade && !this.canHoldNewItem()) return this.getCellItems().containsKey(what) ? amount : inserted;

        return this.hasVoidUpgrade ? amount : inserted;
    }

    private long innerInsert(AEKey what, long amount, Actionable mode) {
        if (what instanceof AEItemKey itemKey) {
            var cellInventory = StorageCells.getCellInventory(itemKey.toStack(), null);

            if (cellInventory != null && !cellInventory.canFitInsideCell()) return 0;
        }

        var currentAmount = this.getCellItems().getLong(what);
        var remainingItemCount = this.getRemainingItemCount();

        if (currentAmount <= 0) {
            if (!this.canHoldNewItem()) return 0;

            if (this.totalAmountLimit <= 0) {
                remainingItemCount -= SaturatedMath.multiply(this.getBytesPerType(), this.amountPerByte);

                if (remainingItemCount <= 0) return 0;
            }
        }

        remainingItemCount = Math.max(0, Math.min(Math.min(this.maxItemsPerType - currentAmount, remainingItemCount), Long.MAX_VALUE - currentAmount));

        var insertedAmount = Math.min(amount, remainingItemCount);

        if (mode == Actionable.MODULATE) {
            this.getCellItems().put(what, currentAmount + insertedAmount);
            this.saveChanges();
        }

        return insertedAmount;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        var currentAmount = this.getCellItems().getLong(what);

        if (currentAmount <= 0) return 0;

        if (amount >= currentAmount) {
            if (mode == Actionable.MODULATE) {
                this.getCellItems().remove(what, currentAmount);
                this.saveChanges();
            }

            return currentAmount;
        }

        if (mode == Actionable.MODULATE) {
            this.getCellItems().put(what, currentAmount - amount);
            this.saveChanges();
        }

        return amount;
    }

    @Override
    public Component getDescription() {
        return this.cellStack.getHoverName();
    }

    @Override
    public boolean acceptsSticky(AEKey what) {
        return this.sticky && this.cellType.acceptsKey(what) && this.partitionList.matchesFilter(what, this.partitionListMode) && !this.cellType.isBlackListed(this.cellStack, what);
    }

    @Nullable
    @Override
    public StickyClaim getStickyClaim() {
        if (!this.sticky) return null;

        return this.partitionListMode == IncludeExclude.WHITELIST && this.partitionList instanceof PrecisePriorityList precisePriorityList ? new StickyClaim(precisePriorityList.getItems()) : StickyClaim.UNINDEXED;
    }
}
