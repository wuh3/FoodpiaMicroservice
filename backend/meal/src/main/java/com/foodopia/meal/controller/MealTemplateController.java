package com.foodopia.meal.controller;

import com.foodopia.meal.constants.MealConstants;
import com.foodopia.meal.dto.ErrorResponseDto;
import com.foodopia.meal.dto.MealTemplateDto;
import com.foodopia.meal.dto.ResponseDto;
import com.foodopia.meal.service.IMealTemplateService;
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
        name = "CRUD REST APIs for Meal Templates",
        description = "REST APIs to CREATE, FETCH and VALIDATE meal templates"
)
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class MealTemplateController {

    private IMealTemplateService mealTemplateService;

    @Operation(
            summary = "Create Meal Template REST API",
            description = "REST API to create new meal template"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "HTTP Status CREATED"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/meal-templates")
    public ResponseEntity<ResponseDto> createMealTemplate(@Valid @RequestBody MealTemplateDto mealTemplateDto) {
        mealTemplateService.createMealTemplate(mealTemplateDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(MealConstants.STATUS_201, MealConstants.MESSAGE_201));
    }

    @Operation(
            summary = "Fetch Meal Template REST API",
            description = "REST API to fetch meal template details by ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/meal-templates/{id}")
    public ResponseEntity<MealTemplateDto> fetchMealTemplate(@PathVariable String id) {
        MealTemplateDto mealTemplateDto = mealTemplateService.fetchMealTemplate(id);
        return ResponseEntity.status(HttpStatus.OK).body(mealTemplateDto);
    }

    @Operation(
            summary = "Fetch All Meal Templates REST API",
            description = "REST API to fetch all meal templates"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/meal-templates")
    public ResponseEntity<List<MealTemplateDto>> fetchAllMealTemplates() {
        List<MealTemplateDto> templates = mealTemplateService.fetchAllMealTemplates();
        return ResponseEntity.status(HttpStatus.OK).body(templates);
    }

    @Operation(
            summary = "Validate Dish Selection REST API",
            description = "REST API to validate if dish selection matches template requirements"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/meal-templates/{id}/validate")
    public ResponseEntity<ResponseDto> validateDishSelection(
            @PathVariable String id,
            @RequestBody List<String> dishIds) {
        boolean isValid = mealTemplateService.validateDishSelection(id, dishIds);
        if (isValid) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(MealConstants.STATUS_200, "Dish selection is valid"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(MealConstants.STATUS_417, "Dish selection does not match template"));
        }
    }

}