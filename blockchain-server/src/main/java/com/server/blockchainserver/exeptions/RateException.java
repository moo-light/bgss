package com.server.blockchainserver.exeptions;

public class RateException extends RuntimeException {
    public RateException(String message) {
        super(message);
    }
}
