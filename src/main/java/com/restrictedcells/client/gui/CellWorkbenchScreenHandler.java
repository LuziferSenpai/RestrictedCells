package com.restrictedcells.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.restrictedcells.RestrictedCells;
import com.restrictedcells.api.storage.cells.IRestrictedCellItem;
import com.restrictedcells.client.gui.widgets.DisableableTabButton;
import com.restrictedcells.core.localization.FCGuiText;
import com.restrictedcells.menu.implementations.RestrictedCellSettingsMenu;

import appeng.client.gui.implementations.CellWorkbenchScreen;
import appeng.core.definitions.AEItems;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.SwitchGuisPacket;

@Mod.EventBusSubscriber(modid = RestrictedCells.MOD_ID, value = Dist.CLIENT)
public final class CellWorkbenchScreenHandler {

    private static final int BUTTON_LEFT = 154;
    private static final int BUTTON_TOP = 0;

    private CellWorkbenchScreenHandler() {}

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof CellWorkbenchScreen screen)) return;

        var button = new DisableableTabButton(
                AEItems.CERTUS_QUARTZ_WRENCH.stack(),
                FCGuiText.Restrictions.text(),
                pressed -> NetworkHandler.instance().sendToServer(SwitchGuisPacket.openSubMenu(RestrictedCellSettingsMenu.TYPE)),
                () -> screen.getMenu().getWorkbenchItem().getItem() instanceof IRestrictedCellItem);

        button.setPosition(screen.getGuiLeft() + BUTTON_LEFT, screen.getGuiTop() + BUTTON_TOP);
        event.addListener(button);
    }
}
