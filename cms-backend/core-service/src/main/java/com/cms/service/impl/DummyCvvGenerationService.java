package com.cms.service.impl;

import com.cms.service.CvvGenerationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Dummy CVV provider for development when HSM is not available.
 * Returns fixed values; do not use in production with real card data.
 */
@Service
@ConditionalOnProperty(name = "cms.card.dummy-hsm", havingValue = "true", matchIfMissing = true)
public class DummyCvvGenerationService implements CvvGenerationService {

    private static final String DUMMY_CVV1 = "111";
    private static final String DUMMY_CVV2 = "222";
    private static final String DUMMY_ICVV = "333";

    @Override
    public CvvResult generate(String pan, String expiryYyMm) {
        return new CvvResult(DUMMY_CVV1, DUMMY_CVV2, DUMMY_ICVV);
    }
}
