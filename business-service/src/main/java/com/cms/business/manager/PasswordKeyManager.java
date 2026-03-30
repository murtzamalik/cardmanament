package com.cms.business.manager;

import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.common.service.ResponseHelperService;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class PasswordKeyManager extends AbstractManagerStub {
    public PasswordKeyManager(ResponseHelperService responseHelper) { super(responseHelper); }

    @Override
    protected boolean dispatch(String methodName, IProcessMessage req, IProcessMessage res) {
        if ("GetAllCryptoHistory".equals(methodName)) return getAllCryptoHistory(req, res);
        return super.dispatch(methodName, req, res);
    }

    private boolean getAllCryptoHistory(IProcessMessage req, IProcessMessage res) {
        res.setMsgObjArray(Collections.singletonMap("list", Collections.emptyList()));
        res.setSuccess(true);
        responseHelper.setResponse(res, ResponseCodeEnum.Success);
        return true;
    }
}
