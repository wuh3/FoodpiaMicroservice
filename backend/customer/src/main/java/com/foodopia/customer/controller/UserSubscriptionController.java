package com.foodopia.customer.controller;

import com.foodopia.customer.constants.CustomerConstants;
import com.foodopia.customer.dto.ResponseDto;
import com.foodopia.customer.service.IUserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserSubscriptionController {

    private final IUserSubscriptionService subscriptionService;

    @PostMapping("/{subscriptionId}/pause")
    public ResponseEntity<ResponseDto> pauseSubscription(@PathVariable String subscriptionId) {
        subscriptionService.pauseSubscription(subscriptionId);
        return ResponseEntity.ok(new ResponseDto(CustomerConstants.STATUS_200, CustomerConstants.MESSAGE_200));
    }

    @PostMapping("/{subscriptionId}/resume")
    public ResponseEntity<ResponseDto> resumeSubscription(@PathVariable String subscriptionId) {
        subscriptionService.resumeSubscription(subscriptionId);
        return ResponseEntity.ok(new ResponseDto(CustomerConstants.STATUS_200, CustomerConstants.MESSAGE_200));
    }

    @PostMapping("/{subscriptionId}/cancel")
    public ResponseEntity<ResponseDto> cancelSubscription(@PathVariable String subscriptionId) {
        subscriptionService.cancelSubscription(subscriptionId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDto(CustomerConstants.STATUS_200, CustomerConstants.MESSAGE_200));
    }
}
