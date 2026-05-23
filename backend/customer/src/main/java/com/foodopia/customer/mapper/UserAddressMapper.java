package com.foodopia.customer.mapper;

import com.foodopia.customer.dto.UserAddressDto;
import com.foodopia.customer.entity.UserAddress;

public final class UserAddressMapper {

    private UserAddressMapper() {}

    public static UserAddressDto mapToDto(UserAddress address, UserAddressDto dto) {
        dto.setId(address.getId());
        dto.setUserId(address.getUserId());
        dto.setLabel(address.getLabel());
        dto.setDefault(address.isDefault());
        dto.setLine1(address.getLine1());
        dto.setLine2(address.getLine2());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setPostalCode(address.getPostalCode());
        dto.setCountry(address.getCountry());
        dto.setCreatedAt(address.getCreatedAt());
        dto.setUpdatedAt(address.getUpdatedAt());
        return dto;
    }

    public static UserAddress mapToEntity(UserAddressDto dto, UserAddress address) {
        address.setUserId(dto.getUserId());
        address.setLabel(dto.getLabel());
        address.setDefault(dto.isDefault());
        address.setLine1(dto.getLine1());
        address.setLine2(dto.getLine2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());
        return address;
    }
}
