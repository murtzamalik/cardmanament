package com.cms.dto.request;


public class PasswordExpressionUpdateRequest {
    private String pwdExpName;
    private String pwdExpression;
    private String pwdExpDescription;
    public String getPwdExpName() { return pwdExpName; }
    public void setPwdExpName(String pwdExpName) { this.pwdExpName = pwdExpName; }
    public String getPwdExpression() { return pwdExpression; }
    public void setPwdExpression(String pwdExpression) { this.pwdExpression = pwdExpression; }
    public String getPwdExpDescription() { return pwdExpDescription; }
    public void setPwdExpDescription(String pwdExpDescription) { this.pwdExpDescription = pwdExpDescription; }
}
