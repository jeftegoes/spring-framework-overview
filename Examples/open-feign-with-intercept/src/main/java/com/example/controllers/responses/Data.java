package com.example.controllers.responses;

import com.example.entities.Address;
import lombok.AllArgsConstructor;

@lombok.Data
@AllArgsConstructor
public class Data {
    private String origin;
    private Address address;
}
