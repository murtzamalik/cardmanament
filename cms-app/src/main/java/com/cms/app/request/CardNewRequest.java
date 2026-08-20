package com.cms.app.request;

import com.cms.app.validation.ProductCode;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CardNewRequest {

    @NotBlank
    private String cardTitle;

    @NotBlank
    @Length(min = 10, max = 10)
    private String accountNumber;

    @ProductCode
    private String productCode;

    @NotBlank
    private String cardType;

    @NotBlank
    @Length(min = 13, max = 13)
    private String relationshipNumber;

    @NotBlank
    private String requestTypeId;
}
