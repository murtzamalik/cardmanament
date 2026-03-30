package com.cms.dto.response;

import java.time.LocalDateTime;

public class CardAccountLinkResponse {
    private String panMasked;
    private String accountNum;
    private String accountTitle;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private Integer isOverallDefault;
    private Integer isAcctTypeDefault;

    public String getPanMasked() { return panMasked; }
    public void setPanMasked(String panMasked) { this.panMasked = panMasked; }
    public String getAccountNum() { return accountNum; }
    public void setAccountNum(String accountNum) { this.accountNum = accountNum; }
    public String getAccountTitle() { return accountTitle; }
    public void setAccountTitle(String accountTitle) { this.accountTitle = accountTitle; }
    public LocalDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    public LocalDateTime getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDateTime effectiveTo) { this.effectiveTo = effectiveTo; }
    public Integer getIsOverallDefault() { return isOverallDefault; }
    public void setIsOverallDefault(Integer isOverallDefault) { this.isOverallDefault = isOverallDefault; }
    public Integer getIsAcctTypeDefault() { return isAcctTypeDefault; }
    public void setIsAcctTypeDefault(Integer isAcctTypeDefault) { this.isAcctTypeDefault = isAcctTypeDefault; }
}
