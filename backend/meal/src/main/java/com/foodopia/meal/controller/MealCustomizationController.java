package com.foodopia.meal.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "REST APIs for Meal Customizations",
        description = "REST APIs to manage meal customizations"
)
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class MealCustomizationController {

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
        MealCustomizationDto customizationDto = mealCustomizationService.fetchCustomizationByScheduledMeal(scheduledMealId);
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
        boolean isUpdated = mealCustomizationService.updateCustomization(customizationDto);
        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(MealConstants.STATUS_200, MealConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(MealConstants.STATUS_417, MealConstants.MESSAGE_417_UPDATE));
        }
    }

}