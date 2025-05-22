package com.example.clients;

import com.example.clients.responses.AddressClientResponse;
import com.example.interceptors.RestClientLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ZipCodeRestClient {
    private final RestClientLoggingInterceptor interceptor;
    @Value("${zipcode.client.url}")
    private String baseUrl;

    public ZipCodeRestClient(RestClientLoggingInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    public AddressClientResponse getZipCode(String zipCode) {
        RestClient restClient = RestClient.builder().requestInterceptor(interceptor).baseUrl(baseUrl).build();

        ResponseEntity<AddressClientResponse> response = restClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("ws/01001000/json/").build())
                .header("Accept", "application/json")
                .retrieve()
                .toEntity(AddressClientResponse.class);

        return response.getBody();
    }
}