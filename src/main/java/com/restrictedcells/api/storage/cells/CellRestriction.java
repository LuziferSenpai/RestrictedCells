package com.restrictedcells.api.storage.cells;

import com.restrictedcells.util.SaturatedMath;

public record CellRestriction(int types, long amount) {

    public static final CellRestriction NONE = new CellRestriction(0, 0);

    public static long maxAmount(long totalBytes, long bytesPerType, int totalTypes, int types, int amountPerByte) {
        var effectiveTypes = types == 0 ? totalTypes : types;
        var availableBytes = totalBytes - bytesPerType * effectiveTypes;

        return SaturatedMath.multiply(Math.max(0, availableBytes), amountPerByte);
    }

    public static int distributionTypes(int totalTypes, int types, int whitelistSize) {
        var limit = types > 0 ? Math.min(types, totalTypes) : totalTypes;

        return Math.max(1, whitelistSize > 0 ? Math.min(whitelistSize, limit) : limit);
    }

    public static long maxAmountPerType(long totalBytes, long bytesPerType, int distributionTypes,
            int amountPerByte) {
        var capacity = SaturatedMath.multiply(Math.max(0, totalBytes - bytesPerType * distributionTypes),
                amountPerByte);

        return capacity <= 0 ? 0 : (capacity - 1) / distributionTypes + 1;
    }

    public boolean isRestricted() {
        return this.types > 0 || this.amount > 0;
    }
}
