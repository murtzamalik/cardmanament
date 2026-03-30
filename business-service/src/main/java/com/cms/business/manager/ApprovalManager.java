package com.cms.business.manager;

import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.common.service.ResponseHelperService;
import org.springframework.stereotype.Service;

@Service
public class ApprovalManager extends AbstractManagerStub {
    public ApprovalManager(ResponseHelperService responseHelper) { super(responseHelper); }

    @Override
    protected boolean dispatch(String methodName, IProcessMessage req, IProcessMessage res) {
        if ("InsertCheckerData".equals(methodName)) return insertCheckerData(req, res);
        return super.dispatch(methodName, req, res);
    }

    private boolean insertCheckerData(IProcessMessage req, IProcessMessage res) {
        if (req.getMsgObjData() == null) {
            responseHelper.setResponse(res, ResponseCodeEnum.BadRequest, "Nothing to Add");
            return true;
        }
        res.setSuccess(true);
        responseHelper.setResponse(res, ResponseCodeEnum.Success);
        return true;
    }
}
