package com.foodopia.meal.controller;

import com.foodopia.meal.constants.MealConstants;
import com.foodopia.meal.dto.ErrorResponseDto;
import com.foodopia.meal.dto.MealPlanTypeDto;
import com.foodopia.meal.dto.ResponseDto;
import com.foodopia.meal.service.IMealPlanTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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

@Tag(
        name = "CRUD REST APIs for Meal Plan Types",
        description = "REST APIs to CREATE, FETCH and UPDATE meal plan types"
)
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class MealPlanTypeController {

    private static final Logger log = LoggerFactory.getLogger(MealPlanTypeController.class);
    private IMealPlanTypeService mealPlanTypeService;

    @Operation(
            summary = "Create Meal Plan Type REST API",
            description = "REST API to create a new meal plan type"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "HTTP Status CREATED"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/meal-plan-types")
    public ResponseEntity<ResponseDto> createMealPlanType(@Valid @RequestBody MealPlanTypeDto mealPlanTypeDto) {
        log.debug("Received request to create meal plan type: {}", mealPlanTypeDto.getPlanCode());
        mealPlanTypeService.createMealPlanType(mealPlanTypeDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(MealConstants.STATUS_201, MealConstants.MESSAGE_201));
    }

    @Operation(
            summary = "Fetch Meal Plan Type REST API",
            description = "REST API to fetch meal plan type details by planCode"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/meal-plan-types/{planCode}")
    public ResponseEntity<MealPlanTypeDto> fetchMealPlanType(@PathVariable String planCode) {
        log.debug("Received request to fetch meal plan type with planCode: {}", planCode);
        return ResponseEntity.ok(mealPlanTypeService.fetchMealPlanType(planCode));
    }

    @Operation(
            summary = "Fetch All Active Meal Plan Types REST API",
            description = "REST API to fetch all active meal plan types"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/meal-plan-types")
    public ResponseEntity<List<MealPlanTypeDto>> fetchAllActiveMealPlanTypes() {
        log.debug("Received request to fetch all active meal plan types");
        return ResponseEntity.ok(mealPlanTypeService.fetchAllActiveMealPlanTypes());
    }

    @Operation(
            summary = "Update Meal Plan Type REST API",
            description = "REST API to update meal plan type or level config"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PutMapping("/meal-plan-types/{planCode}")
    public ResponseEntity<ResponseDto> updateMealPlanType(
            @PathVariable String planCode,
            @Valid @RequestBody MealPlanTypeDto mealPlanTypeDto) {
        log.debug("Received request to update meal plan type with planCode: {}", planCode);
        boolean updated = mealPlanTypeService.updateMealPlanType(planCode, mealPlanTypeDto);
        if (updated) {
            return ResponseEntity.ok(new ResponseDto(MealConstants.STATUS_200, MealConstants.MESSAGE_200));
        }
        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(new ResponseDto(MealConstants.STATUS_417, "Failed to update meal plan type"));
    }
}
