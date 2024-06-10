package com.server.blockchainserver.exeptions;

public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }
}
