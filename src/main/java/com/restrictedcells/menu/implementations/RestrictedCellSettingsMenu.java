package com.restrictedcells.menu.implementations;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import com.restrictedcells.api.storage.cells.CellRestriction;
import com.restrictedcells.api.storage.cells.IRestrictedCellItem;
import com.restrictedcells.me.cells.RestrictedCellInventory;
import com.restrictedcells.menu.WorkbenchSubMenuHost;

import lombok.Getter;

import appeng.api.inventories.ISegmentedInventory;
import appeng.api.storage.ISubMenuHost;
import appeng.blockentity.misc.CellWorkbenchBlockEntity;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;

public class RestrictedCellSettingsMenu extends AEBaseMenu implements ISubMenu {

    public static final String ID = "restricted_cell_settings";
    public static final String ACTION_SET_RESTRICTION = "setRestriction";
    public static final String ACTION_RESET_RESTRICTION = "resetRestriction";

    public static final MenuType<RestrictedCellSettingsMenu> TYPE = MenuTypeBuilder.create(RestrictedCellSettingsMenu::new, CellWorkbenchBlockEntity.class).build(ID);

    @GuiSync(1)
    @Getter
    private long totalBytes;
    @GuiSync(2)
    @Getter
    private int totalTypes;
    @GuiSync(3)
    @Getter
    private long bytesPerType;
    @GuiSync(4)
    @Getter
    private int amountPerByte;
    @GuiSync(5)
    @Getter
    private int restrictedTypes;
    @GuiSync(6)
    @Getter
    private long restrictedAmount;
    @GuiSync(7)
    @Getter
    private boolean isPerType;
    @GuiSync(8)
    @Getter
    private int whitelistSize;

    private final CellWorkbenchBlockEntity workbench;

    public RestrictedCellSettingsMenu(int id, Inventory playerInventory, CellWorkbenchBlockEntity workbench) {
        super(TYPE, id, playerInventory, workbench);

        this.workbench = workbench;

        this.registerClientAction(ACTION_SET_RESTRICTION, CellRestriction.class, this::setRestriction);
        this.registerClientAction(ACTION_RESET_RESTRICTION, this::resetRestriction);
    }

    @Override
    public ISubMenuHost getHost() {
        return WorkbenchSubMenuHost.INSTANCE;
    }

    public void setRestriction(CellRestriction restriction) {
        if (this.isClientSide()) {
            this.sendClientAction(ACTION_SET_RESTRICTION, restriction);

            return;
        }

        var cellStack = this.getCellStack();

        if (!(cellStack.getItem() instanceof IRestrictedCellItem restrictedCell)) return;

        var inventory = RestrictedCellInventory.createInventory(cellStack, null);
        var types = Math.max(0, Math.min(restriction.types(), restrictedCell.getTotalTypes(cellStack)));
        var amount = Math.min(restriction.amount(), inventory.getMaxRestrictedAmount(types));

        restrictedCell.setRestriction(cellStack, new CellRestriction(restriction.types(), amount));

        this.workbench.saveChanges();
    }

    public void resetRestriction() {
        if (this.isClientSide()) {
            this.sendClientAction(ACTION_RESET_RESTRICTION);

            return;
        }

        this.setRestriction(CellRestriction.NONE);
    }

    @Override
    public void broadcastChanges() {
        if (this.isServerSide()) {
            this.updateCellData();
        }

        super.broadcastChanges();
    }

    private ItemStack getCellStack() {
        return this.workbench.getSubInventory(ISegmentedInventory.CELLS).getStackInSlot(0);
    }

    private void updateCellData() {
        var cellStack = this.getCellStack();

        if (!(cellStack.getItem() instanceof IRestrictedCellItem restrictedCell)) {
            this.totalBytes = 0;
            this.totalTypes = 0;
            this.bytesPerType = 0;
            this.amountPerByte = 0;
            this.restrictedTypes = 0;
            this.restrictedAmount = 0;
            this.isPerType = false;
            this.whitelistSize = 0;

            return;
        }

        var restriction = restrictedCell.getRestriction(cellStack);
        var inventory = RestrictedCellInventory.createInventory(cellStack, null);

        this.totalBytes = restrictedCell.getBytes(cellStack);
        this.totalTypes = restrictedCell.getTotalTypes(cellStack);
        this.bytesPerType = restrictedCell.getBytesPerType(cellStack);
        this.amountPerByte = restrictedCell.getAmountPerByte();
        this.restrictedTypes = restriction.types();
        this.restrictedAmount = restriction.amount();
        this.isPerType = inventory.isEqualDistribution();
        this.whitelistSize = inventory.getWhitelistSize();
    }
}
