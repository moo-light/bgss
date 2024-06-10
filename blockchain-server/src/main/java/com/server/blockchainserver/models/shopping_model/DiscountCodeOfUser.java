package com.server.blockchainserver.models.shopping_model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.blockchainserver.models.user_model.User;
import jakarta.persistence.*;

@Entity
@Table(name = "discount_code_user")
public class DiscountCodeOfUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean available = true;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    @ManyToOne
    @JoinColumn(name = "discount_id")
    private Discount discount;

    private int quantity_default;
    public DiscountCodeOfUser() {
    }

    public DiscountCodeOfUser(Discount discount, int quantity_default, User user) {
        this.discount = discount;
        this.quantity_default = quantity_default;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getQuantity_default() {
        return quantity_default;
    }

    public void setQuantity_default(int quantity_default) {
        this.quantity_default = quantity_default;
    }
}
