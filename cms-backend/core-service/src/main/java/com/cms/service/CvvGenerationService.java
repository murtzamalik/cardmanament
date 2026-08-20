package com.cms.service;

/**
 * Generates CVV, CVV2, and ICVV for card issuance.
 * Implementations: dummy (for dev/test) or HSM (when available).
 */
public interface CvvGenerationService {

    /**
     * Generate raw CVV values for the given PAN and expiry.
     * Caller must Base64-encode before persisting (matches .NET behaviour).
     *
     * @param pan    clear PAN
     * @param expiryYyMm expiry as yyMM (e.g. "31" for 2031 March)
     * @return holder with cvv1, cvv2, icvv (raw strings)
     */
    CvvResult generate(String pan, String expiryYyMm);

    /** Result of CVV generation: raw CVV1, CVV2, ICVV. */
    record CvvResult(String cvv1, String cvv2, String icvv) {}
}
