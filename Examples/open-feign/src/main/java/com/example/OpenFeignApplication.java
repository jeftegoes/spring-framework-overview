package com.example;

import com.example.clients.ZipCodeClient;
import com.example.entities.Address;
import com.example.mappers.AddressResponseMapper;
import com.example.responses.AddressResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@EnableFeignClients
@SpringBootApplication
public class OpenFeignApplication {
    private final ZipCodeClient zipCodeClient;
    private final AddressResponseMapper addressResponseMapper;

    public OpenFeignApplication(ZipCodeClient zipCodeClient, AddressResponseMapper addressResponseMapper) {
        this.zipCodeClient = zipCodeClient;
        this.addressResponseMapper = addressResponseMapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(OpenFeignApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return runner -> {
            AddressResponse addressResponse = this.zipCodeClient.getZipCode("01001000");

            Address address = this.addressResponseMapper.toAddress(addressResponse);

            System.out.println(address);
        };
    }
}
