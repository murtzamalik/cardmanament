package com.cms.controller;

import com.cms.dto.request.*;
import com.cms.dto.response.*;
import com.cms.service.CardService;
import com.cms.service.CardExportFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Card API. PCI DSS: never log or expose PAN, CVV, or full card numbers in responses or logs.
 */
@RestController
@RequestMapping("/api/cards")
@Tag(name = "Cards", description = "Card operations API")
public class CardController {

    private final CardService cardService;
    private final CardExportFileService cardExportFileService;

    public CardController(CardService cardService, CardExportFileService cardExportFileService) {
        this.cardService = cardService;
        this.cardExportFileService = cardExportFileService;
    }

    @GetMapping
    @Operation(summary = "Get all cards")
    public ResponseEntity<ApiResponse<List<CardResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(cardService.getAllCards()));
    }

    /**
     * Search cards. Supports both ID parameters (branchId, cardStatusId) and code parameters (branchCode, cardStatusCode).
     * When both are provided for the same dimension, ID takes precedence.
     */
    @PostMapping("/search")
    @Operation(summary = "Search cards")
    public ResponseEntity<ApiResponse<PageResponse<CardResponse>>> search(@RequestBody CardSearchRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(cardService.searchCards(request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get card by ID")
    public ResponseEntity<ApiResponse<CardResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(cardService.getCardById(id)));
    }

    @GetMapping("/{id}/export-file")
    @Operation(summary = "Download export file for card if one was generated")
    public ResponseEntity<InputStreamResource> downloadExportFile(@PathVariable Long id) throws IOException {
        CardResponse card = cardService.getCardById(id);
        if (card.getExportFilePath() == null || card.getExportFilePath().isBlank()) {
            return ResponseEntity.notFound().build();
        }
        Optional<InputStream> opt = cardExportFileService.openExportFileForDownload(card.getExportFilePath());
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        String filename = cardExportFileService.getExportFileName(card.getExportFilePath());
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .body(new InputStreamResource(opt.get()));
    }

    @GetMapping("/dropdowns")
    @Operation(summary = "Get dropdown data for cards")
    public ResponseEntity<ApiResponse<CardDropdownsResponse>> getDropdowns() {
        return ResponseEntity.ok(ApiResponse.ok(cardService.getDropdowns()));
    }

    @GetMapping("/available-accounts")
    @Operation(summary = "Get available accounts for linking")
    public ResponseEntity<ApiResponse<List<AccountOptionResponse>>> getAvailableAccounts(
            @RequestParam(required = false) String relationshipNum) {
        return ResponseEntity.ok(ApiResponse.ok(cardService.getAvailableAccounts(relationshipNum)));
    }

    @PostMapping("/link-account")
    @Operation(summary = "Link account to card")
    public ResponseEntity<ApiResponse<Void>> linkAccount(@Valid @RequestBody LinkCardAccountRequest request) {
        cardService.linkCardAccount(request);
        return ResponseEntity.ok(ApiResponse.ok("Account linked", null));
    }

    @PostMapping("/{id}/link-account")
    @Operation(summary = "Link account to card by card ID")
    public ResponseEntity<ApiResponse<Void>> linkAccountByCardId(@PathVariable Long id, @Valid @RequestBody LinkCardAccountByCardIdRequest request) {
        cardService.linkCardAccountByCardId(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Account linked", null));
    }

    @PostMapping("/delink-account")
    @Operation(summary = "Delink account from card")
    public ResponseEntity<ApiResponse<Void>> delinkAccount(@RequestParam String pan, @RequestParam String accountNum) {
        cardService.delinkCardAccount(pan, accountNum);
        return ResponseEntity.ok(ApiResponse.ok("Account delinked", null));
    }

    @PostMapping("/{id}/delink-account")
    @Operation(summary = "Delink account from card by card ID")
    public ResponseEntity<ApiResponse<Void>> delinkAccountByCardId(@PathVariable Long id, @RequestParam String accountNum) {
        cardService.delinkCardAccountByCardId(id, accountNum);
        return ResponseEntity.ok(ApiResponse.ok("Account delinked", null));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update card")
    public ResponseEntity<ApiResponse<CardResponse>> update(@PathVariable Long id, @RequestBody CardUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Card updated", cardService.updateCard(id, request)));
    }

    /**
     * Set limit profile on card. Prefer limitProfileId when set; limitProfile is the profile code (legacy).
     */
    @PutMapping("/{id}/limit-profile")
    @Operation(summary = "Set limit profile on card")
    public ResponseEntity<ApiResponse<Void>> linkLimitProfile(
            @PathVariable Long id,
            @RequestParam(required = false) Long limitProfileId,
            @RequestParam(required = false) String limitProfile) {
        cardService.linkLimitProfile(id, limitProfileId, limitProfile);
        return ResponseEntity.ok(ApiResponse.ok("Limit profile set", null));
    }

    @GetMapping("/{id}/linked-account")
    @Operation(summary = "Get linked accounts by card ID")
    public ResponseEntity<ApiResponse<List<CardAccountLinkResponse>>> getLinkedAccountsByCardId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(cardService.getLinkedAccountsByCardId(id)));
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "Close card")
    public ResponseEntity<ApiResponse<Void>> closeCard(@PathVariable Long id) {
        cardService.closeCard(id);
        return ResponseEntity.ok(ApiResponse.ok("Card closed", null));
    }

    @GetMapping("/customer-by-relation")
    @Operation(summary = "Get customer by relationship number")
    public ResponseEntity<ApiResponse<CustomerInfoResponse>> getCustomerByRelation(
            @RequestParam String rel, @RequestParam(required = false) String linked, @RequestParam(required = false) Long cardId) {
        return ResponseEntity.ok(ApiResponse.ok(cardService.getCustomerByRelation(rel, linked, cardId)));
    }

    @PostMapping("/expiry-search")
    @Operation(summary = " Search cards by expiry date range")
    public ResponseEntity<ApiResponse<List<CardResponse>>> searchByExpiryDate (
        @RequestBody ExpirySearchRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(cardService.searchByExpiryDate(request)));
    }

    @PostMapping("/{id}/change-card-type")
    @Operation(summary = "Create card request for card type change")
    public ResponseEntity<ApiResponse<Long>> changeCardType(
            @PathVariable Long id,
            @RequestBody ChangeCardTypeRequest request) {
        Long requestId = cardService.changeCardType(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Card type change request created", requestId));
    }

    @PostMapping("/{id}/replacement-request")
    @Operation(summary = "Mark card inactive and create replacement card request")
    public ResponseEntity<ApiResponse<Long>> replacementRequest(@PathVariable Long id) {
        Long newRequestId = cardService.replacementRequest(id);
        return ResponseEntity.ok(ApiResponse.ok("Replacement request created", newRequestId));
    }

    @PostMapping("/export-ready")
    @Operation(summary = "Get cards ready for export by card type")
    public ResponseEntity<ApiResponse<List<CardResponse>>> getExportReadyCards(
            @RequestBody ExportReadyRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(cardService.getExportReadyCards(request)));
    }

    @PostMapping("/bulk-export")
    @Operation(summary = "Bulk export selected cards to bureau file")
    public ResponseEntity<ApiResponse<String>> bulkExport(
            @RequestBody BulkExportRequest request) {
        String exportPath = cardService.bulkExport(request);
        return ResponseEntity.ok(ApiResponse.ok("Export file generated", exportPath));
    }

    @PostMapping("/bulk-renew")
    @Operation(summary = "Bulk renew selected cards by extending expiry date")
    public ResponseEntity<ApiResponse<List<Long>>> bulkRenew(
            @RequestBody BulkRenewRequest request) {
        List<Long> renewedIds = cardService.bulkRenew(request);
        return ResponseEntity.ok(ApiResponse.ok("Cards renewed successfully", renewedIds));
    }



}
