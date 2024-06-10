package com.server.blockchainserver.dto.user_dto;

import com.server.blockchainserver.dto.balance_dto.BalanceDTO;
import com.server.blockchainserver.dto.gold_inventory_dto.GoldTransactionDTO;
import com.server.blockchainserver.models.user_model.AvatarData;
import com.server.blockchainserver.models.user_model.CICardImage;
import com.server.blockchainserver.platform.data_transfer_object.UserGoldInventoryDTO;

import java.sql.Date;
import java.util.List;
import java.util.Set;

public class UserInfoDTOs {

    private long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private AvatarData avatarData;
    private Date doB;
    private String address;
    private String ciCard;
    private List<CICardImage> ciCardImage;
    private BalanceDTO balance;
    private UserGoldInventoryDTO inventory;
    private Set<GoldTransactionDTO> goldTransactions;

    public UserInfoDTOs() {
    }


    public UserInfoDTOs(long userInfoId, String firstName, String lastName,
                        String phoneNumber, AvatarData avatarData, Date doB, String address, String ciCard,
                        List<CICardImage> ciCardImage, BalanceDTO balance, UserGoldInventoryDTO inventory,
                        Set<GoldTransactionDTO> goldTransactions) {
        this.id = userInfoId;
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
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public Date getDoB() {
        return doB;
    }

    public void setDoB(Date doB) {
        this.doB = doB;
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

    public List<CICardImage> getCiCardImage() {
        return ciCardImage;
    }

    public void setCiCardImage(List<CICardImage> ciCardImage) {
        this.ciCardImage = ciCardImage;
    }

    public BalanceDTO getBalance() {
        return balance;
    }

    public void setBalance(BalanceDTO balance) {
        this.balance = balance;
    }

    public UserGoldInventoryDTO getInventory() {
        return inventory;
    }

    public void setInventory(UserGoldInventoryDTO inventory) {
        this.inventory = inventory;
    }

    public Set<GoldTransactionDTO> getGoldTransactions() {
        return goldTransactions;
    }

    public void setGoldTransactions(Set<GoldTransactionDTO> goldTransactions) {
        this.goldTransactions = goldTransactions;
    }
}
