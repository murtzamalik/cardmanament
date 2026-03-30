package com.cms.controller;

import com.cms.dto.request.AccountTypeCreateRequest;
import com.cms.dto.request.AccountTypeUpdateRequest;
import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.AccountTypeResponse;
import com.cms.service.AccountTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account-types")
@Tag(name = "Account Types", description = "Account type reference data API")
public class AccountTypeController {

    private final AccountTypeService accountTypeService;

    public AccountTypeController(AccountTypeService accountTypeService) {
        this.accountTypeService = accountTypeService;
    }

    @PostMapping
    @Operation(summary = "Create account type")
    public ResponseEntity<ApiResponse<AccountTypeResponse>> create(@Valid @RequestBody AccountTypeCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Account type created successfully", accountTypeService.create(request)));
    }

    @GetMapping
    @Operation(summary = "Get all account types")
    public ResponseEntity<ApiResponse<List<AccountTypeResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(accountTypeService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account type by code")
    public ResponseEntity<ApiResponse<AccountTypeResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(accountTypeService.getById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update account type")
    public ResponseEntity<ApiResponse<AccountTypeResponse>> update(@PathVariable Long id, @RequestBody AccountTypeUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Account type updated successfully", accountTypeService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete account type")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        accountTypeService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Account type deleted", null));
    }
}
