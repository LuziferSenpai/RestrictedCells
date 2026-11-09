package com.restrictedcells.init.internal;

import com.restrictedcells.RestrictedCells;
import com.restrictedcells.core.definitions.FCItems;
import com.restrictedcells.core.registries.cell.RestrictedCellGuiHandler;
import com.restrictedcells.me.cells.RestrictedCellHandler;

import appeng.api.client.StorageCellModels;
import appeng.api.storage.StorageCells;
import appeng.hotkeys.HotkeyActions;

public final class InitStorageCells {

    private InitStorageCells() {}

    public static void init() {
        StorageCells.addCellHandler(RestrictedCellHandler.INSTANCE);
        StorageCells.addCellGuiHandler(new RestrictedCellGuiHandler());

        for (var cell : FCItems.getPortableCells()) {
            var hotkeyId = cell.asItem().getHotkeyId();

            if (hotkeyId != null) HotkeyActions.registerPortableCell(cell, hotkeyId);
        }

        for (var cell : FCItems.getCells()) {
            StorageCellModels.registerModel(cell, RestrictedCells.createId("block/drive/cells/" + cell.id().getPath()));
        }
    }
}
