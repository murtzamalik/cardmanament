package com.cms.common.service;

import com.cms.dal.entity.BizProcess;
import com.cms.dal.entity.BizProcessStates;
import com.cms.common.bizprocess.BizProcessKey;
import com.cms.common.bizprocess.BizProcessModel;
import com.cms.common.bizprocess.ProcessState;
import com.cms.common.enums.MessageType;
import com.cms.dal.repository.BizProcessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads BIZ_PROCESS and BIZ_PROCESS_STATES at startup and exposes getBizProcess(channelId, messageType).
 */
@Service
public class BizProcessConfigService {

    private static final Logger log = LoggerFactory.getLogger(BizProcessConfigService.class);

    private final BizProcessRepository bizProcessRepository;
    private final Map<BizProcessKey, BizProcessModel> processStatesConfig = new ConcurrentHashMap<>();

    public BizProcessConfigService(BizProcessRepository bizProcessRepository) {
        this.bizProcessRepository = bizProcessRepository;
    }

    @PostConstruct
    public void loadConfig() {
        try {
            log.info("Begin loading biz state config.");
            List<BizProcess> list = bizProcessRepository.findAllWithStates();
            list.stream()
                .sorted(Comparator.comparing(BizProcess::getBizProcessId))
                .forEach(bProc -> {
                    BizProcessModel model = new BizProcessModel();
                    model.setChannelId(bProc.getChannelId());
                    model.setMessageType(MessageType.fromValue(bProc.getMessageType() != null ? bProc.getMessageType() : 0));
                    model.setName(bProc.getBizProcessName());
                    model.setDescription(bProc.getDescription());
                    model.setActionType(bProc.getActionType() != null ? bProc.getActionType() : 0);
                    updateStates(model, bProc.getBizProcessStates());
                    BizProcessKey key = new BizProcessKey(bProc.getChannelId(), model.getMessageType());
                    processStatesConfig.put(key, model);
                });
            log.info("End loading biz state config. Loaded {} processes.", processStatesConfig.size());
        } catch (Exception e) {
            log.error("Failed to load biz state config.", e);
            throw new IllegalStateException("Failed to load biz state config.", e);
        }
    }

    private void updateStates(BizProcessModel model, List<BizProcessStates> states) {
        if (states == null) return;
        states.stream()
            .sorted(Comparator.comparing(BizProcessStates::getSequenceNumber))
            .forEach(bps -> {
                ProcessState ps = new ProcessState();
                ps.setClassName(bps.getClassName());
                ps.setMethodName(bps.getMethodName());
                ps.setSequenceNum(bps.getSequenceNumber());
                ps.setNextSeqNum_Success(bps.getNextSeqNumSuccess());
                ps.setNextSeqNum_Fail(bps.getNextSeqNumFail());
                model.getStateTable().put(bps.getSequenceNumber(), ps);
            });
    }

    public boolean getBizProcess(long channelId, MessageType messageType, BizProcessModel out) {
        BizProcessKey key = new BizProcessKey(channelId, messageType);
        BizProcessModel found = processStatesConfig.get(key);
        if (found == null) return false;
        out.setChannelId(found.getChannelId());
        out.setMessageType(found.getMessageType());
        out.setName(found.getName());
        out.setDescription(found.getDescription());
        out.setActionType(found.getActionType());
        out.getStateTable().clear();
        out.getStateTable().putAll(found.getStateTable());
        return true;
    }

    public BizProcessModel getBizProcess(long channelId, MessageType messageType) {
        BizProcessKey key = new BizProcessKey(channelId, messageType);
        return processStatesConfig.get(key);
    }
}
