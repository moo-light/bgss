package com.server.blockchainserver.exeptions;

public class CheckEnumException extends IllegalArgumentException {
    public CheckEnumException(String message, CheckEnumException e) {
        super(message);
    }
}
