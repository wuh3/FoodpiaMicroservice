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
import org.springframework.web.bind.annotation.RestController;

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

@Tag(
        name = "CRUD REST APIs for Dishes",
        description = "REST APIs to CREATE, UPDATE, FETCH dishes and manage availability"
)
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class DishController {

    private static final Logger log = LoggerFactory.getLogger(DishController.class);
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
        log.debug("Received request to create dish: {}", dishDto.getName());
        dishService.createDish(dishDto);
        log.debug("Successfully created dish: {}", dishDto.getName());
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
        log.debug("Received request to fetch dish with id: {}", id);
        DishDto dishDto = dishService.fetchDish(id);
        log.debug("Successfully fetched dish with id: {}", id);
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
        log.debug("Received request to fetch all dishes");
        List<DishDto> dishes = dishService.fetchAllDishes();
        log.debug("Successfully fetched {} dishes", dishes.size());
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
        log.debug("Received request to fetch dishes by category: {}", category);
        List<DishDto> dishes = dishService.fetchDishesByCategory(category);
        log.debug("Successfully fetched {} dishes for category: {}", dishes.size(), category);
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
        log.debug("Received request to update dish with id: {}", dishDto.getId());
        boolean isUpdated = dishService.updateDish(dishDto);
        if (isUpdated) {
            log.debug("Successfully updated dish with id: {}", dishDto.getId());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(MealConstants.STATUS_200, MealConstants.MESSAGE_200));
        } else {
            log.warn("Failed to update dish with id: {}", dishDto.getId());
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(MealConstants.STATUS_417, MealConstants.MESSAGE_417_UPDATE));
        }
    }

}