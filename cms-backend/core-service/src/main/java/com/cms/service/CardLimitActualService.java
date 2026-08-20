package com.cms.service;

import com.cms.dto.request.CardLimitActualCreateRequest;
import com.cms.dto.request.CardLimitActualUpdateRequest;
import com.cms.dto.response.CardLimitActualResponse;

import java.util.List;

public interface CardLimitActualService {

    CardLimitActualResponse create(CardLimitActualCreateRequest request);

    List<CardLimitActualResponse> findByPan(String pan);

    CardLimitActualResponse get(String pan, String channelCode, String tranCode);

    CardLimitActualResponse update(CardLimitActualUpdateRequest request);
}
