package com.cms.controller;

import com.cms.dto.request.ResponseCodeCreateRequest;
import com.cms.dto.request.ResponseCodeUpdateRequest;
import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.ResponseCodeResponse;
import com.cms.service.ResponseCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/response-codes")
@Tag(name = "Response Codes", description = "Response code reference data API")
public class ResponseCodeController {

    private final ResponseCodeService responseCodeService;

    public ResponseCodeController(ResponseCodeService responseCodeService) {
        this.responseCodeService = responseCodeService;
    }

    @PostMapping
    @Operation(summary = "Create response code")
    public ResponseEntity<ApiResponse<ResponseCodeResponse>> create(@Valid @RequestBody ResponseCodeCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Response code created successfully", responseCodeService.create(request)));
    }

    @GetMapping
    @Operation(summary = "Get all response codes")
    public ResponseEntity<ApiResponse<List<ResponseCodeResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(responseCodeService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get response code by code")
    public ResponseEntity<ApiResponse<ResponseCodeResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(responseCodeService.getById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update response code")
    public ResponseEntity<ApiResponse<ResponseCodeResponse>> update(@PathVariable Long id, @RequestBody ResponseCodeUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Response code updated successfully", responseCodeService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete response code")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        responseCodeService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Response code deleted", null));
    }
}
