package com.server.blockchainserver.dto.user_dto;

import java.util.Date;

public class UserInfoData {


    private String firstName;
    private String lastName;
    private Date dob;
    private String address;
    private String ciCard;


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

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
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
}
