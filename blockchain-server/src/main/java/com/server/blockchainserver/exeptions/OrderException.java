package com.server.blockchainserver.exeptions;

public class OrderException extends RuntimeException {
    public OrderException(String message) {
        super(message);
    }
}
