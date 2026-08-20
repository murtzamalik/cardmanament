package com.cms.app.response;

import lombok.Data;

@Data
public class CardLimitValidateResponse {
    private Double availableLimit;
    private Long availableTranCount;
    private Double maxLimit;
}
