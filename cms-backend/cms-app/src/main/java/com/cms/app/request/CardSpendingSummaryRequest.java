package com.cms.app.request;

import lombok.Data;

@Data
public class CardSpendingSummaryRequest {
    /** Account number (preferred). */
    private String accountNumber;
    /** PAN — used if accountNumber is blank, or as alternate match like legacy SQL. */
    private String pan;
    /** Optional: filter CARD_LIMIT_PROFILE / CARD_LIMIT_ACTUAL */
    private String channelCode;
    /** Optional: filter CARD_LIMIT_PROFILE / CARD_LIMIT_ACTUAL */
    private String tranCode;
}
