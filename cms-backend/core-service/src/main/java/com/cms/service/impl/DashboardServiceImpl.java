package com.cms.service.impl;

import com.cms.dal.entity.Card;
import com.cms.dal.entity.CardRequest;
import com.cms.dal.repository.CardRepository;
import com.cms.dal.repository.CardRequestRepository;
import com.cms.dto.response.CardRequestResponse;
import com.cms.dto.response.DashboardExpiringCardResponse;
import com.cms.dto.response.DashboardSummaryResponse;
import com.cms.mapper.CardRequestMapper;
import com.cms.service.DashboardService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Karachi");
    private static final int QUEUE_LIMIT = 10;
    private static final int EXPIRING_LIMIT = 10;
    private static final int EXPIRY_DAYS = 30;

    private final CardRepository cardRepository;
    private final CardRequestRepository cardRequestRepository;
    private final CardRequestMapper cardRequestMapper;

    public DashboardServiceImpl(CardRepository cardRepository,
                                CardRequestRepository cardRequestRepository,
                                CardRequestMapper cardRequestMapper) {
        this.cardRepository = cardRepository;
        this.cardRequestRepository = cardRequestRepository;
        this.cardRequestMapper = cardRequestMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {
        LocalDate today = LocalDate.now(BUSINESS_ZONE);
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.plusDays(1).atStartOfDay();
        LocalDateTime expiryFrom = dayStart;
        LocalDateTime expiryTo = today.plusDays(EXPIRY_DAYS).atTime(23, 59, 59);

        DashboardSummaryResponse summary = new DashboardSummaryResponse();
        summary.setPendingApproval(cardRequestRepository.countByProgressFlag(0));
        summary.setOpenRequests(cardRequestRepository.countByIsProcessed(0));
        summary.setIssuedToday(cardRepository.countIssuedBetween(dayStart, dayEnd));
        summary.setExpiringIn30Days(cardRepository.countExpiringBetween(expiryFrom, expiryTo));
        summary.setHotCards(cardRepository.countHotCards());
        summary.setRequestsByStatus(buildRequestStatusMap());
        summary.setCardsByStatus(buildCardStatusMap());
        summary.setCheckerQueue(limitQueue(cardRequestRepository.findByProgressFlag(0)));
        summary.setMakerQueue(limitQueue(cardRequestRepository.findByIsProcessed(0)));
        summary.setExpiringSoon(mapExpiring(
                cardRepository.findExpiringBetween(expiryFrom, expiryTo, PageRequest.of(0, EXPIRING_LIMIT))));
        return summary;
    }

    private Map<String, Long> buildRequestStatusMap() {
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("PENDING", 0L);
        map.put("APPROVED", 0L);
        map.put("REJECTED", 0L);
        map.put("OTHER", 0L);
        for (Object[] row : cardRequestRepository.countGroupedByProgressFlag()) {
            Integer flag = row[0] != null ? ((Number) row[0]).intValue() : null;
            long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            if (flag == null) {
                map.merge("OTHER", count, Long::sum);
            } else if (flag == 0) {
                map.put("PENDING", count);
            } else if (flag == 1) {
                map.put("APPROVED", count);
            } else if (flag == -1) {
                map.put("REJECTED", count);
            } else {
                map.merge("OTHER", count, Long::sum);
            }
        }
        return map;
    }

    private Map<String, Long> buildCardStatusMap() {
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] row : cardRepository.countGroupedByStatus()) {
            String code = row[0] != null ? String.valueOf(row[0]).trim() : "UNKNOWN";
            if (code.isEmpty()) code = "UNKNOWN";
            long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            map.merge(code, count, Long::sum);
        }
        return map;
    }

    private List<CardRequestResponse> limitQueue(List<CardRequest> source) {
        if (source == null || source.isEmpty()) return List.of();
        return source.stream()
                .sorted(Comparator.comparing(CardRequest::getCreatedOn, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(QUEUE_LIMIT)
                .map(cardRequestMapper::toResponse)
                .collect(Collectors.toList());
    }

    private List<DashboardExpiringCardResponse> mapExpiring(List<Card> cards) {
        if (cards == null || cards.isEmpty()) return List.of();
        List<DashboardExpiringCardResponse> out = new ArrayList<>(cards.size());
        for (Card c : cards) {
            DashboardExpiringCardResponse item = new DashboardExpiringCardResponse();
            item.setCardId(c.getCardId());
            item.setPanLast4(c.getPanLast4());
            item.setRelationshipNum(c.getRelationshipNum());
            item.setCardTitle(c.getCardTitle());
            item.setExpiryDate(c.getExpiryDate());
            item.setCardStatusCode(c.getCardStatusCode());
            item.setBranchCode(c.getBranchCode());
            out.add(item);
        }
        return out;
    }
}
