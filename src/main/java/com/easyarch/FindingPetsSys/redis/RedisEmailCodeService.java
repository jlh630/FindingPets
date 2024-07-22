package com.easyarch.FindingPetsSys.redis;

public interface RedisEmailCodeService {
    String PREFIX = "FP::Code::";

    void expireEmailCode(String service,String email, String code);

    String queryEmailCode(String service,String email);
}
