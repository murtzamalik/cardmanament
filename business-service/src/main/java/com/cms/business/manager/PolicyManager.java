package com.cms.business.manager;

import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.common.service.ResponseHelperService;
import com.cms.dal.repository.UsmPolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyManager extends AbstractManagerStub {

    private static final Logger log = LoggerFactory.getLogger(PolicyManager.class);
    private final UsmPolicyRepository usmPolicyRepository;

    public PolicyManager(ResponseHelperService responseHelper, UsmPolicyRepository usmPolicyRepository) {
        super(responseHelper);
        this.usmPolicyRepository = usmPolicyRepository;
    }

    @Override
    protected boolean dispatch(String methodName, IProcessMessage req, IProcessMessage res) {
        if ("GetAllPolicies".equals(methodName)) return getAllPolicies(req, res);
        return super.dispatch(methodName, req, res);
    }

    private boolean getAllPolicies(IProcessMessage req, IProcessMessage res) {
        try {
            List<?> list = usmPolicyRepository.findAll();
            res.setMsgObjArray(java.util.Map.of("list", list));
            res.setSuccess(true);
            responseHelper.setResponse(res, ResponseCodeEnum.Success);
            return true;
        } catch (Exception e) {
            log.error("GetAllPolicies failed", e);
            responseHelper.setResponse(res, ResponseCodeEnum.InternalServerError);
            res.setMessage("Failed to get All Policies.");
            return true;
        }
    }
}
