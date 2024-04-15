package bolsa;

import broker.BrokerClient;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Transacao {
    private static ArrayList<String[]> transacoes = new ArrayList<String[]>();

    public synchronized static void store(String host, String[] venda, String[] compra) {
        String[] data = new String[6];
        data[0] = LocalDateTime.now().toString();
        data[1] = venda[0];
        data[2] = Integer.parseInt(venda[1]) < Integer.parseInt(compra[1]) ? venda[1] : compra[1];
        data[3] = venda[2];
        data[4] = venda[3];
        data[5] = compra[3];
        transacoes.add(data);
        String topic = "transacao." + data[1];
        String message = "<" + data[0] + "; " + data[4] + "; " + data[5] + "; " + data[2] + "; " + data[3] + ">";
        BrokerClient emit = new BrokerClient(host, topic, message);
        emit.start();
    }
}
