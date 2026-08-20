package com.cms.dto.request;

public class CardUpdateRequest {
    private Long cardStatusId;
    private String cardStatusCode;
    private Long limitProfileId;
    private String limitProfile;
    private String cardTitle;

    public Long getCardStatusId() { return cardStatusId; }
    public void setCardStatusId(Long cardStatusId) { this.cardStatusId = cardStatusId; }
    public String getCardStatusCode() { return cardStatusCode; }
    public void setCardStatusCode(String cardStatusCode) { this.cardStatusCode = cardStatusCode; }
    public Long getLimitProfileId() { return limitProfileId; }
    public void setLimitProfileId(Long limitProfileId) { this.limitProfileId = limitProfileId; }
    public String getLimitProfile() { return limitProfile; }
    public void setLimitProfile(String limitProfile) { this.limitProfile = limitProfile; }
    public String getCardTitle() { return cardTitle; }
    public void setCardTitle(String cardTitle) { this.cardTitle = cardTitle; }
}
