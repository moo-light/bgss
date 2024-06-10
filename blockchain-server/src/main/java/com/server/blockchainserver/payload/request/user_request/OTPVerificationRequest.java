package com.server.blockchainserver.payload.request.user_request;

public class OTPVerificationRequest {

    private String phoneNumber;
    private String OTP;

    public OTPVerificationRequest() {
    }

    public OTPVerificationRequest(String phoneNumber, String OTP) {
        this.phoneNumber = phoneNumber;
        this.OTP = OTP;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOTP() {
        return OTP;
    }

    public void setOTP(String OTP) {
        this.OTP = OTP;
    }
}
