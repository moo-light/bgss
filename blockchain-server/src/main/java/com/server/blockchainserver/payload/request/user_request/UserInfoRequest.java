package com.server.blockchainserver.payload.request.user_request;


import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;

import java.sql.Date;

public class UserInfoRequest {

    private String firstName;

    private String lastName;

    private String phoneNumber;

    //    private byte[] avatar;
    private Date doB;

    private String address;

    private String ciCard;

//    private byte[] ciCardImage;

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

    public Date getDoB() {
        return doB;
    }

    public void setDoB(Date doB) {
        this.doB = doB;
    }
}
