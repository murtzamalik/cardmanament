package com.cms.dal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "USM_PWD_EXPRESSION")
public class UsmPwdExpression {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usm_pwd_expression_seq_gen")
    @SequenceGenerator(name = "usm_pwd_expression_seq_gen", sequenceName = "USM_PWD_EXPRESSION_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PWD_EXP_ID", length = 50)
    private String pwdExpId;

    @Column(name = "PWD_EXP_NAME", length = 255)
    private String pwdExpName;

    @Column(name = "PWD_EXPRESSION", length = 500)
    private String pwdExpression;

    @Column(name = "PWD_EXP_DESCRIPTION", length = 500)
    private String pwdExpDescription;

    public String getPwdExpId() { return pwdExpId; }
    public void setPwdExpId(String pwdExpId) { this.pwdExpId = pwdExpId; }
    public String getPwdExpName() { return pwdExpName; }
    public void setPwdExpName(String pwdExpName) { this.pwdExpName = pwdExpName; }
    public String getPwdExpression() { return pwdExpression; }
    public void setPwdExpression(String pwdExpression) { this.pwdExpression = pwdExpression; }
    public String getPwdExpDescription() { return pwdExpDescription; }
    public void setPwdExpDescription(String pwdExpDescription) { this.pwdExpDescription = pwdExpDescription; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
