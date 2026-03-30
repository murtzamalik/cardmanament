package com.cms.controller;

import com.cms.dto.request.AccountStatusCreateRequest;
import com.cms.dto.request.AccountStatusUpdateRequest;
import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.AccountStatusResponse;
import com.cms.service.AccountStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account-statuses")
@Tag(name = "Account Statuses", description = "Account status reference data API")
public class AccountStatusController {

    private final AccountStatusService accountStatusService;

    public AccountStatusController(AccountStatusService accountStatusService) {
        this.accountStatusService = accountStatusService;
    }

    @PostMapping
    @Operation(summary = "Create account status")
    public ResponseEntity<ApiResponse<AccountStatusResponse>> create(@Valid @RequestBody AccountStatusCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Account status created successfully", accountStatusService.create(request)));
    }

    @GetMapping
    @Operation(summary = "Get all account statuses")
    public ResponseEntity<ApiResponse<List<AccountStatusResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(accountStatusService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account status by code")
    public ResponseEntity<ApiResponse<AccountStatusResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(accountStatusService.getById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update account status")
    public ResponseEntity<ApiResponse<AccountStatusResponse>> update(@PathVariable Long id, @RequestBody AccountStatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Account status updated successfully", accountStatusService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete account status")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        accountStatusService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Account status deleted", null));
    }
}
