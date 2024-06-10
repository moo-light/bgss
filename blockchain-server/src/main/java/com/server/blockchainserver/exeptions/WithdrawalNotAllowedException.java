package com.server.blockchainserver.exeptions;

public class WithdrawalNotAllowedException extends RuntimeException {
    public WithdrawalNotAllowedException(String message) {
        super(message);
    }
}
