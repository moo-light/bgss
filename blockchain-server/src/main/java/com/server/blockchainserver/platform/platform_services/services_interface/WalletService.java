package com.server.blockchainserver.platform.platform_services.services_interface;

import com.server.blockchainserver.dto.balance_dto.BalanceDTO;

public interface WalletService {

    BalanceDTO balance(long userInfo);
}
