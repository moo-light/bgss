package com.server.blockchainserver.models.user_model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.blockchainserver.platform.entity.GoldTransaction;
import com.server.blockchainserver.platform.entity.SecretKey;
import com.server.blockchainserver.platform.entity.UserGoldInventory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.sql.Date;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "user_info")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Size(max = 20)
    private String phoneNumber;

    @OneToOne(mappedBy = "userInfo")
    private AvatarData avatarData;

    private Date doB;
    private String address;

    private String ciCard;

    @OneToMany
    private List<CICardImage> ciCardImage;

    @OneToOne(mappedBy = "userInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Balance balance;

    @OneToOne(mappedBy = "userInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserGoldInventory inventory; // Kho vàng riêng của người dùng

    @OneToMany(mappedBy = "actionParty", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GoldTransaction> goldTransactions; // Các giao dịch vàng của người dùng

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "userInfo")
    private SecretKey secretKey;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

    public UserInfo() {
    }

    public UserInfo(String firstName, String lastName, String phoneNumber, AvatarData avatarData,
                    Date doB, String address, String ciCard, List<CICardImage> ciCardImage, Balance balance,
                    UserGoldInventory inventory, Set<GoldTransaction> goldTransactions, User user) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.avatarData = avatarData;
        this.doB = doB;
        this.address = address;
        this.ciCard = ciCard;
        this.ciCardImage = ciCardImage;
        this.balance = balance;
        this.inventory = inventory;
        this.goldTransactions = goldTransactions;
        this.user = user;
    }

    public UserInfo(String firstName, String lastName, String phoneNumber, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AvatarData getAvatarData() {
        return avatarData;
    }

    public void setAvatarData(AvatarData avatarData) {
        this.avatarData = avatarData;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCiCard() {
        return ciCard;
    }

    public void setCiCard(String ciCard) {
        this.ciCard = ciCard;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CICardImage> getCiCardImage() {
        return ciCardImage;
    }

    public void setCiCardImage(List<CICardImage> ciCardImage) {
        this.ciCardImage = ciCardImage;
    }

    public Date getDoB() {
        return doB;
    }

    public void setDoB(Date doB) {
        this.doB = doB;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }

    public UserGoldInventory getInventory() {
        return inventory;
    }

    public void setInventory(UserGoldInventory inventory) {
        this.inventory = inventory;
    }

    public Set<GoldTransaction> getGoldTransactions() {
        return goldTransactions;
    }

    public void setGoldTransactions(Set<GoldTransaction> goldTransactions) {
        this.goldTransactions = goldTransactions;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }
}
