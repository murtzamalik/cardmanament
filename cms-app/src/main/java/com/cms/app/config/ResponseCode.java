package com.cms.app.config;

import java.util.HashMap;
import java.util.Map;

public final class ResponseCode {
    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;
    public static final int ERROR = 2;
    public static final int NO_DATA = 3;
    public static final int ALREADY_DATA = 4;
    public static final int CARD_STATUS_UPDATE_ERROR = 6;
    public static final int CARD_REQUEST_ALREADY_PENDING = 9;
    public static final int CARD_LIMIT_EXCEED = 10;
    public static final int CARD_IN_PROCESS = 13;
    public static final int CARD_EXPIRED = 14;
    public static final int PINS_NOT_MATCHED = 8;
    public static final int CARD_PIN_NOT_MATCHED = 12;
    public static final int CVV_NOT_MATCHED = 15;
    public static final int TRACK2_NOT_MATCHED = 16;
    public static final int PIN_NOT_AVAILABLE = 17;
    public static final int ACCOUNT_NOT_FOUND = 18;
    public static final int INVALID_PRODUCT = 19;
    public static final int INVALID_CARD_TYPE = 20;
    /** First-time Set PIN rejected because a PIN already exists. */
    public static final int PIN_ALREADY_SET = 21;
    public static final int INVALID_AMOUNT = 22;
    public static final int PIN_REQUIRED = 23;

    private static final Map<Integer, String> MESSAGE = new HashMap<>();

    static {
        MESSAGE.put(SUCCESS, "success");
        MESSAGE.put(FAILURE, "failure");
        MESSAGE.put(ERROR, "error");
        MESSAGE.put(NO_DATA, "no data found");
        MESSAGE.put(ALREADY_DATA, "already data found");
        MESSAGE.put(CARD_STATUS_UPDATE_ERROR, "Can not update status of HOT card");
        MESSAGE.put(CARD_REQUEST_ALREADY_PENDING, "Previous card request is already in pending status");
        MESSAGE.put(CARD_LIMIT_EXCEED, "Card limit exceeded");
        MESSAGE.put(CARD_IN_PROCESS, "Card already in process");
        MESSAGE.put(CARD_EXPIRED, "Card is expired");
        MESSAGE.put(PINS_NOT_MATCHED, "Pin and confirm pin does not match");
        MESSAGE.put(CARD_PIN_NOT_MATCHED, "Card pin does not match");
        MESSAGE.put(CVV_NOT_MATCHED, "Invalid cvv");
        MESSAGE.put(TRACK2_NOT_MATCHED, "invalid track2");
        MESSAGE.put(PIN_NOT_AVAILABLE, "PIN feature not available yet");
        MESSAGE.put(ACCOUNT_NOT_FOUND, "Account not found");
        MESSAGE.put(INVALID_PRODUCT, "Invalid product code");
        MESSAGE.put(INVALID_CARD_TYPE, "Invalid card type");
        MESSAGE.put(PIN_ALREADY_SET, "PIN already set. Use Change PIN or Forgot PIN");
        MESSAGE.put(INVALID_AMOUNT, "Invalid transaction amount");
        MESSAGE.put(PIN_REQUIRED, "PIN is required");
    }

    private ResponseCode() {
    }

    public static String getMessage(int code) {
        return MESSAGE.getOrDefault(code, "unknown");
    }
}
