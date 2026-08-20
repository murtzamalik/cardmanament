package com.cms.common.enums;

public enum MessageType {
    Undefined(0),
    Prepaid(1),
    Postpaid(2),
    // Add other message types as in legacy MesssageTypes.cs
    ;

    private final int value;

    MessageType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MessageType fromValue(int value) {
        for (MessageType mt : values()) {
            if (mt.value == value) return mt;
        }
        return Undefined;
    }
}
