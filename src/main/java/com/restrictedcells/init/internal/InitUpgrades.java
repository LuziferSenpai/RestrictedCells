package com.restrictedcells.init.internal;

import com.restrictedcells.core.definitions.FCItems;

import appeng.api.stacks.AEKeyType;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;

public final class InitUpgrades {

    private static final int CARD_LIMIT = 1;
    private static final int ENERGY_CARD_LIMIT = 2;

    private InitUpgrades() {}

    public static void init() {
        var storageCellGroup = GuiText.StorageCells.getTranslationKey();

        for (var cell : FCItems.getCells()) {
            if (cell.asItem().getKeyTypes().contains(AEKeyType.items())) Upgrades.add(AEItems.FUZZY_CARD, cell, CARD_LIMIT, storageCellGroup);

            Upgrades.add(AEItems.INVERTER_CARD, cell, CARD_LIMIT, storageCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, cell, CARD_LIMIT, storageCellGroup);
            Upgrades.add(AEItems.VOID_CARD, cell, CARD_LIMIT, storageCellGroup);
            Upgrades.add(AEItems.STICKY_CARD, cell, CARD_LIMIT, storageCellGroup);
        }

        var portableCellGroup = GuiText.PortableCells.getTranslationKey();

        for (var cell : FCItems.getPortableCells()) {
            if (cell.asItem().getKeyTypes().contains(AEKeyType.items())) Upgrades.add(AEItems.FUZZY_CARD, cell, CARD_LIMIT, portableCellGroup);

            Upgrades.add(AEItems.INVERTER_CARD, cell, CARD_LIMIT, portableCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, cell, CARD_LIMIT, portableCellGroup);
            Upgrades.add(AEItems.VOID_CARD, cell, CARD_LIMIT, portableCellGroup);
            Upgrades.add(AEItems.ENERGY_CARD, cell, ENERGY_CARD_LIMIT, portableCellGroup);
        }
    }
}
