package com.cms.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Central logging for system and activity. Can be extended to write to DB audit tables and ELK.
 */
@Service
public class ActivityLoggerService {

    private static final Logger log = LoggerFactory.getLogger(ActivityLoggerService.class);
    private static final String ACTION_CORE = "CoreFunction";

    public void systemLogInfo(String message, String actionType, String source, String detail) {
        log.info("[{}] {} | {} | {} | {}", actionType, source, message, detail != null ? detail : "", System.identityHashCode(this));
    }

    public void systemLogError(String message, String actionType, String source, String detail, Throwable ex) {
        if (ex != null) {
            log.error("[{}] {} | {} | {} | ", actionType, source, message, detail != null ? detail : "", ex);
        } else {
            log.error("[{}] {} | {} | {}", actionType, source, message, detail != null ? detail : "");
        }
    }

    public void systemLog(String level, String message, String actionType, String source, String detail, int status) {
        if ("ERROR".equalsIgnoreCase(level)) {
            systemLogError(message, actionType, source, detail, null);
        } else {
            systemLogInfo(message, actionType, source, detail);
        }
    }

    public void activityLog(String entityId, String loginId, String action, String methodName, String loggerName) {
        log.info("Activity | entityId={} loginId={} action={} method={} logger={}", entityId, loginId, action, methodName, loggerName);
    }
}
