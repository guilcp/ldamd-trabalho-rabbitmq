package bolsa;

import com.rabbitmq.client.*;

public class BolsaClient extends Thread{
    private String host;
    private String topico;
    private String mensagem;

    public BolsaClient(String host, String topico, String mensagem) {
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
            channel.exchangeDeclare("BOLSADEVALORES", BuiltinExchangeType.TOPIC);
            channel.basicPublish("BOLSADEVALORES", topico, null, mensagem.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
