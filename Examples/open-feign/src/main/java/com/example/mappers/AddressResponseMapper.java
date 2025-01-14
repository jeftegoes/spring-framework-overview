package com.example.mappers;

import com.example.entities.Address;
import com.example.responses.AddressResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AddressResponseMapper {
    @Mapping(source = "estado", target = "state")
    Address toAddress(AddressResponse addressResponse);
}
