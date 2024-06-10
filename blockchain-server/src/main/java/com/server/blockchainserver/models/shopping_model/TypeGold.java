package com.server.blockchainserver.models.shopping_model;

import com.server.blockchainserver.platform.entity.enums.GoldUnit;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "type_golds")
public class TypeGold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String typeName;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private GoldUnit goldUnit;
    private boolean active;

    public TypeGold() {
    }

    public TypeGold(String typeName, BigDecimal price, GoldUnit goldUnit,  boolean active) {
        this.typeName = typeName;
        this.price = price;
        this.goldUnit = goldUnit;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public GoldUnit getGoldUnit() {
        return goldUnit;
    }

    public void setGoldUnit(GoldUnit goldUnit) {
        this.goldUnit = goldUnit;
    }
}
