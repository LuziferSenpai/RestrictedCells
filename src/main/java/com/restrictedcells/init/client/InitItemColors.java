package com.restrictedcells.init.client;

import net.minecraftforge.client.event.RegisterColorHandlersEvent;

import com.restrictedcells.core.definitions.FCItems;
import com.restrictedcells.items.storage.RestrictedStorageCell;

import appeng.items.tools.powered.AbstractPortableCell;

public final class InitItemColors {

    private InitItemColors() {}

    public static void init(RegisterColorHandlersEvent.Item event) {
        for (var cell : FCItems.getCells()) {
            event.register(RestrictedStorageCell::getColor, cell.asItem());
        }

        for (var cell : FCItems.getPortableCells()) {
            event.register(AbstractPortableCell::getColor, cell.asItem());
        }
    }
}
