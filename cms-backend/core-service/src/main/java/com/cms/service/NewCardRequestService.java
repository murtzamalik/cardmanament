package com.cms.service;

import com.cms.dto.request.NewCardRequestCreate;
import com.cms.dto.response.CardRequestResponse;
import com.cms.dto.response.CustomerInfoResponse;
import com.cms.dto.response.PageResponse;

import java.util.List;

public interface NewCardRequestService {

    CardRequestResponse create(NewCardRequestCreate request, String createdBy);

    void reject(Long requestId);

    List<CardRequestResponse> getCheckerList();

    List<CardRequestResponse> getMakerList();

    CardRequestResponse update(Long requestId, NewCardRequestCreate request);

    CustomerInfoResponse getCustomerInfo(String relationshipNum);

    PageResponse<CardRequestResponse> search(String relationshipNum, String branchCode, Integer isProcessed,
                                             String requestTypeId, Integer page, Integer size);

    CardRequestResponse getById(Long requestId);
}
