package com.cms.controller;

import com.cms.dto.request.LimitProfileCreateRequest;
import com.cms.dto.request.LimitProfileUpdateRequest;
import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.LimitProfileResponse;
import com.cms.service.LimitProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/limit-profiles")
@Tag(name = "Limit Profiles", description = "Limit profile management API")
public class LimitProfileController {

    private final LimitProfileService limitProfileService;

    public LimitProfileController(LimitProfileService limitProfileService) {
        this.limitProfileService = limitProfileService;
    }

    @PostMapping
    @Operation(summary = "Create limit profile")
    public ResponseEntity<ApiResponse<LimitProfileResponse>> create(@Valid @RequestBody LimitProfileCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Limit profile created", limitProfileService.create(request)));
    }

    @GetMapping
    @Operation(summary = "Get all limit profiles")
    public ResponseEntity<ApiResponse<List<LimitProfileResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(limitProfileService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get limit profile by code")
    public ResponseEntity<ApiResponse<LimitProfileResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(limitProfileService.getById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update limit profile")
    public ResponseEntity<ApiResponse<LimitProfileResponse>> update(@PathVariable Long id, @RequestBody LimitProfileUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Limit profile updated", limitProfileService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete limit profile")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        limitProfileService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Limit profile deleted", null));
    }
}
