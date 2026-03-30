package com.cms.business.manager;

import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.common.service.ResponseHelperService;
import com.cms.dal.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AccountManager extends AbstractManagerStub {
    private final AccountRepository accountRepository;

    public AccountManager(ResponseHelperService responseHelper, AccountRepository accountRepository) {
        super(responseHelper);
        this.accountRepository = accountRepository;
    }

    @Override
    protected boolean dispatch(String methodName, IProcessMessage req, IProcessMessage res) {
        if ("SearchAccount".equals(methodName)) { res.setMsgObjArray(Collections.singletonMap("list", accountRepository.findAll())); res.setSuccess(true); responseHelper.setResponse(res, ResponseCodeEnum.Success); return true; }
        return super.dispatch(methodName, req, res);
    }
}
