package com.foodopia.customer.mapper;

import com.foodopia.customer.dto.CreditCardDto;
import com.foodopia.customer.dto.PaymentMethodDto;
import com.foodopia.customer.entity.CreditCard;
import com.foodopia.customer.entity.PaymentMethod;

public final class PaymentMethodMapper {

    private PaymentMethodMapper() {}

    public static PaymentMethodDto mapToDto(PaymentMethod method, PaymentMethodDto dto) {
        dto.setId(method.getId());
        dto.setUserId(method.getUserId());
        dto.setType(method.getType());
        dto.setDefault(method.isDefault());
        dto.setCreditsBalance(method.getCreditsBalance());
        if (method.getCreditCard() != null) {
            dto.setCreditCard(CreditCardMapper.mapToDto(method.getCreditCard(), new CreditCardDto()));
        }
        dto.setCreatedAt(method.getCreatedAt());
        dto.setUpdatedAt(method.getUpdatedAt());
        return dto;
    }

    public static PaymentMethod mapToEntity(PaymentMethodDto dto, PaymentMethod method) {
        method.setUserId(dto.getUserId());
        method.setType(dto.getType());
        method.setDefault(dto.isDefault());
        method.setCreditsBalance(dto.getCreditsBalance());
        if (dto.getCreditCard() != null) {
            CreditCard card = method.getCreditCard() != null ? method.getCreditCard() : new CreditCard();
            method.setCreditCard(CreditCardMapper.mapToEntity(dto.getCreditCard(), card));
        }
        return method;
    }
}
