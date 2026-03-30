package com.cms.service;

import com.cms.dto.request.CardSearchRequest;
import com.cms.dto.request.CardUpdateRequest;
import com.cms.dto.request.LinkCardAccountRequest;
import com.cms.dto.request.LinkCardAccountByCardIdRequest;
import com.cms.dto.response.*;

import java.util.List;

public interface CardService {

    List<CardResponse> getAllCards();

    PageResponse<CardResponse> searchCards(CardSearchRequest request);

    CardResponse getCardByPan(String pan);

    CardResponse getCardById(Long cardId);

    CardDropdownsResponse getDropdowns();

    List<AccountOptionResponse> getAvailableAccounts(String relationshipNum);

    void linkCardAccount(LinkCardAccountRequest request);

    void linkCardAccountByCardId(Long cardId, LinkCardAccountByCardIdRequest request);

    void delinkCardAccount(String pan, String accountNum);

    void delinkCardAccountByCardId(Long cardId, String accountNum);

    CardResponse updateCard(Long cardId, CardUpdateRequest request);

    /**
     * Set limit profile on card. Prefers limitProfileId when non-null; otherwise uses limitProfile (code).
     */
    void linkLimitProfile(Long cardId, Long limitProfileId, String limitProfile);

    List<CardAccountLinkResponse> getLinkedAccountsByCardId(Long cardId);

    List<CardAccountLinkResponse> getLinkedAccountsByPan(String pan);

    void closeCard(Long cardId);

    CustomerInfoResponse getCustomerByRelation(String rel, String linked, Long cardId);
}
