package com.foodopia.meal.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "CRUD REST APIs for Ingredients",
        description = "REST APIs to CREATE, FETCH ingredients and UPDATE prices"
)
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class IngredientController {

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
        ingredientService.createIngredient(ingredientDto);
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
        IngredientDto ingredientDto = ingredientService.fetchIngredient(id);
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
        List<IngredientDto> ingredients = ingredientService.fetchAllIngredients();
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
        boolean isUpdated = ingredientService.updateIngredientPrice(id, newPrice);
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