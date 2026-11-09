package com.restrictedcells.items.storage;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.restrictedcells.api.storage.cells.IRestrictedCellItem;
import com.restrictedcells.me.cells.RestrictedCellHandler;

import lombok.Getter;

import appeng.api.features.HotkeyAction;
import appeng.api.stacks.AEKeyType;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.items.contents.PortableCellMenuHost;
import appeng.items.tools.powered.AbstractPortableCell;
import appeng.items.tools.powered.PortableCellItem;

public class RestrictedPortableCell extends AbstractPortableCell implements IRestrictedCellItem {

    private final Supplier<Set<AEKeyType>> keyTypes;
    @Getter
    private final ResourceLocation baseItemId;

    public RestrictedPortableCell(MenuType<?> menuType, Properties properties, Supplier<Set<AEKeyType>> keyTypes, ResourceLocation baseItemId) {
        super(menuType, properties, 0);

        this.keyTypes = keyTypes;
        this.baseItemId = baseItemId;
    }

    @Override
    public Set<AEKeyType> getKeyTypes() {
        return this.keyTypes.get();
    }

    @Override
    public Component getName(ItemStack stack) {
        return this.createRestrictedName(stack);
    }

    @Nullable
    @Override
    public PortableCellMenuHost getMenuHost(Player player, int inventorySlot, ItemStack stack, @Nullable BlockPos pos) {
        var hotkeyId = this.getHotkeyId();

        return new PortableCellMenuHost(player, inventorySlot, this, stack, (menuPlayer, subMenu) -> this.openFromInventory(menuPlayer, inventorySlot, true)) {
            @Override
            public String getCloseHotkey() {
                return hotkeyId;
            }
        };
    }

    @Nullable
    public String getHotkeyId() {
        var types = this.getKeyTypes();

        if (types.contains(AEKeyType.items())) return HotkeyAction.PORTABLE_ITEM_CELL;

        return types.contains(AEKeyType.fluids()) ? HotkeyAction.PORTABLE_FLUID_CELL : null;
    }

    @Override
    public ResourceLocation getRecipeId() {
        return BuiltInRegistries.ITEM.getKey(this);
    }

    @Override
    public int getColor(ItemStack stack) {
        return this.getBaseCell().getColor(stack);
    }

    @Override
    public double getChargeRate(ItemStack stack) {
        return this.getBaseCell().getChargeRate(stack);
    }

    @Override
    public double getAEMaxPower(ItemStack stack) {
        return this.getBaseCell().getAEMaxPower(stack);
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack stack) {
        var slots = this.getKeyTypes().contains(AEKeyType.items()) ? 4 : 3;

        return UpgradeInventories.forItem(stack, slots, this::onUpgradesChanged);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, level, lines, flag);

        RestrictedCellHandler.INSTANCE.addCellInformationToTooltip(stack, lines);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return RestrictedCellHandler.INSTANCE.getTooltipImage(stack);
    }

    @Override
    public long getBytes(ItemStack cellItem) {
        return this.getBaseCell().getBytes(cellItem);
    }

    @Override
    public long getBytesPerType(ItemStack cellItem) {
        return this.getBaseCell().getBytesPerType(cellItem);
    }

    @Override
    public int getTotalTypes(ItemStack cellItem) {
        return this.getBaseCell().getTotalTypes(cellItem);
    }

    @Override
    public double getIdleDrain() {
        return this.getBaseCell().getIdleDrain();
    }

    private PortableCellItem getBaseCell() {
        return (PortableCellItem) BuiltInRegistries.ITEM.get(this.baseItemId);
    }
}
