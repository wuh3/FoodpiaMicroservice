package com.foodopia.meal.controller;

import com.foodopia.meal.constants.MealConstants;
import com.foodopia.meal.dto.DishDto;
import com.foodopia.meal.dto.ErrorResponseDto;
import com.foodopia.meal.dto.ResponseDto;
import com.foodopia.meal.service.IDishService;
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
        name = "CRUD REST APIs for Dishes",
        description = "REST APIs to CREATE, UPDATE, FETCH dishes and manage availability"
)
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class DishController {

    private IDishService dishService;

    @Operation(
            summary = "Create Dish REST API",
            description = "REST API to create new dish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "HTTP Status CREATED"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/dishes")
    public ResponseEntity<ResponseDto> createDish(@Valid @RequestBody DishDto dishDto) {
        dishService.createDish(dishDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(MealConstants.STATUS_201, MealConstants.MESSAGE_201));
    }

    @Operation(
            summary = "Fetch Dish REST API",
            description = "REST API to fetch dish details by ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/dishes/{id}")
    public ResponseEntity<DishDto> fetchDish(@PathVariable String id) {
        DishDto dishDto = dishService.fetchDish(id);
        return ResponseEntity.status(HttpStatus.OK).body(dishDto);
    }

    @Operation(
            summary = "Fetch All Dishes REST API",
            description = "REST API to fetch all dishes"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/dishes")
    public ResponseEntity<List<DishDto>> fetchAllDishes() {
        List<DishDto> dishes = dishService.fetchAllDishes();
        return ResponseEntity.status(HttpStatus.OK).body(dishes);
    }

    @Operation(
            summary = "Fetch Dishes by Category REST API",
            description = "REST API to fetch dishes by category"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/dishes/category/{category}")
    public ResponseEntity<List<DishDto>> fetchDishesByCategory(@PathVariable String category) {
        List<DishDto> dishes = dishService.fetchDishesByCategory(category);
        return ResponseEntity.status(HttpStatus.OK).body(dishes);
    }

    @Operation(
            summary = "Update Dish REST API",
            description = "REST API to update dish details"
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
    @PutMapping("/dishes/{id}")
    public ResponseEntity<ResponseDto> updateDish(@Valid @RequestBody DishDto dishDto) {
        boolean isUpdated = dishService.updateDish(dishDto);
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