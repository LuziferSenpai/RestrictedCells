package com.restrictedcells;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import com.restrictedcells.core.MainCreativeTab;
import com.restrictedcells.init.InitItems;
import com.restrictedcells.init.InitMenuTypes;
import com.restrictedcells.init.internal.InitStorageCells;
import com.restrictedcells.init.internal.InitUpgrades;
import com.restrictedcells.recipe.RestrictedCellRecipe;

@Mod(RestrictedCells.MOD_ID)
public class RestrictedCells {

    public static final String MOD_ID = "restrictedcells";

    public RestrictedCells() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::register);
        modEventBus.addListener(this::commonSetup);
    }

    public static ResourceLocation createId(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private void register(RegisterEvent event) {
        var registryKey = event.getRegistryKey();

        if (registryKey.equals(Registries.ITEM)) InitItems.init(ForgeRegistries.ITEMS);
        else if (registryKey.equals(Registries.MENU)) InitMenuTypes.init(ForgeRegistries.MENU_TYPES);
        else if (registryKey.equals(Registries.RECIPE_SERIALIZER)) event.register(Registries.RECIPE_SERIALIZER, createId("restricted_cell"), () -> RestrictedCellRecipe.SERIALIZER);
        else if (registryKey.equals(Registries.CREATIVE_MODE_TAB)) event.register(Registries.CREATIVE_MODE_TAB, MainCreativeTab::init);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(InitStorageCells::init);
        event.enqueueWork(InitUpgrades::init);
    }
}
