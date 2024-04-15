package broker;

import javax.swing.*;
import com.rabbitmq.client.*;

public class BrokerRcv extends Thread{
    private String host;
    private String topico;

    public BrokerRcv(String host, String topico) {
        this.host = host;
        this.topico = topico;
    }

    @Override
    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost(host);
        try {
            factory.setUri(host);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare("BOLSADEVALORES", BuiltinExchangeType.TOPIC);
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "BOLSADEVALORES", topico);
            DeliverCallback deliveryCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                String topico = delivery.getEnvelope().getRoutingKey();
                String[] splitTopic = topico.split("\\.");
                String[] splitMessage = message.split(">|;|<");
                String messageFormat;
                if (splitMessage.length > 5) {
                    messageFormat = splitTopic[0] + " " + splitTopic[1].toUpperCase() + " | data: " + splitMessage[1]
                            + ", crr_vd: " + splitMessage[2] + ", crr_cp: " + splitMessage[3] + ", qtde: "
                            + splitMessage[4] + ", val: " + splitMessage[5];
                } else {
                    messageFormat = splitTopic[0] + " " + splitTopic[1].toUpperCase() + " | qtde: " + splitMessage[1]
                            + ", val: " + splitMessage[2] + ", crr: " + splitMessage[3];
                }
                JLabel messageL = new JLabel(messageFormat);
                messageL.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                BrokerView.actionsP.add(messageL);
                BrokerView.actionsP.revalidate();
                BrokerView.actionsP.repaint();
            };
            channel.basicConsume(queueName, true, deliveryCallback, consumerTag -> { });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
