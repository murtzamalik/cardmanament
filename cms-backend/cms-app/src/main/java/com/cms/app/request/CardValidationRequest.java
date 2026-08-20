package com.cms.app.request;

import lombok.Data;

@Data
public class CardValidationRequest {
    private String pan;
    private String pin;
    private String cvv;
    private String track2;
}
