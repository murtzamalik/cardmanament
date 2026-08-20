package com.cms.app.request;

import lombok.Data;

/**
 * Authorize / consume a daily spending amount for a card channel.
 * For testing via Postman: send pan, amount, channelCode (1=ATM, 2=POS, 3=Ecommerce).
 * tranCode defaults to the same value as channelCode when omitted.
 */
@Data
public class CardAuthorizeRequest {
    private String pan;
    /** AES-encrypted PIN (same as /card/validate and mobile app). */
    private String pin;
    /** Optional MM/YYYY or MM/YY — also checked against card expiry in DB. */
    private String expiryDate;
    /** 1=ATM, 2=POS, 3=Ecommerce */
    private Long channelCode;
    /**
     * Mostly same as channel for this app. Defaults to channelCode when null.
     * Also accepts 1/2/3 or ATM/POS/ECOM style codes for customized lookup.
     */
    private String tranCode;
    /** Transaction amount to authorize and deduct. */
    private String amount;
}
