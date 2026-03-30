package com.cms.business.manager;

import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.common.service.ResponseHelperService;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ExportManager extends AbstractManagerStub {
    public ExportManager(ResponseHelperService responseHelper) { super(responseHelper); }

    @Override
    protected boolean dispatch(String methodName, IProcessMessage req, IProcessMessage res) {
        if ("ExportCard".equals(methodName)) { res.setMsgObjArray(Collections.singletonMap("list", Collections.emptyList())); res.setSuccess(true); responseHelper.setResponse(res, ResponseCodeEnum.Success); return true; }
        return super.dispatch(methodName, req, res);
    }
}
