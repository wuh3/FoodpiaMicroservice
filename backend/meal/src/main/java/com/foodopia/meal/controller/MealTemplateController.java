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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

@Tag(
        name = "CRUD REST APIs for Meal Templates",
        description = "REST APIs to CREATE, FETCH and VALIDATE meal templates"
)
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class MealTemplateController {

    private static final Logger log = LoggerFactory.getLogger(MealTemplateController.class);
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
        log.debug("Received request to create meal template: {}", mealTemplateDto.getName());
        mealTemplateService.createMealTemplate(mealTemplateDto);
        log.debug("Successfully created meal template: {}", mealTemplateDto.getName());
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
        log.debug("Received request to fetch meal template with id: {}", id);
        MealTemplateDto mealTemplateDto = mealTemplateService.fetchMealTemplate(id);
        log.debug("Successfully fetched meal template with id: {}", id);
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
        log.debug("Received request to fetch all meal templates");
        List<MealTemplateDto> templates = mealTemplateService.fetchAllMealTemplates();
        log.debug("Successfully fetched {} meal templates", templates.size());
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
        log.debug("Received request to validate dish selection for template id: {} with {} dishes", id, dishIds.size());
        boolean isValid = mealTemplateService.validateDishSelection(id, dishIds);
        if (isValid) {
            log.debug("Dish selection validation passed for template id: {}", id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(MealConstants.STATUS_200, "Dish selection is valid"));
        } else {
            log.debug("Dish selection validation failed for template id: {}", id);
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(MealConstants.STATUS_417, "Dish selection does not match template"));
        }
    }

}