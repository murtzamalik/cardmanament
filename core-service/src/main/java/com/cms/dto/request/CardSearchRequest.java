package com.cms.dto.request;

public class CardSearchRequest {
    private String pan;
    private String relationshipNum;
    private Long branchId;
    private String branchCode;
    private Long cardStatusId;
    private String cardStatusCode;
    private String productCode;
    private Long cardTypeId;
    private String cardTypeCode;
    private java.time.LocalDate dateFrom;
    private java.time.LocalDate dateTo;
    private Integer page = 0;
    private Integer size = 20;
    private String sort = "createdOn";
    private String sortDir = "desc";

    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }
    public String getRelationshipNum() { return relationshipNum; }
    public void setRelationshipNum(String relationshipNum) { this.relationshipNum = relationshipNum; }
    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
    public Long getCardStatusId() { return cardStatusId; }
    public void setCardStatusId(Long cardStatusId) { this.cardStatusId = cardStatusId; }
    public String getCardStatusCode() { return cardStatusCode; }
    public void setCardStatusCode(String cardStatusCode) { this.cardStatusCode = cardStatusCode; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public Long getCardTypeId() { return cardTypeId; }
    public void setCardTypeId(Long cardTypeId) { this.cardTypeId = cardTypeId; }
    public String getCardTypeCode() { return cardTypeCode; }
    public void setCardTypeCode(String cardTypeCode) { this.cardTypeCode = cardTypeCode; }
    public java.time.LocalDate getDateFrom() { return dateFrom; }
    public void setDateFrom(java.time.LocalDate dateFrom) { this.dateFrom = dateFrom; }
    public java.time.LocalDate getDateTo() { return dateTo; }
    public void setDateTo(java.time.LocalDate dateTo) { this.dateTo = dateTo; }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }
    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
}
