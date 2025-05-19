package com.example.controllers;

import com.example.clients.ZipCodeFeignClient;
import com.example.clients.ZipCodeRestClient;
import com.example.entities.Address;
import com.example.controllers.mappers.AddressResponseMapper;
import com.example.clients.responses.AddressClientResponse;
import com.example.controllers.responses.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/zipcodes")
public class ZipCodeController {
    private final AddressResponseMapper addressResponseMapper;
    private final ZipCodeFeignClient zipCodeFeignClient;
    private final ZipCodeRestClient zipCodeRestClient;

    @Autowired
    public ZipCodeController(
            AddressResponseMapper addressResponseMapper,
            ZipCodeFeignClient zipCodeFeignClient,
            ZipCodeRestClient zipCodeRestClient) {
        this.addressResponseMapper = addressResponseMapper;
        this.zipCodeFeignClient = zipCodeFeignClient;
        this.zipCodeRestClient = zipCodeRestClient;
    }

    @GetMapping("/feign")
    public Data getZipCodeFeingCLient() {
        AddressClientResponse addressClientResponse = this.zipCodeFeignClient.getZipCode("01001000");
        Address address = this.addressResponseMapper.toAddress(addressClientResponse);

        return new Data("feign", address);
    }

    @GetMapping("/rest-client")
    public Data getZipCodeRestCLient() {
        AddressClientResponse addressClientResponse = this.zipCodeRestClient.getZipCode("01001000");
        Address address = this.addressResponseMapper.toAddress(addressClientResponse);

        return new Data("rest-client", address);
    }
}
