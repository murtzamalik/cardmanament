package com.cms.dto.response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardSummaryResponse {
    private long pendingApproval;
    private long openRequests;
    private long issuedToday;
    private long expiringIn30Days;
    private long hotCards;

    /** Keys: PENDING, APPROVED, REJECTED, OTHER */
    private Map<String, Long> requestsByStatus = new LinkedHashMap<>();
    /** Keys: status codes e.g. 001, 002, 003, HOT */
    private Map<String, Long> cardsByStatus = new LinkedHashMap<>();

    private List<CardRequestResponse> checkerQueue = new ArrayList<>();
    private List<CardRequestResponse> makerQueue = new ArrayList<>();
    private List<DashboardExpiringCardResponse> expiringSoon = new ArrayList<>();

    public long getPendingApproval() { return pendingApproval; }
    public void setPendingApproval(long pendingApproval) { this.pendingApproval = pendingApproval; }
    public long getOpenRequests() { return openRequests; }
    public void setOpenRequests(long openRequests) { this.openRequests = openRequests; }
    public long getIssuedToday() { return issuedToday; }
    public void setIssuedToday(long issuedToday) { this.issuedToday = issuedToday; }
    public long getExpiringIn30Days() { return expiringIn30Days; }
    public void setExpiringIn30Days(long expiringIn30Days) { this.expiringIn30Days = expiringIn30Days; }
    public long getHotCards() { return hotCards; }
    public void setHotCards(long hotCards) { this.hotCards = hotCards; }
    public Map<String, Long> getRequestsByStatus() { return requestsByStatus; }
    public void setRequestsByStatus(Map<String, Long> requestsByStatus) { this.requestsByStatus = requestsByStatus; }
    public Map<String, Long> getCardsByStatus() { return cardsByStatus; }
    public void setCardsByStatus(Map<String, Long> cardsByStatus) { this.cardsByStatus = cardsByStatus; }
    public List<CardRequestResponse> getCheckerQueue() { return checkerQueue; }
    public void setCheckerQueue(List<CardRequestResponse> checkerQueue) { this.checkerQueue = checkerQueue; }
    public List<CardRequestResponse> getMakerQueue() { return makerQueue; }
    public void setMakerQueue(List<CardRequestResponse> makerQueue) { this.makerQueue = makerQueue; }
    public List<DashboardExpiringCardResponse> getExpiringSoon() { return expiringSoon; }
    public void setExpiringSoon(List<DashboardExpiringCardResponse> expiringSoon) { this.expiringSoon = expiringSoon; }
}
