package com.cms.common.enums;

/**
 * Alert status codes (ported from old AlertStatus static dict).
 */
public enum AlertStatus {
    Open("1"),
    InProgress("2"),
    Closed("3"),
    OnHold("4"),
    Reopen("5");

    private final String code;

    AlertStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
