package bolsa;

import java.util.ArrayList;

public class LivroOfertas {
    private static ArrayList<String[]> ofertasVendas = new ArrayList<String[]>();
    private static ArrayList<String[]> ofertasCompras = new ArrayList<String[]>();

    public synchronized static void armazena(String host, String asset, String offer) {
        String[] splitAtivo = asset.split("\\.");
        String[] splitOferta = offer.split(">|;|<");
        String[] data = new String[4];
        int idOferta;
        if (splitAtivo[0].equals("venda")) {
            data[0] = splitAtivo[1];
            for (int i = 1; i <= 3; i++)
                data[i] = splitOferta[i];
            idOferta = ofertasVendas.size();
            ofertasVendas.add(data);

        } else if (splitAtivo[0].equals("compra")) {
            data[0] = splitAtivo[1];
            for (int i = 1; i <= 3; i++)
                data[i] = splitOferta[i];
            idOferta = ofertasCompras.size();
            ofertasCompras.add(data);
        } else return;
        unirOfertas(host, splitAtivo[0], idOferta);
    }

    private synchronized static void unirOfertas(String host, String type, int idOferta) {
        if (type.equals("compra")) {
            String[] offer = ofertasCompras.get(idOferta);
            for (int s = 0; s < ofertasVendas.size(); s++) {
                String[] sale = ofertasVendas.get(s);
                float sPrice = Float.parseFloat(sale[2]);
                float oPrice = Float.parseFloat(offer[2]);
                if (sale[0].equals(offer[0]) && oPrice >= sPrice) {
                    int sLength = Integer.parseInt(sale[1]);
                    int oLength = Integer.parseInt(offer[1]);
                    if (sLength > oLength) {
                        Transacao.store(host, sale, offer);
                        sale[1] = Integer.toString(sLength - oLength);
                        ofertasVendas.set(s, sale);
                        ofertasCompras.remove(idOferta);
                        return;
                    } else if (oLength > sLength) {
                        Transacao.store(host, sale, offer);
                        offer[1] = Integer.toString(oLength - sLength);
                        ofertasVendas.remove(s);
                        ofertasCompras.set(idOferta, offer);
                        s--;
                    } else {
                        Transacao.store(host, sale, offer);
                        ofertasVendas.remove(s);
                        ofertasCompras.remove(idOferta);
                        return;
                    }
                }
            }
        } else if (type.equals("venda")) {
            String[] offer = ofertasVendas.get(idOferta);
            for (int p = 0; p < ofertasCompras.size(); p++) {
                String[] purchase = ofertasCompras.get(p);
                float pPrice = Float.parseFloat(purchase[2]);
                float oPrice = Float.parseFloat(offer[2]);
                if (purchase[0].equals(offer[0]) && oPrice <= pPrice) {
                    int pLength = Integer.parseInt(purchase[1]);
                    int oLength = Integer.parseInt(offer[1]);
                    if (pLength > oLength) {
                        Transacao.store(host, offer, purchase);
                        purchase[1] = Integer.toString(pLength - oLength);
                        ofertasCompras.set(p, purchase);
                        ofertasVendas.remove(idOferta);
                        return;
                    } else if (oLength > pLength) {
                        Transacao.store(host, offer, purchase);
                        offer[1] = Integer.toString(oLength - pLength);
                        ofertasCompras.remove(p);
                        ofertasVendas.set(idOferta, offer);
                        p--;
                    } else {
                        Transacao.store(host, offer, purchase);
                        ofertasCompras.remove(p);
                        ofertasVendas.remove(idOferta);
                        return;
                    }
                }
            }
        } else return;
    }
}
