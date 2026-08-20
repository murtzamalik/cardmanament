package com.cms.app.service;

import com.cms.app.config.AESencryption;
import com.cms.app.config.ResponseCode;
import com.cms.app.entity.Account;
import com.cms.app.entity.AccountStatus;
import com.cms.app.entity.AccountType;
import com.cms.app.entity.Branch;
import com.cms.app.entity.Card;
import com.cms.app.entity.CardLimitActual;
import com.cms.app.entity.CardLimitCustomized;
import com.cms.app.entity.CardProduct;
import com.cms.app.entity.CardProductionStatus;
import com.cms.app.entity.CardRequest;
import com.cms.app.entity.CardStatus;
import com.cms.app.entity.CardType;
import com.cms.app.entity.LimitProfile;
import com.cms.app.repository.AccountRepository;
import com.cms.app.repository.AccountStatusRepository;
import com.cms.app.repository.AccountTypeRepository;
import com.cms.app.repository.BranchRepository;
import com.cms.app.repository.CardLimitActualRepository;
import com.cms.app.repository.CardLimitCustomizedRepository;
import com.cms.app.repository.CardProductRepository;
import com.cms.app.repository.CardProductionStatusRepository;
import com.cms.app.repository.CardRepository;
import com.cms.app.repository.CardRequestRepository;
import com.cms.app.repository.CardSpendingSummaryRepository;
import com.cms.app.repository.CardStatusRepository;
import com.cms.app.repository.CardTypeRepository;
import com.cms.app.repository.LimitProfileRepository;
import com.cms.app.request.CardAuthorizeRequest;
import com.cms.app.request.CardAvailableLimitRequest;
import com.cms.app.request.CardInquiryRequest;
import com.cms.app.request.CardLimitValidateRequest;
import com.cms.app.request.CardNewRequest;
import com.cms.app.request.CardSpendingSummaryRequest;
import com.cms.app.request.CardUpdateStatusRequest;
import com.cms.app.request.CardValidationRequest;
import com.cms.app.request.ChangePinRequest;
import com.cms.app.request.ForgotPin;
import com.cms.app.response.CardAuthorizeResponse;
import com.cms.app.response.CardInquiryResponse;
import com.cms.app.response.CardLimitValidateResponse;
import com.cms.app.response.CardLovResponse;
import com.cms.app.response.CardSpendingSummaryResponse;
import com.cms.app.response.ResponseWrapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {

    private static final String DEFAULT_ACCOUNT_TYPE = "00";
    private static final String DEFAULT_ACCOUNT_STATUS = "OPEN";
    private static final String DEFAULT_CURRENCY = "AFN";

    private final CardRepository cardRepository;
    private final CardRequestRepository cardRequestRepository;
    private final AccountRepository accountRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final AccountStatusRepository accountStatusRepository;
    private final CardProductRepository cardProductRepository;
    private final CardTypeRepository cardTypeRepository;
    private final CardStatusRepository cardStatusRepository;
    private final BranchRepository branchRepository;
    private final LimitProfileRepository limitProfileRepository;
    private final CardProductionStatusRepository cardProductionStatusRepository;
    private final CardSpendingSummaryRepository cardSpendingSummaryRepository;
    private final CardLimitActualRepository cardLimitActualRepository;
    private final CardLimitCustomizedRepository cardLimitCustomizedRepository;
    private final AESencryption aesEncryption;

    public CardService(CardRepository cardRepository,
                       CardRequestRepository cardRequestRepository,
                       AccountRepository accountRepository,
                       AccountTypeRepository accountTypeRepository,
                       AccountStatusRepository accountStatusRepository,
                       CardProductRepository cardProductRepository,
                       CardTypeRepository cardTypeRepository,
                       CardStatusRepository cardStatusRepository,
                       BranchRepository branchRepository,
                       LimitProfileRepository limitProfileRepository,
                       CardProductionStatusRepository cardProductionStatusRepository,
                       CardSpendingSummaryRepository cardSpendingSummaryRepository,
                       CardLimitActualRepository cardLimitActualRepository,
                       CardLimitCustomizedRepository cardLimitCustomizedRepository,
                       AESencryption aesEncryption) {
        this.cardRepository = cardRepository;
        this.cardRequestRepository = cardRequestRepository;
        this.accountRepository = accountRepository;
        this.accountTypeRepository = accountTypeRepository;
        this.accountStatusRepository = accountStatusRepository;
        this.cardProductRepository = cardProductRepository;
        this.cardTypeRepository = cardTypeRepository;
        this.cardStatusRepository = cardStatusRepository;
        this.branchRepository = branchRepository;
        this.limitProfileRepository = limitProfileRepository;
        this.cardProductionStatusRepository = cardProductionStatusRepository;
        this.cardSpendingSummaryRepository = cardSpendingSummaryRepository;
        this.cardLimitActualRepository = cardLimitActualRepository;
        this.cardLimitCustomizedRepository = cardLimitCustomizedRepository;
        this.aesEncryption = aesEncryption;
    }

    public ResponseWrapper<CardInquiryResponse> inquiry(CardInquiryRequest request) {
        ResponseWrapper<CardInquiryResponse> response = new ResponseWrapper<>();
        response.setResponseCode(ResponseCode.NO_DATA);
        response.setResponseMessage(ResponseCode.getMessage(ResponseCode.NO_DATA));

        Optional<CardRequest> pending = cardRequestRepository
                .findTopByRelationshipNumAndIsProcessedOrderByCreatedOnDesc(request.getRelationshipNum(), 0);
        if (pending.isPresent()) {
            response.setResponseCode(ResponseCode.CARD_IN_PROCESS);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.CARD_IN_PROCESS));
            return response;
        }

        Optional<Card> card = cardRepository.findFirstByRelationshipNumOrderByCreatedOnDesc(request.getRelationshipNum());
        if (card.isPresent()) {
            response.setResponseCode(ResponseCode.SUCCESS);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.SUCCESS));
            response.setResponseBody(toInquiryResponse(card.get()));
        }
        return response;
    }

    private CardInquiryResponse toInquiryResponse(Card card) {
        CardInquiryResponse body = new CardInquiryResponse();
        body.setCardTitle(card.getCardTitle());
        body.setPan(card.getPan());
        body.setCreatedOn(card.getCreatedOn());
        body.setExpiryDate(card.getExpiryDate());
        body.setCvv(decodeMaybe(card.getCvv()));
        body.setCvv2(decodeMaybe(card.getCvv2()));

        if (card.getCardStatusCode() != null) {
            cardStatusRepository.findByCardStatusCode(card.getCardStatusCode())
                    .map(CardStatus::getCardStatusName)
                    .ifPresentOrElse(body::setCardStatusCode, () -> body.setCardStatusCode(card.getCardStatusCode()));
        }
        if (card.getCardTypeCode() != null) {
            cardTypeRepository.findByCardTypeCode(card.getCardTypeCode())
                    .map(CardType::getCardTypeName)
                    .ifPresentOrElse(body::setCardTypeName, () -> body.setCardTypeName(card.getCardTypeCode()));
        }
        if (card.getCardProdStatusId() != null) {
            cardProductionStatusRepository.findByCardProdStatusId(card.getCardProdStatusId())
                    .map(CardProductionStatus::getCardProdStatusName)
                    .ifPresentOrElse(body::setCardProdStatus, () -> body.setCardProdStatus(card.getCardProdStatusId()));
        }
        body.setPinSet(card.getPinOffset() != null && !card.getPinOffset().isBlank());
        return body;
    }

    private String decodeMaybe(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        try {
            return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            return value;
        }
    }

    /**
     * Like old cms: create ACCOUNT if missing, then insert CARD_REQUEST.
     * No CUSTOMER_ACCOUNT (table not on portal schema).
     */
    @Transactional
    public ResponseWrapper<Void> newRequest(CardNewRequest request) {
        ResponseWrapper<Void> response = new ResponseWrapper<>();

        Optional<CardProduct> product = cardProductRepository.findByProductCode(request.getProductCode());
        if (product.isEmpty()) {
            response.setResponseCode(ResponseCode.INVALID_PRODUCT);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.INVALID_PRODUCT));
            return response;
        }

        Optional<CardType> cardType = cardTypeRepository.findByCardTypeCode(request.getCardType());
        if (cardType.isEmpty()) {
            response.setResponseCode(ResponseCode.INVALID_CARD_TYPE);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.INVALID_CARD_TYPE));
            return response;
        }

        Optional<CardRequest> pending = cardRequestRepository
                .findTopByRelationshipNumAndIsProcessedOrderByCreatedOnDesc(request.getRelationshipNumber(), 0);
        if (pending.isPresent()) {
            response.setResponseCode(ResponseCode.CARD_REQUEST_ALREADY_PENDING);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.CARD_REQUEST_ALREADY_PENDING));
            return response;
        }

        Account account = accountRepository.findByAccountNum(request.getAccountNumber())
                .orElseGet(() -> createAccount(request));
        if (account == null) {
            response.setResponseCode(ResponseCode.FAILURE);
            response.setResponseMessage("Unable to create account (missing account type/status/branch lookup)");
            return response;
        }

        String branchCode = resolveBranchCode(account);

        CardRequest cr = new CardRequest();
        cr.setRelationshipNum(request.getRelationshipNumber());
        cr.setAccountNum(request.getAccountNumber());
        cr.setCardTitle(request.getCardTitle());
        cr.setCardTypeCode(request.getCardType());
        cr.setProductCode(request.getProductCode());
        cr.setBranchCode(branchCode);
        cr.setRequestTypeId(request.getRequestTypeId());
        cr.setSupplementaryCount(0);
        cr.setIsProcessed(0);
        cr.setProgressFlag(0);
        cr.setCreatedBy("API_APP");
        cr.setUpdatedBy("API_APP");
        cr.setCreatedOn(LocalDateTime.now());
        cr.setUpdatedOn(LocalDateTime.now());
        cardRequestRepository.save(cr);

        response.setResponseCode(ResponseCode.SUCCESS);
        response.setResponseMessage(ResponseCode.getMessage(ResponseCode.SUCCESS));
        return response;
    }

    /** Create ACCOUNT like old cms when accountNumber does not exist yet. */
    private Account createAccount(CardNewRequest request) {
        Optional<AccountType> typeOpt = accountTypeRepository.findByAcctTypeCode(DEFAULT_ACCOUNT_TYPE);
        if (typeOpt.isEmpty()) {
            typeOpt = accountTypeRepository.findAll().stream().findFirst();
        }
        Optional<AccountStatus> statusOpt = accountStatusRepository.findByAcctStatusCode(DEFAULT_ACCOUNT_STATUS);
        if (statusOpt.isEmpty()) {
            statusOpt = accountStatusRepository.findByAcctStatusCode("10");
        }
        if (statusOpt.isEmpty()) {
            statusOpt = accountStatusRepository.findAll().stream().findFirst();
        }
        Optional<Branch> branchOpt = branchRepository.findByBranchCode("1");
        if (branchOpt.isEmpty()) {
            branchOpt = branchRepository.findAll().stream().findFirst();
        }
        if (typeOpt.isEmpty() || statusOpt.isEmpty() || branchOpt.isEmpty()) {
            return null;
        }

        AccountType type = typeOpt.get();
        AccountStatus status = statusOpt.get();
        Branch branch = branchOpt.get();
        LocalDateTime now = LocalDateTime.now();

        Account account = new Account();
        account.setAccountNum(request.getAccountNumber());
        account.setAccountTitle(request.getCardTitle());
        account.setAcctTypeCode(type.getAcctTypeCode());
        account.setAccountTypeId(type.getId());
        account.setAcctStatusCode(status.getAcctStatusCode());
        account.setAccountStatusId(status.getId());
        account.setBranchCode(branch.getBranchCode());
        account.setBranchId(branch.getId());
        account.setCurrencyCode(branch.getCurrencyCode() != null && !branch.getCurrencyCode().isBlank()
                ? branch.getCurrencyCode() : DEFAULT_CURRENCY);
        account.setIsJoint(BigDecimal.ZERO);
        account.setIsClosed(Boolean.FALSE);
        account.setOpenedDate(now);
        account.setCreatedOn(now);
        account.setUpdatedOn(now);
        account.setCreatedBy("API_APP");
        account.setUpdatedBy("API_APP");
        return accountRepository.save(account);
    }

    private String resolveBranchCode(Account account) {
        if (account.getBranchCode() != null && !account.getBranchCode().isBlank()) {
            return account.getBranchCode();
        }
        return branchRepository.findAll().stream()
                .findFirst()
                .map(Branch::getBranchCode)
                .orElse("1");
    }

    @Transactional
    public ResponseWrapper<Void> updateStatus(CardUpdateStatusRequest request) {
        ResponseWrapper<Void> response = new ResponseWrapper<>();
        response.setResponseCode(ResponseCode.NO_DATA);
        response.setResponseMessage(ResponseCode.getMessage(ResponseCode.NO_DATA));

        Optional<Card> cardOpt = cardRepository.findByPan(request.getPan());
        if (cardOpt.isEmpty()) {
            return response;
        }
        Card card = cardOpt.get();
        if (card.getCardStatusCode() != null && "003".equals(card.getCardStatusCode().trim())) {
            response.setResponseCode(ResponseCode.CARD_STATUS_UPDATE_ERROR);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.CARD_STATUS_UPDATE_ERROR));
            return response;
        }

        Optional<CardStatus> status = cardStatusRepository.findByCardStatusCode(request.getStatusCode());
        if (status.isEmpty()) {
            return response;
        }

        card.setCardStatusCode(status.get().getCardStatusCode());
        card.setUpdatedOn(LocalDateTime.now());
        card.setUpdatedBy("API_APP");
        cardRepository.save(card);

        response.setResponseCode(ResponseCode.SUCCESS);
        response.setResponseMessage(ResponseCode.getMessage(ResponseCode.SUCCESS));
        return response;
    }

    public ResponseWrapper<List<CardLovResponse>> getLov(String slug) {
        ResponseWrapper<List<CardLovResponse>> response = new ResponseWrapper<>();
        List<CardLovResponse> lovs = new ArrayList<>();
        lovs.add(cardProducts());
        lovs.add(cardTypes());
        lovs.add(cardStatuses());
        lovs.add(emptyRequestTypes());
        response.setResponseCode(ResponseCode.SUCCESS);
        response.setResponseMessage(ResponseCode.getMessage(ResponseCode.SUCCESS));
        response.setResponseBody(lovs);
        return response;
    }

    private CardLovResponse cardProducts() {
        CardLovResponse lov = new CardLovResponse();
        lov.setType("card-product");
        HashMap<String, String> map = new HashMap<>();
        for (CardProduct p : cardProductRepository.findAll()) {
            if (p == null || p.getProductCode() == null || p.getProductCode().isBlank()) {
                continue;
            }
            if (p.getIsActive() != null && p.getIsActive() != 1) {
                continue;
            }
            map.put(p.getProductCode(), p.getProductName() != null ? p.getProductName() : p.getProductCode());
        }
        if (map.isEmpty()) {
            for (CardProduct p : cardProductRepository.findAll()) {
                if (p == null || p.getProductCode() == null || p.getProductCode().isBlank()) {
                    continue;
                }
                map.put(p.getProductCode(), p.getProductName() != null ? p.getProductName() : p.getProductCode());
            }
        }
        lov.setLov(map);
        return lov;
    }

    private CardLovResponse cardTypes() {
        CardLovResponse lov = new CardLovResponse();
        lov.setType("card-types");
        HashMap<String, String> map = new HashMap<>();
        for (CardType t : cardTypeRepository.findAll()) {
            if (t == null || t.getCardTypeCode() == null || t.getCardTypeCode().isBlank()) {
                continue;
            }
            if (t.getIsActive() != null && t.getIsActive() != 1) {
                continue;
            }
            map.put(t.getCardTypeCode(), t.getCardTypeName() != null ? t.getCardTypeName() : t.getCardTypeCode());
        }
        if (map.isEmpty()) {
            for (CardType t : cardTypeRepository.findAll()) {
                if (t == null || t.getCardTypeCode() == null || t.getCardTypeCode().isBlank()) {
                    continue;
                }
                map.put(t.getCardTypeCode(), t.getCardTypeName() != null ? t.getCardTypeName() : t.getCardTypeCode());
            }
        }
        lov.setLov(map);
        return lov;
    }

    private CardLovResponse cardStatuses() {
        CardLovResponse lov = new CardLovResponse();
        lov.setType("card-status");
        HashMap<String, String> map = new HashMap<>();
        for (CardStatus s : cardStatusRepository.findAll()) {
            if (s == null || s.getCardStatusCode() == null || s.getCardStatusCode().isBlank()) {
                continue;
            }
            map.put(s.getCardStatusCode(), s.getCardStatusName() != null ? s.getCardStatusName() : s.getCardStatusCode());
        }
        lov.setLov(map);
        return lov;
    }

    private CardLovResponse emptyRequestTypes() {
        CardLovResponse lov = new CardLovResponse();
        lov.setType("card-request-types");
        HashMap<String, String> map = new HashMap<>();
        map.put("1", "NEW");
        map.put("NEW", "NEW");
        lov.setLov(map);
        return lov;
    }

    public ResponseWrapper<Void> validate(CardValidationRequest request) {
        ResponseWrapper<Void> response = new ResponseWrapper<>();
        response.setResponseCode(ResponseCode.SUCCESS);
        response.setResponseMessage(ResponseCode.getMessage(ResponseCode.SUCCESS));

        Optional<Card> cardOpt = cardRepository.findByPan(request.getPan());
        if (cardOpt.isEmpty()) {
            response.setResponseCode(ResponseCode.NO_DATA);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.NO_DATA));
            return response;
        }
        Card card = cardOpt.get();
        if (card.getExpiryDate() != null && card.getExpiryDate().isBefore(LocalDateTime.now())) {
            response.setResponseCode(ResponseCode.CARD_EXPIRED);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.CARD_EXPIRED));
            return response;
        }

        if (request.getPin() != null && !request.getPin().isEmpty()) {
            if (card.getPinOffset() == null || card.getPinOffset().isBlank()) {
                response.setResponseCode(ResponseCode.NO_DATA);
                response.setResponseMessage(ResponseCode.getMessage(ResponseCode.NO_DATA));
                return response;
            }
            if (!pinsMatch(card.getPinOffset(), request.getPin())) {
                response.setResponseCode(ResponseCode.CARD_PIN_NOT_MATCHED);
                response.setResponseMessage(ResponseCode.getMessage(ResponseCode.CARD_PIN_NOT_MATCHED));
                return response;
            }
        }

        if (request.getCvv() != null && !request.getCvv().isEmpty()) {
            String stored = decodeMaybe(card.getCvv());
            String stored2 = decodeMaybe(card.getCvv2());
            if (!request.getCvv().equals(stored) && !request.getCvv().equals(stored2)
                    && !request.getCvv().equals(card.getCvv()) && !request.getCvv().equals(card.getCvv2())) {
                response.setResponseCode(ResponseCode.CVV_NOT_MATCHED);
                response.setResponseMessage(ResponseCode.getMessage(ResponseCode.CVV_NOT_MATCHED));
                return response;
            }
        }

        if (request.getTrack2() != null && !request.getTrack2().isEmpty()) {
            String track2 = decodeMaybe(card.getTrack2Data());
            if (!request.getTrack2().equals(track2) && !request.getTrack2().equals(card.getTrack2Data())) {
                response.setResponseCode(ResponseCode.TRACK2_NOT_MATCHED);
                response.setResponseMessage(ResponseCode.getMessage(ResponseCode.TRACK2_NOT_MATCHED));
                return response;
            }
        }
        return response;
    }

    /**
     * Same as old cms change-pin: verify old PIN, confirm new, store new encrypted value.
     */
    @Transactional
    public ResponseWrapper<Void> changePin(@Valid ChangePinRequest request) {
        ResponseWrapper<Void> response = new ResponseWrapper<>();

        Optional<Card> cardOpt = cardRepository.findByRelationshipNumAndPan(request.getRelationshipNum(), request.getPan());
        if (cardOpt.isEmpty()) {
            response.setResponseCode(ResponseCode.NO_DATA);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.NO_DATA));
            return response;
        }
        Card card = cardOpt.get();
        if (card.getPinOffset() == null || card.getPinOffset().isBlank()) {
            response.setResponseCode(ResponseCode.NO_DATA);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.NO_DATA));
            return response;
        }
        if (!pinsMatch(card.getPinOffset(), request.getOldPin())) {
            response.setResponseCode(ResponseCode.CARD_PIN_NOT_MATCHED);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.CARD_PIN_NOT_MATCHED));
            return response;
        }
        if (request.getNewPin() == null || !request.getNewPin().equals(request.getConfirmNewPin())) {
            response.setResponseCode(ResponseCode.PINS_NOT_MATCHED);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.PINS_NOT_MATCHED));
            return response;
        }

        card.setPinOffset(request.getNewPin());
        card.setPinGeneratedOn(LocalDateTime.now());
        card.setUpdatedOn(LocalDateTime.now());
        card.setUpdatedBy("API_APP");
        cardRepository.save(card);

        response.setResponseCode(ResponseCode.SUCCESS);
        response.setResponseMessage(ResponseCode.getMessage(ResponseCode.SUCCESS));
        return response;
    }

    /**
     * Same as old cms generate-pin / forgotPin:
     * flag=F → reset existing PIN; otherwise first-time set + status 001.
     */
    @Transactional
    public ResponseWrapper<Void> forgotPin(ForgotPin request) {
        ResponseWrapper<Void> response = new ResponseWrapper<>();

        if (request.getPin() == null || request.getConfirmPin() == null
                || !request.getPin().equals(request.getConfirmPin())) {
            response.setResponseCode(ResponseCode.PINS_NOT_MATCHED);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.PINS_NOT_MATCHED));
            return response;
        }

        Optional<Card> cardOpt = cardRepository.findByRelationshipNumAndPan(request.getRelationshipNum(), request.getPan());
        if (cardOpt.isEmpty()) {
            response.setResponseCode(ResponseCode.NO_DATA);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.NO_DATA));
            return response;
        }
        Card card = cardOpt.get();

        boolean pinAlreadySet = card.getPinOffset() != null && !card.getPinOffset().isBlank();

        if (request.getFlag() != null && request.getFlag().equalsIgnoreCase("F")) {
            if (!pinAlreadySet) {
                response.setResponseCode(ResponseCode.NO_DATA);
                response.setResponseMessage(ResponseCode.getMessage(ResponseCode.NO_DATA));
                return response;
            }
            card.setPinOffset(request.getPin());
            card.setPinGeneratedOn(LocalDateTime.now());
            card.setUpdatedOn(LocalDateTime.now());
            card.setUpdatedBy("API_APP");
            cardRepository.save(card);
        } else {
            // First-time Set PIN only — do not overwrite an existing PIN
            if (pinAlreadySet) {
                response.setResponseCode(ResponseCode.PIN_ALREADY_SET);
                response.setResponseMessage(ResponseCode.getMessage(ResponseCode.PIN_ALREADY_SET));
                return response;
            }
            card.setPinOffset(request.getPin());
            card.setPinGeneratedOn(LocalDateTime.now());
            card.setPinStatus(0);
            card.setPinRetryAvailable(3);
            card.setPinMaxRetry(3);
            card.setCardStatusCode("001");
            card.setUpdatedOn(LocalDateTime.now());
            card.setUpdatedBy("API_APP");
            cardRepository.save(card);
        }

        response.setResponseCode(ResponseCode.SUCCESS);
        response.setResponseMessage(ResponseCode.getMessage(ResponseCode.SUCCESS));
        return response;
    }

    /** Same compare style as old cms: decrypt(stored) equals decrypt(request). */
    private boolean pinsMatch(String storedEncrypted, String requestEncrypted) {
        String stored = aesEncryption.decrypt(storedEncrypted);
        String incoming = aesEncryption.decrypt(requestEncrypted);
        if (stored == null || incoming == null) {
            return false;
        }
        return stored.equals(incoming);
    }

    public ResponseWrapper<CardLimitValidateResponse> availableLimit(CardAvailableLimitRequest request) {
        return resolveLimit(request.getPan(), request.getChannelCode(), null, false);
    }

    public ResponseWrapper<List<CardSpendingSummaryResponse>> spendingSummary(CardSpendingSummaryRequest request) {
        ResponseWrapper<List<CardSpendingSummaryResponse>> response = new ResponseWrapper<>();

        String key = firstNonBlank(request.getAccountNumber(), request.getPan());
        if (key == null) {
            response.setResponseCode(ResponseCode.FAILURE);
            response.setResponseMessage("accountNumber or pan is required");
            return response;
        }

        String channelCode = blankToNull(request.getChannelCode());
        String tranCode = blankToNull(request.getTranCode());

        List<Object[]> rows = cardSpendingSummaryRepository.findSpendingSummary(key, channelCode, tranCode);
        if (rows == null || rows.isEmpty()) {
            response.setResponseCode(ResponseCode.NO_DATA);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.NO_DATA));
            return response;
        }

        List<CardSpendingSummaryResponse> body = new ArrayList<>();
        for (Object[] row : rows) {
            CardSpendingSummaryResponse item = new CardSpendingSummaryResponse();
            item.setAccountNumber(asString(row[0]));
            String rowPan = asString(row[1]);
            item.setPan(rowPan);
            item.setCardTitle(asString(row[2]));
            item.setExpiryDate(asExpiryString(row[3]));
            item.setCardStatusName(asString(row[4]));
            item.setMaxLimit(asDouble(row[5]));
            item.setSingleTranLimit(asDouble(row[6]));
            item.setDailyAvailableSpending(asDouble(row[7]));
            item.setMonthlyAvailableSpending(asDouble(row[8]));

            // Overlay same-day CARD_LIMIT_ACTUAL (authorize writes channel/tran as "1"/"2"/"3")
            String panForActual = rowPan != null ? rowPan : key;
            Long channelLong = null;
            if (channelCode != null) {
                try {
                    channelLong = Long.valueOf(channelCode);
                } catch (NumberFormatException ignored) {
                    channelLong = null;
                }
            }
            BigDecimal actualAvail = findTodayActualAvailable(panForActual, channelLong, tranCode);
            if (actualAvail != null) {
                item.setDailyAvailableSpending(actualAvail.doubleValue());
                item.setMonthlyAvailableSpending(actualAvail.doubleValue());
            }
            body.add(item);
        }

        response.setResponseCode(ResponseCode.SUCCESS);
        response.setResponseMessage(ResponseCode.getMessage(ResponseCode.SUCCESS));
        response.setResponseBody(body);
        return response;
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a.trim();
        }
        if (b != null && !b.isBlank()) {
            return b.trim();
        }
        return null;
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static Double asDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return new BigDecimal(value.toString()).doubleValue();
    }

    private static String asExpiryString(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Timestamp ts) {
            return ts.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        if (value instanceof LocalDateTime ldt) {
            return ldt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        if (value instanceof LocalDate ld) {
            return ld.atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        if (value instanceof Date date) {
            return new Timestamp(date.getTime()).toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        return String.valueOf(value);
    }

    public ResponseWrapper<CardLimitValidateResponse> validateLimit(CardLimitValidateRequest request) {
        return resolveLimit(request.getPan(), request.getChannelCode(), request.getAmount(), true);
    }

    /**
     * Authorize a transaction amount:
     * 1) verify PAN / PIN / expiry
     * 2) ceiling from CARD_LIMIT_CUSTOMIZED (if active) else profile/max
     * 3) same-day CARD_LIMIT_ACTUAL remaining (create row if missing / new day)
     * 4) deduct amount and update the same actual row
     *
     * channelCode: 1=ATM, 2=POS, 3=Ecommerce. tranCode defaults to same as channel.
     */
    @Transactional
    public ResponseWrapper<CardAuthorizeResponse> authorizeTransaction(CardAuthorizeRequest request) {
        ResponseWrapper<CardAuthorizeResponse> response = new ResponseWrapper<>();

        if (request == null || request.getPan() == null || request.getPan().isBlank()) {
            response.setResponseCode(ResponseCode.FAILURE);
            response.setResponseMessage("pan is required");
            return response;
        }
        if (request.getAmount() == null || request.getAmount().isBlank()) {
            response.setResponseCode(ResponseCode.INVALID_AMOUNT);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.INVALID_AMOUNT));
            return response;
        }
        if (request.getChannelCode() == null) {
            response.setResponseCode(ResponseCode.FAILURE);
            response.setResponseMessage("channelCode is required (1=ATM, 2=POS, 3=Ecommerce)");
            return response;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(request.getAmount().trim());
        } catch (NumberFormatException ex) {
            response.setResponseCode(ResponseCode.INVALID_AMOUNT);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.INVALID_AMOUNT));
            return response;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            response.setResponseCode(ResponseCode.INVALID_AMOUNT);
            response.setResponseMessage("amount must be greater than zero");
            return response;
        }

        String pan = request.getPan().trim();
        Optional<Card> cardOpt = cardRepository.findByPan(pan);
        if (cardOpt.isEmpty()) {
            response.setResponseCode(ResponseCode.NO_DATA);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.NO_DATA));
            return response;
        }
        Card card = cardOpt.get();

        if (card.getCardStatusCode() != null) {
            String status = card.getCardStatusCode().trim();
            if ("003".equals(status) || "HOT".equalsIgnoreCase(status)) {
                response.setResponseCode(ResponseCode.CARD_STATUS_UPDATE_ERROR);
                response.setResponseMessage(ResponseCode.getMessage(ResponseCode.CARD_STATUS_UPDATE_ERROR));
                return response;
            }
        }

        if (card.getExpiryDate() != null && card.getExpiryDate().isBefore(LocalDateTime.now())) {
            response.setResponseCode(ResponseCode.CARD_EXPIRED);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.CARD_EXPIRED));
            return response;
        }

        if (request.getExpiryDate() != null && !request.getExpiryDate().isBlank()
                && card.getExpiryDate() != null
                && !expiryMatches(card.getExpiryDate(), request.getExpiryDate().trim())) {
            response.setResponseCode(ResponseCode.CARD_EXPIRED);
            response.setResponseMessage("Expiry date does not match card");
            return response;
        }

        if (request.getPin() == null || request.getPin().isBlank()) {
            response.setResponseCode(ResponseCode.PIN_REQUIRED);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.PIN_REQUIRED));
            return response;
        }
        if (card.getPinOffset() == null || card.getPinOffset().isBlank()) {
            response.setResponseCode(ResponseCode.NO_DATA);
            response.setResponseMessage("PIN not set on card");
            return response;
        }
        if (!pinsMatchFlexible(card.getPinOffset(), request.getPin())) {
            response.setResponseCode(ResponseCode.CARD_PIN_NOT_MATCHED);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.CARD_PIN_NOT_MATCHED));
            return response;
        }

        String channelKey = String.valueOf(request.getChannelCode());
        String tranKey = (request.getTranCode() != null && !request.getTranCode().isBlank())
                ? request.getTranCode().trim()
                : channelKey;

        CeilingResult ceiling = resolveAuthorizeCeiling(card, pan, request.getChannelCode(), tranKey);
        if (ceiling.maxLimit == null || ceiling.maxLimit.compareTo(BigDecimal.ZERO) <= 0) {
            response.setResponseCode(ResponseCode.FAILURE);
            response.setResponseMessage("No daily limit configured for this channel");
            return response;
        }

        if (amount.compareTo(ceiling.maxLimit) > 0) {
            response.setResponseCode(ResponseCode.CARD_LIMIT_EXCEED);
            response.setResponseMessage("Amount exceeds customized/max daily limit");
            CardAuthorizeResponse body = buildAuthorizeBody(
                    pan, channelKey, tranKey, amount, ceiling.maxLimit, ceiling.maxLimit,
                    ceiling.customizedApplied, false);
            // available unchanged conceptually — still report ceiling as available before reject
            body.setAvailableLimit(null);
            response.setResponseBody(body);
            return response;
        }

        LocalDate today = LocalDate.now();
        Optional<CardLimitActual> actualOpt =
                cardLimitActualRepository.findByPanAndChannelCodeAndTranCode(pan, channelKey, tranKey);

        boolean created = false;
        CardLimitActual actual;
        BigDecimal remainingBefore;

        if (actualOpt.isPresent()
                && actualOpt.get().getCycleBeginDate() != null
                && actualOpt.get().getCycleBeginDate().equals(today)) {
            actual = actualOpt.get();
            remainingBefore = actual.getAvailableLimit() != null
                    ? actual.getAvailableLimit()
                    : ceiling.maxLimit;
        } else if (actualOpt.isPresent()) {
            // Prior day row — reset cycle for today from full ceiling
            actual = actualOpt.get();
            remainingBefore = ceiling.maxLimit;
            actual.setCycleBeginDate(today);
        } else {
            actual = new CardLimitActual();
            actual.setPan(pan);
            actual.setChannelCode(channelKey);
            actual.setTranCode(tranKey);
            actual.setCycleBeginDate(today);
            actual.setAvailableTranCount(0L);
            actual.setCreatedOn(today);
            actual.setCreatedBy("API_APP");
            remainingBefore = ceiling.maxLimit;
            created = true;
        }

        if (amount.compareTo(remainingBefore) > 0) {
            response.setResponseCode(ResponseCode.CARD_LIMIT_EXCEED);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.CARD_LIMIT_EXCEED));
            CardAuthorizeResponse body = buildAuthorizeBody(
                    pan, channelKey, tranKey, amount, ceiling.maxLimit, remainingBefore,
                    ceiling.customizedApplied, created);
            response.setResponseBody(body);
            return response;
        }

        BigDecimal remainingAfter = remainingBefore.subtract(amount);
        actual.setAvailableLimit(remainingAfter);
        if (actual.getAvailableTranCount() == null) {
            actual.setAvailableTranCount(0L);
        }
        actual.setUpdatedOn(today);
        actual.setUpdatedBy("API_APP");
        cardLimitActualRepository.save(actual);

        response.setResponseCode(ResponseCode.SUCCESS);
        response.setResponseMessage(ResponseCode.getMessage(ResponseCode.SUCCESS));
        response.setResponseBody(buildAuthorizeBody(
                pan, channelKey, tranKey, amount, ceiling.maxLimit, remainingAfter,
                ceiling.customizedApplied, created));
        return response;
    }

    private static final class CeilingResult {
        private final BigDecimal maxLimit;
        private final boolean customizedApplied;

        private CeilingResult(BigDecimal maxLimit, boolean customizedApplied) {
            this.maxLimit = maxLimit;
            this.customizedApplied = customizedApplied;
        }
    }

    private CeilingResult resolveAuthorizeCeiling(Card card, String pan, Long channelCode, String tranKey) {
        // 1) CARD_LIMIT_CUSTOMIZED — try numeric channel and named aliases
        for (String code : customizedLookupCodes(channelCode, tranKey)) {
            Optional<CardLimitCustomized> customized =
                    cardLimitCustomizedRepository.findByPanAndTranCodeAndIsActive(pan, code, 1);
            if (customized.isPresent() && customized.get().getCustomizedLimit() != null) {
                return new CeilingResult(customized.get().getCustomizedLimit(), true);
            }
        }

        // 2) CARD_LIMIT_PROFILE via spending summary MAX_LIMIT
        List<Object[]> rows = cardSpendingSummaryRepository.findSpendingSummary(
                pan, String.valueOf(channelCode), null);
        if (rows != null && !rows.isEmpty()) {
            Double maxVal = asDouble(rows.get(0)[5]);
            if (maxVal != null && maxVal > 0) {
                return new CeilingResult(BigDecimal.valueOf(maxVal), false);
            }
        }

        // 3) LIMIT_PROFILE daily by channel
        Long profileId = card.getLimitProfileId() != null ? card.getLimitProfileId() : card.getLimitProfile();
        if (profileId != null) {
            Optional<LimitProfile> profileOpt = limitProfileRepository.findById(profileId);
            if (profileOpt.isPresent()) {
                BigDecimal daily = pickChannelLimit(profileOpt.get(), channelCode);
                if (daily != null) {
                    return new CeilingResult(daily, false);
                }
            }
        }
        return new CeilingResult(null, false);
    }

    private static String[] customizedLookupCodes(Long channelCode, String tranKey) {
        java.util.LinkedHashSet<String> codes = new java.util.LinkedHashSet<>();
        if (tranKey != null && !tranKey.isBlank()) {
            codes.add(tranKey.trim());
            codes.add(tranKey.trim().toUpperCase());
        }
        if (channelCode != null) {
            codes.add(String.valueOf(channelCode));
            switch (channelCode.intValue()) {
                case 1 -> {
                    codes.add("ATM");
                    codes.add("WITHDRAWAL");
                    codes.add("WD");
                }
                case 2 -> codes.add("POS");
                case 3 -> {
                    codes.add("ECOM");
                    codes.add("ECOMMERCE");
                }
                default -> {
                }
            }
        }
        return codes.toArray(new String[0]);
    }

    private CardAuthorizeResponse buildAuthorizeBody(
            String pan,
            String channelKey,
            String tranKey,
            BigDecimal amount,
            BigDecimal maxLimit,
            BigDecimal available,
            boolean customizedApplied,
            boolean created) {
        CardAuthorizeResponse body = new CardAuthorizeResponse();
        body.setPan(pan);
        body.setChannelCode(channelKey);
        body.setTranCode(tranKey);
        body.setAmount(amount.doubleValue());
        body.setMaxLimit(maxLimit != null ? maxLimit.doubleValue() : null);
        body.setAvailableLimit(available != null ? available.doubleValue() : null);
        body.setCustomizedApplied(customizedApplied);
        body.setActualRowCreated(created);
        return body;
    }

    /** Accepts AES-encrypted PIN (mobile) or plaintext (Postman convenience). */
    private boolean pinsMatchFlexible(String storedEncrypted, String requestPin) {
        if (pinsMatch(storedEncrypted, requestPin)) {
            return true;
        }
        String stored = aesEncryption.decrypt(storedEncrypted);
        return stored != null && stored.equals(requestPin);
    }

    private boolean expiryMatches(LocalDateTime cardExpiry, String requestExpiry) {
        String raw = requestExpiry.trim();
        var slash = java.util.regex.Pattern.compile("^(\\d{1,2})\\s*/\\s*(\\d{2}|\\d{4})$").matcher(raw);
        if (!slash.matches()) {
            return false;
        }
        int mm = Integer.parseInt(slash.group(1));
        int yy = Integer.parseInt(slash.group(2));
        if (slash.group(2).length() == 2) {
            yy += 2000;
        }
        return cardExpiry.getMonthValue() == mm && cardExpiry.getYear() == yy;
    }

    private ResponseWrapper<CardLimitValidateResponse> resolveLimit(String pan, Long channelCode, String amountStr, boolean consumeCheck) {
        ResponseWrapper<CardLimitValidateResponse> response = new ResponseWrapper<>();
        Optional<Card> cardOpt = cardRepository.findByPan(pan);
        if (cardOpt.isEmpty()) {
            response.setResponseCode(ResponseCode.NO_DATA);
            response.setResponseMessage(ResponseCode.getMessage(ResponseCode.NO_DATA));
            return response;
        }
        Card card = cardOpt.get();

        BigDecimal max = BigDecimal.ZERO;
        BigDecimal available = null;

        String channelKey = channelCode == null ? null : String.valueOf(channelCode);

        // 1) Prefer same-day CARD_LIMIT_ACTUAL written by /transaction/authorize (channel/tran = 1|2|3)
        BigDecimal actualAvail = findTodayActualAvailable(pan, channelCode, channelKey);
        if (actualAvail != null) {
            available = actualAvail;
        }

        // 2) Max (+ legacy available) from spending summary / CARD_LIMIT_PROFILE
        List<Object[]> rows = cardSpendingSummaryRepository.findSpendingSummary(pan, channelKey, null);
        if (rows != null && !rows.isEmpty()) {
            Object[] row = rows.get(0);
            Double maxVal = asDouble(row[5]);
            Double dailyAvail = asDouble(row[7]);
            if (maxVal != null) {
                max = BigDecimal.valueOf(maxVal);
            }
            // Only use summary available if we did not already get actual
            if (available == null && dailyAvail != null) {
                available = BigDecimal.valueOf(dailyAvail);
            }
        }

        // 3) Fallback to LIMIT_PROFILE daily channel caps
        if (available == null || max.compareTo(BigDecimal.ZERO) == 0) {
            Long profileId = card.getLimitProfileId() != null ? card.getLimitProfileId() : card.getLimitProfile();
            if (profileId == null && available == null) {
                response.setResponseCode(ResponseCode.FAILURE);
                response.setResponseMessage("No limit profile on card");
                return response;
            }
            if (profileId != null) {
                Optional<LimitProfile> profileOpt = limitProfileRepository.findById(profileId);
                if (profileOpt.isEmpty() && available == null) {
                    response.setResponseCode(ResponseCode.FAILURE);
                    response.setResponseMessage("Limit profile not found");
                    return response;
                }
                if (profileOpt.isPresent()) {
                    BigDecimal profileMax = pickChannelLimit(profileOpt.get(), channelCode);
                    if (max.compareTo(BigDecimal.ZERO) == 0) {
                        max = profileMax != null ? profileMax : BigDecimal.ZERO;
                    }
                    if (available == null) {
                        available = max;
                    }
                }
            }
        }

        if (available == null) {
            available = max;
        }

        if (consumeCheck && amountStr != null && !amountStr.isBlank()) {
            BigDecimal amount = new BigDecimal(amountStr);
            if (amount.compareTo(available) > 0) {
                response.setResponseCode(ResponseCode.CARD_LIMIT_EXCEED);
                response.setResponseMessage(ResponseCode.getMessage(ResponseCode.CARD_LIMIT_EXCEED));
                CardLimitValidateResponse exceeded = new CardLimitValidateResponse();
                exceeded.setMaxLimit(max.doubleValue());
                exceeded.setAvailableLimit(available.doubleValue());
                exceeded.setAvailableTranCount(null);
                response.setResponseBody(exceeded);
                return response;
            }
        }

        CardLimitValidateResponse body = new CardLimitValidateResponse();
        body.setMaxLimit(max.doubleValue());
        body.setAvailableLimit(available.doubleValue());
        body.setAvailableTranCount(null);
        response.setResponseCode(ResponseCode.SUCCESS);
        response.setResponseMessage(ResponseCode.getMessage(ResponseCode.SUCCESS));
        response.setResponseBody(body);
        return response;
    }

    /**
     * Read today's remaining from CARD_LIMIT_ACTUAL.
     * Matches authorize keys: CHANNEL_CODE + TRAN_CODE (tran defaults to channel).
     */
    private BigDecimal findTodayActualAvailable(String pan, Long channelCode, String tranCode) {
        if (pan == null || pan.isBlank() || channelCode == null) {
            return null;
        }
        String channelKey = String.valueOf(channelCode);
        String tranKey = (tranCode != null && !tranCode.isBlank()) ? tranCode.trim() : channelKey;
        LocalDate today = LocalDate.now();

        Optional<CardLimitActual> exact =
                cardLimitActualRepository.findByPanAndChannelCodeAndTranCode(pan.trim(), channelKey, tranKey);
        if (exact.isPresent() && isSameDayCycle(exact.get(), today)) {
            return exact.get().getAvailableLimit();
        }

        // Any same-day row for this channel (if profile used a different TRAN_CODE historically)
        List<CardLimitActual> byChannel =
                cardLimitActualRepository.findByPanAndChannelCode(pan.trim(), channelKey);
        for (CardLimitActual row : byChannel) {
            if (isSameDayCycle(row, today) && row.getAvailableLimit() != null) {
                return row.getAvailableLimit();
            }
        }
        return null;
    }

    private static boolean isSameDayCycle(CardLimitActual row, LocalDate today) {
        return row != null && row.getCycleBeginDate() != null && row.getCycleBeginDate().equals(today);
    }

    /** Simple channel mapping: 1=ATM, 2=POS, else ecommerce daily. */
    private BigDecimal pickChannelLimit(LimitProfile profile, Long channelCode) {
        if (channelCode != null && channelCode == 1L) {
            return profile.getAtmDailyAmount();
        }
        if (channelCode != null && channelCode == 2L) {
            return profile.getPosDailyAmount();
        }
        if (profile.getEcommerceDailyAmount() != null) {
            return profile.getEcommerceDailyAmount();
        }
        if (profile.getPosDailyAmount() != null) {
            return profile.getPosDailyAmount();
        }
        return profile.getAtmDailyAmount();
    }
}
