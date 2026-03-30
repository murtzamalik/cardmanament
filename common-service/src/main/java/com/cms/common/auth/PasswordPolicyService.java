package com.cms.common.auth;

import com.cms.dal.entity.UsmPolicy;
import com.cms.dal.entity.UsmPwdHistory;
import com.cms.dal.entity.UsmUser;
import com.cms.dal.repository.UsmPolicyRepository;
import com.cms.dal.repository.UsmPwdHistoryRepository;
import com.cms.dal.repository.UsmUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * BLR-Common-1 to BLR-Common-4: Password retry lock, duration, history, time expression.
 */
@Service
public class PasswordPolicyService {

    private static final Logger log = LoggerFactory.getLogger(PasswordPolicyService.class);
    private static final int DEFAULT_PWD_RETRY_COUNT = 6;
    private static final int DEFAULT_PWD_HISTORY_COUNT = 4;

    private final UsmUserRepository usmUserRepository;
    private final UsmPolicyRepository usmPolicyRepository;
    private final UsmPwdHistoryRepository usmPwdHistoryRepository;

    @Value("${app.default-app-id:CMS}")
    private String defaultAppId;

    public PasswordPolicyService(UsmUserRepository usmUserRepository,
                                 UsmPolicyRepository usmPolicyRepository,
                                 UsmPwdHistoryRepository usmPwdHistoryRepository) {
        this.usmUserRepository = usmUserRepository;
        this.usmPolicyRepository = usmPolicyRepository;
        this.usmPwdHistoryRepository = usmPwdHistoryRepository;
    }

    /**
     * BLR-Common-1: If retry >= policy PWD_RETRY_COUNT (default 6), set user IS_ACTIVE=0.
     */
    @Transactional
    public boolean checkPasswordRetryCount(String loginId, String appId) {
        String effectiveAppId = appId != null ? appId : defaultAppId;
        Optional<UsmUser> userOpt = usmUserRepository.findByLoginIdAndAppId(loginId, effectiveAppId);
        if (userOpt.isEmpty()) return false;
        UsmUser user = userOpt.get();
        BigDecimal retryAttempt = user.getPwdRetryCount() != null ? user.getPwdRetryCount() : BigDecimal.ZERO;
        int policyRetry = DEFAULT_PWD_RETRY_COUNT;
        if (user.getPolicyId() != null) {
            Optional<UsmPolicy> policyOpt = usmPolicyRepository.findByPolicyId(user.getPolicyId());
            if (policyOpt.isPresent() && policyOpt.get().getPwdRetryCount() != null)
                policyRetry = policyOpt.get().getPwdRetryCount().intValue();
            if (policyRetry == 0) policyRetry = DEFAULT_PWD_RETRY_COUNT;
        }
        if (retryAttempt.intValue() >= policyRetry) {
            user.setIsActive(BigDecimal.ZERO);
            user.setUpdatedBy(loginId);
            user.setUpdatedOn(LocalDateTime.now());
            usmUserRepository.save(user);
            log.info("User {} set to inactive due to password retry limit", loginId);
            return false;
        }
        return true;
    }

    /**
     * BLR-Common-2: If TotalDays > Policy.PWD_EXPIRY_PERIOD (default 90), password invalid.
     */
    public boolean checkPasswordDuration(String loginId, String appId, LocalDateTime pwdUpdatedOn) {
        if (pwdUpdatedOn == null) return true;
        String effectiveAppId = appId != null ? appId : defaultAppId;
        Optional<UsmUser> userOpt = usmUserRepository.findByLoginIdAndAppId(loginId, effectiveAppId);
        if (userOpt.isEmpty()) return false;
        UsmUser user = userOpt.get();
        int expiryDays = 90;
        if (user.getPolicyId() != null) {
            Optional<UsmPolicy> policyOpt = usmPolicyRepository.findByPolicyId(user.getPolicyId());
            if (policyOpt.isPresent() && policyOpt.get().getPwdExpiryPeriod() != null)
                expiryDays = policyOpt.get().getPwdExpiryPeriod().intValue();
        }
        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(pwdUpdatedOn, LocalDateTime.now());
        return totalDays <= expiryDays;
    }

    /**
     * BLR-Common-3: New password must not match last N (policy PWD_HISTORY_COUNT, default 4).
     */
    public boolean checkValidPassword(String loginId, String newPasswordPlain) {
        if (newPasswordPlain == null) return false;
        Optional<UsmUser> userOpt = usmUserRepository.findByLoginIdAndAppId(loginId, defaultAppId);
        if (userOpt.isEmpty()) return true;
        UsmUser user = userOpt.get();
        int historyCount = DEFAULT_PWD_HISTORY_COUNT;
        if (user.getPolicyId() != null) {
            Optional<UsmPolicy> policyOpt = usmPolicyRepository.findByPolicyId(user.getPolicyId());
            if (policyOpt.isPresent() && policyOpt.get().getPwdHistoryCount() != null)
                historyCount = policyOpt.get().getPwdHistoryCount().intValue();
        }
        List<UsmPwdHistory> history = usmPwdHistoryRepository.findByLoginIdOrderByCreatedOnDesc(loginId, PageRequest.of(0, historyCount));
        for (UsmPwdHistory h : history) {
            if (newPasswordPlain.equals(h.getPassword())) return false;
        }
        return true;
    }

    /**
     * BLR-Common-4: Time expression day-of-week comma-separated; current day value "y" = allowed.
     */
    public boolean checkTimeExpression(String timeExpression) {
        if (timeExpression == null || timeExpression.isBlank()) return true;
        String[] days = timeExpression.split(",");
        int dayOfWeek = LocalDateTime.now().getDayOfWeek().getValue(); // 1=Monday .. 7=Sunday
        int index = dayOfWeek - 1;
        if (index < 0 || index >= days.length) return false;
        return "y".equalsIgnoreCase(days[index].trim());
    }

    @Transactional
    public void resetRetryCount(String loginId, String appId) {
        String effectiveAppId = appId != null ? appId : defaultAppId;
        usmUserRepository.findByLoginIdAndAppId(loginId, effectiveAppId).ifPresent(user -> {
            user.setPwdRetryCount(BigDecimal.ZERO);
            user.setUpdatedOn(LocalDateTime.now());
            user.setUpdatedBy(loginId);
            usmUserRepository.save(user);
        });
    }

    @Transactional
    public void incrementPasswordRetryCount(String loginId, String appId) {
        String effectiveAppId = appId != null ? appId : defaultAppId;
        usmUserRepository.findByLoginIdAndAppId(loginId, effectiveAppId).ifPresent(user -> {
            BigDecimal retry = user.getPwdRetryCount() != null ? user.getPwdRetryCount() : BigDecimal.ZERO;
            user.setPwdRetryCount(retry.add(BigDecimal.ONE));
            user.setUpdatedOn(LocalDateTime.now());
            user.setUpdatedBy(loginId);
            usmUserRepository.save(user);
        });
    }

    @Transactional
    public boolean insertPasswordHistory(String loginId, String password) {
        try {
            UsmPwdHistory h = new UsmPwdHistory();
            h.setPwdHistoryId(UUID.randomUUID().toString());
            h.setLoginId(loginId);
            h.setPassword(password);
            h.setCreatedOn(LocalDateTime.now());
            h.setCreatedBy(loginId);
            h.setUpdatedOn(LocalDateTime.now());
            h.setUpdatedBy(loginId);
            usmPwdHistoryRepository.save(h);
            return true;
        } catch (Exception e) {
            log.error("InsertPasswordHistory failed for {}", loginId, e);
            return false;
        }
    }
}
