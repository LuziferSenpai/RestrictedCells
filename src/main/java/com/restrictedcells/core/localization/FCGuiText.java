package com.restrictedcells.core.localization;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import com.restrictedcells.RestrictedCells;

import lombok.Getter;

public enum FCGuiText {

    CreativeTab("Restricted Cells", "Restricted Cells"),
    RestrictedName("Restricted %s", "Eingeschränkte %s"),
    Restrictions("Cell restrictions", "Zelleneinschränkungen"),
    ResourceAmount("Resource amount", "Ressourcenmenge"),
    ResourceAmountPerType("Resources per type", "Ressourcen pro Typ"),
    Types("Types", "Typen"),
    Reset("Reset", "Zurücksetzen"),
    Restricted("Restricted", "Eingeschränkt"),
    RestrictedSuffix("(restricted)", "(eingeschränkt)"),
    MaxResources("Max resources: %s", "Max. Ressourcen: %s"),
    MaxResourcesPerType("Max resources per type: %s", "Max. Ressourcen pro Typ: %s"),
    MaximumOfResource("Max: %s", "Max.: %s"),
    MaximumPerType("Max per type: %s", "Max. pro Typ: %s"),
    BytesTotal("Total bytes: %s", "Bytes gesamt: %s"),
    BytesAllocated("Allocated bytes: %s", "Belegte Bytes: %s"),
    BytesFree("Free bytes: %s", "Freie Bytes: %s"),
    ResourcesPerByte("Resources per byte: %s", "Ressourcen pro Byte: %s"),
    BytesPerType("Bytes per type: %s", "Bytes pro Typ: %s");

    @Getter
    private final String englishText;
    @Getter
    private final String germanText;

    FCGuiText(String englishText, String germanText) {
        this.englishText = englishText;
        this.germanText = germanText;
    }

    public String getTranslationKey() {
        return "gui." + RestrictedCells.MOD_ID + "." + this.name();
    }

    public MutableComponent text(Object... args) {
        return Component.translatable(this.getTranslationKey(), args);
    }
}
