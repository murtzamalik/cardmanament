package com.cms.dto.request;

import jakarta.validation.constraints.NotNull;

public class RejectCardRequestRequest {
    @NotNull
    private Long requestId;

    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }
}
