package com.cms.controller;

import com.cms.dto.request.CardTypeCreateRequest;
import com.cms.dto.request.CardTypeUpdateRequest;
import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.CardTypeResponse;
import com.cms.service.CardTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/card-types")
@Tag(name = "Card Types", description = "Card type reference data API")
public class CardTypeController {

    private final CardTypeService cardTypeService;

    public CardTypeController(CardTypeService cardTypeService) {
        this.cardTypeService = cardTypeService;
    }

    @PostMapping
    @Operation(summary = "Create card type")
    public ResponseEntity<ApiResponse<CardTypeResponse>> create(@Valid @RequestBody CardTypeCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Card type created successfully", cardTypeService.create(request)));
    }

    @GetMapping
    @Operation(summary = "Get all card types")
    public ResponseEntity<ApiResponse<List<CardTypeResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(cardTypeService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get card type by code")
    public ResponseEntity<ApiResponse<CardTypeResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(cardTypeService.getById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update card type")
    public ResponseEntity<ApiResponse<CardTypeResponse>> update(@PathVariable Long id, @RequestBody CardTypeUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Card type updated successfully", cardTypeService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete card type")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        cardTypeService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Card type deleted", null));
    }
}
