package com.restrictedcells.client.gui.implementations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import com.restrictedcells.api.storage.cells.CellRestriction;
import com.restrictedcells.core.localization.FCGuiText;
import com.restrictedcells.menu.implementations.RestrictedCellSettingsMenu;
import com.restrictedcells.util.SaturatedMath;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.MathExpressionParser;
import appeng.client.gui.implementations.AESubScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AETextField;

public class RestrictedCellSettingsScreen extends AEBaseScreen<RestrictedCellSettingsMenu> {

    private static final BigDecimal MAX_LONG = BigDecimal.valueOf(Long.MAX_VALUE);
    private static final Map<Character, BigDecimal> SUFFIX_MULTIPLIERS = Map.of('k', BigDecimal.valueOf(1_000L), 'm', BigDecimal.valueOf(1_000_000L), 'g', BigDecimal.valueOf(1_000_000_000L), 't', BigDecimal.valueOf(1_000_000_000_000L));

    private final AETextField amountField;
    private final AETextField typesField;
    private boolean isRefreshingFields;

    public RestrictedCellSettingsScreen(RestrictedCellSettingsMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        AESubScreen.addBackButton(menu, "back", this.widgets);

        this.widgets.addButton("reset", FCGuiText.Reset.text(), menu::resetRestriction);

        this.amountField = this.widgets.addTextField("amountInput");
        this.typesField = this.widgets.addTextField("typesInput");

        this.amountField.setMaxLength(24);
        this.typesField.setMaxLength(2);
        this.amountField.setResponder(text -> this.onFieldChanged());
        this.typesField.setResponder(text -> this.onFieldChanged());
    }

    private static Locale getLocale() {
        return Locale.forLanguageTag(Minecraft.getInstance().getLanguageManager().getSelected().replace('_', '-'));
    }

    private static DecimalFormat createDecimalFormat(Locale locale) {
        var decimalFormat = new DecimalFormat("#.######", DecimalFormatSymbols.getInstance(locale));

        decimalFormat.setParseBigDecimal(true);

        return decimalFormat;
    }

    private static Optional<Long> parseExpression(String text) {
        if (text.isBlank()) return Optional.of(0L);

        var locale = getLocale();

        return MathExpressionParser.parse(expandSuffixes(text, locale), createDecimalFormat(locale)).map(value -> value.max(BigDecimal.ZERO).min(MAX_LONG).longValue());
    }

    private static String expandSuffixes(String text, Locale locale) {
        var separator = Pattern.quote(String.valueOf(DecimalFormatSymbols.getInstance(locale).getDecimalSeparator()));
        var suffixedNumber = Pattern.compile("(\\d+(?:" + separator + "\\d+)?)\\s*([kKmMgGtT])");

        return suffixedNumber.matcher(text).replaceAll(match -> {
            var number = new BigDecimal(match.group(1).replace(DecimalFormatSymbols.getInstance(locale).getDecimalSeparator(), '.'));
            var multiplier = SUFFIX_MULTIPLIERS.get(Character.toLowerCase(match.group(2).charAt(0)));

            return number.multiply(multiplier).setScale(0, RoundingMode.HALF_UP).toPlainString();
        });
    }

    private static long parse(String text) {
        return parseExpression(text).orElse(0L);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        var hasCell = this.menu.getTotalBytes() > 0;

        this.amountField.setEditable(hasCell);
        this.typesField.setEditable(hasCell);

        if (!this.amountField.isFocused()) {
            this.updateField(this.amountField, this.menu.getRestrictedAmount());
        }

        if (!this.typesField.isFocused()) {
            this.updateField(this.typesField, this.menu.getRestrictedTypes());
        }

        if (hasCell) {
            this.updateInformation();
        } else {
            this.clearInformation();
        }
    }

    private void updateInformation() {
        var types = this.getTypes();
        var isPerType = this.menu.isPerType();
        var reservedTypes = isPerType ? this.getDistributionTypes(types) : types == 0 ? this.menu.getTotalTypes() : types;
        var amount = this.getAmount(types);
        var storedAmount = isPerType ? SaturatedMath.multiply(amount, reservedTypes) : amount;
        var amountBytes = storedAmount == 0 ? 0 : (storedAmount - 1) / this.menu.getAmountPerByte() + 1;
        var allocatedBytes = Math.min(this.menu.getTotalBytes(), this.menu.getBytesPerType() * reservedTypes + amountBytes);
        var amountLabel = isPerType ? FCGuiText.ResourceAmountPerType : FCGuiText.ResourceAmount;
        var maxLabel = isPerType ? FCGuiText.MaximumPerType : FCGuiText.MaximumOfResource;

        this.setTextContent("amount_label", amountLabel.text());
        this.setTextContent("max_items", maxLabel.text(this.format(this.getMaxAmount(types))));
        this.setTextContent("bytes_total", FCGuiText.BytesTotal.text(this.format(this.menu.getTotalBytes())));
        this.setTextContent("bytes_allocated", FCGuiText.BytesAllocated.text(this.format(allocatedBytes)));
        this.setTextContent("bytes_free", FCGuiText.BytesFree.text(this.format(this.menu.getTotalBytes() - allocatedBytes)));
        this.setTextContent("resources_per_byte", FCGuiText.ResourcesPerByte.text(this.format(this.menu.getAmountPerByte())));
        this.setTextContent("bytes_per_type", FCGuiText.BytesPerType.text(this.format(this.menu.getBytesPerType())));
    }

    private void clearInformation() {
        this.setTextContent("amount_label", FCGuiText.ResourceAmount.text());
        this.setTextContent("max_items", Component.empty());
        this.setTextContent("bytes_total", Component.empty());
        this.setTextContent("bytes_allocated", Component.empty());
        this.setTextContent("bytes_free", Component.empty());
        this.setTextContent("resources_per_byte", Component.empty());
        this.setTextContent("bytes_per_type", Component.empty());
    }

    private String format(long value) {
        return NumberFormat.getIntegerInstance(getLocale()).format(value);
    }

    private void updateField(AETextField field, long value) {
        var text = Long.toString(value);

        if (field.getValue().equals(text)) return;

        this.isRefreshingFields = true;

        field.setValue(text);

        this.isRefreshingFields = false;
    }

    private void onFieldChanged() {
        if (this.isRefreshingFields) return;

        this.applyRestriction();
    }

    private int getTypes() {
        return (int) Math.min(parse(this.typesField.getValue()), this.menu.getTotalTypes());
    }

    private int getDistributionTypes(int types) {
        return CellRestriction.distributionTypes(this.menu.getTotalTypes(), types, this.menu.getWhitelistSize());
    }

    private long getMaxAmount(int types) {
        var menu = this.menu;

        if (menu.isPerType()) return CellRestriction.maxAmountPerType(menu.getTotalBytes(), menu.getBytesPerType(), this.getDistributionTypes(types), menu.getAmountPerByte());

        return CellRestriction.maxAmount(menu.getTotalBytes(), menu.getBytesPerType(), menu.getTotalTypes(), types, menu.getAmountPerByte());
    }

    private long getAmount(int types) {
        return Math.min(parse(this.amountField.getValue()), this.getMaxAmount(types));
    }

    private void applyRestriction() {
        if (parseExpression(this.typesField.getValue()).isEmpty() || parseExpression(this.amountField.getValue()).isEmpty()) return;

        var types = this.getTypes();

        this.menu.setRestriction(new CellRestriction(types, this.getAmount(types)));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        var isEnter = keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER;

        if (isEnter && (this.amountField.isFocused() || this.typesField.isFocused())) {
            this.applyRestriction();

            this.amountField.setFocused(false);
            this.typesField.setFocused(false);

            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
