package com.example.controllers.mappers;

import com.example.entities.Address;
import com.example.clients.responses.AddressClientResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressResponseMapper {
    @Mapping(source = "estado", target = "state")
    Address toAddress(AddressClientResponse addressClientResponse);
}
