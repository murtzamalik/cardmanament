package com.cms.app.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePinRequest {
    @NotBlank
    private String pan;

    @NotBlank
    private String relationshipNum;

    @NotBlank
    private String oldPin;

    @NotBlank
    private String newPin;

    @NotBlank
    @JsonProperty("confirmNewPin")
    @JsonAlias({"ConfirmNewPin"})
    private String confirmNewPin;
}
