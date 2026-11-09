package com.restrictedcells.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.api.storage.ISubMenuHost;
import appeng.core.definitions.AEBlocks;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.implementations.CellWorkbenchMenu;

public final class WorkbenchSubMenuHost implements ISubMenuHost {

    public static final WorkbenchSubMenuHost INSTANCE = new WorkbenchSubMenuHost();

    private WorkbenchSubMenuHost() {}

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(CellWorkbenchMenu.TYPE, player, subMenu.getLocator());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return AEBlocks.CELL_WORKBENCH.stack();
    }
}
