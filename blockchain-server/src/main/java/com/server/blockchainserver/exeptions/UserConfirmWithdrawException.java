package com.server.blockchainserver.exeptions;

public class UserConfirmWithdrawException extends RuntimeException{
    public UserConfirmWithdrawException(String message){
        super(message);
    }
}
