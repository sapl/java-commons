package org.sapl.commons.utils;


import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;

import java.util.concurrent.TimeUnit;

public class CacheMgr {


    private static CacheMgr instance;
    private CacheManager cacheManager;

    public static synchronized CacheMgr getInstance() {
        if (instance == null) instance = new CacheMgr();
        return instance;
    }

    private CacheMgr() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
    }

    public <K, V> Cache<K, V> createCache(String name, Class<K> keyType, Class<V> valueType,
                                          int maxSize, long duration) {
        Cache<K, V> cache = cacheManager.getCache(name, keyType, valueType);
        if (cache != null) return cache;

        return cacheManager.createCache(name,
                CacheConfigurationBuilder.newCacheConfigurationBuilder(keyType, valueType,
                        ResourcePoolsBuilder.heap(maxSize))
                        .withExpiry(Expirations.timeToLiveExpiration(Duration.of(duration, TimeUnit.MILLISECONDS)))
                        .build());
    }


}
