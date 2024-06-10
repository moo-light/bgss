package com.server.blockchainserver.controllers;

import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.models.TransferUnitGold;
import com.server.blockchainserver.payload.request.TransferUnitGoldRequest;
import com.server.blockchainserver.services.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class TransferUnitGoldController {

    @Autowired
    Services transferUnitGoldService;

    @GetMapping("/get-all-information-transfer")
    public ResponseEntity<Response> getAllInformationTransfer(){
        List<TransferUnitGold> transferUnitGolds = transferUnitGoldService.getAllInformationTransfer();

        Response response = new Response(HttpStatus.OK, "Show all information transfer unit of gold success.", transferUnitGolds);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/create-information-transfer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> createInformationTransfer(@RequestBody TransferUnitGoldRequest request){
        TransferUnitGold transferUnitGold = transferUnitGoldService.createInformationTransfer(request);

        Response response = new Response(HttpStatus.OK, "Create a information transfer unit of gold success.", transferUnitGold);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update-information-transfer/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updateInformationTransfer(@PathVariable Long id, @RequestBody TransferUnitGoldRequest request){
        TransferUnitGold transferUnitGold = transferUnitGoldService.updateInformationTransfer(id, request);

        Response response = new Response(HttpStatus.OK, "Update a information transfer unit of gold success.", transferUnitGold);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping ("/delete-information-transfer/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteInformationTransfer(@PathVariable Long id){
        boolean check = transferUnitGoldService.deleteInformationTransfer(id);

        Response response = new Response(HttpStatus.OK, "Delete a information transfer unit of gold success.", check);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
