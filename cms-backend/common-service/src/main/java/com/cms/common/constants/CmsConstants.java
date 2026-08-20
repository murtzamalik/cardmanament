package com.cms.common.constants;

/**
 * Centralized magic numbers and strings (ported from old config/constants).
 * Documented in migration-spec/CONSTANTS_AND_ENUMS.md.
 */
public final class CmsConstants {

    private CmsConstants() {}

    /** Default cache expiry in minutes (1 day). */
    public static final int DEFAULT_EXPIRY_MINUTES = 1440;

    /** Refresh time interval threshold for cache (minutes). */
    public static final int REFRESH_TIME_INTERVAL = 1440;

    /** Parent permission id for reports categories. */
    public static final String CATEGORIES_PARENT_PERMISSION = "reports";

    /** Default date format for display. */
    public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
}
