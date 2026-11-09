package com.restrictedcells.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.IForgeRegistry;

import com.restrictedcells.RestrictedCells;
import com.restrictedcells.menu.implementations.RestrictedCellSettingsMenu;

public final class InitMenuTypes {

    private InitMenuTypes() {}

    public static void init(IForgeRegistry<MenuType<?>> registry) {
        registry.register(RestrictedCells.createId(RestrictedCellSettingsMenu.ID), RestrictedCellSettingsMenu.TYPE);
    }
}
