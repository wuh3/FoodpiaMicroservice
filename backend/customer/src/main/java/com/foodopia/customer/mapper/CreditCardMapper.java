package com.foodopia.customer.mapper;

import com.foodopia.customer.dto.CreditCardDto;
import com.foodopia.customer.entity.CreditCard;

public final class CreditCardMapper {

    private CreditCardMapper() {}

    public static CreditCardDto mapToDto(CreditCard card, CreditCardDto dto) {
        if (card == null) {
            return null;
        }
        dto.setLastFour(card.getLastFour());
        dto.setBrand(card.getBrand());
        dto.setExpiryMonth(card.getExpiryMonth());
        dto.setExpiryYear(card.getExpiryYear());
        dto.setCardholderName(card.getCardholderName());
        return dto;
    }

    public static CreditCard mapToEntity(CreditCardDto dto, CreditCard card) {
        if (dto == null) {
            return null;
        }
        card.setLastFour(dto.getLastFour());
        card.setBrand(dto.getBrand());
        card.setExpiryMonth(dto.getExpiryMonth());
        card.setExpiryYear(dto.getExpiryYear());
        card.setCardholderName(dto.getCardholderName());
        return card;
    }
}
