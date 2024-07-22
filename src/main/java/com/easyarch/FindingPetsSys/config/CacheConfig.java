//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

@Configuration
public class CacheConfig {
    @Autowired
    private RedisConnectionFactory connectionFactory;

    public CacheConfig() {
    }

    @Bean
    @Primary
    public RedisCacheManager cacheManager5M(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = this.instanceConfig(5L);
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(config).build();
    }

    @Bean
    public RedisCacheManager cacheManager2H(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = this.instanceConfig(120L);
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(config).build();
    }

    @Bean
    public RedisCacheManager cacheManager6H(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = this.instanceConfig(360L);
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(config).build();
    }

    private RedisCacheConfiguration instanceConfig(Long cacheTtl) {
        return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(cacheTtl)).serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer())).serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
}
