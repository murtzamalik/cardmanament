package com.cms.dal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "USM_PERMISSION")
public class UsmPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usm_permission_seq_gen")
    @SequenceGenerator(name = "usm_permission_seq_gen", sequenceName = "USM_PERMISSION_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PERMISSION_ID", length = 50)
    private String permissionId;

    @Column(name = "PER_PARENT_ID", length = 50)
    private String perParentId;

    @Column(name = "PERMISSION_NAME", length = 255)
    private String permissionName;

    @Column(name = "OTHER_LANG_NAME", length = 255)
    private String otherLangName;

    @Column(name = "PERMISSION_TYPE", length = 50)
    private String permissionType;

    @Column(name = "CAN_CREATE")
    private BigDecimal canCreate;

    @Column(name = "CAN_UPDATE")
    private BigDecimal canUpdate;

    @Column(name = "CAN_DELETE")
    private BigDecimal canDelete;

    @Column(name = "IS_AUDITING_ALLOWED")
    private BigDecimal isAuditingAllowed;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "FIELD_TYPE", length = 50)
    private String fieldType;

    @Column(name = "SEQUENCE")
    private Integer sequence;

    @Column(name = "LOCATION", length = 255)
    private String location;

    @Column(name = "VIEW_NAME", length = 255)
    private String viewName;

    @Column(name = "CSS_CLASS_NAME", length = 255)
    private String cssClassName;

    @Column(name = "TABLE_NAME", length = 255)
    private String tableName;

    @Column(name = "TRAN_CODE", length = 50)
    private String tranCode;

    @Column(name = "IS_VISIBLE")
    private Boolean isVisible;

    public String getPermissionId() { return permissionId; }
    public void setPermissionId(String permissionId) { this.permissionId = permissionId; }
    public String getPerParentId() { return perParentId; }
    public void setPerParentId(String perParentId) { this.perParentId = perParentId; }
    public String getPermissionName() { return permissionName; }
    public void setPermissionName(String permissionName) { this.permissionName = permissionName; }
    public String getOtherLangName() { return otherLangName; }
    public void setOtherLangName(String otherLangName) { this.otherLangName = otherLangName; }
    public String getPermissionType() { return permissionType; }
    public void setPermissionType(String permissionType) { this.permissionType = permissionType; }
    public BigDecimal getCanCreate() { return canCreate; }
    public void setCanCreate(BigDecimal canCreate) { this.canCreate = canCreate; }
    public BigDecimal getCanUpdate() { return canUpdate; }
    public void setCanUpdate(BigDecimal canUpdate) { this.canUpdate = canUpdate; }
    public BigDecimal getCanDelete() { return canDelete; }
    public void setCanDelete(BigDecimal canDelete) { this.canDelete = canDelete; }
    public BigDecimal getIsAuditingAllowed() { return isAuditingAllowed; }
    public void setIsAuditingAllowed(BigDecimal isAuditingAllowed) { this.isAuditingAllowed = isAuditingAllowed; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    public Integer getSequence() { return sequence; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getViewName() { return viewName; }
    public void setViewName(String viewName) { this.viewName = viewName; }
    public String getCssClassName() { return cssClassName; }
    public void setCssClassName(String cssClassName) { this.cssClassName = cssClassName; }
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public String getTranCode() { return tranCode; }
    public void setTranCode(String tranCode) { this.tranCode = tranCode; }
    public Boolean getIsVisible() { return isVisible; }
    public void setIsVisible(Boolean isVisible) { this.isVisible = isVisible; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
