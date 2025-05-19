package com.example.clients;

import com.example.clients.responses.AddressClientResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ZipCodeRestClient {
    @Value("${zipcode.client.url}")
    private String baseUrl;

    public AddressClientResponse getZipCode(String zipCode) {
        RestClient restClient = RestClient.builder().baseUrl(baseUrl).build();

        ResponseEntity<AddressClientResponse> response = restClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("ws/01001000/json/").build())
                .retrieve()
                .toEntity(AddressClientResponse.class);

        return response.getBody();
    }
}