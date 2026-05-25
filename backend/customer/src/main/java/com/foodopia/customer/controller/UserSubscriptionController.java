package com.foodopia.customer.controller;

import com.foodopia.customer.constants.CustomerConstants;
import com.foodopia.customer.dto.ResponseDto;
import com.foodopia.customer.dto.UserSubscriptionDto;
import com.foodopia.customer.service.IUserSubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class UserSubscriptionController {

    private static final Logger log = LoggerFactory.getLogger(UserSubscriptionController.class);

    private final IUserSubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<ResponseDto> createSubscription(@Valid @RequestBody UserSubscriptionDto subscriptionDto) {
        log.debug("Received request to create subscription for userId: {}", subscriptionDto.getUserId());
        subscriptionService.createSubscription(subscriptionDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(CustomerConstants.STATUS_201, CustomerConstants.MESSAGE_201));
    }

    @GetMapping("/{subscriptionId}")
    public ResponseEntity<UserSubscriptionDto> fetchSubscription(@PathVariable String subscriptionId) {
        log.debug("Received request to fetch subscription with id: {}", subscriptionId);
        return ResponseEntity.ok(subscriptionService.fetchSubscription(subscriptionId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserSubscriptionDto>> fetchSubscriptionsByUserId(@PathVariable String userId) {
        log.debug("Received request to fetch subscriptions for userId: {}", userId);
        return ResponseEntity.ok(subscriptionService.fetchSubscriptionsByUserId(userId));
    }

    @PutMapping("/{subscriptionId}")
    public ResponseEntity<ResponseDto> updateSubscription(
            @PathVariable String subscriptionId,
            @Valid @RequestBody UserSubscriptionDto subscriptionDto) {
        log.debug("Received request to update subscription with id: {}", subscriptionId);
        subscriptionDto.setId(subscriptionId);
        boolean updated = subscriptionService.updateSubscription(subscriptionDto);
        if (updated) {
            return ResponseEntity.ok(new ResponseDto(CustomerConstants.STATUS_200, CustomerConstants.MESSAGE_200));
        }
        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(new ResponseDto(CustomerConstants.STATUS_417, CustomerConstants.MESSAGE_417_UPDATE));
    }

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
        return ResponseEntity.ok(new ResponseDto(CustomerConstants.STATUS_200, CustomerConstants.MESSAGE_200));
    }
}
