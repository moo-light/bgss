package com.server.blockchainserver.exeptions;

public class CartException extends RuntimeException {
    public CartException(String message) {
        super(message);
    }
}
