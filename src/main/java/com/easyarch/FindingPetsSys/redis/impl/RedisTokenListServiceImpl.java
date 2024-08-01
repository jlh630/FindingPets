//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.redis.impl;

import com.easyarch.FindingPetsSys.redis.RedisTokenListService;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisTokenListServiceImpl implements RedisTokenListService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * redis 是否存在tokenId
     *
     * @param jti tokenId
     * @return Y/N
     */
    public boolean existsTokenId(String jti) {
        log.info("redis find key：{} ", PREFIX + jti);
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + jti));
    }

    /**
     * redis set&expire tokenId
     *
     * @param jti tokenId
     */
    public void setTokenId(String jti) {
        //set
        redisTemplate.opsForValue().set(PREFIX + jti, "",120L, TimeUnit.MINUTES);
//        expire
//        redisTemplate.expire(PREFIX + jti, 120L, TimeUnit.MINUTES);
        log.info("redis expire {} success!", PREFIX + jti);
    }

    /**
     * redis 删除 tokenId
     *
     * @param jti tokenId
     */
    public void removeTokenId(String jti) {
        this.redisTemplate.delete(PREFIX + jti);
        log.info("redis delete {} success!", PREFIX + jti);
    }
}
