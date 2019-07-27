package com.fih.aiovpoint.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurationSelector;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisCacheConfig extends CachingConfigurationSelector {
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();  // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
        config = config.entryTtl(Duration.ofSeconds(60 * 60 * 24))    // 设置缓存的默认过期时间，也是使用Duration设置
                .disableCachingNullValues();     // 不缓存空值
        return new RedisCacheManager(RedisCacheWriter.nonLockingRedisCacheWriter(factory), config);
    }
}
