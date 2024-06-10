package com.server.blockchainserver.platform.platform_services.metal_api_service;

import com.server.blockchainserver.models.enums.ELiveRateType;
import com.server.blockchainserver.platform.entity.LiveRates;
import com.server.blockchainserver.platform.payload.response.LiveRatesResponse;
import com.server.blockchainserver.platform.platform_services.services_interface.MetalPriceApiService;
import com.server.blockchainserver.platform.server.MetalPriceApiServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MetalPriceApiServiceImpl implements MetalPriceApiService {

    @Autowired
    private MetalPriceApiServer server;

    @Override
    public LiveRatesResponse getLiveGoldPrice() {
        return server.fetchLiveGoldPrice();
    }

    @Override
    public String getHistoricalData(String startDate, String endDate, String base) {
        return server.fetchHistoricalGoldPrice(startDate, endDate, base);
    }

    @Override
    public Map<String, Double> fetchTimeframeGoldPrice() {
        return server.fetchTimeframeGoldPrice();
    }

    @Override
    public List<LiveRates> getLiveRates(ELiveRateType type) {
        return server.getLiveRates(type);
    }
}
