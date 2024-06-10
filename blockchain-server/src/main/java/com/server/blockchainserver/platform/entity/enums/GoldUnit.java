package com.server.blockchainserver.platform.entity.enums;


import java.math.BigDecimal;
import java.math.RoundingMode;

public enum GoldUnit {
    KILOGRAM,
    GRAM,
    TAEL,
    MACE,
    TROY_OZ;

    public BigDecimal convertToTroyOunce(BigDecimal weight) {
        return switch (this) {
            case MACE -> weight.multiply(new BigDecimal("0.1214655").setScale(2, RoundingMode.HALF_UP));
            case TAEL ->
                    weight.multiply(new BigDecimal("1.21528").setScale(2, RoundingMode.HALF_UP));
            case KILOGRAM ->
                    weight.multiply(new BigDecimal("32.1507").setScale(2, RoundingMode.HALF_UP));
            case TROY_OZ -> weight.setScale(2, RoundingMode.HALF_UP);
            case GRAM -> weight.multiply(new BigDecimal("0.0321507").setScale(2, RoundingMode.HALF_UP));
        };
    }
}
