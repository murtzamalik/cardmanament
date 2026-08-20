package com.cms.controller;

import com.cms.dto.request.NewCardRequestCreate;
import com.cms.dto.request.RejectCardRequestRequest;
import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.CardRequestResponse;
import com.cms.dto.response.CustomerInfoResponse;
import com.cms.dto.response.PageResponse;
import com.cms.service.NewCardRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/card-requests")
@Tag(name = "Card Requests", description = "New card request API")
public class NewCardRequestController {

    private final NewCardRequestService newCardRequestService;

    public NewCardRequestController(NewCardRequestService newCardRequestService) {
        this.newCardRequestService = newCardRequestService;
    }

    @PostMapping
    @Operation(summary = "Create new card request")
    public ResponseEntity<ApiResponse<CardRequestResponse>> create(
            @Valid @RequestBody NewCardRequestCreate request,
            Authentication auth) {
        String createdBy = auth != null && auth.getName() != null ? auth.getName() : "system";
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Card request created", newCardRequestService.create(request, createdBy)));
    }

    @PostMapping("/reject")
    @Operation(summary = "Reject card request")
    public ResponseEntity<ApiResponse<Void>> reject(@Valid @RequestBody RejectCardRequestRequest request) {
        newCardRequestService.reject(request.getRequestId());
        return ResponseEntity.ok(ApiResponse.ok("Request rejected", null));
    }

    @GetMapping("/checker")
    @Operation(summary = "Get list for checker")
    public ResponseEntity<ApiResponse<List<CardRequestResponse>>> getCheckerList() {
        return ResponseEntity.ok(ApiResponse.ok(newCardRequestService.getCheckerList()));
    }

    @GetMapping("/maker")
    @Operation(summary = "Get list for maker")
    public ResponseEntity<ApiResponse<List<CardRequestResponse>>> getMakerList() {
        return ResponseEntity.ok(ApiResponse.ok(newCardRequestService.getMakerList()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update card request")
    public ResponseEntity<ApiResponse<CardRequestResponse>> update(
            @PathVariable Long id, @RequestBody NewCardRequestCreate request) {
        return ResponseEntity.ok(ApiResponse.ok("Request updated", newCardRequestService.update(id, request)));
    }

    @GetMapping("/customer-info")
    @Operation(summary = "Get customer info by relationship number")
    public ResponseEntity<ApiResponse<CustomerInfoResponse>> getCustomerInfo(@RequestParam String relationshipNum) {
        return ResponseEntity.ok(ApiResponse.ok(newCardRequestService.getCustomerInfo(relationshipNum)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search card requests")
    public ResponseEntity<ApiResponse<PageResponse<CardRequestResponse>>> search(
            @RequestParam(required = false) String relationshipNum,
            @RequestParam(required = false) String branchCode,
            @RequestParam(required = false) Integer isProcessed,
            @RequestParam(required = false) String requestTypeId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return ResponseEntity.ok(ApiResponse.ok(
            newCardRequestService.search(relationshipNum, branchCode, isProcessed, requestTypeId, page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get card request by ID")
    public ResponseEntity<ApiResponse<CardRequestResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(newCardRequestService.getById(id)));
    }
}
