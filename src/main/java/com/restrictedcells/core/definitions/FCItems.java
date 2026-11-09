package com.restrictedcells.core.definitions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import com.restrictedcells.RestrictedCells;
import com.restrictedcells.core.Addon;
import com.restrictedcells.items.storage.RestrictedPortableCell;
import com.restrictedcells.items.storage.RestrictedStorageCell;

import me.ramidzkh.mekae2.AMMenus;

import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;
import appeng.core.AppEng;
import appeng.core.definitions.ItemDefinition;
import appeng.items.storage.StorageTier;
import appeng.menu.me.common.MEStorageMenu;

import static net.minecraft.core.registries.BuiltInRegistries.ITEM;

public final class FCItems {

    private static final int ITEM_CELL_TYPES = 63;
    private static final int FLUID_CELL_TYPES = 18;
    private static final int DUAL_CELL_TYPES = 63;
    private static final int CHEMICAL_CELL_TYPES = 5;
    private static final int UNIVERSE_CELL_TYPES = 63;
    private static final int MEGA_FIRST_TIER_INDEX = 6;
    private static final int MEGA_TIER_COUNT = 5;
    private static final int BYTES_PER_KILOBYTE = 1024;
    private static final int BYTES_PER_TYPE_DIVISOR = 128;
    private static final double MEGA_IDLE_DRAIN_PER_TIER = 0.5;
    private static final long UNIVERSE_BYTES = Long.MAX_VALUE / 8;
    private static final long UNIVERSE_BYTES_PER_TYPE = Long.MAX_VALUE / (8 * BYTES_PER_TYPE_DIVISOR);
    private static final double UNIVERSE_IDLE_DRAIN = 100;

    public static final List<StorageTier> STORAGE_TIERS = List.of(
            StorageTier.SIZE_1K,
            StorageTier.SIZE_4K,
            StorageTier.SIZE_16K,
            StorageTier.SIZE_64K,
            StorageTier.SIZE_256K);
    public static final List<StorageTier> MEGA_TIERS = createMegaTiers();

    private static final List<ItemDefinition<RestrictedStorageCell>> CELLS = new ArrayList<>();
    private static final List<ItemDefinition<RestrictedPortableCell>> PORTABLE_CELLS = new ArrayList<>();
    private static final List<ItemDefinition<? extends Item>> ITEMS = new ArrayList<>();

    public static final List<ItemDefinition<RestrictedStorageCell>> ITEM_CELLS = createCells("Item", ITEM_CELL_TYPES, () -> Set.of(AEKeyType.items()), STORAGE_TIERS, StorageTier::idleDrain);
    public static final List<ItemDefinition<RestrictedStorageCell>> FLUID_CELLS = createCells("Fluid", FLUID_CELL_TYPES, () -> Set.of(AEKeyType.fluids()), STORAGE_TIERS, StorageTier::idleDrain);
    public static final List<ItemDefinition<RestrictedStorageCell>> MEGA_ITEM_CELLS = createAddonCells(Addon.MEGA_CELLS, "Item", ITEM_CELL_TYPES, () -> Set.of(AEKeyType.items()), MEGA_TIERS, StorageTier::idleDrain);
    public static final List<ItemDefinition<RestrictedStorageCell>> MEGA_FLUID_CELLS = createAddonCells(Addon.MEGA_CELLS, "Fluid", FLUID_CELL_TYPES, () -> Set.of(AEKeyType.fluids()), MEGA_TIERS, StorageTier::idleDrain);
    public static final List<ItemDefinition<RestrictedStorageCell>> DUAL_CELLS = createAddonCells(Addon.EXPANDED_AE, "Dual", DUAL_CELL_TYPES, () -> Set.of(AEKeyType.items(), AEKeyType.fluids()), createDualTiers(), StorageTier::index);
    public static final List<ItemDefinition<RestrictedStorageCell>> UNIVERSE_CELLS = createUniverseCells();
    public static final List<ItemDefinition<RestrictedStorageCell>> CHEMICAL_CELLS = createChemicalCells(STORAGE_TIERS, Addon.APPLIED_MEKANISTICS);
    public static final List<ItemDefinition<RestrictedStorageCell>> MEGA_CHEMICAL_CELLS = createChemicalCells(MEGA_TIERS, Addon.APPLIED_MEKANISTICS, Addon.MEGA_CELLS);
    public static final List<ItemDefinition<RestrictedPortableCell>> PORTABLE_ITEM_CELLS = createPortableCells("Item", () -> Set.of(AEKeyType.items()), MEStorageMenu.PORTABLE_ITEM_CELL_TYPE, STORAGE_TIERS);
    public static final List<ItemDefinition<RestrictedPortableCell>> PORTABLE_FLUID_CELLS = createPortableCells("Fluid", () -> Set.of(AEKeyType.fluids()), MEStorageMenu.PORTABLE_FLUID_CELL_TYPE, STORAGE_TIERS);
    public static final List<ItemDefinition<RestrictedPortableCell>> MEGA_PORTABLE_ITEM_CELLS = createAddonPortableCells("Item", () -> Set.of(AEKeyType.items()), MEStorageMenu.PORTABLE_ITEM_CELL_TYPE);
    public static final List<ItemDefinition<RestrictedPortableCell>> MEGA_PORTABLE_FLUID_CELLS = createAddonPortableCells("Fluid", () -> Set.of(AEKeyType.fluids()), MEStorageMenu.PORTABLE_FLUID_CELL_TYPE);
    public static final List<ItemDefinition<RestrictedPortableCell>> PORTABLE_CHEMICAL_CELLS = createChemicalPortableCells(STORAGE_TIERS, Addon.APPLIED_MEKANISTICS);
    public static final List<ItemDefinition<RestrictedPortableCell>> MEGA_PORTABLE_CHEMICAL_CELLS = createChemicalPortableCells(MEGA_TIERS, Addon.APPLIED_MEKANISTICS, Addon.MEGA_CELLS);

    private FCItems() {}

    public static List<ItemDefinition<RestrictedStorageCell>> getCells() {
        return Collections.unmodifiableList(CELLS);
    }

    public static List<ItemDefinition<RestrictedPortableCell>> getPortableCells() {
        return Collections.unmodifiableList(PORTABLE_CELLS);
    }

    public static List<ItemDefinition<? extends Item>> getItems() {
        return Collections.unmodifiableList(ITEMS);
    }

    private static List<StorageTier> createMegaTiers() {
        return IntStream.range(MEGA_FIRST_TIER_INDEX, MEGA_FIRST_TIER_INDEX + MEGA_TIER_COUNT)
                .mapToObj(FCItems::createMegaTier)
                .toList();
    }

    private static StorageTier createMegaTier(int index) {
        var multiplier = (int) Math.pow(4, index - 1);
        var prefix = multiplier / BYTES_PER_KILOBYTE + "m";
        var componentId = new ResourceLocation(Addon.MEGA_CELLS.getModId(), "cell_component_" + prefix);

        return new StorageTier(index, prefix, BYTES_PER_KILOBYTE * multiplier, MEGA_IDLE_DRAIN_PER_TIER * index, () -> ITEM.get(componentId));
    }

    private static List<StorageTier> createDualTiers() {
        var tiers = new ArrayList<>(STORAGE_TIERS);

        if (Addon.MEGA_CELLS.isLoaded()) tiers.addAll(MEGA_TIERS);

        return tiers;
    }

    private static List<ItemDefinition<RestrictedStorageCell>> createAddonCells(Addon addon, String name, int totalTypes, Supplier<Set<AEKeyType>> keyTypes, List<StorageTier> tiers, ToDoubleFunction<StorageTier> idleDrain) {
        if (!addon.isLoaded()) return List.of();

        return createCells(name, totalTypes, keyTypes, tiers, idleDrain);
    }

    private static List<ItemDefinition<RestrictedStorageCell>> createChemicalCells(List<StorageTier> tiers, Addon... addons) {
        if (!Arrays.stream(addons).allMatch(Addon::isLoaded)) return List.of();

        return createCells("Chemical", CHEMICAL_CELL_TYPES, FCItems::createChemicalKeyTypes, tiers, StorageTier::idleDrain);
    }

    private static List<ItemDefinition<RestrictedStorageCell>> createCells(String name, int totalTypes, Supplier<Set<AEKeyType>> keyTypes, List<StorageTier> tiers, ToDoubleFunction<StorageTier> idleDrain) {
        return tiers.stream()
                .map(tier -> createCell(name, totalTypes, keyTypes, tier, idleDrain.applyAsDouble(tier)))
                .toList();
    }

    private static ItemDefinition<RestrictedStorageCell> createCell(String name, int totalTypes, Supplier<Set<AEKeyType>> keyTypes, StorageTier tier, double idleDrain) {
        var id = "restricted_" + name.toLowerCase(Locale.ROOT) + "_cell_" + tier.namePrefix();
        var item = new RestrictedStorageCell(
                new Item.Properties().stacksTo(1), tier.bytes(),
                tier.bytes() / BYTES_PER_TYPE_DIVISOR, idleDrain, totalTypes, keyTypes,
                createBaseItemId(name, tier));

        return register(createEnglishName(name, tier), id, item);
    }

    private static ResourceLocation createBaseItemId(String name, StorageTier tier) {
        if (name.equals("Dual")) return new ResourceLocation(Addon.EXPANDED_AE.getModId(), "dual_storage_cell_" + tier.namePrefix());

        var isMega = tier.index() > STORAGE_TIERS.size();

        if (name.equals("Chemical")) {
            var modId = isMega ? Addon.MEGA_CELLS.getModId() : Addon.APPLIED_MEKANISTICS.getModId();

            return new ResourceLocation(modId, "chemical_storage_cell_" + tier.namePrefix());
        }

        var modId = isMega ? Addon.MEGA_CELLS.getModId() : AppEng.MOD_ID;

        return new ResourceLocation(modId, name.toLowerCase(Locale.ROOT) + "_storage_cell_" + tier.namePrefix());
    }

    private static List<ItemDefinition<RestrictedStorageCell>> createUniverseCells() {
        if (!Addon.EXPANDED_AE.isLoaded()) return List.of();

        return Stream.of(createUniverseCell("Item", AEKeyType.items()), createUniverseCell("Fluid", AEKeyType.fluids())).toList();
    }

    private static ItemDefinition<RestrictedStorageCell> createUniverseCell(String name, AEKeyType keyType) {
        var id = "restricted_universe_" + name.toLowerCase(Locale.ROOT) + "_cell";
        var properties = new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant();
        var item = new RestrictedStorageCell(
                properties,
                UNIVERSE_BYTES,
                UNIVERSE_BYTES_PER_TYPE,
                UNIVERSE_IDLE_DRAIN,
                UNIVERSE_CELL_TYPES,
                () -> Set.of(keyType),
                new ResourceLocation(Addon.EXPANDED_AE.getModId(), "artificial_universe_" + name.toLowerCase(Locale.ROOT) + "_cell"));

        return register("Restricted Artificial Universe " + name + " Storage Cell", id, item);
    }

    private static ItemDefinition<RestrictedStorageCell> register(String englishName, String id, RestrictedStorageCell item) {
        var definition = new ItemDefinition<>(englishName, RestrictedCells.createId(id), item);

        CELLS.add(definition);
        ITEMS.add(definition);

        return definition;
    }

    private static List<ItemDefinition<RestrictedPortableCell>> createChemicalPortableCells(List<StorageTier> tiers, Addon... addons) {
        if (!Arrays.stream(addons).allMatch(Addon::isLoaded)) return List.of();

        return createPortableCells("Chemical", FCItems::createChemicalKeyTypes, AMMenus.PORTABLE_CHEMICAL_CELL_TYPE, tiers);
    }

    private static Set<AEKeyType> createChemicalKeyTypes() {
        return Set.of(AEKeyTypes.get(new ResourceLocation(Addon.APPLIED_MEKANISTICS.getModId(), "chemical")));
    }

    private static List<ItemDefinition<RestrictedPortableCell>> createAddonPortableCells(String name, Supplier<Set<AEKeyType>> keyTypes, MenuType<?> menuType) {
        if (!Addon.MEGA_CELLS.isLoaded()) return List.of();

        return createPortableCells(name, keyTypes, menuType, MEGA_TIERS);
    }

    private static List<ItemDefinition<RestrictedPortableCell>> createPortableCells(String name, Supplier<Set<AEKeyType>> keyTypes, MenuType<?> menuType, List<StorageTier> tiers) {
        return tiers.stream()
                .map(tier -> createPortableCell(name, keyTypes, menuType, tier))
                .toList();
    }

    private static ItemDefinition<RestrictedPortableCell> createPortableCell(String name, Supplier<Set<AEKeyType>> keyTypes, MenuType<?> menuType, StorageTier tier) {
        var kind = name.toLowerCase(Locale.ROOT);
        var baseId = createPortableBaseItemId(kind, tier);
        var item = new RestrictedPortableCell(menuType, new Item.Properties().stacksTo(1), keyTypes, baseId);
        var definition = new ItemDefinition<>(
                "Restricted Portable " + name + " Cell " + tier.namePrefix(),
                RestrictedCells.createId("restricted_portable_" + kind + "_cell_" + tier.namePrefix()),
                item);

        PORTABLE_CELLS.add(definition);
        ITEMS.add(definition);

        return definition;
    }

    private static ResourceLocation createPortableBaseItemId(String kind, StorageTier tier) {
        var isMega = tier.index() > STORAGE_TIERS.size();

        if (kind.equals("chemical") && !isMega) return new ResourceLocation(Addon.APPLIED_MEKANISTICS.getModId(), "portable_chemical_storage_cell_" + tier.namePrefix());

        var modId = isMega ? Addon.MEGA_CELLS.getModId() : AppEng.MOD_ID;

        return new ResourceLocation(modId, "portable_" + kind + "_cell_" + tier.namePrefix());
    }

    private static String createEnglishName(String name, StorageTier tier) {
        if (tier.index() > STORAGE_TIERS.size()) return "Restricted " + tier.namePrefix().toUpperCase(Locale.ROOT) + " MEGA " + name + " Storage Cell";

        return "Restricted " + tier.namePrefix() + " ME " + name + " Storage Cell";
    }
}
