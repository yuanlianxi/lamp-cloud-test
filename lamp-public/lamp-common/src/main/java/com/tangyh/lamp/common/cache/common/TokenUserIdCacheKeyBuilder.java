package com.tangyh.lamp.common.cache.common;


import com.tangyh.basic.cache.model.CacheKeyBuilder;
import com.tangyh.lamp.common.cache.CacheKeyDefinition;

/**
 * εζ° KEY
 * <p>
 * #c_application
 *
 * @author zuihou
 * @date 2020/9/20 6:45 δΈε
 */
public class TokenUserIdCacheKeyBuilder implements CacheKeyBuilder {
    @Override
    public String getPrefix() {
        return CacheKeyDefinition.TOKEN_USER_ID;
    }

//    @Override
//    public Duration getExpire() {
//        return Duration.ofMinutes(15);
//    }
}
