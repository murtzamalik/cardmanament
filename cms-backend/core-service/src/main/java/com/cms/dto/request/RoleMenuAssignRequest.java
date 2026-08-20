package com.cms.dto.request;

import java.util.List;

public class RoleMenuAssignRequest {
    private List<Long> menuIds;

    public List<Long> getMenuIds() { return menuIds; }
    public void setMenuIds(List<Long> menuIds) { this.menuIds = menuIds; }
}
