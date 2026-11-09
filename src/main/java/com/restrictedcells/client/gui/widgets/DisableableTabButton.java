package com.restrictedcells.client.gui.widgets;

import java.util.function.BooleanSupplier;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import appeng.client.gui.widgets.TabButton;

public class DisableableTabButton extends TabButton {

    private static final int INACTIVE_OVERLAY_COLOR = 0xAAC6C6C6;
    private static final int OVERLAY_DEPTH = 400;
    private static final int BORDER_SIZE = 3;

    private final BooleanSupplier isEnabled;

    public DisableableTabButton(ItemStack icon, Component message, Button.OnPress onPress, BooleanSupplier isEnabled) {
        super(icon, message, onPress);

        this.isEnabled = isEnabled;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        this.active = this.isEnabled.getAsBoolean();

        super.renderWidget(guiGraphics, mouseX, mouseY, partial);

        if (!this.visible || this.active) return;

        var pose = guiGraphics.pose();

        pose.pushPose();
        pose.translate(0, 0, OVERLAY_DEPTH);
        guiGraphics.fill(
                this.getX() + BORDER_SIZE, this.getY() + BORDER_SIZE,
                this.getX() + this.width - BORDER_SIZE, this.getY() + this.height - BORDER_SIZE,
                INACTIVE_OVERLAY_COLOR);
        pose.popPose();
    }
}
