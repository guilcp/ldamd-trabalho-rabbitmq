package broker;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class BrokerClient extends Thread {
    private String host;
    private String topico;
    private String mensagem;

    public BrokerClient(String host, String topico, String mensagem) {
        this.host = host;
        this.topico = topico;
        this.mensagem = mensagem;
    }

    @Override
    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost(host);
        try {
            factory.setUri(host);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare("BROKER", BuiltinExchangeType.TOPIC);
            channel.basicPublish("BROKER", topico, null, mensagem.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
