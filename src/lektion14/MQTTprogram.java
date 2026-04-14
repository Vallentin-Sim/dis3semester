package lektion14;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTprogram {

    public static void main(String[] args) throws InterruptedException {
        String broker = "tcp://192.168.0.115:1883";
        String topicPower = "cmnd/grp3297/Power1";
        String topicSensor = "tele/grp3297/SENSOR";

        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(
                    broker,
                    MqttClient.generateClientId(),
                    persistence
            );

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            // Giv callback adgang til client og power-topic
            sampleClient.setCallback(new SimpleMqttCallBack(sampleClient, topicPower));

            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");

            sampleClient.subscribe(topicSensor);
            System.out.println("Subscribed to: " + topicSensor);

            Thread.sleep(200000);

            sampleClient.disconnect();
            System.out.println("Disconnected");

            System.exit(0);

        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    public static void publishMessage(MqttClient sampleClient, String topicsend, String content)
            throws MqttPersistenceException, MqttException {

        MqttMessage message = new MqttMessage();
        message.setPayload(content.getBytes());
        sampleClient.publish(topicsend, message);

        System.out.println("Published '" + content + "' to " + topicsend);
    }
}