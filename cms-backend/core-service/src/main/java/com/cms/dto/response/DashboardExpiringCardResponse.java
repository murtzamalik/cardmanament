package com.cms.dto.response;

import java.time.LocalDateTime;

public class DashboardExpiringCardResponse {
    private Long cardId;
    private String panLast4;
    private String relationshipNum;
    private String cardTitle;
    private LocalDateTime expiryDate;
    private String cardStatusCode;
    private String branchCode;

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public String getPanLast4() { return panLast4; }
    public void setPanLast4(String panLast4) { this.panLast4 = panLast4; }
    public String getRelationshipNum() { return relationshipNum; }
    public void setRelationshipNum(String relationshipNum) { this.relationshipNum = relationshipNum; }
    public String getCardTitle() { return cardTitle; }
    public void setCardTitle(String cardTitle) { this.cardTitle = cardTitle; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    public String getCardStatusCode() { return cardStatusCode; }
    public void setCardStatusCode(String cardStatusCode) { this.cardStatusCode = cardStatusCode; }
    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
}
