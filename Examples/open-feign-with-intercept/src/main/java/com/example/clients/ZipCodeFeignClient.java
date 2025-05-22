package com.example.clients;

import com.example.clients.responses.AddressClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        url = "${zipcode.client.url}",
        value = "ZipCodeClient"
)
public interface ZipCodeFeignClient {
    @GetMapping("{zipCode}/json/")
    AddressClientResponse getZipCode(@PathVariable("zipCode") String zipCode);
}
