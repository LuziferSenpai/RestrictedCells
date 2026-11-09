package com.restrictedcells.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

import com.restrictedcells.core.definitions.FCItems;

public final class InitItems {

    private InitItems() {}

    public static void init(IForgeRegistry<Item> registry) {
        for (var definition : FCItems.getItems()) {
            registry.register(definition.id(), definition.asItem());
        }
    }
}
