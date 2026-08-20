package com.cms.app.config;

public final class JwtConstants {
    public static final String ALGORITHM = "AES";
    public static final String PADDING_CBC = "AES/CBC/PKCS5PADDING";
    public static final String AES_SECRET_KEY = "ais-567890123456789012345678-256";
    public static final String JWT_SUPRESS_WARNING = "java:S3329";
    public static final String IV_PARAM = "1234567890123456";

    private JwtConstants() {
    }
}
