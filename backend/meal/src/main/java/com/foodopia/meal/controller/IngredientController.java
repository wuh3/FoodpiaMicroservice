package com.foodopia.meal.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodopia.meal.constants.MealConstants;
import com.foodopia.meal.dto.ErrorResponseDto;
import com.foodopia.meal.dto.IngredientDto;
import com.foodopia.meal.dto.ResponseDto;
import com.foodopia.meal.service.IIngredientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Tag(
        name = "CRUD REST APIs for Ingredients",
        description = "REST APIs to CREATE, FETCH ingredients and UPDATE prices"
)
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class IngredientController {

    private static final Logger log = LoggerFactory.getLogger(IngredientController.class);
    private IIngredientService ingredientService;

    @Operation(
            summary = "Create Ingredient REST API",
            description = "REST API to create new ingredient"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "HTTP Status CREATED"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/ingredients")
    public ResponseEntity<ResponseDto> createIngredient(@Valid @RequestBody IngredientDto ingredientDto) {
        log.debug("Received request to create ingredient: {}", ingredientDto.getName());
        ingredientService.createIngredient(ingredientDto);
        log.debug("Successfully created ingredient: {}", ingredientDto.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(MealConstants.STATUS_201, MealConstants.MESSAGE_201));
    }

    @Operation(
            summary = "Fetch Ingredient REST API",
            description = "REST API to fetch ingredient details by ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/ingredients/{id}")
    public ResponseEntity<IngredientDto> fetchIngredient(@PathVariable String id) {
        log.debug("Received request to fetch ingredient with id: {}", id);
        IngredientDto ingredientDto = ingredientService.fetchIngredient(id);
        log.debug("Successfully fetched ingredient with id: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(ingredientDto);
    }

    @Operation(
            summary = "Fetch Ingredient by Name REST API",
            description = "REST API to fetch ingredient details by unique name"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/ingredients/by-name")
    public ResponseEntity<IngredientDto> fetchIngredientByName(@RequestParam String name) {
        log.debug("Received request to fetch ingredient with name: {}", name);
        IngredientDto ingredientDto = ingredientService.fetchIngredientByName(name);
        log.debug("Successfully fetched ingredient with name: {}", name);
        return ResponseEntity.status(HttpStatus.OK).body(ingredientDto);
    }

    @Operation(
            summary = "Fetch All Ingredients REST API",
            description = "REST API to fetch all ingredients"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/ingredients")
    public ResponseEntity<List<IngredientDto>> fetchAllIngredients() {
        log.debug("Received request to fetch all ingredients");
        List<IngredientDto> ingredients = ingredientService.fetchAllIngredients();
        log.debug("Successfully fetched {} ingredients", ingredients.size());
        return ResponseEntity.status(HttpStatus.OK).body(ingredients);
    }

    @Operation(
            summary = "Update Ingredient Price REST API",
            description = "REST API to update ingredient unit price"
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
    @PutMapping("/ingredients/{id}/price")
    public ResponseEntity<ResponseDto> updateIngredientPrice(
            @PathVariable String id,
            @RequestParam double newPrice) {
        log.debug("Received request to update ingredient price for id: {} to: {}", id, newPrice);
        boolean isUpdated = ingredientService.updateIngredientPrice(id, newPrice);
        if (isUpdated) {
            log.debug("Successfully updated ingredient price for id: {}", id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(MealConstants.STATUS_200, MealConstants.MESSAGE_200));
        } else {
            log.warn("Failed to update ingredient price for id: {}", id);
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(MealConstants.STATUS_417, MealConstants.MESSAGE_417_UPDATE));
        }
    }

}