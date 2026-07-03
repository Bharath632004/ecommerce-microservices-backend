package com.ecommerce.redis.config;

/**
 * Centralised cache name constants.
 * Use these in @Cacheable(CacheNames.PRODUCT) etc.
 */
public final class CacheNames {
    private CacheNames() {}

    public static final String OTP           = "otp";
    public static final String JWT_BLACKLIST  = "jwt:blacklist";
    public static final String USER_PROFILE  = "user:profile";
    public static final String PRODUCT        = "product";
    public static final String CART           = "cart";
    public static final String ORDER          = "order";
    public static final String INVENTORY      = "inventory";
    public static final String RATE_LIMIT     = "rate:limit";
    public static final String SESSION        = "session";
}
