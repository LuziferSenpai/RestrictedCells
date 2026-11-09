package com.restrictedcells.datagen;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.restrictedcells.RestrictedCells;
import com.restrictedcells.core.localization.FCGuiText;
import com.restrictedcells.datagen.providers.localization.LocalizationProvider;
import com.restrictedcells.datagen.providers.recipes.RestrictedCellRecipeProvider;

@Mod.EventBusSubscriber(modid = RestrictedCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DatagenProvider {

    private DatagenProvider() {}

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new LocalizationProvider(output, "en_us", FCGuiText::getEnglishText));
        generator.addProvider(event.includeClient(), new LocalizationProvider(output, "de_de", FCGuiText::getGermanText));
        generator.addProvider(event.includeServer(), new RestrictedCellRecipeProvider(output));
    }
}
