package com.cms.dto.request;


public class ResponseCodeUpdateRequest {
    private String shortDescription;
    private String fullDescription;
    private String alertType;
    private String httpResponseCode;
    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public String getFullDescription() { return fullDescription; }
    public void setFullDescription(String fullDescription) { this.fullDescription = fullDescription; }
    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
    public String getHttpResponseCode() { return httpResponseCode; }
    public void setHttpResponseCode(String httpResponseCode) { this.httpResponseCode = httpResponseCode; }
}
