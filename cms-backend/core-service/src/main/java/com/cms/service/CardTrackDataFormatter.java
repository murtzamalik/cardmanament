package com.cms.service;

import org.springframework.stereotype.Component;

/**
 * Formats TRACK1_DATA and TRACK2_DATA in .NET-compatible form for card issuance.
 * Output strings should be Base64-encoded before persisting.
 */
@Component
public class CardTrackDataFormatter {

    private static final int TITLE_FIELD_LENGTH = 26;

    /**
     * Format track 2: ;{PAN}={expiryYyMm}22600000{cvv}00000?
     */
    public String formatTrack2(String pan, String expiryYyMm, String rawCvv) {
        if (pan == null) pan = "";
        if (expiryYyMm == null) expiryYyMm = "";
        if (rawCvv == null) rawCvv = "";
        return ";" + pan + "=" + expiryYyMm + "22600000" + rawCvv + "00000?";
    }

    /**
     * Format track 1: %B{PAN}^{titleFormatted}^{expiryYyMm}226000000000000000{cvv}000000?
     * Title is formatted via .NET SplitWithSplash (last name/first name, pad to 26 chars).
     */
    public String formatTrack1(String pan, String expiryYyMm, String cardTitle, String rawCvv) {
        if (pan == null) pan = "";
        if (expiryYyMm == null) expiryYyMm = "";
        if (rawCvv == null) rawCvv = "";
        String titleFormatted = formatTitleForTrack1(cardTitle);
        return "%B" + pan + "^" + titleFormatted + "^" + expiryYyMm + "226000000000000000" + rawCvv + "000000?";
    }

    /**
     * .NET SplitWithSplash: one word -> "/" + word; multiple -> "LastName/ FirstName " pad to 26.
     */
    public String formatTitleForTrack1(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return String.format("%-" + TITLE_FIELD_LENGTH + "s", "/");
        }
        try {
            String trimmed = fullName.trim();
            String[] arr = trimmed.split("\\s+");
            String out;
            if (arr.length == 1) {
                out = "/" + arr[0];
            } else {
                out = arr[arr.length - 1] + "/";
                for (int i = 0; i < arr.length - 1; i++) {
                    out = out + arr[i] + " ";
                }
            }
            if (out.length() >= TITLE_FIELD_LENGTH) {
                return out.substring(0, TITLE_FIELD_LENGTH);
            }
            return String.format("%-" + TITLE_FIELD_LENGTH + "s", out);
        } catch (Exception e) {
            if (fullName.length() > 25) {
                return "/" + fullName.substring(0, 25);
            }
            return "/" + fullName;
        }
    }
}
