package com.cms.app.request;

import com.cms.app.validation.CardStatusCode;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CardUpdateStatusRequest {
    @NotBlank
    private String pan;

    @CardStatusCode
    private String statusCode;
}
