package com.cms.mapper;

import com.cms.dal.entity.UsmUser;
import com.cms.dto.response.UserResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    public UserResponse toResponse(UsmUser u) {
        if (u == null) return null;
        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setLoginId(u.getLoginId());
        r.setFullName(u.getFullName());
        r.setEmailAddress(u.getEmailAddress());
        r.setAppId(u.getAppId());
        r.setActive(u.getIsActive() != null && u.getIsActive().intValue() == 1);
        return r;
    }

    public List<UserResponse> toResponseList(List<UsmUser> list) {
        if (list == null) return null;
        List<UserResponse> out = new ArrayList<>();
        for (UsmUser e : list) out.add(toResponse(e));
        return out;
    }
}
