package com.cms.common.context;

import com.cms.common.dto.LogRequestAudit;

/**
 * Thread-local (or request-scoped) audit context for AddForAuditLogs.
 */
public final class AuditContext {

    private static final ThreadLocal<LogRequestAudit> currentAudit = new ThreadLocal<>();

    private AuditContext() {}

    public static void set(LogRequestAudit audit) {
        currentAudit.set(audit);
    }

    public static LogRequestAudit get() {
        return currentAudit.get();
    }

    public static void clear() {
        currentAudit.remove();
    }
}
