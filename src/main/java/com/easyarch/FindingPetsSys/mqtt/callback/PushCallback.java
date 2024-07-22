//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.mqtt.callback;

import com.easyarch.FindingPetsSys.mqtt.model.ConnectMessage;
import com.easyarch.FindingPetsSys.mqtt.model.LocationMessage;
import com.easyarch.FindingPetsSys.rocketmq.producer.RocketMQLocationProducerService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PushCallback implements MqttCallback {

    private MqttClient client;
    @Autowired
    private Gson gson;
    @Autowired
    private RocketMQLocationProducerService rocketMQLocationProducerService;

    public void connectionLost(Throwable throwable) {
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String messJson = new String(message.getPayload());

        try {
            if (topic.equals("client/status")) {
                ConnectMessage var4 = gson.fromJson(messJson, ConnectMessage.class);
            } else{
                //Location/{deviceId}
                Long deviceId = Long.parseLong(topic.split("/")[1]);
                LocationMessage messageBase = gson.fromJson(messJson, LocationMessage.class);
                this.rocketMQLocationProducerService.sendHandleLocationMessage(messageBase, deviceId);
            }

            JsonElement jsonElement = JsonParser.parseString(messJson);
            String compactJson = this.gson.toJson(jsonElement);
            log.info("Mqtt received a message from topic {} with {} content qos of {} as {}", topic, topic, message.getQos(), compactJson);
        } catch (Exception e) {
            log.warn("Mqtt received a message from {} but the content was incorrect ", topic);
        }

    }

    public void deliveryComplete(IMqttDeliveryToken token) {
            String logStr = "Mqtt {} send a message";
            if (token.isComplete()) {
                log.info(String.format(logStr, "successfully"));
            } else {
                log.warn(String.format(logStr, "failed to"));
            }
    }

    public PushCallback() {
    }
}
