package com.server.blockchainserver.platform.platform_services.wallet_services;

import com.server.blockchainserver.dto.balance_dto.BalanceDTO;
import com.server.blockchainserver.platform.platform_services.services_interface.WalletService;
import com.server.blockchainserver.server.PaymentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl implements WalletService {


    @Autowired
    PaymentHandler paymentHandler;

    @Override
    public BalanceDTO balance(long userInfo) {
        return paymentHandler.balance(userInfo);
    }
}
