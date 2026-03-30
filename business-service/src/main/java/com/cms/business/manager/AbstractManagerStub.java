package com.cms.business.manager;

import com.cms.common.bizprocess.IBusinessProcess;
import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.common.service.ResponseHelperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base stub for managers that only need execute() dispatch. Override execute() or add method cases as BLRs are ported.
 */
public abstract class AbstractManagerStub implements IBusinessProcess {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final ResponseHelperService responseHelper;

    protected AbstractManagerStub(ResponseHelperService responseHelper) {
        this.responseHelper = responseHelper;
    }

    @Override
    public boolean execute(String methodName, IProcessMessage requestMessage, IProcessMessage responseMessage) {
        try {
            if (requestMessage != null && responseMessage != null) {
                responseMessage.setMsgData(requestMessage.getMsgData());
                responseMessage.setLoginId(requestMessage.getLoginId());
                responseMessage.setPermissionId(requestMessage.getPermissionId());
                responseMessage.setEntityId(requestMessage.getEntityId());
                responseMessage.setMachineName(requestMessage.getMachineName());
            }
            return dispatch(methodName, requestMessage, responseMessage);
        } catch (Exception e) {
            log.error("{} execute failed for {}", getClass().getSimpleName(), methodName, e);
            responseHelper.setResponse(responseMessage, ResponseCodeEnum.InternalServerError);
            return true;
        }
    }

    /** Override to add method dispatch. Default: set BadRequest and return false. */
    protected boolean dispatch(String methodName, IProcessMessage requestMessage, IProcessMessage responseMessage) {
        responseHelper.setResponse(responseMessage, ResponseCodeEnum.BadRequest, "Method not implemented: " + methodName);
        return false;
    }
}
