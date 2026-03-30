package com.cms.dto.response;


public class PasswordExpressionResponse {
    private Long id;
    private String pwdExpId;
    private String pwdExpName;
    private String pwdExpression;
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
