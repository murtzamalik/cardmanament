package com.cms.app.response;

import lombok.Data;

@Data
public class CardSpendingSummaryResponse {
    private String accountNumber;
    private String pan;
    private String cardTitle;
    private String expiryDate;
    private String cardStatusName;
    private Double maxLimit;
    private Double singleTranLimit;
    private Double dailyAvailableSpending;
    private Double monthlyAvailableSpending;
}
