package com.cms.dto.request;

import java.util.List;

public class BulkExportRequest {
    private List<Long> cardIds;

    public List<Long> getCardIds() { return cardIds; }
    public void setCardIds(List<Long> cardIds) { this.cardIds = cardIds; }
}
