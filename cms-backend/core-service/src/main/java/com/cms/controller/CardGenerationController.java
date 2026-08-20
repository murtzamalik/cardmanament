package com.cms.controller;

import com.cms.dto.request.CardGenerationProcessRequest;
import com.cms.dto.request.CardGenerationRequestByCode;
import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.CardGenerationResultResponse;
import com.cms.dto.response.CardRequestResponse;
import com.cms.service.CardGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/card-generation")
@Tag(name = "Card Generation", description = "Card generation API")
public class CardGenerationController {

    private final CardGenerationService cardGenerationService;

    public CardGenerationController(CardGenerationService cardGenerationService) {
        this.cardGenerationService = cardGenerationService;
    }

    @PostMapping("/request-by-code")
    @Operation(summary = "Get card request by relationship and account")
    public ResponseEntity<ApiResponse<List<CardRequestResponse>>> getRequestByCode(
            @Valid @RequestBody CardGenerationRequestByCode request) {
        return ResponseEntity.ok(ApiResponse.ok(
            cardGenerationService.getCardRequestByCode(request.getRelationshipNum(), request.getAccountNum())));
    }

    @PutMapping("/request/{id}/progress")
    @Operation(summary = "Update card request progress")
    public ResponseEntity<ApiResponse<Void>> updateProgress(@PathVariable Long id, @RequestParam Integer progressFlag) {
        cardGenerationService.updateCardRequestProgress(id, progressFlag);
        return ResponseEntity.ok(ApiResponse.ok("Progress updated", null));
    }

    @PostMapping("/request/{id}/approve-and-generate")
    @Operation(summary = "Approve request and generate card")
    public ResponseEntity<ApiResponse<CardGenerationResultResponse>> approveAndGenerate(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(cardGenerationService.approveAndGenerate(id)));
    }

    @PostMapping("/process")
    @Operation(summary = "Process new card generation")
    public ResponseEntity<ApiResponse<CardGenerationResultResponse>> process(
            @Valid @RequestBody CardGenerationProcessRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(cardGenerationService.processNewCardGeneration(request.getRequestId())));
    }
}
