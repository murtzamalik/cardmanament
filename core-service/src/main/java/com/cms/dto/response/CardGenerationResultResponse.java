package com.cms.dto.response;

public class CardGenerationResultResponse {
    private boolean success;
    private String message;
    private Long cardId;
    private String panMasked;
    /** Path to export file when approve-and-generate is used and export is enabled. */
    private String exportFilePath;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public String getPanMasked() { return panMasked; }
    public void setPanMasked(String panMasked) { this.panMasked = panMasked; }
    public String getExportFilePath() { return exportFilePath; }
    public void setExportFilePath(String exportFilePath) { this.exportFilePath = exportFilePath; }
}
