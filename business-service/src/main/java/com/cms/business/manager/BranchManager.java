package com.cms.business.manager;

import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.common.service.ResponseHelperService;
import com.cms.dal.entity.Branch;
import com.cms.dal.repository.BranchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BranchManager extends AbstractManagerStub {

    private static final Logger log = LoggerFactory.getLogger(BranchManager.class);
    private final BranchRepository branchRepository;

    public BranchManager(ResponseHelperService responseHelper, BranchRepository branchRepository) {
        super(responseHelper);
        this.branchRepository = branchRepository;
    }

    @Override
    protected boolean dispatch(String methodName, IProcessMessage req, IProcessMessage res) {
        return switch (methodName != null ? methodName : "") {
            case "CreateBranch" -> createBranch(req, res);
            case "UpdateBranch" -> updateBranch(req, res);
            case "SearchBranch" -> searchBranch(req, res);
            default -> super.dispatch(methodName, req, res);
        };
    }

    private static String getStr(IProcessMessage msg, String key) {
        return msg.getMsgData() != null ? msg.getMsgData().get(key) : null;
    }

    @Transactional
    private boolean createBranch(IProcessMessage req, IProcessMessage res) {
        String code = getStr(req, "BRANCH_CODE");
        if (code == null || code.isBlank()) {
            responseHelper.setResponse(res, ResponseCodeEnum.BadRequest, "BRANCH_CODE required");
            return true;
        }
        if (branchRepository.findByBranchCode(code).isPresent()) {
            responseHelper.setResponse(res, ResponseCodeEnum.Conflict, "Branch already exists");
            return true;
        }
        Branch e = new Branch();
        e.setBranchCode(code);
        e.setBranchName(getStr(req, "BRANCH_NAME"));
        e.setEntityId(getStr(req, "ENTITY_ID"));
        e.setCityCode(getStr(req, "CITY_CODE"));
        e.setCountryCode(getStr(req, "COUNTRY_CODE"));
        e.setSwiftCode(getStr(req, "SWIFT_CODE"));
        e.setCurrencyCode(getStr(req, "CURRENCY_CODE"));
        e.setCreatedOn(LocalDateTime.now());
        e.setCreatedBy(req.getLoginId());
        e.setUpdatedOn(LocalDateTime.now());
        e.setUpdatedBy(req.getLoginId());
        branchRepository.save(e);
        res.setSuccess(true);
        responseHelper.setResponse(res, ResponseCodeEnum.Success);
        return true;
    }

    @Transactional
    private boolean updateBranch(IProcessMessage req, IProcessMessage res) {
        String code = getStr(req, "BRANCH_CODE");
        if (code == null || code.isBlank()) {
            responseHelper.setResponse(res, ResponseCodeEnum.BadRequest, "BRANCH_CODE required");
            return true;
        }
        Branch e = branchRepository.findByBranchCode(code).orElse(null);
        if (e == null) {
            responseHelper.setResponse(res, ResponseCodeEnum.NotFound);
            return true;
        }
        if (getStr(req, "BRANCH_NAME") != null) e.setBranchName(getStr(req, "BRANCH_NAME"));
        if (getStr(req, "CITY_CODE") != null) e.setCityCode(getStr(req, "CITY_CODE"));
        if (getStr(req, "COUNTRY_CODE") != null) e.setCountryCode(getStr(req, "COUNTRY_CODE"));
        e.setUpdatedOn(LocalDateTime.now());
        e.setUpdatedBy(req.getLoginId());
        branchRepository.save(e);
        res.setSuccess(true);
        responseHelper.setResponse(res, ResponseCodeEnum.Success);
        return true;
    }

    private boolean searchBranch(IProcessMessage req, IProcessMessage res) {
        try {
            List<Branch> list = branchRepository.findAll();
            res.setMsgObjArray(java.util.Map.of("list", list));
            res.setSuccess(true);
            responseHelper.setResponse(res, ResponseCodeEnum.Success);
            return true;
        } catch (Exception ex) {
            log.error("SearchBranch failed", ex);
            responseHelper.setResponse(res, ResponseCodeEnum.InternalServerError);
            return true;
        }
    }
}
