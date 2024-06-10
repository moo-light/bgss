package com.server.blockchainserver.exeptions;

public class VerifyOTPOrderException extends RuntimeException{
    public VerifyOTPOrderException(String message){
        super(message);
    }
}
