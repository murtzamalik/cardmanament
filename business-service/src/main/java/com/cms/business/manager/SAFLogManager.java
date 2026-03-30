package com.cms.business.manager;

import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.common.service.ResponseHelperService;
import com.cms.common.service.DataHelperService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SAFLogManager extends AbstractManagerStub {
    private final DataHelperService dataHelperService;

    public SAFLogManager(ResponseHelperService responseHelper, DataHelperService dataHelperService) {
        super(responseHelper);
        this.dataHelperService = dataHelperService;
    }

    @Override
    protected boolean dispatch(String methodName, IProcessMessage req, IProcessMessage res) {
        if ("ClearSAF".equals(methodName)) return clearSAF(req, res);
        if ("SearchSAFLog".equals(methodName)) { res.setMsgObjArray(Collections.singletonMap("list", Collections.emptyList())); res.setSuccess(true); responseHelper.setResponse(res, ResponseCodeEnum.Success); return true; }
        return super.dispatch(methodName, req, res);
    }

    private boolean clearSAF(IProcessMessage req, IProcessMessage res) {
        List<?> logIds = Collections.emptyList();
        Object objData = req.getMsgObjData();
        if (objData instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> map = (java.util.Map<String, Object>) objData;
            if (map.get("LOG_IDS") instanceof List) logIds = (List<?>) map.get("LOG_IDS");
        }
        if (logIds.isEmpty() && req.getMsgData() != null && req.getMsgData().get("LOG_IDS") != null) {
            String s = req.getMsgData().get("LOG_IDS");
            logIds = java.util.Arrays.asList(s.split("\\s*,\\s*"));
        }
        DataHelperService.validateInListSize(logIds);
        if (logIds.isEmpty()) { responseHelper.setResponse(res, ResponseCodeEnum.BadRequest, "LOG_IDS required"); return true; }
        try {
            dataHelperService.executeUpdate("UPDATE SAF_LOG SET IS_PROCESSED = 'Y' WHERE LOG_ID IN (:ids)", Collections.singletonMap("ids", logIds));
            res.setSuccess(true);
            responseHelper.setResponse(res, ResponseCodeEnum.Success);
            return true;
        } catch (Exception e) {
            log.error("ClearSAF failed", e);
            responseHelper.setResponse(res, ResponseCodeEnum.InternalServerError);
            return true;
        }
    }
}
