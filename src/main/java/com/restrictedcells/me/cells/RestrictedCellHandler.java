package com.restrictedcells.me.cells;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import com.restrictedcells.api.storage.cells.CellRestriction;
import com.restrictedcells.api.storage.cells.IRestrictedCellItem;
import com.restrictedcells.core.localization.FCGuiText;

import appeng.api.config.IncludeExclude;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.core.AEConfig;
import appeng.core.localization.GuiText;
import appeng.core.localization.Tooltips;
import appeng.items.storage.StorageCellTooltipComponent;

public class RestrictedCellHandler implements ICellHandler {

    public static final RestrictedCellHandler INSTANCE = new RestrictedCellHandler();

    @Override
    public boolean isCell(ItemStack stack) {
        return stack.getItem() instanceof IRestrictedCellItem;
    }

    @Nullable
    @Override
    public RestrictedCellInventory getCellInventory(ItemStack stack, @Nullable ISaveProvider host) {
        return RestrictedCellInventory.createInventory(stack, host);
    }

    public void addCellInformationToTooltip(ItemStack stack, List<Component> lines) {
        var inventory = this.getCellInventory(stack, null);

        if (inventory == null) return;

        lines.add(Tooltips.bytesUsed(inventory.getUsedBytes(), inventory.getTotalBytes()));
        lines.add(Tooltips.typesUsed(inventory.getStoredItemTypes(), inventory.getTotalItemTypes()));

        var restriction = ((IRestrictedCellItem) stack.getItem()).getRestriction(stack);

        if (restriction.isRestricted()) {
            lines.add(Component.empty());
            lines.add(createRestrictionLine(restriction, inventory.isEqualDistribution()));
        }

        if (inventory.isNotPreformatted()) return;

        var list = (inventory.getPartitionListMode() == IncludeExclude.WHITELIST ? GuiText.Included : GuiText.Excluded).text();
        var precision = inventory.isFuzzy() ? GuiText.Fuzzy : GuiText.Precise;

        lines.add(GuiText.Partitioned.withSuffix(" - ").append(list).append(" ").append(precision.text()));
    }

    private static Component createRestrictionLine(CellRestriction restriction, boolean isEqualDistribution) {
        if (restriction.amount() <= 0) return FCGuiText.Restricted.text().withStyle(ChatFormatting.GOLD);

        var text = isEqualDistribution ? FCGuiText.MaxResourcesPerType : FCGuiText.MaxResources;
        var maxResources = text.text(Tooltips.ofNumber(restriction.amount())).withStyle(Tooltips.UNIT_TEXT);

        return maxResources.append(" ").append(FCGuiText.RestrictedSuffix.text().withStyle(ChatFormatting.GOLD));
    }

    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        var inventory = this.getCellInventory(stack, null);

        if (inventory == null) return Optional.empty();

        var upgradeStacks = new ArrayList<ItemStack>();

        if (AEConfig.instance().isTooltipShowCellUpgrades()) {
            for (var upgrade : inventory.getUpgradesInventory()) {
                upgradeStacks.add(upgrade);
            }
        }

        var hasMoreContent = false;

        List<GenericStack> content = Collections.emptyList();

        if (AEConfig.instance().isTooltipShowCellContent()) {
            content = new ArrayList<>();

            var maxCountShown = AEConfig.instance().getTooltipMaxCellContentShown();
            var availableStacks = inventory.getAvailableStacks();

            for (var entry : availableStacks) {
                content.add(new GenericStack(entry.getKey(), entry.getLongValue()));
            }

            if (content.size() < maxCountShown && inventory.getPartitionListMode() == IncludeExclude.WHITELIST) {
                var config = inventory.getConfigInventory();

                for (var index = 0; index < config.size() && content.size() <= maxCountShown; index++) {
                    var what = config.getKey(index);

                    if (what != null && availableStacks.get(what) <= 0) content.add(new GenericStack(what, 0));
                }
            }

            content.sort(Comparator.comparingLong(GenericStack::amount).reversed());

            hasMoreContent = content.size() > maxCountShown;

            if (hasMoreContent) content.subList(maxCountShown, content.size()).clear();
        }

        return Optional.of(new StorageCellTooltipComponent(upgradeStacks, content, hasMoreContent, true));
    }
}
