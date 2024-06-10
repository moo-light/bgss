package com.server.blockchainserver.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.blockchainserver.models.user_model.UserInfo;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;


@Entity
@Table(name = "user_inventory")
public class UserGoldInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_info_id", nullable = false)
    @JsonIgnore
    private UserInfo userInfo; // Mỗi người dùng có một kho vàng riêng

    @Column(nullable = false)
    private BigDecimal totalWeightOz; // Tổng trọng lượng vàng của người dùng tính bằng Troy Ounce

    // Thêm quan hệ với WithdrawalRequest
    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL)
    private List<WithdrawGold> withdrawGold;


    public UserGoldInventory() {

    }

    public UserGoldInventory(UserInfo userInfo, BigDecimal totalWeightOz, List<WithdrawGold> withdrawGold) {
        this.userInfo = userInfo;
        this.totalWeightOz = totalWeightOz;
        this.withdrawGold = withdrawGold;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public BigDecimal getTotalWeightOz() {
        return totalWeightOz;
    }

    public void setTotalWeightOz(BigDecimal totalWeightOz) {
        this.totalWeightOz = totalWeightOz;
    }

    public List<WithdrawGold> getWithdrawGold() {
        return withdrawGold;
    }

    public void setWithdrawGold(List<WithdrawGold> withdrawGold) {
        this.withdrawGold = withdrawGold;
    }
}
