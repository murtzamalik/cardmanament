package com.cms.controller;

import com.cms.dto.request.CardLimitCustomizedCreateRequest;
import com.cms.dto.request.CardLimitCustomizedUpdateRequest;
import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.CardLimitCustomizedResponse;
import com.cms.service.CardLimitCustomizedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/card-limits/customized")
@Tag(name = "Card Limit Customized", description = "Personalized / exception card limit ceilings")
public class CardLimitCustomizedController {

    private final CardLimitCustomizedService service;

    public CardLimitCustomizedController(CardLimitCustomizedService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create customized limit for a card/tran code")
    public ResponseEntity<ApiResponse<CardLimitCustomizedResponse>> create(
            @Valid @RequestBody CardLimitCustomizedCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Customized limit created", service.create(request)));
    }

    @GetMapping
    @Operation(summary = "List customized limits by PAN")
    public ResponseEntity<ApiResponse<List<CardLimitCustomizedResponse>>> findByPan(
            @RequestParam String pan) {
        return ResponseEntity.ok(ApiResponse.ok(service.findByPan(pan)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customized limit by id")
    public ResponseEntity<ApiResponse<CardLimitCustomizedResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customized limit")
    public ResponseEntity<ApiResponse<CardLimitCustomizedResponse>> update(
            @PathVariable Long id,
            @RequestBody CardLimitCustomizedUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Customized limit updated", service.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate customized limit (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok("Customized limit deactivated", null));
    }
}
