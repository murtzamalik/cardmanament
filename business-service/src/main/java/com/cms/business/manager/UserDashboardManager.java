package com.cms.business.manager;

import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.common.service.ResponseHelperService;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDashboardManager extends AbstractManagerStub {
    public UserDashboardManager(ResponseHelperService responseHelper) { super(responseHelper); }

    @Override
    protected boolean dispatch(String methodName, IProcessMessage req, IProcessMessage res) {
        if ("GetUserDashboardData".equals(methodName)) return getUserDashboardData(req, res);
        return super.dispatch(methodName, req, res);
    }

    private boolean getUserDashboardData(IProcessMessage req, IProcessMessage res) {
        res.setMsgObjArray(Collections.singletonMap("sessions", Collections.emptyList()));
        res.setSuccess(true);
        responseHelper.setResponse(res, ResponseCodeEnum.Success);
        return true;
    }
}
