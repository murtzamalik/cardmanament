package com.cms.service;

import com.cms.dal.entity.CardRequest;
import com.cms.dal.repository.CardRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CardGenerationScheduler {
    private static final Logger log = LoggerFactory.getLogger(CardGenerationScheduler.class);

    private final CardRequestRepository cardRequestRepository;
    private final CardGenerationService cardGenerationService;


    public CardGenerationScheduler(CardRequestRepository cardRequestRepository, CardGenerationService cardGenerationService) {
        this.cardRequestRepository = cardRequestRepository;
        this.cardGenerationService = cardGenerationService;
    }

    //@Scheduled(fixedDelay = 120000)
    @Scheduled(fixedDelay = 900000)
    public void processCardRequests() {
        List<CardRequest> pending = cardRequestRepository.findByIsProcessed(0);
        if (pending.isEmpty()) {
            log.info("Scheduler : No card requests found");
            return;
        }
        log.info("Scheduler : Found {} pending card requests.. Processing... ", pending.size() );

//        for (CardRequest req : pending){
//            try {
//                cardGenerationService.approveAndGenerate(req.getRequestId());
//                log.info("Scheduler : Card generated for requestId = {}", req.getRequestId());
//            }
//            catch (Exception e) {
//                log.error("Scheduler : Failed to generated card for requestId={}" , req.getRequestId(), e);
//
//            }
//        }
    }

}
