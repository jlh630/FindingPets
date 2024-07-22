//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.redis;

public interface RedisTokenListService {
    //白名单前缀
    String PREFIX = "FP::W::";

    boolean existsTokenId(String var1);

    void setTokenId(String var1);

    void removeTokenId(String var1);
}
