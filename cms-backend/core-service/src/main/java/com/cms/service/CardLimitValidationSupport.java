package com.cms.service;

import com.cms.dal.entity.Card;
import com.cms.dal.entity.LimitProfile;
import com.cms.dal.repository.CardLimitCustomizedRepository;
import com.cms.dal.repository.CardRepository;
import com.cms.dal.repository.LimitProfileRepository;
import com.cms.exception.BusinessValidationException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.CardLimitCustomizedMapper;
import com.cms.service.CardDataEncryptionService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

/**
 * Shared validation for card-limit Actual / Customized APIs:
 * PAN exists, status not HOT, limit profile assigned,
 * and ceiling resolution (customized override or profile daily max).
 */
@Component
public class CardLimitValidationSupport {

    public static final Set<String> ALLOWED_TRAN_CODES = Set.of("POS", "ECOM", "WITHDRAWAL", "WD", "ATM", "ECOMMERCE");

    private final CardRepository cardRepository;
    private final LimitProfileRepository limitProfileRepository;
    private final CardLimitCustomizedRepository cardLimitCustomizedRepository;
    private final CardDataEncryptionService encryptionService;

    public CardLimitValidationSupport(CardRepository cardRepository,
                                      LimitProfileRepository limitProfileRepository,
                                      CardLimitCustomizedRepository cardLimitCustomizedRepository,
                                      CardDataEncryptionService encryptionService) {
        this.cardRepository = cardRepository;
        this.limitProfileRepository = limitProfileRepository;
        this.cardLimitCustomizedRepository = cardLimitCustomizedRepository;
        this.encryptionService = encryptionService;
    }

    public String normalizeTranCode(String tranCode) {
        String code = CardLimitCustomizedMapper.normalizeTranCode(tranCode);
        if (code == null || code.isBlank()) {
            throw new BusinessValidationException("tranCode is required");
        }
        if (!ALLOWED_TRAN_CODES.contains(code)) {
            throw new BusinessValidationException(
                "tranCode must be one of POS, ECOM, WITHDRAWAL (aliases: WD, ATM, ECOMMERCE)");
        }
        return canonicalizeTranCode(code);
    }

    /** Map aliases to canonical codes stored in tables. */
    public String canonicalizeTranCode(String code) {
        return switch (code) {
            case "WD", "ATM" -> "WITHDRAWAL";
            case "ECOMMERCE" -> "ECOM";
            default -> code;
        };
    }

    public Card requireEligibleCard(String pan) {
        if (pan == null || pan.isBlank()) {
            throw new BusinessValidationException("pan is required");
        }
        Card card = findCardByPan(pan.trim())
            .orElseThrow(() -> new ResourceNotFoundException("Card", pan));

        if (card.getWhenDeleted() != null) {
            throw new BusinessValidationException("Card is deleted");
        }

        String status = card.getCardStatusCode();
        if (status != null) {
            String s = status.trim();
            if ("HOT".equalsIgnoreCase(s) || "003".equals(s)) {
                throw new BusinessValidationException("Card status HOT is not allowed for limit changes");
            }
        }

        // PIN: CMS has no PIN-verify endpoint yet; require card to be present and not HOT.
        // Callers treat this as "card identity OK" until PIN verification is wired.

        if ((card.getLimitProfileId() == null)
            && (card.getLimitProfile() == null || card.getLimitProfile().isBlank())) {
            throw new BusinessValidationException("Card has no limit profile assigned");
        }
        return card;
    }

    public Optional<Card> findCardByPan(String pan) {
        if (pan.contains("*")) {
            return cardRepository.findByPan(pan);
        }
        String hash = encryptionService.panHashForLookup(pan);
        if (hash != null) {
            Optional<Card> byHash = cardRepository.findByPanHash(hash);
            if (byHash.isPresent()) return byHash;
        }
        return cardRepository.findByPan(pan);
    }

    /**
     * Effective ceiling for a card/tran: active customized limit if present, else profile daily amount.
     */
    public BigDecimal resolveMaxCeiling(Card card, String canonicalTranCode) {
        Optional<BigDecimal> customized = cardLimitCustomizedRepository
            .findByPanAndTranCodeAndIsActive(card.getPan(), canonicalTranCode, 1)
            .map(c -> c.getCustomizedLimit());
        // Customized rows may store full PAN while card.clear pan is masked — try request pan variants later in service.
        if (customized.isPresent() && customized.get() != null) {
            return customized.get();
        }

        LimitProfile profile = resolveLimitProfile(card);
        BigDecimal daily = dailyAmountForTran(profile, canonicalTranCode);
        if (daily == null) {
            throw new BusinessValidationException(
                "Limit profile " + profile.getProfileCode() + " has no daily amount for " + canonicalTranCode);
        }
        return daily;
    }

    public BigDecimal resolveMaxCeiling(Card card, String panForLookup, String canonicalTranCode) {
        Optional<BigDecimal> customized = cardLimitCustomizedRepository
            .findByPanAndTranCodeAndIsActive(panForLookup, canonicalTranCode, 1)
            .map(c -> c.getCustomizedLimit());
        if (customized.isEmpty() && card.getPan() != null && !card.getPan().equals(panForLookup)) {
            customized = cardLimitCustomizedRepository
                .findByPanAndTranCodeAndIsActive(card.getPan(), canonicalTranCode, 1)
                .map(c -> c.getCustomizedLimit());
        }
        if (customized.isPresent() && customized.get() != null) {
            return customized.get();
        }
        LimitProfile profile = resolveLimitProfile(card);
        BigDecimal daily = dailyAmountForTran(profile, canonicalTranCode);
        if (daily == null) {
            throw new BusinessValidationException(
                "Limit profile " + profile.getProfileCode() + " has no daily amount for " + canonicalTranCode);
        }
        return daily;
    }

    public LimitProfile resolveLimitProfile(Card card) {
        if (card.getLimitProfileId() != null) {
            return limitProfileRepository.findById(card.getLimitProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("LimitProfile", String.valueOf(card.getLimitProfileId())));
        }
        String codeOrId = card.getLimitProfile();
        if (codeOrId != null && codeOrId.matches("\\d+")) {
            return limitProfileRepository.findById(Long.parseLong(codeOrId))
                .orElseThrow(() -> new ResourceNotFoundException("LimitProfile", codeOrId));
        }
        return limitProfileRepository.findByProfileCode(codeOrId)
            .orElseThrow(() -> new ResourceNotFoundException("LimitProfile", codeOrId != null ? codeOrId : "null"));
    }

    public BigDecimal dailyAmountForTran(LimitProfile profile, String canonicalTranCode) {
        return switch (canonicalTranCode) {
            case "POS" -> profile.getPosDailyAmount();
            case "ECOM" -> profile.getEcommerceDailyAmount();
            case "WITHDRAWAL" -> profile.getAtmDailyAmount();
            default -> null;
        };
    }

    public void assertWithinCeiling(BigDecimal requested, BigDecimal maxCeiling) {
        if (requested == null) {
            throw new BusinessValidationException("availableLimit / customizedLimit is required");
        }
        if (requested.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("limit cannot be negative");
        }
        if (maxCeiling != null && requested.compareTo(maxCeiling) > 0) {
            throw new BusinessValidationException(
                "limit " + requested + " exceeds allowed maximum " + maxCeiling);
        }
    }

    /** Customer may only decrease available limit (same-row rule). */
    public void assertDecreaseOnly(BigDecimal current, BigDecimal requested) {
        if (current != null && requested != null && requested.compareTo(current) > 0) {
            throw new BusinessValidationException(
                "Customer can only decrease available limit; current=" + current + ", requested=" + requested);
        }
    }

    public String auditUser(String requested) {
        if (requested == null || requested.isBlank()) return "system";
        return requested.length() > 30 ? requested.substring(0, 30) : requested;
    }
}
