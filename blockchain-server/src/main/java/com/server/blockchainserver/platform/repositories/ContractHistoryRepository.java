package com.server.blockchainserver.platform.repositories;

import com.server.blockchainserver.platform.entity.Contract;
import com.server.blockchainserver.platform.entity.ContractHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractHistoryRepository extends JpaRepository<ContractHistory, Long> {

    List<ContractHistory> findByContract(Contract contract);
}
