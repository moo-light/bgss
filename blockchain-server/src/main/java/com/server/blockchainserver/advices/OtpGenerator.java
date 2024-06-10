package com.server.blockchainserver.advices;

import org.apache.commons.lang3.RandomStringUtils;

import static com.server.blockchainserver.advices.Constants.OTP_LENGTH;

public class OtpGenerator {


    private String generateOTP() {
        return RandomStringUtils.randomNumeric(OTP_LENGTH);
    }

    // Store OTP temporarily
    private void storeOTP(String phoneNumber, String OTP) {
        // Implement your storage logic here
    }

    // Retrieve OTP
    private String retrieveOTP(String phoneNumber) {
        // Implement your retrieval logic here
        return phoneNumber;
    }
}
