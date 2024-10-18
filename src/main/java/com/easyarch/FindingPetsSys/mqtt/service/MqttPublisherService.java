package com.easyarch.FindingPetsSys.mqtt.service;

import org.eclipse.paho.client.mqttv3.MqttException;

public interface MqttPublisherService {
     void publish(String topic, int qos,boolean retain,String message) throws MqttException;
}
