package com.cms.business.manager;

import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.common.service.ResponseHelperService;
import org.springframework.stereotype.Service;

@Service
public class SetupDataManager extends AbstractManagerStub {
    public SetupDataManager(ResponseHelperService responseHelper) { super(responseHelper); }

    @Override
    protected boolean dispatch(String methodName, IProcessMessage req, IProcessMessage res) {
        if ("GetSetupData".equals(methodName)) { res.setSuccess(true); responseHelper.setResponse(res, ResponseCodeEnum.Success); return true; }
        return super.dispatch(methodName, req, res);
    }
}
