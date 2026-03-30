package com.cms.controller;

import com.cms.dto.request.PolicyCreateRequest;
import com.cms.dto.request.PolicyUpdateRequest;
import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.PolicyResponse;
import com.cms.service.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@Tag(name = "Policies", description = "User/security policy management API")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping
    @Operation(summary = "Create policy")
    public ResponseEntity<ApiResponse<PolicyResponse>> create(@Valid @RequestBody PolicyCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Policy created successfully", policyService.create(request)));
    }

    @GetMapping
    @Operation(summary = "Get all policies")
    public ResponseEntity<ApiResponse<List<PolicyResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(policyService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get policy by id")
    public ResponseEntity<ApiResponse<PolicyResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(policyService.getById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update policy")
    public ResponseEntity<ApiResponse<PolicyResponse>> update(@PathVariable Long id, @RequestBody PolicyUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Policy updated successfully", policyService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete policy")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        policyService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Policy deleted", null));
    }
}
