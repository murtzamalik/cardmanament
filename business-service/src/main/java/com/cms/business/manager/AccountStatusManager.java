package com.cms.business.manager;

import com.cms.common.bizprocess.IBusinessProcess;
import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.common.service.ActivityLoggerService;
import com.cms.common.service.ResponseHelperService;
import com.cms.dal.entity.AccountStatus;
import com.cms.dal.repository.AccountStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AccountStatusManager implements IBusinessProcess {

    private static final Logger log = LoggerFactory.getLogger(AccountStatusManager.class);

    private final AccountStatusRepository accountStatusRepository;
    private final ResponseHelperService responseHelper;
    private final ActivityLoggerService activityLogger;

    public AccountStatusManager(AccountStatusRepository accountStatusRepository,
                               ResponseHelperService responseHelper,
                               ActivityLoggerService activityLogger) {
        this.accountStatusRepository = accountStatusRepository;
        this.responseHelper = responseHelper;
        this.activityLogger = activityLogger;
    }

    @Override
    public boolean execute(String methodName, IProcessMessage requestMessage, IProcessMessage responseMessage) {
        try {
            responseMessage.setMsgData(requestMessage.getMsgData());
            responseMessage.setHeaders(requestMessage.getHeaders());
            responseMessage.setLoginId(requestMessage.getLoginId());
            responseMessage.setPermissionId(requestMessage.getPermissionId());
            responseMessage.setEntityId(requestMessage.getEntityId());
            responseMessage.setMachineName(requestMessage.getMachineName());

            return switch (methodName != null ? methodName : "") {
                case "CreateAccountStatus" -> createAccountStatus(requestMessage, responseMessage);
                case "UpdateAccountStatus" -> updateAccountStatus(requestMessage, responseMessage);
                case "SearchAccountStatus" -> searchAccountStatus(requestMessage, responseMessage);
                case "ValidateMappingId" -> validateMappingId(requestMessage, responseMessage);
                default -> {
                    log.warn("Unknown method: {}", methodName);
                    responseHelper.setResponse(responseMessage, ResponseCodeEnum.BadRequest, "Unknown method: " + methodName);
                    yield false;
                }
            };
        } catch (Exception e) {
            log.error("AccountStatusManager.execute failed for " + methodName, e);
            responseHelper.setResponse(responseMessage, ResponseCodeEnum.InternalServerError);
            responseMessage.setMessage("Failed: " + e.getMessage());
            return true; // step executed, result is failure
        }
    }

    @Transactional
    public boolean createAccountStatus(IProcessMessage tranMessage, IProcessMessage responseMessage) {
        String name = getStr(tranMessage, "ACCT_STATUS_NAME");
        if (name == null || name.isBlank()) {
            responseHelper.setResponse(responseMessage, ResponseCodeEnum.BadRequest, "ACCT_STATUS_NAME required");
            return true;
        }
        if (accountStatusRepository.existsByAcctStatusNameIgnoreCase(name)) {
            responseHelper.setResponse(responseMessage, ResponseCodeEnum.Conflict, "Account Status already exists");
            return true;
        }
        AccountStatus entity = new AccountStatus();
        entity.setAcctStatusCode("AS" + System.currentTimeMillis()); // TODO: use sequence when available
        entity.setAcctStatusName(name);
        entity.setDescription(getStr(tranMessage, "DESCRIPTION"));
        entity.setIsLinkingAllowed(toDecimal(tranMessage, "IS_LINKING_ALLOWED"));
        entity.setIsTranAllowed(toDecimal(tranMessage, "IS_TRAN_ALLOWED"));
        entity.setIsActive(toDecimal(tranMessage, "IS_ACTIVE"));
        entity.setCreatedBy(tranMessage.getLoginId());
        entity.setCreatedOn(LocalDateTime.now());
        entity.setUpdatedBy(tranMessage.getLoginId());
        entity.setUpdatedOn(LocalDateTime.now());
        accountStatusRepository.save(entity);
        responseMessage.setSuccess(true);
        responseMessage.setMessage("Account Status created successfully");
        responseMessage.setMsgObjData(entity);
        responseHelper.setResponse(responseMessage, ResponseCodeEnum.Success);
        return true;
    }

    @Transactional
    public boolean updateAccountStatus(IProcessMessage tranMessage, IProcessMessage responseMessage) {
        String code = getStr(tranMessage, "ACCT_STATUS_CODE");
        if (code == null || code.isBlank()) {
            responseHelper.setResponse(responseMessage, ResponseCodeEnum.BadRequest, "ACCT_STATUS_CODE required");
            return true;
        }
        Optional<AccountStatus> opt = accountStatusRepository.findByAcctStatusCode(code);
        if (opt.isEmpty()) {
            throw new IllegalStateException("Account status not found having code: " + code);
        }
        AccountStatus entity = opt.get();
        String name = getStr(tranMessage, "ACCT_STATUS_NAME");
        if (name != null && !name.isBlank()) {
            if (accountStatusRepository.existsByAcctStatusNameIgnoreCase(name) && !name.equalsIgnoreCase(entity.getAcctStatusName())) {
                responseHelper.setResponse(responseMessage, ResponseCodeEnum.Conflict, "Account Status already exists");
                return true;
            }
            entity.setAcctStatusName(name);
        }
        if (tranMessage.getMsgData().containsKey("DESCRIPTION")) entity.setDescription(getStr(tranMessage, "DESCRIPTION"));
        if (tranMessage.getMsgData().containsKey("IS_LINKING_ALLOWED")) entity.setIsLinkingAllowed(toDecimal(tranMessage, "IS_LINKING_ALLOWED"));
        if (tranMessage.getMsgData().containsKey("IS_TRAN_ALLOWED")) entity.setIsTranAllowed(toDecimal(tranMessage, "IS_TRAN_ALLOWED"));
        if (tranMessage.getMsgData().containsKey("IS_ACTIVE")) entity.setIsActive(toDecimal(tranMessage, "IS_ACTIVE"));
        entity.setUpdatedBy(tranMessage.getLoginId());
        entity.setUpdatedOn(LocalDateTime.now());
        accountStatusRepository.save(entity);
        responseMessage.setSuccess(true);
        responseMessage.setMessage("Account Status updated successfully");
        responseMessage.setMsgObjData(entity);
        responseHelper.setResponse(responseMessage, ResponseCodeEnum.Success);
        return true;
    }

    public boolean searchAccountStatus(IProcessMessage tranMessage, IProcessMessage responseMessage) {
        try {
            Map<String, Object> result = new HashMap<>();
            accountStatusRepository.findAllByOrderByAcctStatusCodeAsc().forEach(e -> result.put(e.getAcctStatusCode(), e));
            responseMessage.setMsgObjArray(Map.of("list", result));
            responseMessage.setSuccess(true);
            responseHelper.setResponse(responseMessage, ResponseCodeEnum.Success);
            return true;
        } catch (Exception e) {
            log.error("SearchAccountStatus failed", e);
            responseHelper.setResponse(responseMessage, ResponseCodeEnum.InternalServerError);
            return true;
        }
    }

    public boolean validateMappingId(IProcessMessage tranMessage, IProcessMessage responseMessage) {
        responseMessage.setSuccess(true);
        return true;
    }

    private static String getStr(IProcessMessage msg, String key) {
        Map<String, String> data = msg.getMsgData();
        return data != null ? data.get(key) : null;
    }

    private static BigDecimal toDecimal(IProcessMessage msg, String key) {
        String v = getStr(msg, key);
        if (v == null || v.isBlank()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(v);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
