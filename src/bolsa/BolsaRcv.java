package bolsa;

import com.rabbitmq.client.*;
import javax.swing.*;
import java.awt.*;

public class BolsaRcv extends Thread{
    private String host;
    public BolsaRcv(String host) {
        this.host = host;
    }

    @Override
    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        // factory.setHost(host);
        try {
            factory.setUri(host);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare("BROKER", BuiltinExchangeType.TOPIC);
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "BROKER", "#");
            JLabel openMsgL = new JLabel("Negociacoes abertas...");
            openMsgL.setFont(new Font("Serif", Font.BOLD, 12));
            openMsgL.setForeground(Color.MAGENTA);
            openMsgL.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            BolsaView.actionsP.add(openMsgL);
            BolsaView.actionsP.revalidate();
            BolsaView.actionsP.repaint();
            DeliverCallback deliveryCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                String topic = delivery.getEnvelope().getRoutingKey();
                String[] splitTopic = topic.split("\\.");
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
                BolsaView.actionsP.add(messageL);
                BolsaView.actionsP.revalidate();
                BolsaView.actionsP.repaint();
                BolsaClient emit = new BolsaClient(host, topic, message);
                emit.start();
                LivroOfertas.armazena(host, topic, message);
            };
            channel.basicConsume(queueName, true, deliveryCallback, consumerTag -> { });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
