package com.server.blockchainserver.platform.repositories;

import com.server.blockchainserver.platform.entity.LiveRates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveRatesRepository extends JpaRepository<LiveRates, Long> {

    @Query(value = "SELECT COALESCE(lr.id, 0) AS id,base,rates,second_timestamp as timestamp\r\n" + //
            "FROM (\r\n" + //
            "    SELECT generate_series(\r\n" + //
            "        (SELECT MAX(timestamp) - Cast(:intervalTime as Interval) FROM live_rates)  , \r\n" + //
            "        (SELECT Max(timestamp) FROM live_rates),  \r\n" + //
            "        Cast(:interval as Interval)\r\n" + //
            "    ) AS second_timestamp\r\n" + //
            ") AS seconds\r\n" + //
            "Left JOIN live_rates lr ON lr.timestamp >= second_timestamp - interval '5 second' \r\n" + //
            "                        AND lr.timestamp < second_timestamp + interval '5 second'\r\n" + //
            "ORDER BY timestamp DESC\r\n" + //
            "LIMIT 1000;\r\n", nativeQuery = true)
        // Note '5 seconds' is plural
    List<LiveRates> findFirst1000rateWithInterval(@Param("interval") String interval, @Param("intervalTime") String intervalTime);

    // Hiện tại tui chỉ dùng đc Cast(? as Interval) nên nếu có cách hay hơn thì
    // chỉnh giùm tui
    @Query(value = "SELECT * FROM live_rates WHERE timestamp >= CURRENT_TIMESTAMP - CAST(:interval AS INTERVAL)", nativeQuery = true)
    List<LiveRates> testQuery(@Param("interval") String interval);
}
