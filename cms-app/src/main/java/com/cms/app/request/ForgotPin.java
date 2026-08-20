package com.cms.app.request;

import lombok.Data;

@Data
public class ForgotPin {
    private String pan;
    private String relationshipNum;
    private String pin;
    private String confirmPin;
    private String flag;
}
