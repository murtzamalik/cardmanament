# Bureau Format Export File — Implementation Guide

## Overview
This document contains all the code needed to implement the bureau file format.
When ready, copy each method into the exact class and location mentioned.

---

## File to Modify
```
core-service/src/main/java/com/cms/service/CardExportFileService.java
```

---

## Step 1 — Update application.properties

Change the format value from `CSV` to `BUREAU`:

```properties
cms.card.export.format=BUREAU
```

---

## Step 2 — Add BUREAU condition in `generateExportFile()` method

### Location
Inside `generateExportFile()` method, after the existing `if ("CSV".equals(format)...)` block.

### Code to add
```java
// Rizwan — Bureau format block
if ("BUREAU".equals(format)) {
    String bureauFileName = baseName.endsWith(".txt") ? baseName : baseName + ".txt";
    Path bureauPath = dir.resolve(bureauFileName);
    writeBureauFormat(card, requestId, bureauPath);
    if (primaryPath == null) {
        primaryPath = bureauPath.toAbsolutePath().toString();
    }
}
```

---

## Step 3 — Add new method `writeBureauFormat()`

### Location
Inside `CardExportFileService` class, after the existing `writeCsv()` method.

### Code to add
```java
/**
 * Rizwan — Writes bureau feed file in fixed format required by card personalization bureau.
 * Format per line:
 * SeqNum(6) + " + PAN(4N 4N 4N 4N) + " + 4spaces + IssueYY + 5spaces + ExpiryMM/YY + 3spaces
 * + "*" + CardTitle + " + CardTitle!:PAN(4N format) + 1space + CVV2
 * + Track1 + Track2 + RelationshipNum(16) + iCVV
 */
private void writeBureauFormat(Card card, Long requestId, Path path) throws IOException {

    // --- Decode Base64 fields ---
    String track1 = card.getTrack1Data() != null
            ? new String(Base64.getDecoder().decode(card.getTrack1Data()), StandardCharsets.UTF_8) : "";
    String track2 = card.getTrack2Data() != null
            ? new String(Base64.getDecoder().decode(card.getTrack2Data()), StandardCharsets.UTF_8) : "";
    String cvv2 = card.getCvv2() != null
            ? new String(Base64.getDecoder().decode(card.getCvv2()), StandardCharsets.UTF_8) : "";
    String icvv = card.getIcvv() != null
            ? new String(Base64.getDecoder().decode(card.getIcvv()), StandardCharsets.UTF_8) : "";

    // --- Resolve PAN ---
    String pan = resolvePan(card);
    if (pan == null) pan = card.getPanLast4() != null ? "************" + card.getPanLast4() : "";

    // --- Format PAN as 4N 4N 4N 4N ---
    String panFormatted = formatPanWithSpaces(pan);

    // --- Issue Year (YY) ---
    String issueYear = card.getIssuedDate() != null
            ? card.getIssuedDate().format(DateTimeFormatter.ofPattern("yy")) : "";

    // --- Expiry MM/YY ---
    String expiryMMYY = card.getExpiryDate() != null
            ? card.getExpiryDate().format(DateTimeFormatter.ofPattern("MM/yy")) : "";

    // --- Card Title ---
    String cardTitle = card.getCardTitle() != null ? card.getCardTitle() : "";

    // --- Sequence Number (6 digits, padded) ---
    String seqNum = String.format("%06d", requestId);

    // --- Relationship Number (padded to 16 chars) ---
    String relNum = padRelationshipNum(card.getRelationshipNum());

    // --- Build the line ---
    String line = seqNum
            + "\""
            + panFormatted
            + "\""
            + "    "                          // 4 spaces
            + issueYear
            + "     "                         // 5 spaces
            + expiryMMYY
            + "   "                           // 3 spaces
            + "\"*\""
            + cardTitle
            + "\""
            + cardTitle + "!:" + panFormatted
            + " " + cvv2                      // 1 space then CVV2
            + track1
            + track2
            + relNum
            + icvv;

    Files.writeString(path, line + "\n", StandardCharsets.UTF_8);
}
```

---

## Step 4 — Add helper method `formatPanWithSpaces()`

### Location
Inside `CardExportFileService` class, after `writeBureauFormat()` method.

### Code to add
```java
/**
 * Rizwan — Formats PAN as 4N 4N 4N 4N (space every 4 digits).
 * Example: 4111111111111234 -> 4111 1111 1111 1234
 */
private String formatPanWithSpaces(String pan) {
    if (pan == null || pan.length() < 16) return pan != null ? pan : "";
    return pan.substring(0, 4) + " "
         + pan.substring(4, 8) + " "
         + pan.substring(8, 12) + " "
         + pan.substring(12, 16);
}
```

---

## Step 5 — Add helper method `padRelationshipNum()`

### Location
Inside `CardExportFileService` class, after `formatPanWithSpaces()` method.

### Code to add
```java
/**
 * Rizwan — Pads relationship number to 16 characters.
 * If numeric: left-pad with zeros. If alphanumeric: right-pad with spaces.
 * Example: "123456" -> "0000000000123456"
 */
private String padRelationshipNum(String rel) {
    if (rel == null || rel.isBlank()) return String.format("%16s", "").replace(' ', '0');
    rel = rel.trim();
    if (rel.matches("\\d+")) {
        // numeric — left pad with zeros
        return String.format("%016d", Long.parseLong(rel));
    }
    // alphanumeric — right pad with spaces to 16
    return String.format("%-16s", rel).substring(0, 16);
}
```

---

## Summary — What goes where

| Step | What | Where in file |
|------|------|---------------|
| 1 | Change `format=BUREAU` | `application.properties` |
| 2 | Add BUREAU condition | Inside `generateExportFile()` after CSV block |
| 3 | Add `writeBureauFormat()` | After `writeCsv()` method |
| 4 | Add `formatPanWithSpaces()` | After `writeBureauFormat()` method |
| 5 | Add `padRelationshipNum()` | After `formatPanWithSpaces()` method |

---

## Expected Output — One line per card

```
000001"4111 1111 1111 1234"    26     12/31   "*"JOHN DOE"JOHN DOE!:4111 1111 1111 1234 222%B4111111111111234^DOE/JOHN                  ^25122260000000000000000111000000?;4111111111111234=251222600000111000000?0000000000001234333
```

---

## How to test after implementation

1. Set `cms.card.export.format=BUREAU` in `application.properties`
2. Restart backend
3. Login to frontend → Card Production → New Card Request → Submit
4. Card Production → Card Requests → Approve & Generate
5. Open `core-service/export/` folder
6. Open the `.txt` file — verify the line matches the format above
