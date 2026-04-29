package com.cms;

import com.cms.dal.entity.AuditLog;
import com.cms.dal.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public AuditAspect(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    @Around("execution(* com.cms.controller.*.*(..))")
    public Object auditApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String loginId = "anonymous";
        String httpMethod = "";
        String apiPath = "";
        String ipAddress = "";
        String requestBody = "";

        try {
            // Get logged in user from JWT
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null) {
                loginId = auth.getName();
            }

            // Get HTTP request details
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                httpMethod = request.getMethod();
                apiPath = request.getRequestURI();
                ipAddress = getClientIp(request);
            }

            // Get request body (method arguments)
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                try {
                    requestBody = objectMapper.writeValueAsString(args[0]);
                    // Mask sensitive fields
                    if (requestBody.contains("password")) {
                        requestBody = requestBody.replaceAll("\"password\":\"[^\"]*\"", "\"password\":\"***\"");
                    }
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            log.warn("Audit pre-processing failed", e);
        }

        String action = joinPoint.getSignature().toShortString();

        try {
            // Execute the actual API method
            Object result = joinPoint.proceed();

            // Save SUCCESS audit log
            saveLog(loginId, action, httpMethod, apiPath, requestBody, "SUCCESS", null, ipAddress);
            return result;

        } catch (Throwable ex) {
            // Save FAILED audit log
            saveLog(loginId, action, httpMethod, apiPath, requestBody, "FAILED",
                    ex.getMessage() != null ? ex.getMessage().substring(0, Math.min(ex.getMessage().length(), 999)) : "Unknown error",
                    ipAddress);
            throw ex;
        }
    }

    private void saveLog(String loginId, String action, String httpMethod,
                         String apiPath, String requestBody, String status,
                         String errorMessage, String ipAddress) {
        try {
            AuditLog log = new AuditLog();
            log.setLoginId(loginId);
            log.setAction(action);
            log.setHttpMethod(httpMethod);
            log.setApiPath(apiPath);
            log.setRequestBody(requestBody);
            log.setResponseStatus(status);
            log.setErrorMessage(errorMessage);
            log.setIpAddress(ipAddress);
            log.setCreatedAt(LocalDateTime.now());
            auditLogRepository.save(log);
        } catch (Exception e) {
            // Never let audit logging break the actual API
            LoggerFactory.getLogger(AuditAspect.class).error("Failed to save audit log", e);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        // X-Forwarded-For can have multiple IPs — take first one
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
        return ip;
    }
}
