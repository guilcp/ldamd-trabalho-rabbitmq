package bolsa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BolsaView {
    public static JPanel actionsP = new JPanel();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Stock Exchange");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(320, 600);
        frame.setResizable(false);
        frame.setLocation(new Point(948, 60));
        frame.setLayout(new BorderLayout());
        JPanel topP = new JPanel();
        topP.setLayout(new FlowLayout());
        JPanel centerP = new JPanel();
        centerP.setLayout(new BoxLayout(centerP, BoxLayout.Y_AXIS));
        centerP.setBorder(BorderFactory.createEmptyBorder(0, 10, 9, 10));
        JLabel titleL = new JLabel("Bolsa de Valores - Painel");
        topP.add(titleL);
        JPanel content = new JPanel();
        content.setLayout(new GridLayout(3, 1, 5, 5));
        content.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel serverL = new JLabel("Servidor:");
        JTextField serverTF = new JTextField("localhost");
        JButton runBtn = new JButton("Abrir Negociações");
        actionsP.setLayout(new BoxLayout(actionsP, BoxLayout.Y_AXIS));
        JScrollPane scrollP = new JScrollPane(actionsP);
        scrollP.setPreferredSize(new Dimension(200, 425));
        content.add(serverL);
        content.add(serverTF);
        content.add(runBtn);
        centerP.add(content);
        centerP.add(scrollP);
        frame.add(topP, BorderLayout.NORTH);
        frame.add(centerP, BorderLayout.CENTER);
        frame.setVisible(true);
        runBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (serverTF.getText().length() > 0) {
                    BolsaRcv sReceive = new BolsaRcv(serverTF.getText());
                    sReceive.start();
                }
            }
        });
    }
}
