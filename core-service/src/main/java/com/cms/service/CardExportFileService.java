package com.cms.service;

import com.cms.dal.entity.Card;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64; // Rizwan Adds this line for format change
import java.util.Locale;

/**
 * Generates card export files (bureau feed) when a card is created via approve-and-generate.
 * PCI DSS: do not log full PAN; write to restricted output directory only.
 */
@Service
public class CardExportFileService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ROOT);

    private final String outputDir;
    private final String filenamePattern;
    private final boolean includePan;
    private final String format;
    private final String externalCommand;
    private final CardDataEncryptionService encryptionService;

    public CardExportFileService(
            @Value("${cms.card.export.output-dir:}") String outputDir,
            @Value("${cms.card.export.filename-pattern:card_export_{date}_{requestId}.txt}") String filenamePattern,
            @Value("${cms.card.export.include-pan:false}") boolean includePan,
            @Value("${cms.card.export.format:PAN_LIST}") String format,
            @Value("${cms.card.export.external-command:}") String externalCommand,
            CardDataEncryptionService encryptionService) {
        this.outputDir = outputDir != null ? outputDir.trim() : "";
        this.filenamePattern = filenamePattern != null ? filenamePattern : "card_export_{date}_{requestId}.txt";
        this.includePan = includePan;
        this.format = format != null ? format.toUpperCase(Locale.ROOT) : "PAN_LIST";
        this.externalCommand = externalCommand != null && !externalCommand.isBlank() ? externalCommand.trim() : "";
        this.encryptionService = encryptionService;
    }

    /**
     * Generates export file(s) for the given card. Returns the primary file path (PAN list or CSV), or null if export is disabled.
     */
    public String generateExportFile(Card card, Long requestId) {
        if (outputDir.isBlank()) {
            return null;
        }
        try {
            Path dir = Paths.get(outputDir);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            String date = LocalDateTime.now().format(DATE_FORMAT);
            String timestamp = String.valueOf(System.currentTimeMillis());
            String baseName = filenamePattern
                    .replace("{date}", date)
                    .replace("{requestId}", String.valueOf(requestId))
                    .replace("{timestamp}", timestamp);

            String panForFile = null;
            if (includePan || "PAN_LIST".equals(format) || "BOTH".equals(format)) {
                panForFile = resolvePan(card);
            }

            String primaryPath = null;

            if ("PAN_LIST".equals(format) || "BOTH".equals(format)) {
                String panListFileName = baseName.endsWith(".txt") ? baseName : baseName + ".txt";
                Path panListPath = dir.resolve(panListFileName);
                String content = formatPanListContent(panForFile, card);
                Files.writeString(panListPath, content, StandardCharsets.UTF_8);
                primaryPath = panListPath.toAbsolutePath().toString();
            }

            if ("CSV".equals(format) || "BOTH".equals(format)) {
                String csvFileName = baseName.endsWith(".csv") ? baseName : baseName.replace(".txt", ".csv");
                if (!"BOTH".equals(format)) {
                    csvFileName = baseName.endsWith(".csv") ? baseName : baseName + ".csv";
                }
                Path csvPath = dir.resolve(csvFileName);
                writeCsv(card, requestId, csvPath);
                if (primaryPath == null) {
                    primaryPath = csvPath.toAbsolutePath().toString();
                }
            }

            // Added this code for Bureau file in export folder
            if ("BUREAU".equals(format)) {
                String bureauFileName = baseName.endsWith(".txt") ? baseName : baseName + ".txt";
                Path bureauPath = dir.resolve(bureauFileName);
                writeBureauFormat(card, requestId, bureauPath);
                if (primaryPath == null) {
                    primaryPath = bureauPath.toAbsolutePath().toString();
                }
            }

            if (primaryPath != null && !externalCommand.isBlank()) {
                runExternalCommand(primaryPath);
            }

            return primaryPath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to write export file: " + e.getMessage(), e);
        }
    }

    private String resolvePan(Card card) {
        if (card.getPanEncrypted() != null && !card.getPanEncrypted().isBlank()) {
            return encryptionService.decrypt(card.getPanEncrypted());
        }
        return card.getPan();
    }

    /** .NET-style: 'pan1','pan2' or one PAN. If includePan false we write last4 only for safety (external exe may need PAN via config). */
    private String formatPanListContent(String pan, Card card) {
        if (pan != null && !pan.isBlank()) {
            return "'" + pan + "'";
        }
        String last4 = card.getPanLast4();
        if (last4 != null && !last4.isBlank()) {
            return "'****" + last4 + "'";
        }
        return "";
    }

private void writeCsv(Card card, Long requestId, Path path) throws IOException {
    String header = "request_id,card_id,pan_last4,card_title,product_code,card_type_code,branch_code,expiry_date,relationship_num,issued_date,activation_date,generated_at";
    String expiry = card.getExpiryDate() != null ? card.getExpiryDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "";
    String issued = card.getIssuedDate() != null ? card.getIssuedDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "";
    String activation = card.getActivationDate() != null ? card.getActivationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "";
    String last4 = card.getPanLast4() != null ? card.getPanLast4() : "";
    Long cardId = card.getCardId();
    String line = String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
            requestId,
            cardId != null ? cardId : "",
            escapeCsv(last4),
            escapeCsv(card.getCardTitle()),
            escapeCsv(card.getProductCode()),
            escapeCsv(card.getCardTypeCode()),
            escapeCsv(card.getBranchCode()),
            expiry,
            escapeCsv(card.getRelationshipNum()),
            issued,
            activation,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    Files.writeString(path, header + "\n" + line + "\n", StandardCharsets.UTF_8);
}

    private static String escapeCsv(String value) {        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // Added this method for format change of export file
    private void writeBureauFormat(Card card, Long requestId, Path path) throws IOException {
        String line = buildBureauLine(card, requestId);
        Files.writeString(path, line + "\n", StandardCharsets.UTF_8);
    }

    private String buildBureauLine(Card card, Long seqNum) {
        String track1 = card.getTrack1Data() != null ? new String(Base64.getDecoder().decode(card.getTrack1Data()), StandardCharsets.UTF_8) : "";
        String track2 = card.getTrack2Data() != null ? new String(Base64.getDecoder().decode(card.getTrack2Data()), StandardCharsets.UTF_8) : "";
        String cvv2 = card.getCvv2() != null ? new String(Base64.getDecoder().decode(card.getCvv2()), StandardCharsets.UTF_8) : "";
        String icvv = card.getIcvv() != null ? new String(Base64.getDecoder().decode(card.getIcvv()), StandardCharsets.UTF_8) : "";
        String pan = resolvePan(card);
        if (pan == null) pan = card.getPanLast4() != null ? "************" + card.getPanLast4() : "";
        String panFormatted = formatPanWithSpaces(pan);
        String issueYear = card.getIssuedDate() != null ? card.getIssuedDate().format(DateTimeFormatter.ofPattern("yy")) : "";
        String expiryMMYY = card.getExpiryDate() != null ? card.getExpiryDate().format(DateTimeFormatter.ofPattern("MM/yy")) : "";
        String cardTitle = card.getCardTitle() != null ? card.getCardTitle() : "";
        String seqFormatted = String.format("%06d", seqNum);
        String relNum = padRelationshipNum(card.getRelationshipNum());
        return seqFormatted
                + "\"" + panFormatted + "\""
                + "    "
                + issueYear
                + "     "
                + expiryMMYY
                + "   "
                + "\"*\"" + cardTitle + "\""
                + cardTitle + "!:" + panFormatted
                + " " + cvv2
                + "<"
                + track1
                + track2
                + relNum
                + icvv;
    }

    // Added this method for (Spaces in between primary account number , 4 digits then space etc)
    private String formatPanWithSpaces(String pan) {
        if (pan == null || pan.length() < 16) return pan != null ? pan : "";
        return pan.substring(0, 4) + " " + pan.substring(4, 8) + " " + pan.substring(8, 12) + " " + pan.substring(12, 16);
    }

    // Added this method for (it will gives Rel num into 16 digits if 0 there 16 zeros if 123 then 000000----0123 like this)
    private String padRelationshipNum(String rel) {
        if (rel == null || rel.isBlank()) return String.format("%16s", "").replace(' ', '0');
        rel = rel.trim();
        if (rel.matches("\\d+")) return String.format("%016d", Long.parseLong(rel));
        return String.format("%-16s", rel).substring(0, 16);
    }

    private void runExternalCommand(String filePath) {        try {
            ProcessBuilder pb = new ProcessBuilder(externalCommand, filePath);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            p.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("External export command interrupted", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to run external export command: " + e.getMessage(), e);
        }
    }

    /**
     * Validates that storedPath is under the configured output dir and returns an InputStream for download.
     * Returns empty if path is invalid or file does not exist (path traversal safe).
     */
    public Optional<InputStream> openExportFileForDownload(String storedPath) throws IOException {
        if (outputDir.isBlank() || storedPath == null || storedPath.isBlank()) return Optional.empty();
        Path base = Paths.get(outputDir).toAbsolutePath().normalize();
        Path resolved = Paths.get(storedPath).toAbsolutePath().normalize();
        if (!resolved.startsWith(base) || !Files.isRegularFile(resolved)) return Optional.empty();
        return Optional.of(Files.newInputStream(resolved));
    }

    /** Safe filename for Content-Disposition from stored path. */
    public String getExportFileName(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) return "card_export.txt";
        String name = Paths.get(storedPath).getFileName().toString();
        return name != null && !name.isBlank() ? name : "card_export.txt";
    }

    public String generateBulkExportFile(java.util.List<Card> cards) {
        if (outputDir.isBlank()) return null;
        try {
            Path dir = Paths.get(outputDir);
            if (!Files.exists(dir)) Files.createDirectories(dir);
            String date = LocalDateTime.now().format(DATE_FORMAT);
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = "bulk_export_" + date + "_" + timestamp + ".txt";
            Path filePath = dir.resolve(fileName);
            StringBuilder sb = new StringBuilder();
            int seq = 1;
            for (Card card : cards) {
                sb.append(buildBureauLine(card, (long) seq));
                sb.append("\n");
                seq++;
            }
            Files.writeString(filePath, sb.toString(), StandardCharsets.UTF_8);
            return filePath.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write bulk export file: " + e.getMessage(), e);
        }
    }

}
