package com.tangyh.lamp.common.cache.common;


import com.tangyh.basic.cache.model.CacheKey;
import com.tangyh.basic.cache.model.CacheKeyBuilder;
import com.tangyh.lamp.common.cache.CacheKeyDefinition;

/**
 * εζ° KEY
 * {tenant}:TOTAL_PV -> long
 *
 * @author zuihou
 * @date 2020/9/20 6:45 δΈε
 */
public class TotalPvCacheKeyBuilder implements CacheKeyBuilder {
    public static CacheKey build() {
        return new TotalPvCacheKeyBuilder().key();
    }

    @Override
    public String getPrefix() {
        return CacheKeyDefinition.TOTAL_PV;
    }
}
