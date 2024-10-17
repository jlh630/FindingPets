//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.config;

import com.easyarch.FindingPetsSys.mqtt.callback.PushCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmqxConfig {
    private static final Logger log = LoggerFactory.getLogger(EmqxConfig.class);
    @Autowired
    private PushCallback pushCallback;
    @Value("${mqtt.host}")
    private String host;
    @Value("${mqtt.clientID}")
    private String clientID;
    @Value("${mqtt.username}")
    private String username;
    @Value("${mqtt.password}")
    private String password;
    @Value("${mqtt.timeout}")
    private int timeout;
    @Value("${mqtt.topic}")
    private String topic;
    @Value("${mqtt.keepalive}")
    private int keepalive;

    public EmqxConfig() {
    }

    @Bean
    public MqttClient getMqttClient() throws MqttException {
        MqttClient client = new MqttClient(this.host, this.clientID, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setUserName(this.username);
        options.setPassword(this.password.toCharArray());
        options.setConnectionTimeout(this.timeout);
        options.setKeepAliveInterval(this.keepalive);
        client.setCallback(this.pushCallback);
        client.connect(options);
        String[] topics = this.topic.split(",");
        for (String topic : topics) {
            client.subscribe(topic);
        }
        return client;
    }
}
