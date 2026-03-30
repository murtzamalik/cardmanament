package com.cms.common.dto;

import java.io.Serializable;

public class CmsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private String shortDescription;
    private String fullDescription;
    private String alertType;
    private int httpCode;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public String getFullDescription() { return fullDescription; }
    public void setFullDescription(String fullDescription) { this.fullDescription = fullDescription; }
    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
    public int getHttpCode() { return httpCode; }
    public void setHttpCode(int httpCode) { this.httpCode = httpCode; }
}
