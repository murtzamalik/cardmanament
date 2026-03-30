package com.cms.dal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "USM_GROUP_PERMISSION")
public class UsmGroupPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usm_group_permission_seq_gen")
    @SequenceGenerator(name = "usm_group_permission_seq_gen", sequenceName = "USM_GROUP_PERMISSION_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "GROUP_ID", length = 50)
    private String groupId;

    @Column(name = "PERMISSION_ID", length = 50)
    private String permissionId;

    @Column(name = "CAN_CREATE")
    private BigDecimal canCreate;

    @Column(name = "IS_AUDITING_ALLOWED")
    private Boolean isAuditingAllowed;

    @Column(name = "CAN_UPDATE")
    private BigDecimal canUpdate;

    @Column(name = "CAN_DELETE")
    private BigDecimal canDelete;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "WINDOWS_UPDATED_BY", length = 50)
    private String windowsUpdatedBy;

    @Column(name = "WINDOWS_CREATED_BY", length = 50)
    private String windowsCreatedBy;

    @Column(name = "MACHINE_NAME", length = 255)
    private String machineName;

    @Column(name = "SEQUENCE")
    private Integer sequence;

    @Column(name = "APP_ID", length = 50)
    private String appId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getPermissionId() { return permissionId; }
    public void setPermissionId(String permissionId) { this.permissionId = permissionId; }
    public BigDecimal getCanCreate() { return canCreate; }
    public void setCanCreate(BigDecimal canCreate) { this.canCreate = canCreate; }
    public Boolean getIsAuditingAllowed() { return isAuditingAllowed; }
    public void setIsAuditingAllowed(Boolean isAuditingAllowed) { this.isAuditingAllowed = isAuditingAllowed; }
    public BigDecimal getCanUpdate() { return canUpdate; }
    public void setCanUpdate(BigDecimal canUpdate) { this.canUpdate = canUpdate; }
    public BigDecimal getCanDelete() { return canDelete; }
    public void setCanDelete(BigDecimal canDelete) { this.canDelete = canDelete; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public String getWindowsUpdatedBy() { return windowsUpdatedBy; }
    public void setWindowsUpdatedBy(String windowsUpdatedBy) { this.windowsUpdatedBy = windowsUpdatedBy; }
    public String getWindowsCreatedBy() { return windowsCreatedBy; }
    public void setWindowsCreatedBy(String windowsCreatedBy) { this.windowsCreatedBy = windowsCreatedBy; }
    public String getMachineName() { return machineName; }
    public void setMachineName(String machineName) { this.machineName = machineName; }
    public Integer getSequence() { return sequence; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
}
