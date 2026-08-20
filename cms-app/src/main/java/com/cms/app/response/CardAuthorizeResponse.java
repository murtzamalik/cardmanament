package com.cms.app.response;

import lombok.Data;

@Data
public class CardAuthorizeResponse {
    private String pan;
    private String channelCode;
    private String tranCode;
    private Double amount;
    /** Daily ceiling used (customized if present, else profile/max). */
    private Double maxLimit;
    /** Remaining available after this transaction. */
    private Double availableLimit;
    private Boolean customizedApplied;
    private Boolean actualRowCreated;
}
