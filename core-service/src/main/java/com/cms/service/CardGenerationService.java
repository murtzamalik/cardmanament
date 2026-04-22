package com.cms.service;

import com.cms.dto.request.CardGenerationRequestByCode;
import com.cms.dto.request.CardGenerationProcessRequest;
import com.cms.dto.response.CardRequestResponse;
import com.cms.dto.response.CardGenerationResultResponse;

import java.util.List;

public interface CardGenerationService {

    List<CardRequestResponse> getCardRequestByCode(String relationshipNum, String accountNum);

    CardGenerationResultResponse processNewCardGeneration(Long requestId);

    void updateCardRequestProgress(Long requestId, Integer progressFlag);

    /**
     * Approve request and generate card.
     */
    CardGenerationResultResponse approveAndGenerate(Long requestId);
}
