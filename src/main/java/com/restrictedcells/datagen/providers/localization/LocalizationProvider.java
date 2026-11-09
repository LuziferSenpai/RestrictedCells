package com.restrictedcells.datagen.providers.localization;

import java.util.function.Function;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

import com.restrictedcells.RestrictedCells;
import com.restrictedcells.core.localization.FCGuiText;

public class LocalizationProvider extends LanguageProvider {

    private final Function<FCGuiText, String> texts;

    public LocalizationProvider(PackOutput output, String locale, Function<FCGuiText, String> texts) {
        super(output, RestrictedCells.MOD_ID, locale);

        this.texts = texts;
    }

    @Override
    protected void addTranslations() {
        for (var text : FCGuiText.values()) {
            this.add(text.getTranslationKey(), this.texts.apply(text));
        }
    }
}
