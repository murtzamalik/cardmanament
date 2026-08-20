package com.cms.app.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWrapper<E> {
    private int responseCode;
    private String responseMessage;
    private E responseBody;
}
