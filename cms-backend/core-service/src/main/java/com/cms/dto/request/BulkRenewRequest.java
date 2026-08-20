package com.cms.dto.request;

import java.util.List;

public class BulkRenewRequest {

    private List<Long> cardIds;

    public List<Long> getCardIds() {
        return cardIds;
    }

    public void setCardIds(List<Long> cardIds) {
        this.cardIds = cardIds;
    }

    /**
     * Backward-compatible alias in case any caller still sends/uses "cardsIds".
     */
    public void setCardsIds(List<Long> cardsIds) {
        this.cardIds = cardsIds;
    }
}
