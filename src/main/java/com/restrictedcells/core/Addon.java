package com.restrictedcells.core;

import net.minecraftforge.fml.ModList;

import lombok.Getter;

public enum Addon {

    MEGA_CELLS("megacells"),
    EXPANDED_AE("expandedae"),
    APPLIED_MEKANISTICS("appmek");

    @Getter
    private final String modId;

    Addon(String modId) {
        this.modId = modId;
    }

    public boolean isLoaded() {
        var modList = ModList.get();

        return modList != null && modList.isLoaded(this.modId);
    }
}
