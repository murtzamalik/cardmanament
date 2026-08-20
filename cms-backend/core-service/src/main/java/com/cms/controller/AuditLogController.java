package com.cms.controller;

import com.cms.dal.entity.AuditLog;
import com.cms.dal.repository.AuditLogRepository;
import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<ApiResponse<PageResponse<AuditLog>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String loginId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String method) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var result = auditLogRepository.findAll(buildSpec(loginId, status, method), pageable);

        PageResponse<AuditLog> pr = new PageResponse<>();
        pr.setContent(result.getContent());
        pr.setPage(result.getNumber());
        pr.setSize(result.getSize());
        pr.setTotalElements(result.getTotalElements());
        pr.setTotalPages(result.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(pr));
    }

    private static Specification<AuditLog> buildSpec(String loginId, String status, String method) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (loginId != null && !loginId.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("loginId")), loginId.trim().toLowerCase()));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("responseStatus")), status.trim().toUpperCase()));
            }
            if (method != null && !method.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("httpMethod")), method.trim().toUpperCase()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
