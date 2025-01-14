package com.example.clients;

import com.example.responses.AddressResponse;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(
        url = "${zipcode.client.url}",
        value = "ZipCodeClient"
)
public interface ZipCodeClient {
    @GetMapping("{zipCode}/json/")
    AddressResponse getZipCode(@PathVariable("zipCode") String zipCode);
}
