package com.server.blockchainserver.platform.entity;

import com.server.blockchainserver.platform.entity.enums.GoldUnit;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "gold_inventory")
public class GoldInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private BigDecimal totalWeightKg; // Tổng số vàng trong kho tính bằng kilogram

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoldUnit goldUnit = GoldUnit.TROY_OZ;

    public GoldInventory(BigDecimal totalWeightKg) {
        this.totalWeightKg = totalWeightKg;
    }


    public GoldInventory() {

    }

    // Getters và Setters cho các trường
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalWeightKg() {
        return totalWeightKg;
    }

    public void setTotalWeightKg(BigDecimal totalWeightKg) {
        this.totalWeightKg = totalWeightKg;
    }

    public GoldUnit getGoldUnit() {
        return goldUnit;
    }

    public void setGoldUnit(GoldUnit goldUnit) {
        this.goldUnit = goldUnit;
    }
}