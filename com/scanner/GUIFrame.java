import javax.swing.*;
import java.awt.*;

public class GUIFrame extends JFrame {

    public static final int TEXTAREA_ROWS = 8;
    public static final int TEXTAREA_COLUMNS = 20;

    public GUIFrame()
    {
        JTextField IPaddress = new JTextField();
        JTextField firstPort = new JTextField();
        JTextField secondPort = new JTextField();

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new GridLayout(2, 3));
        northPanel.add(new JLabel("Insert Computer IP Address: ", SwingConstants.RIGHT));
        northPanel.add(IPaddress);
        northPanel.add(new JLabel("Specify Port (will scan all by default, or specify a port or range)", SwingConstants.RIGHT));
        northPanel.add(firstPort);
        northPanel.add(secondPort);
        JButton insertButton = new JButton("Insert");
        JPanel midPanel = new JPanel();
        midPanel.add(insertButton);

        add(northPanel, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea(TEXTAREA_ROWS, TEXTAREA_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(textArea);

        add(scrollPane, BorderLayout.CENTER);

        // add button to append text into the text area

        JPanel southPanel = new JPanel();

        insertButton.addActionListener(event ->
                textArea.append("IP address entered: " + IPaddress.getText() ));

        add(southPanel, BorderLayout.SOUTH);
        pack();
    }
}
