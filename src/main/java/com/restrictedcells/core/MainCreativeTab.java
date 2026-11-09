package com.restrictedcells.core;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.RegisterEvent.RegisterHelper;

import com.restrictedcells.RestrictedCells;
import com.restrictedcells.core.definitions.FCItems;
import com.restrictedcells.core.localization.FCGuiText;

import appeng.items.AEBaseItem;

public final class MainCreativeTab {

    private MainCreativeTab() {}

    public static void init(RegisterHelper<CreativeModeTab> helper) {
        var tab = CreativeModeTab.builder()
                .title(FCGuiText.CreativeTab.text())
                .icon(FCItems.ITEM_CELLS.get(0)::stack)
                .displayItems(MainCreativeTab::buildDisplayItems)
                .build();

        helper.register(RestrictedCells.createId("main"), tab);
    }

    private static void buildDisplayItems(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        for (var definition : FCItems.getItems()) {
            if (definition.asItem() instanceof AEBaseItem baseItem) {
                baseItem.addToMainCreativeTab(output);
            } else {
                output.accept(definition);
            }
        }
    }
}
