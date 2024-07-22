package com.easyarch.FindingPetsSys.email.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class handleMailFactory {
    private final Map<Integer, HandleMailStrategy> strategies = new HashMap<>();

    @Autowired
    GraphCaptchaStrategy graphCaptchaStrategy;
    @Autowired
    NotifyOrderStrategy notifyOrderStrategy;

    @PostConstruct
    private void init() {
        Integer GRAPH_CAPTCHA_HANDLE_MAIL_NUM = 1;
        Integer SIMPLE_HANDLE_MAIL_NUM = 2;
        strategies.put(GRAPH_CAPTCHA_HANDLE_MAIL_NUM, graphCaptchaStrategy);
        strategies.put(SIMPLE_HANDLE_MAIL_NUM, notifyOrderStrategy);

    }

    public HandleMailStrategy getStrategy(Integer type) {
        return strategies.get(type);
    }
}
