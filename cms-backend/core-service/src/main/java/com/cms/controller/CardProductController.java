package com.cms.controller;

import com.cms.dto.request.CardProductCreateRequest;
import com.cms.dto.request.CardProductUpdateRequest;
import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.CardProductResponse;
import com.cms.service.CardProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Card product reference data API")
public class CardProductController {

    private final CardProductService cardProductService;

    public CardProductController(CardProductService cardProductService) {
        this.cardProductService = cardProductService;
    }

    @PostMapping
    @Operation(summary = "Create product")
    public ResponseEntity<ApiResponse<CardProductResponse>> create(@Valid @RequestBody CardProductCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Product created successfully", cardProductService.create(request)));
    }

    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<ApiResponse<List<CardProductResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(cardProductService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by code")
    public ResponseEntity<ApiResponse<CardProductResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(cardProductService.getById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product")
    public ResponseEntity<ApiResponse<CardProductResponse>> update(@PathVariable Long id, @Valid @RequestBody CardProductUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Product updated successfully", cardProductService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        cardProductService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Product deleted", null));
    }
}
