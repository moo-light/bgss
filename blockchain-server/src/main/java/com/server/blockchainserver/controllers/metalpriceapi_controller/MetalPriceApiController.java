package com.server.blockchainserver.controllers.metalpriceapi_controller;

import com.server.blockchainserver.models.enums.ELiveRateType;
import com.server.blockchainserver.platform.entity.LiveRates;
import com.server.blockchainserver.platform.platform_services.services_interface.MetalPriceApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class MetalPriceApiController {

    @Autowired
    MetalPriceApiService metalPriceApiService;


    @GetMapping("/gold/timeframe")
    public ResponseEntity<Map<String, Double>> getTimeframeGoldPrice() {
        Map<String, Double> goldPrices = metalPriceApiService.fetchTimeframeGoldPrice();
        if (goldPrices.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(goldPrices);
        }
    }

    @GetMapping("/gold/live-rate")
    public ResponseEntity<List<LiveRates>> getLiveRates(@RequestParam(defaultValue = "_1H") ELiveRateType type) {
        List<LiveRates> goldPrices = metalPriceApiService.getLiveRates(type);
        if (goldPrices.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(goldPrices);
        }
    }
}
