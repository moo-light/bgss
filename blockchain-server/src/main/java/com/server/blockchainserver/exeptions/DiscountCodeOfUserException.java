package com.server.blockchainserver.exeptions;

public class DiscountCodeOfUserException extends RuntimeException {
    public DiscountCodeOfUserException(String message) {
        super(message);
    }
}
