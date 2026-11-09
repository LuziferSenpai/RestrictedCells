package com.restrictedcells.items.storage;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.restrictedcells.api.storage.cells.IRestrictedCellItem;
import com.restrictedcells.me.cells.RestrictedCellHandler;

import lombok.Getter;

import appeng.api.stacks.AEKeyType;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.CellState;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.definitions.AEItems;
import appeng.items.AEBaseItem;
import appeng.util.InteractionUtil;

public class RestrictedStorageCell extends AEBaseItem implements IRestrictedCellItem {

    private final Supplier<Set<AEKeyType>> keyTypes;
    private final long bytes;
    private final long bytesPerType;
    private final int totalTypes;
    private final double idleDrain;
    @Getter
    private final ResourceLocation baseItemId;

    public RestrictedStorageCell(Item.Properties properties, long bytes, long bytesPerType, double idleDrain, int totalTypes, Supplier<Set<AEKeyType>> keyTypes, ResourceLocation baseItemId) {
        super(properties);

        this.baseItemId = baseItemId;
        this.bytes = bytes;
        this.bytesPerType = bytesPerType;
        this.idleDrain = idleDrain;
        this.totalTypes = totalTypes;
        this.keyTypes = keyTypes;
    }

    @Override
    public Set<AEKeyType> getKeyTypes() {
        return this.keyTypes.get();
    }

    @Override
    public Component getName(ItemStack stack) {
        return this.createRestrictedName(stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        this.disassemble(player.getItemInHand(hand), level, player);

        return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide()), player.getItemInHand(hand));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        if (!this.disassemble(stack, context.getLevel(), context.getPlayer())) return InteractionResult.PASS;

        return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
    }

    private boolean disassemble(ItemStack stack, Level level, Player player) {
        if (level.isClientSide() || !InteractionUtil.isInAlternateUseMode(player)) return false;

        var inventory = player.getInventory();

        if (inventory.getSelected() != stack) return false;

        var baseCell = new ItemStack(BuiltInRegistries.ITEM.get(this.baseItemId));

        if (stack.hasTag()) {
            var tag = stack.getTag().copy();

            tag.remove(RESTRICTED_TYPES_TAG);
            tag.remove(RESTRICTED_AMOUNT_TAG);
            baseCell.setTag(tag.isEmpty() ? null : tag);
        }

        inventory.setItem(inventory.selected, ItemStack.EMPTY);
        inventory.placeItemBackInInventory(baseCell);
        inventory.placeItemBackInInventory(AEItems.LOGIC_PROCESSOR.stack());

        return true;
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 1) return 0xFFFFFF;

        var cellInventory = StorageCells.getCellInventory(stack, null);
        var cellStatus = cellInventory != null ? cellInventory.getStatus() : CellState.EMPTY;

        return cellStatus.getStateColor();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
        RestrictedCellHandler.INSTANCE.addCellInformationToTooltip(stack, lines);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return RestrictedCellHandler.INSTANCE.getTooltipImage(stack);
    }

    @Override
    public long getBytes(ItemStack cellItem) {
        return this.bytes;
    }

    @Override
    public long getBytesPerType(ItemStack cellItem) {
        return this.bytesPerType;
    }

    @Override
    public int getTotalTypes(ItemStack cellItem) {
        return this.totalTypes;
    }

    @Override
    public double getIdleDrain() {
        return this.idleDrain;
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack stack) {
        return UpgradeInventories.forItem(stack, this.getKeyTypes().contains(AEKeyType.items()) ? 4 : 3);
    }
}
