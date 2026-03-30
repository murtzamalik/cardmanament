package com.cms.business.manager;

import com.cms.common.bizprocess.IBusinessProcess;
import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.common.service.ResponseHelperService;
import com.cms.dal.entity.AccountType;
import com.cms.dal.repository.AccountTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountTypeManager implements IBusinessProcess {

    private static final Logger log = LoggerFactory.getLogger(AccountTypeManager.class);
    private final AccountTypeRepository accountTypeRepository;
    private final ResponseHelperService responseHelper;

    public AccountTypeManager(AccountTypeRepository accountTypeRepository, ResponseHelperService responseHelper) {
        this.accountTypeRepository = accountTypeRepository;
        this.responseHelper = responseHelper;
    }

    @Override
    public boolean execute(String methodName, IProcessMessage requestMessage, IProcessMessage responseMessage) {
        try {
            copyContext(requestMessage, responseMessage);
            return switch (methodName != null ? methodName : "") {
                case "CreateAccountType" -> createAccountType(requestMessage, responseMessage);
                case "UpdateAccountType" -> updateAccountType(requestMessage, responseMessage);
                case "SearchAccountType" -> searchAccountType(requestMessage, responseMessage);
                default -> { responseHelper.setResponse(responseMessage, ResponseCodeEnum.BadRequest, "Unknown method"); yield false; }
            };
        } catch (Exception e) {
            log.error("AccountTypeManager.execute failed", e);
            responseHelper.setResponse(responseMessage, ResponseCodeEnum.InternalServerError);
            return true;
        }
    }

    @Transactional
    public boolean createAccountType(IProcessMessage req, IProcessMessage res) {
        String code = getStr(req, "ACCT_TYPE_CODE");
        if (code == null || accountTypeRepository.findByAcctTypeCode(code).isPresent()) {
            responseHelper.setResponse(res, ResponseCodeEnum.Conflict, "Account type code exists");
            return true;
        }
        AccountType e = new AccountType();
        e.setAcctTypeCode(code);
        e.setAcctTypeName(getStr(req, "ACCT_TYPE_NAME"));
        accountTypeRepository.save(e);
        res.setSuccess(true);
        responseHelper.setResponse(res, ResponseCodeEnum.Success);
        return true;
    }

    @Transactional
    public boolean updateAccountType(IProcessMessage req, IProcessMessage res) {
        String code = getStr(req, "ACCT_TYPE_CODE");
        AccountType e = accountTypeRepository.findByAcctTypeCode(code).orElse(null);
        if (e == null) { responseHelper.setResponse(res, ResponseCodeEnum.NotFound); return true; }
        if (getStr(req, "ACCT_TYPE_NAME") != null) e.setAcctTypeName(getStr(req, "ACCT_TYPE_NAME"));
        accountTypeRepository.save(e);
        res.setSuccess(true);
        responseHelper.setResponse(res, ResponseCodeEnum.Success);
        return true;
    }

    public boolean searchAccountType(IProcessMessage req, IProcessMessage res) {
        List<AccountType> list = accountTypeRepository.findAll();
        res.setMsgObjArray(java.util.Map.of("list", list));
        res.setSuccess(true);
        responseHelper.setResponse(res, ResponseCodeEnum.Success);
        return true;
    }

    private void copyContext(IProcessMessage from, IProcessMessage to) {
        to.setMsgData(from.getMsgData());
        to.setLoginId(from.getLoginId());
        to.setPermissionId(from.getPermissionId());
        to.setEntityId(from.getEntityId());
        to.setMachineName(from.getMachineName());
    }

    private static String getStr(IProcessMessage msg, String key) {
        return msg.getMsgData() != null ? msg.getMsgData().get(key) : null;
    }
}
