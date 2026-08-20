package com.cms.app.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardInquiryResponse {
    private String cardTitle;
    private String cardStatusCode;
    private String cardProdStatus;
    private String pan;
    private LocalDateTime createdOn;
    private String cardTypeName;
    private LocalDateTime expiryDate;
    private String cvv;
    private String cvv2;
    /** true when card already has a PIN stored (pinOffset). */
    private Boolean pinSet;
}
