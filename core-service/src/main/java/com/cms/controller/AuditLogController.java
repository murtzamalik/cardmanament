package com.cms.controller;

import com.cms.dal.entity.AuditLog;
import com.cms.dal.repository.AuditLogRepository;
import com.cms.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit-logs")
@Tag(name = "Audit Logs", description = "Audit trail API")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    public AuditLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    @Operation(summary = "Get all audit logs paginated")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String loginId) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AuditLog> result = loginId != null && !loginId.isBlank()
                ? auditLogRepository.findByLoginIdOrderByCreatedAtDesc(loginId, pageable)
                : auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
