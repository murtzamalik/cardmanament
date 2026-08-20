package com.cms.controller;

import com.cms.dto.request.CardLimitActualCreateRequest;
import com.cms.dto.request.CardLimitActualUpdateRequest;
import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.CardLimitActualResponse;
import com.cms.service.CardLimitActualService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/card-limits/actual")
@Tag(name = "Card Limit Actual", description = "Runtime available card limits (same-row update)")
public class CardLimitActualController {

    private final CardLimitActualService service;

    public CardLimitActualController(CardLimitActualService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create actual (available) limit row")
    public ResponseEntity<ApiResponse<CardLimitActualResponse>> create(
            @Valid @RequestBody CardLimitActualCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Card limit actual created", service.create(request)));
    }

    @GetMapping
    @Operation(summary = "List actual limits by PAN, or get one row when channelCode+tranCode provided")
    public ResponseEntity<ApiResponse<?>> find(
            @RequestParam String pan,
            @RequestParam(required = false) String channelCode,
            @RequestParam(required = false) String tranCode) {
        if (channelCode != null && !channelCode.isBlank() && tranCode != null && !tranCode.isBlank()) {
            return ResponseEntity.ok(ApiResponse.ok(service.get(pan, channelCode, tranCode)));
        }
        List<CardLimitActualResponse> list = service.findByPan(pan);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PutMapping
    @Operation(summary = "Update same actual row by PAN+CHANNEL_CODE+TRAN_CODE (decrease only)")
    public ResponseEntity<ApiResponse<CardLimitActualResponse>> update(
            @Valid @RequestBody CardLimitActualUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Card limit actual updated", service.update(request)));
    }
}
