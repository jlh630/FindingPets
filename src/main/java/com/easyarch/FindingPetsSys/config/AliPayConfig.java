//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliPayConfig {
    @Value("${alipay.appId}")
    public String appId;
    @Value("${alipay.appPrivateKey}")
    public String appPrivateKey;
    @Value("${alipay.alipayPublicKey}")
    public String alipayPublicKey;
    @Value("${alipay.notifyUrl}")
    public String notifyUrl;
    @Value("${alipay.serverUrl}")
    public String serverUrl;

    public AliPayConfig() {
    }

    @Bean
    public AlipayClient getAliPayClient() throws AlipayApiException {
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(this.serverUrl);
        alipayConfig.setAppId(this.appId);
        alipayConfig.setPrivateKey(this.appPrivateKey);
        alipayConfig.setAlipayPublicKey(this.alipayPublicKey);
        alipayConfig.setFormat("json");
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        return new DefaultAlipayClient(alipayConfig);
    }
}
