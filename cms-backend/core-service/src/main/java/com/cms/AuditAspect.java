package com.cms;

import com.cms.dal.entity.AuditLog;
import com.cms.dal.repository.AuditLogRepository;
import com.cms.service.CardDataEncryptionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    private static final Set<String> PASSWORD_KEYS = Set.of(
            "password", "newpassword", "oldpassword", "confirmpassword"
    );

    private static final Set<String> PAN_KEYS = Set.of(
            "pan", "primarypan", "trimpan"
    );

    /** Same treatment as CARD table: AES-GCM encrypt at rest in audit. */
    private static final Set<String> ENCRYPT_KEYS = Set.of(
            "cvv", "cvv1", "cvv2", "icvv",
            "track1", "track2", "track1data", "track2data",
            "pin"
    );

    private static final Pattern PAN_DIGITS = Pattern.compile("\\b(\\d{13,19})\\b");

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;
    private final CardDataEncryptionService encryptionService;

    public AuditAspect(AuditLogRepository auditLogRepository,
                       ObjectMapper objectMapper,
                       CardDataEncryptionService encryptionService) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
        this.encryptionService = encryptionService;
    }

    @Around("execution(* com.cms.controller.*.*(..))")
    public Object auditApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String loginId = "anonymous";
        String httpMethod = "";
        String apiPath = "";
        String ipAddress = "";
        String requestBody = "";

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null) {
                loginId = auth.getName();
            }

            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                httpMethod = request.getMethod();
                apiPath = request.getRequestURI();
                ipAddress = getClientIp(request);
            }

            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                try {
                    requestBody = redactForAudit(objectMapper.writeValueAsString(args[0]));
                } catch (Exception ignored) {
                    requestBody = "";
                }
            }
        } catch (Exception e) {
            log.warn("Audit pre-processing failed", e);
        }

        String action = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();
            saveLog(loginId, action, httpMethod, apiPath, requestBody, "SUCCESS", null, ipAddress);
            return result;
        } catch (Throwable ex) {
            String err = ex.getMessage() != null
                    ? ex.getMessage().substring(0, Math.min(ex.getMessage().length(), 999))
                    : "Unknown error";
            saveLog(loginId, action, httpMethod, apiPath, requestBody, "FAILED",
                    redactFreeText(err), ipAddress);
            throw ex;
        }
    }

    /**
     * Audit storage rules:
     * - password → ***
     * - PAN → first6 + **** + last4 (same as API mask)
     * - CVV / track / PIN → AES-GCM encrypt (same as CARD table)
     */
    String redactForAudit(String json) {
        if (json == null || json.isBlank()) return json;
        try {
            JsonNode root = objectMapper.readTree(json);
            redactNode(root);
            return objectMapper.writeValueAsString(root);
        } catch (Exception parseFailed) {
            return redactFreeText(json);
        }
    }

    private void redactNode(JsonNode node) {
        if (node == null || !node.isObject()) {
            if (node != null && node.isArray()) {
                for (JsonNode child : node) {
                    redactNode(child);
                }
            }
            return;
        }
        ObjectNode obj = (ObjectNode) node;
        Iterator<Map.Entry<String, JsonNode>> fields = obj.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String keyNorm = entry.getKey().toLowerCase(Locale.ROOT).replace("_", "");
            JsonNode value = entry.getValue();
            if (PASSWORD_KEYS.contains(keyNorm)) {
                obj.put(entry.getKey(), "***");
            } else if (PAN_KEYS.contains(keyNorm) && value != null && value.isTextual()) {
                obj.put(entry.getKey(), maskPanFirst6Last4(value.asText()));
            } else if (ENCRYPT_KEYS.contains(keyNorm) && value != null && value.isTextual()) {
                obj.put(entry.getKey(), encryptOrStar(value.asText()));
            } else if (value != null && (value.isObject() || value.isArray())) {
                redactNode(value);
            }
        }
    }

    private String encryptOrStar(String plaintext) {
        if (plaintext == null || plaintext.isBlank()) return "***";
        // Already looks like our AES payload / masked — leave as-is
        if (plaintext.contains("*") || plaintext.length() > 40) {
            try {
                encryptionService.decrypt(plaintext);
                return plaintext; // already encrypted
            } catch (RuntimeException ignore) {
                // not encrypted — fall through
            }
        }
        try {
            String enc = encryptionService.encrypt(plaintext);
            return enc != null ? enc : "***";
        } catch (RuntimeException e) {
            log.warn("Audit encrypt failed; storing placeholder");
            return "***";
        }
    }

    /** PAN mask: first 6 + middle stars + last 4 (matches CardDataEncryptionService / CardMapper). */
    static String maskPanFirst6Last4(String pan) {
        if (pan == null || pan.isBlank()) return "****";
        if (pan.contains("*")) return pan;
        String digits = pan.replaceAll("\\D", "");
        String src = digits.length() >= 10 ? digits : pan;
        if (src.length() <= 10) return "****";
        return src.substring(0, 6)
                + "*".repeat(src.length() - 10)
                + src.substring(src.length() - 4);
    }

    private String redactFreeText(String text) {
        if (text == null || text.isBlank()) return text;
        String out = text;
        out = out.replaceAll("(?i)\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"***\"");
        Matcher m = PAN_DIGITS.matcher(out);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, Matcher.quoteReplacement(maskPanFirst6Last4(m.group(1))));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private void saveLog(String loginId, String action, String httpMethod,
                         String apiPath, String requestBody, String status,
                         String errorMessage, String ipAddress) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setLoginId(loginId);
            auditLog.setAction(action);
            auditLog.setHttpMethod(httpMethod);
            auditLog.setApiPath(apiPath);
            auditLog.setRequestBody(requestBody);
            auditLog.setResponseStatus(status);
            auditLog.setErrorMessage(errorMessage);
            auditLog.setIpAddress(ipAddress);
            auditLog.setCreatedAt(LocalDateTime.now());
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log", e);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
        return ip;
    }
}
