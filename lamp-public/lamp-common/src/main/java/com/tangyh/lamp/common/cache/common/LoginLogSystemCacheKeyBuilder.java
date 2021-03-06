package com.tangyh.lamp.common.cache.common;


import com.tangyh.basic.cache.model.CacheKeyBuilder;
import com.tangyh.lamp.common.cache.CacheKeyDefinition;

/**
 * εζ° KEY
 * {tenant}:LOGIN_LOG_SYSTEM -> long
 * <p>
 * #c_login_log
 *
 * @author zuihou
 * @date 2020/9/20 6:45 δΈε
 */
public class LoginLogSystemCacheKeyBuilder implements CacheKeyBuilder {
    @Override
    public String getPrefix() {
        return CacheKeyDefinition.LOGIN_LOG_SYSTEM;
    }

}
