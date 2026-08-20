package com.cms.app.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CardSpendingSummaryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Legacy CMS spending summary: CARD_ACCOUNT + CARD + STATUS + CARD_LIMIT_PROFILE + CARD_LIMIT_ACTUAL.
     * Join profile via TO_CHAR(NVL(LIMIT_PROFILE_ID, LIMIT_PROFILE)) = PROFILE_ID.
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findSpendingSummary(String key, String channelCode, String tranCode) {
        String sql = """
                SELECT CA.ACCOUNT_NUM,
                       C.PAN,
                       C.CARD_TITLE,
                       C.EXPIRY_DATE,
                       S.CARD_STATUS_NAME,
                       L.MAX_LIMIT,
                       L.SINGLE_TRAN_LIMIT,
                       CASE
                           WHEN A.CYCLE_BEGIN_DATE IS NULL THEN 0
                           WHEN TO_CHAR(A.CYCLE_BEGIN_DATE, 'DD-MM-YYYY') <> TO_CHAR(SYSDATE, 'DD-MM-YYYY') THEN 0
                           ELSE NVL(A.AVAILABLE_LIMIT, 0)
                       END AS DAILY_AVAILABLE_SPENDING,
                       CASE
                           WHEN A.CYCLE_BEGIN_DATE IS NULL THEN 0
                           WHEN TO_CHAR(A.CYCLE_BEGIN_DATE, 'MM-YYYY') <> TO_CHAR(SYSDATE, 'MM-YYYY') THEN 0
                           ELSE NVL(A.AVAILABLE_LIMIT, 0)
                       END AS MONTHLY_AVAILABLE_SPENDING
                FROM CARD_ACCOUNT CA
                INNER JOIN CARD C ON CA.PAN = C.PAN
                INNER JOIN CARD_STATUS S ON C.CARD_STATUS_CODE = S.CARD_STATUS_CODE
                INNER JOIN CARD_LIMIT_PROFILE L
                    ON TO_CHAR(NVL(C.LIMIT_PROFILE_ID, C.LIMIT_PROFILE)) = L.PROFILE_ID
                LEFT JOIN CARD_LIMIT_ACTUAL A
                    ON C.PAN = A.PAN
                   AND A.CHANNEL_CODE = L.CHANNEL_CODE
                   AND A.TRAN_CODE = L.TRAN_CODE
                WHERE (CA.ACCOUNT_NUM = :key OR C.PAN = :key)
                  AND (:channelCode IS NULL OR L.CHANNEL_CODE = :channelCode)
                  AND (:tranCode IS NULL OR L.TRAN_CODE = :tranCode)
                  AND (
                        :channelCode IS NOT NULL
                     OR L.IS_DEFAULT = 1
                     OR NOT EXISTS (
                            SELECT 1 FROM CARD_LIMIT_PROFILE L2
                            WHERE L2.PROFILE_ID = L.PROFILE_ID
                              AND L2.IS_DEFAULT = 1
                        )
                      )
                """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("key", key);
        query.setParameter("channelCode", channelCode);
        query.setParameter("tranCode", tranCode);
        return query.getResultList();
    }
}
