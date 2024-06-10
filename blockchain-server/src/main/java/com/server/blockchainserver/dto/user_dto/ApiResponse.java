package com.server.blockchainserver.dto.user_dto;

import java.util.List;

public class ApiResponse {
    private int errorCode;
    private String errorMessage;
    private List<PersonInfo> data;

    // Getters và Setters
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<PersonInfo> getData() {
        return data;
    }

    public void setData(List<PersonInfo> data) {
        this.data = data;
    }
}