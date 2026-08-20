package com.cms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MenuCreateRequest {
    @NotBlank(message = "menuName is required")
    private String menuName;
    @NotBlank(message = "menuPath is required")
    private String menuPath;
    private Long parentMenuId;
    private String menuIcon;
    @NotNull(message = "sortOrder is required")
    private Integer sortOrder;
    private String status = "Y";

    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }
    public String getMenuPath() { return menuPath; }
    public void setMenuPath(String menuPath) { this.menuPath = menuPath; }
    public Long getParentMenuId() { return parentMenuId; }
    public void setParentMenuId(Long parentMenuId) { this.parentMenuId = parentMenuId; }
    public String getMenuIcon() { return menuIcon; }
    public void setMenuIcon(String menuIcon) { this.menuIcon = menuIcon; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
