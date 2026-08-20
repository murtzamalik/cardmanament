package com.cms.app.request;

import lombok.Data;

@Data
public class CardLimitValidateRequest {
    private String pan;
    private Long channelCode;
    private Long tranCode;
    private String amount;
}
