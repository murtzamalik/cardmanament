package com.cms.business.manager;

import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.common.service.ResponseHelperService;
import com.cms.dal.entity.UsmGroup;
import com.cms.dal.entity.UsmUserGroup;
import com.cms.dal.repository.UsmGroupRepository;
import com.cms.dal.repository.UsmUserGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupManager extends AbstractManagerStub {

    private static final Logger log = LoggerFactory.getLogger(GroupManager.class);
    private final UsmGroupRepository usmGroupRepository;
    private final UsmUserGroupRepository usmUserGroupRepository;

    public GroupManager(ResponseHelperService responseHelper,
                        UsmGroupRepository usmGroupRepository,
                        UsmUserGroupRepository usmUserGroupRepository) {
        super(responseHelper);
        this.usmGroupRepository = usmGroupRepository;
        this.usmUserGroupRepository = usmUserGroupRepository;
    }

    @Override
    protected boolean dispatch(String methodName, IProcessMessage req, IProcessMessage res) {
        return switch (methodName != null ? methodName : "") {
            case "SearchGroup" -> searchGroup(req, res);
            case "GetGroupByID" -> getGroupByID(req, res);
            case "GetGroupUsers" -> getGroupUsers(req, res);
            default -> super.dispatch(methodName, req, res);
        };
    }

    private static String getStr(IProcessMessage msg, String key) {
        return msg.getMsgData() != null ? msg.getMsgData().get(key) : null;
    }

    private boolean searchGroup(IProcessMessage req, IProcessMessage res) {
        try {
            List<UsmGroup> list = usmGroupRepository.findAll().stream()
                .filter(g -> g.getWhenDeleted() == null)
                .toList();
            res.setMsgObjArray(java.util.Map.of("list", list));
            res.setSuccess(true);
            responseHelper.setResponse(res, ResponseCodeEnum.Success);
            return true;
        } catch (Exception e) {
            log.error("SearchGroup failed", e);
            responseHelper.setResponse(res, ResponseCodeEnum.InternalServerError);
            res.setMessage("Failed to get All groups.");
            return true;
        }
    }

    private boolean getGroupByID(IProcessMessage req, IProcessMessage res) {
        String groupId = getStr(req, "GROUP_ID");
        if (groupId == null || groupId.isBlank()) {
            responseHelper.setResponse(res, ResponseCodeEnum.BadRequest, "GROUP_ID required");
            return true;
        }
        try {
            UsmGroup group = usmGroupRepository.findByGroupId(groupId).orElse(null);
            if (group == null) {
                responseHelper.setResponse(res, ResponseCodeEnum.NotFound);
                return true;
            }
            res.setMsgObjArray(java.util.Map.of("group", group));
            res.setSuccess(true);
            responseHelper.setResponse(res, ResponseCodeEnum.Success);
            return true;
        } catch (Exception e) {
            log.error("GetGroupByID failed", e);
            responseHelper.setResponse(res, ResponseCodeEnum.InternalServerError);
            res.setMessage("Failed to get group by id.");
            return true;
        }
    }

    private boolean getGroupUsers(IProcessMessage req, IProcessMessage res) {
        String groupId = getStr(req, "GROUP_ID");
        if (groupId == null || groupId.isBlank()) {
            responseHelper.setResponse(res, ResponseCodeEnum.BadRequest, "GROUP_ID required");
            return true;
        }
        try {
            List<UsmUserGroup> userGroups = usmUserGroupRepository.findAll().stream()
                .filter(ug -> ug.getGroupId() != null && groupId.equals(ug.getGroupId()))
                .collect(Collectors.toList());
            res.setMsgObjArray(java.util.Map.of("list", userGroups));
            res.setSuccess(true);
            responseHelper.setResponse(res, ResponseCodeEnum.Success);
            return true;
        } catch (Exception e) {
            log.error("GetGroupUsers failed", e);
            responseHelper.setResponse(res, ResponseCodeEnum.InternalServerError);
            return true;
        }
    }
}
