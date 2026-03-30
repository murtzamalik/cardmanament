package com.cms.business.manager;

import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.common.service.ResponseHelperService;
import com.cms.dal.entity.UsmPolicy;
import com.cms.dal.entity.UsmPwdExpression;
import com.cms.dal.repository.UsmPolicyRepository;
import com.cms.dal.repository.UsmPwdExpressionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PasswordPolicyManager extends AbstractManagerStub {

    private static final Logger log = LoggerFactory.getLogger(PasswordPolicyManager.class);
    private static final String DEFAULT_TIME_EXPRESSION = "y,y,y,y,y,y,y";

    private final UsmPolicyRepository usmPolicyRepository;
    private final UsmPwdExpressionRepository usmPwdExpressionRepository;

    public PasswordPolicyManager(ResponseHelperService responseHelper,
                                 UsmPolicyRepository usmPolicyRepository,
                                 UsmPwdExpressionRepository usmPwdExpressionRepository) {
        super(responseHelper);
        this.usmPolicyRepository = usmPolicyRepository;
        this.usmPwdExpressionRepository = usmPwdExpressionRepository;
    }

    @Override
    protected boolean dispatch(String methodName, IProcessMessage req, IProcessMessage res) {
        return switch (methodName != null ? methodName : "") {
            case "GetAllPasswordPolicies" -> getAllPasswordPolicies(req, res);
            case "GetPasswordPolicyById" -> getPasswordPolicyById(req, res);
            case "SavePasswordPolicy" -> savePasswordPolicy(req, res);
            case "UpdatePasswordPolicy" -> updatePasswordPolicy(req, res);
            case "SearchPasswordPolicy" -> searchPasswordPolicy(req, res);
            case "GetAllPasswordExpressions" -> getAllPasswordExpressions(req, res);
            default -> super.dispatch(methodName, req, res);
        };
    }

    private static String getStr(IProcessMessage msg, String key) {
        return msg.getMsgData() != null ? msg.getMsgData().get(key) : null;
    }

    private boolean getAllPasswordPolicies(IProcessMessage req, IProcessMessage res) {
        try {
            List<UsmPolicy> list = usmPolicyRepository.findAll();
            res.setMsgObjArray(java.util.Map.of("list", list));
            res.setSuccess(true);
            responseHelper.setResponse(res, ResponseCodeEnum.Success);
            return true;
        } catch (Exception e) {
            log.error("GetAllPasswordPolicies failed", e);
            responseHelper.setResponse(res, ResponseCodeEnum.InternalServerError);
            res.setMessage("Failed to get password policies.");
            return true;
        }
    }

    private boolean getPasswordPolicyById(IProcessMessage req, IProcessMessage res) {
        String policyId = getStr(req, "POLICY_ID");
        if (policyId == null || policyId.isBlank()) {
            responseHelper.setResponse(res, ResponseCodeEnum.BadRequest, "POLICY_ID required");
            return true;
        }
        try {
            UsmPolicy policy = usmPolicyRepository.findByPolicyId(policyId).orElse(null);
            if (policy == null) {
                responseHelper.setResponse(res, ResponseCodeEnum.NotFound);
                return true;
            }
            res.setMsgObjArray(java.util.Map.of("policy", policy));
            res.setSuccess(true);
            responseHelper.setResponse(res, ResponseCodeEnum.Success);
            return true;
        } catch (Exception e) {
            log.error("GetPasswordPolicyById failed", e);
            responseHelper.setResponse(res, ResponseCodeEnum.InternalServerError);
            return true;
        }
    }

    @Transactional
    private boolean savePasswordPolicy(IProcessMessage req, IProcessMessage res) {
        String policyId = getStr(req, "POLICY_ID");
        if (policyId == null || policyId.isBlank()) {
            responseHelper.setResponse(res, ResponseCodeEnum.BadRequest, "POLICY_ID required");
            return true;
        }
        try {
            if (usmPolicyRepository.findByPolicyId(policyId).isPresent()) {
                responseHelper.setResponse(res, ResponseCodeEnum.Conflict, "Policy ID already exists in database");
                return true;
            }
            UsmPolicy e = new UsmPolicy();
            e.setPolicyId(policyId);
            e.setPolicyName(getStr(req, "POLICY_NAME"));
            e.setPolicyDescription(getStr(req, "POLICY_DESCRIPTION"));
            String timeExpr = getStr(req, "TIME_EXPRESSION");
            e.setTimeExpression(timeExpr == null || timeExpr.isBlank() ? DEFAULT_TIME_EXPRESSION : timeExpr);
            e.setPwdExpiryPeriod(toBigDecimal(getStr(req, "PWD_EXPIRY_PERIOD")));
            e.setPwdRetryCount(toBigDecimal(getStr(req, "PWD_RETRY_COUNT")));
            e.setPwdHistoryCount(toBigDecimal(getStr(req, "PWD_HISTORY_COUNT")));
            e.setCreatedOn(LocalDateTime.now());
            e.setCreatedBy(req.getLoginId());
            e.setUpdatedOn(LocalDateTime.now());
            e.setUpdatedBy(req.getLoginId());
            usmPolicyRepository.save(e);
            res.setSuccess(true);
            responseHelper.setResponse(res, ResponseCodeEnum.Success);
            return true;
        } catch (Exception e) {
            log.error("SavePasswordPolicy failed", e);
            responseHelper.setResponse(res, ResponseCodeEnum.InternalServerError);
            return true;
        }
    }

    @Transactional
    private boolean updatePasswordPolicy(IProcessMessage req, IProcessMessage res) {
        String policyId = getStr(req, "POLICY_ID");
        if (policyId == null || policyId.isBlank()) {
            responseHelper.setResponse(res, ResponseCodeEnum.BadRequest, "POLICY_ID required");
            return true;
        }
        try {
            UsmPolicy e = usmPolicyRepository.findByPolicyId(policyId).orElse(null);
            if (e == null) {
                responseHelper.setResponse(res, ResponseCodeEnum.NotFound);
                return true;
            }
            if (getStr(req, "POLICY_NAME") != null) e.setPolicyName(getStr(req, "POLICY_NAME"));
            if (getStr(req, "TIME_EXPRESSION") != null) e.setTimeExpression(getStr(req, "TIME_EXPRESSION"));
            e.setUpdatedOn(LocalDateTime.now());
            e.setUpdatedBy(req.getLoginId());
            usmPolicyRepository.save(e);
            res.setSuccess(true);
            responseHelper.setResponse(res, ResponseCodeEnum.Success);
            return true;
        } catch (Exception ex) {
            log.error("UpdatePasswordPolicy failed", ex);
            responseHelper.setResponse(res, ResponseCodeEnum.InternalServerError);
            return true;
        }
    }

    private boolean searchPasswordPolicy(IProcessMessage req, IProcessMessage res) {
        try {
            List<UsmPolicy> list = usmPolicyRepository.findAll();
            res.setMsgObjArray(java.util.Map.of("list", list));
            res.setSuccess(true);
            responseHelper.setResponse(res, ResponseCodeEnum.Success);
            return true;
        } catch (Exception e) {
            log.error("SearchPasswordPolicy failed", e);
            responseHelper.setResponse(res, ResponseCodeEnum.InternalServerError);
            return true;
        }
    }

    private boolean getAllPasswordExpressions(IProcessMessage req, IProcessMessage res) {
        try {
            List<UsmPwdExpression> list = usmPwdExpressionRepository.findAll();
            res.setMsgObjArray(java.util.Map.of("list", list));
            res.setSuccess(true);
            responseHelper.setResponse(res, ResponseCodeEnum.Success);
            return true;
        } catch (Exception e) {
            log.error("GetAllPasswordExpressions failed", e);
            responseHelper.setResponse(res, ResponseCodeEnum.InternalServerError);
            return true;
        }
    }

    private static BigDecimal toBigDecimal(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return new BigDecimal(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
