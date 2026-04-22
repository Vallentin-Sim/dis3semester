package lektion14;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class SimpleMqttCallBack implements MqttCallback {

    private final MqttClient client;
    private final String topicPower;

    private final String contentON = "1";
    private final String contentOFF = "0";

    private int status = 0; // 0 = slukket, 1 = tændt
    private final double humidityLimit = 70.0;

    public SimpleMqttCallBack(MqttClient client, String topicPower) {
        this.client = client;
        this.topicPower = topicPower;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection to MQTT broker lost!");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String res = new String(mqttMessage.getPayload());
        System.out.println("Received: " + res);

        JSONObject jo = new JSONObject(res);
        JSONObject jo2 = jo.getJSONObject("AM2301");

        double temperature = jo2.getDouble("Temperature");
        double humidity = jo2.getDouble("Humidity");

        System.out.println("Temp: " + temperature);
        System.out.println("Humidity: " + humidity);

        // Tænd blæser hvis fugtighed >= grænse
        if (humidity >= humidityLimit && status == 0) {
            MQTTprogram.publishMessage(client, topicPower, contentON);
            status = 1;
            System.out.println("Fan turned ON");
        }
        // Sluk blæser hvis fugtighed < grænse
        else if (humidity < humidityLimit && status == 1) {
            MQTTprogram.publishMessage(client, topicPower, contentOFF);
            status = 0;
            System.out.println("Fan turned OFF");
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // not used in this example
    }
}