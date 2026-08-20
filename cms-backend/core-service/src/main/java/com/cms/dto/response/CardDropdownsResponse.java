package com.cms.dto.response;

import java.util.List;

public class CardDropdownsResponse {
    private List<BranchResponse> branches;
    private List<CardStatusItem> cardStatuses;
    private List<CardProductItem> cardProducts;
    private List<CardTypeItem> cardTypes;
    private List<LimitProfileItem> limitProfiles;

    public List<BranchResponse> getBranches() { return branches; }
    public void setBranches(List<BranchResponse> branches) { this.branches = branches; }
    public List<CardStatusItem> getCardStatuses() { return cardStatuses; }
    public void setCardStatuses(List<CardStatusItem> cardStatuses) { this.cardStatuses = cardStatuses; }
    public List<CardProductItem> getCardProducts() { return cardProducts; }
    public void setCardProducts(List<CardProductItem> cardProducts) { this.cardProducts = cardProducts; }
    public List<CardTypeItem> getCardTypes() { return cardTypes; }
    public void setCardTypes(List<CardTypeItem> cardTypes) { this.cardTypes = cardTypes; }
    public List<LimitProfileItem> getLimitProfiles() { return limitProfiles; }
    public void setLimitProfiles(List<LimitProfileItem> limitProfiles) { this.limitProfiles = limitProfiles; }

    public static class LimitProfileItem {
        private Long id;
        private String code;
        private String name;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class CardStatusItem {
        private Long id;
        private String code;
        private String name;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class CardProductItem {
        private Long id;
        private String code;
        private String name;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class CardTypeItem {
        private Long id;
        private String code;
        private String name;
        private String productCode;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getProductCode() { return productCode; }
        public void setProductCode(String productCode) { this.productCode = productCode; }
    }
}
