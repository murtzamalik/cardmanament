package com.cms.app.request;

import lombok.Data;

@Data
public class CardAvailableLimitRequest {
    private String pan;
    private Long channelCode;
    private Long tranCode;
}
