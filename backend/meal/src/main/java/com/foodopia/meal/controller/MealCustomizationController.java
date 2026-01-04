package com.foodopia.meal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodopia.meal.constants.MealConstants;
import com.foodopia.meal.dto.ErrorResponseDto;
import com.foodopia.meal.dto.MealCustomizationDto;
import com.foodopia.meal.dto.ResponseDto;
import com.foodopia.meal.service.IMealCustomizationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Tag(
        name = "REST APIs for Meal Customizations",
        description = "REST APIs to manage meal customizations"
)
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class MealCustomizationController {

    private static final Logger log = LoggerFactory.getLogger(MealCustomizationController.class);
    private IMealCustomizationService mealCustomizationService;

    @Operation(
            summary = "Fetch Customization REST API",
            description = "REST API to fetch customization details by scheduled meal ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/meal-customizations/{scheduledMealId}")
    public ResponseEntity<MealCustomizationDto> fetchCustomization(@PathVariable String scheduledMealId) {
        log.debug("Received request to fetch meal customization for scheduled meal id: {}", scheduledMealId);
        MealCustomizationDto customizationDto = mealCustomizationService.fetchCustomizationByScheduledMeal(scheduledMealId);
        log.debug("Successfully fetched meal customization for scheduled meal id: {}", scheduledMealId);
        return ResponseEntity.status(HttpStatus.OK).body(customizationDto);
    }

    @Operation(
            summary = "Customize Meal REST API",
            description = "REST API to update meal customization"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(responseCode = "417", description = "Expectation Failed"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PutMapping("/meal-customizations/{scheduledMealId}/customize")
    public ResponseEntity<ResponseDto> customizeMeal(@Valid @RequestBody MealCustomizationDto customizationDto) {
        log.debug("Received request to customize meal with id: {}", customizationDto.getId());
        boolean isUpdated = mealCustomizationService.updateCustomization(customizationDto);
        if (isUpdated) {
            log.debug("Successfully customized meal with id: {}", customizationDto.getId());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(MealConstants.STATUS_200, MealConstants.MESSAGE_200));
        } else {
            log.warn("Failed to customize meal with id: {}", customizationDto.getId());
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(MealConstants.STATUS_417, MealConstants.MESSAGE_417_UPDATE));
        }
    }

}