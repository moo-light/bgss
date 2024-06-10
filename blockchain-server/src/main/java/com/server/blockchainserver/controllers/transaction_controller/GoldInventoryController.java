package com.server.blockchainserver.controllers.transaction_controller;

import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.platform.entity.GoldInventory;
import com.server.blockchainserver.platform.entity.enums.GoldUnit;
import com.server.blockchainserver.platform.platform_services.services_interface.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class GoldInventoryController {

    @Autowired
    InventoryService inventoryService;


    /**
     * Handles the addition of gold to the inventory.
     * This endpoint allows users to add a specified quantity of gold, in a specified unit, to the inventory.
     *
     * @param quantityInOz The quantity of gold to be added to the inventory, expressed in ounces.
     * @param goldUnit The unit of the gold to be added. This is an enum value that specifies the unit of measurement.
     * @return ResponseEntity<Response> Returns a ResponseEntity object that contains a Response object.
     *         The Response object includes the status code, a message indicating the action performed,
     *         and the updated GoldInventory object if the operation is successful.
     *         In case of failure, it returns an internal server error status with an error message.
     */
    @PutMapping("/add-gold-to-inventory")
    public ResponseEntity<Response> addGoldToInventory(@RequestParam BigDecimal quantityInOz, GoldUnit goldUnit) {
        try {
            GoldInventory goldInventory = inventoryService.addGoldToInventory(quantityInOz, goldUnit);
            Response response = new Response(HttpStatus.OK, "Add gold to inventory", goldInventory);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Retrieves the current state of the gold inventory.
     * This endpoint provides the total quantity of gold available in the inventory.
     * It does not require any parameters and returns a detailed view of the gold inventory,
     * including the total amount and units of gold present.
     *
     * @return ResponseEntity<Response> Returns a ResponseEntity object that contains a Response object.
     *         The Response object includes the status code, a message indicating the action performed,
     *         and the current state of the GoldInventory object if the operation is successful.
     *         In case of failure, it returns an internal server error status with an error message.
     */
    @GetMapping("/gold-inventory")
    public ResponseEntity<Response> goldInventory() {
        try {
            Response response = new Response(HttpStatus.OK, "Add gold to inventory", inventoryService.goldInventory());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
