package com.cms.common.enums;

/**
 * Card production progress (ported from old ProgressFlag).
 */
public enum ProgressFlag {
    CARD_REQUEST_BATCH_CREATED(0),
    CARD_REQUEST_CREATED(1),
    SENT_FOR_APPROVAL(2),
    APPROVED(3),
    CARD_GENERATED(4),
    PIN_GENERATED(5),
    CARD_PRINTED(6),
    PIN_PRINTED(7),
    CARD_EXPORT_READY(8),
    CARD_EXPORTED(9),
    COMPLETED(10);

    private final int value;

    ProgressFlag(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
