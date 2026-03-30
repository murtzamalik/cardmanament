package com.cms.controller;

import com.cms.dto.request.PasswordExpressionCreateRequest;
import com.cms.dto.request.PasswordExpressionUpdateRequest;
import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.PasswordExpressionResponse;
import com.cms.service.PasswordExpressionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/password-expressions")
@Tag(name = "Password Expressions", description = "Password expression (validation rules) API")
public class PasswordExpressionController {

    private final PasswordExpressionService passwordExpressionService;

    public PasswordExpressionController(PasswordExpressionService passwordExpressionService) {
        this.passwordExpressionService = passwordExpressionService;
    }

    @PostMapping
    @Operation(summary = "Create password expression")
    public ResponseEntity<ApiResponse<PasswordExpressionResponse>> create(@Valid @RequestBody PasswordExpressionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Password expression created successfully", passwordExpressionService.create(request)));
    }

    @GetMapping
    @Operation(summary = "Get all password expressions")
    public ResponseEntity<ApiResponse<List<PasswordExpressionResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(passwordExpressionService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get password expression by id")
    public ResponseEntity<ApiResponse<PasswordExpressionResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(passwordExpressionService.getById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update password expression")
    public ResponseEntity<ApiResponse<PasswordExpressionResponse>> update(@PathVariable Long id, @RequestBody PasswordExpressionUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Password expression updated successfully", passwordExpressionService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete password expression")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        passwordExpressionService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Password expression deleted", null));
    }
}
