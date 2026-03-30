package com.cms.business.manager;

import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.common.service.ResponseHelperService;
import com.cms.dal.repository.UsmPermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionManager extends AbstractManagerStub {

    private static final Logger log = LoggerFactory.getLogger(PermissionManager.class);
    private final UsmPermissionRepository usmPermissionRepository;

    public PermissionManager(ResponseHelperService responseHelper, UsmPermissionRepository usmPermissionRepository) {
        super(responseHelper);
        this.usmPermissionRepository = usmPermissionRepository;
    }

    @Override
    protected boolean dispatch(String methodName, IProcessMessage req, IProcessMessage res) {
        if ("GetAllPermissions".equals(methodName)) return getAllPermissions(req, res);
        return super.dispatch(methodName, req, res);
    }

    private boolean getAllPermissions(IProcessMessage req, IProcessMessage res) {
        try {
            List<?> list = usmPermissionRepository.findAll();
            res.setMsgObjArray(java.util.Map.of("list", list));
            res.setSuccess(true);
            responseHelper.setResponse(res, ResponseCodeEnum.Success);
            return true;
        } catch (Exception e) {
            log.error("GetAllPermissions failed", e);
            responseHelper.setResponse(res, ResponseCodeEnum.InternalServerError);
            res.setMessage("Failed to get All Permissions.");
            return true;
        }
    }
}
