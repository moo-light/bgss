package com.server.blockchainserver.platform.platform_services.services_interface;

import com.server.blockchainserver.models.enums.ELiveRateType;
import com.server.blockchainserver.platform.entity.LiveRates;
import com.server.blockchainserver.platform.payload.response.LiveRatesResponse;

import java.util.List;
import java.util.Map;

public interface MetalPriceApiService {

    LiveRatesResponse getLiveGoldPrice();

    String getHistoricalData(String startDate, String endDate, String base);

    Map<String, Double> fetchTimeframeGoldPrice();


    List<LiveRates> getLiveRates(ELiveRateType type);
}
