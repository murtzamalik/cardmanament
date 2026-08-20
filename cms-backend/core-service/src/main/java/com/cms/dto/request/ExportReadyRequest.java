package com.cms.dto.request;

public class ExportReadyRequest {
    private Long cardTypeId;
    private String cardTypeCode;

    public Long getCardTypeId() { return cardTypeId; }
    public void setCardTypeId(Long cardTypeId) { this.cardTypeId = cardTypeId; }
    public String getCardTypeCode() { return cardTypeCode; }
    public void setCardTypeCode(String cardTypeCode) { this.cardTypeCode = cardTypeCode; }
}
