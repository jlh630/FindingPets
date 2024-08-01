package com.easyarch.FindingPetsSys.redis.impl;

import com.easyarch.FindingPetsSys.redis.RedisEmailCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisEmailCodeServiceImpl implements RedisEmailCodeService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * redis set&expire code
     *
     * @param email 邮箱
     * @param code  验证码
     */
    public void expireEmailCode(String service, String email, String code) {
        String key =PREFIX+service + "::" + email;
        redisTemplate.opsForValue().set(key, code,5L, TimeUnit.MINUTES);
//        redisTemplate.expire(key, 5L, TimeUnit.MINUTES);
        log.info("redis expire {} value{}!", key, code);
    }

    /**
     * 拿验证码
     *
     * @param email 邮箱
     * @return 验证码
     */
    public String queryEmailCode(String service, String email) {
        String key =PREFIX+service + "::" + email;
        return redisTemplate.opsForValue().get(key);
    }
}
