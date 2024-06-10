package com.server.blockchainserver.platform.repositories;

import com.server.blockchainserver.platform.entity.TimeSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeSeriesRepository extends JpaRepository<TimeSeries, Long> {
}
