package com.server.blockchainserver.controllers.shopping_controller;

import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.exeptions.TypeGoldException;
import com.server.blockchainserver.models.shopping_model.TypeGold;
import com.server.blockchainserver.payload.request.TypeGoldRequest;
import com.server.blockchainserver.payload.response.StatisticProductResponse;
import com.server.blockchainserver.services.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class TypeGoldController {

    @Autowired
    Services typeGoldService;

    @GetMapping("/get-all-type-gold")
    public ResponseEntity<Response> getAllTypeGol(){
        List<TypeGold> typeGold = typeGoldService.getAllTypeGold();
        if(typeGold.isEmpty()){
            Response response = new Response(HttpStatus.OK, "The list type of gold is empty.", typeGold);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Response response = new Response(HttpStatus.OK, "Get all type of gold success.", typeGold);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-type-gold-id/{typeGoldId}")
    public ResponseEntity<Response> getTypeGoldById(@PathVariable Long typeGoldId){
        TypeGold typeGold = typeGoldService.getTypeGoldById(typeGoldId);

        Response response = new Response(HttpStatus.OK, "Get type of gold success.", typeGold);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/create-type-gold")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> createTypeGold(@RequestBody TypeGoldRequest request){
        try{
            TypeGold typeGold = typeGoldService.createTypeGold(request);

            Response response = new Response(HttpStatus.OK, "Create type of gold success.", typeGold);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(TypeGoldException e){
            Response response = new Response(HttpStatus.BAD_REQUEST,  e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update-type-gold/{typeGoldId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updateTypeGold(@PathVariable Long typeGoldId, @RequestBody TypeGoldRequest request){
        try{
            TypeGold typeGold = typeGoldService.updateTypeGold(typeGoldId, request);

            Response response = new Response(HttpStatus.OK, "Update type of gold success.", typeGold);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(TypeGoldException e){
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-type-gold-id/{typeGoldId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteTypeGold(@PathVariable Long typeGoldId){
        boolean check = typeGoldService.deleteTypeGold(typeGoldId);

        Response response = new Response(HttpStatus.OK, "Delete type of gold success.", check);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/statistic-product-by-type-gold")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> statisticProductByTypeGold(@RequestParam Long typeGoldId){
        try{
            StatisticProductResponse statistic = typeGoldService.filterByTypeGold(typeGoldId);

            Response response = new Response(HttpStatus.OK, "Show all product and statistic by type gold success.", statistic);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e){
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}