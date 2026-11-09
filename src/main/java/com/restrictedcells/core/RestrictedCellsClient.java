package com.restrictedcells.core;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import com.restrictedcells.RestrictedCells;
import com.restrictedcells.client.gui.implementations.RestrictedCellSettingsScreen;
import com.restrictedcells.init.client.InitItemColors;
import com.restrictedcells.menu.implementations.RestrictedCellSettingsMenu;

import appeng.init.client.InitScreens;

@Mod.EventBusSubscriber(modid = RestrictedCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class RestrictedCellsClient {

    private static final String CELL_WORKBENCH_STYLE = "/screens/cell_workbench.json";

    private RestrictedCellsClient() {}

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        InitScreens.registerAdditionalStyle(CELL_WORKBENCH_STYLE, RestrictedCells.createId("screens/cell_workbench_additions.json"));
        InitScreens.register(RestrictedCellSettingsMenu.TYPE, RestrictedCellSettingsScreen::new, "/screens/restricted_cell_settings.json");
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        InitItemColors.init(event);
    }
}
