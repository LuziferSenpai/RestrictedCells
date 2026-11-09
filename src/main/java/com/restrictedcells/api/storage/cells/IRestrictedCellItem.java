package com.restrictedcells.api.storage.cells;

import java.util.Set;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.restrictedcells.core.localization.FCGuiText;

import appeng.api.config.FuzzyMode;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.cells.IBasicCellItem;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.items.contents.CellConfig;
import appeng.util.ConfigInventory;

import static net.minecraft.core.registries.BuiltInRegistries.ITEM;

public interface IRestrictedCellItem extends ICellWorkbenchItem {

    String RESTRICTED_TYPES_TAG = "restrictedTypes";
    String RESTRICTED_AMOUNT_TAG = "restrictedAmount";
    String FUZZY_MODE_TAG = "FuzzyMode";

    Set<AEKeyType> getKeyTypes();

    ResourceLocation getBaseItemId();

    long getBytes(ItemStack cellItem);

    long getBytesPerType(ItemStack cellItem);

    int getTotalTypes(ItemStack cellItem);

    double getIdleDrain();

    default boolean isBlackListed(ItemStack cellItem, AEKey requestedAddition) {
        return ITEM.get(this.getBaseItemId()) instanceof IBasicCellItem baseCell && baseCell.isBlackListed(cellItem, requestedAddition);
    }

    default Component createRestrictedName(ItemStack stack) {
        var baseName = ITEM.get(this.getBaseItemId()).getName(stack);

        return FCGuiText.RestrictedName.text(baseName);
    }

    @Override
    default ConfigInventory getConfigInventory(ItemStack stack) {
        return CellConfig.create(this::acceptsKey, stack);
    }

    @Override
    default FuzzyMode getFuzzyMode(ItemStack stack) {
        var fuzzyMode = stack.getOrCreateTag().getString(FUZZY_MODE_TAG);

        if (fuzzyMode.isEmpty()) return FuzzyMode.IGNORE_ALL;

        try {
            return FuzzyMode.valueOf(fuzzyMode);
        } catch (IllegalArgumentException exception) {
            return FuzzyMode.IGNORE_ALL;
        }
    }

    @Override
    default void setFuzzyMode(ItemStack stack, FuzzyMode fuzzyMode) {
        stack.getOrCreateTag().putString(FUZZY_MODE_TAG, fuzzyMode.name());
    }

    default int getAmountPerByte() {
        return this.getKeyTypes().stream().mapToInt(AEKeyType::getAmountPerByte).max().orElse(1);
    }

    default boolean acceptsKey(AEKey what) {
        return this.getKeyTypes().stream().anyMatch(keyType -> keyType.contains(what));
    }

    default CellRestriction getRestriction(ItemStack cellItem) {
        var tag = cellItem.getTag();

        if (tag == null) return CellRestriction.NONE;

        return new CellRestriction(tag.getInt(RESTRICTED_TYPES_TAG), tag.getLong(RESTRICTED_AMOUNT_TAG));
    }

    default long getMaxRestrictedAmount(ItemStack cellItem, int types) {
        return CellRestriction.maxAmount(this.getBytes(cellItem), this.getBytesPerType(cellItem), this.getTotalTypes(cellItem), types, this.getAmountPerByte());
    }

    default void setRestriction(ItemStack cellItem, CellRestriction restriction) {
        var types = Math.max(0, Math.min(restriction.types(), this.getTotalTypes(cellItem)));
        var amount = Math.max(0, Math.min(restriction.amount(), this.getMaxRestrictedAmount(cellItem, types)));
        var tag = cellItem.getOrCreateTag();

        if (types == 0 && amount == 0) {
            tag.remove(RESTRICTED_TYPES_TAG);
            tag.remove(RESTRICTED_AMOUNT_TAG);

            return;
        }

        tag.putInt(RESTRICTED_TYPES_TAG, types);
        tag.putLong(RESTRICTED_AMOUNT_TAG, amount);
    }
}
