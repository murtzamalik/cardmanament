package com.cms.business.manager;

import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.common.service.ResponseHelperService;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CardGenerationManager extends AbstractManagerStub {
    public CardGenerationManager(ResponseHelperService responseHelper) { super(responseHelper); }

    @Override
    protected boolean dispatch(String methodName, IProcessMessage req, IProcessMessage res) {
        if ("GenerateCard".equals(methodName)) { res.setSuccess(true); responseHelper.setResponse(res, ResponseCodeEnum.Success); return true; }
        return super.dispatch(methodName, req, res);
    }
}
