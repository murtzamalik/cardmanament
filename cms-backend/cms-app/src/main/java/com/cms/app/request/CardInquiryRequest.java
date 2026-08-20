package com.cms.app.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CardInquiryRequest {
    @NotNull
    private String relationshipNum;
}
