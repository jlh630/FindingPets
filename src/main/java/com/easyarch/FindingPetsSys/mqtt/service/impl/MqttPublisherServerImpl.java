package com.easyarch.FindingPetsSys.mqtt.service.impl;

import com.easyarch.FindingPetsSys.mqtt.service.MqttPublisherService;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqttPublisherServerImpl implements MqttPublisherService {

    @Autowired
    private MqttClient mqttClient;
    @Override
    public void publish(String topic, int qos, boolean retain, String message) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(qos);
        mqttMessage.setRetained(retain);
        mqttClient.publish(topic, mqttMessage);
    }
}
