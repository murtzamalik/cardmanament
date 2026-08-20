package com.cms.controller;

import com.cms.dto.request.BranchCreateRequest;
import com.cms.dto.request.BranchUpdateRequest;
import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.BranchResponse;
import com.cms.service.BranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@Tag(name = "Branches", description = "Branch management API")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @PostMapping
    @Operation(summary = "Create branch")
    public ResponseEntity<ApiResponse<BranchResponse>> create(@Valid @RequestBody BranchCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Branch created successfully", branchService.create(request)));
    }

    @GetMapping
    @Operation(summary = "Get all branches")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(branchService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get branch by id")
    public ResponseEntity<ApiResponse<BranchResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(branchService.getById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update branch")
    public ResponseEntity<ApiResponse<BranchResponse>> update(@PathVariable Long id, @RequestBody BranchUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Branch updated successfully", branchService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete branch")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        branchService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Branch deleted", null));
    }
}
