package com.cms.business.manager;

import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.common.service.ResponseHelperService;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ReportsManager extends AbstractManagerStub {
    public ReportsManager(ResponseHelperService responseHelper) { super(responseHelper); }

    @Override
    protected boolean dispatch(String methodName, IProcessMessage req, IProcessMessage res) {
        if ("RefreshReports".equals(methodName)) return refreshReports(req, res);
        return super.dispatch(methodName, req, res);
    }

    private boolean refreshReports(IProcessMessage req, IProcessMessage res) {
        res.setMsgObjArray(Collections.singletonMap("categories", Collections.emptyList()));
        res.setSuccess(true);
        responseHelper.setResponse(res, ResponseCodeEnum.Success);
        return true;
    }
}
