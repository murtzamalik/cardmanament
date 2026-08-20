package com.cms.common.enums;

/**
 * Card activation status (ported from old CardActivationStatus).
 */
public enum CardActivationStatus {
    Active("A"),
    Inactive("I");

    private final String code;

    CardActivationStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
