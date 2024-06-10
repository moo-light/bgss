package com.server.blockchainserver.models.shopping_model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.blockchainserver.models.enums.EProcessReceiveProduct;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_detail")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", updatable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", updatable = false)
    private Product product;
    private Double weight;
    private Long quantity;
    private BigDecimal price;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(length = 100)
    private EProcessReceiveProduct processReceiveProduct;

    public OrderDetail() {
    }

    public OrderDetail(Order order, CartItem cartItem, EProcessReceiveProduct processReceiveProduct) {
        this.order = order;
        this.product = cartItem.getProduct();
        this.weight = cartItem.getProduct().getWeight();
        this.quantity = cartItem.getQuantity();
        this.price = cartItem.getPrice();
        this.amount = cartItem.getAmount();
        this.processReceiveProduct = processReceiveProduct;
    }

    public OrderDetail(Order order, Product product, Long quantity, BigDecimal price, BigDecimal amount, EProcessReceiveProduct processReceiveProduct){
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.amount = amount;
        this.processReceiveProduct = processReceiveProduct;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public EProcessReceiveProduct getProcessReceiveProduct() {
        return processReceiveProduct;
    }

    public void setProcessReceiveProduct(EProcessReceiveProduct processReceiveProduct) {
        this.processReceiveProduct = processReceiveProduct;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
