package com.server.blockchainserver.models;

import com.server.blockchainserver.platform.entity.enums.GoldUnit;
import jakarta.persistence.*;

@Entity
@Table(name = "transfer_unit_gold")
public class TransferUnitGold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromUnit;
    private String toUnit;
    private String conversionFactor;

    public TransferUnitGold() {
    }

    public TransferUnitGold(String fromUnit, String toUnit, String conversionFactor) {
        this.fromUnit = fromUnit;
        this.toUnit = toUnit;
        this.conversionFactor = conversionFactor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromUnit() {
        return fromUnit;
    }

    public void setFromUnit(String fromUnit) {
        this.fromUnit = fromUnit;
    }

    public String getToUnit() {
        return toUnit;
    }

    public void setToUnit(String toUnit) {
        this.toUnit = toUnit;
    }

    public String getConversionFactor() {
        return conversionFactor;
    }

    public GoldUnit getGoldUnit() {
        String symbol = getSymbol();
        if (symbol == null) return null;
        symbol = symbol.toUpperCase();
        switch (symbol) {
            case "TOZ":
                return GoldUnit.TROY_OZ;
            case "G":
                return GoldUnit.GRAM;
            case "KG":
                return GoldUnit.KILOGRAM;
            case "TAEL":
                return GoldUnit.TAEL;
            case "MACE":
                return GoldUnit.MACE;
            default:
                return null;
        }
    }

    public String getSymbol() {
        if (fromUnit == null) return null;
        if (fromUnit.contains("("))
            return fromUnit.substring(fromUnit.lastIndexOf('(') + 1, fromUnit.length() - 1);  //Troy Ounce (tOz)
        return fromUnit;
    }

    public void setConversionFactor(String conversionFactor) {
        this.conversionFactor = conversionFactor;
    }
}
