import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ASCIIFinderGUI extends JFrame {
    private JTextField inputField;
    private JLabel resultLabel;

    public ASCIIFinderGUI() {
        setTitle("ASCII Value Finder");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));

        JLabel inputLabel = new JLabel("Enter a character:");
        inputField = new JTextField();
        JButton findButton = new JButton("Find ASCII Value");
        resultLabel = new JLabel();

        // Button click listener
        findButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findASCIIValue();
            }
        });

        panel.add(inputLabel);
        panel.add(inputField);
        panel.add(findButton);
        panel.add(resultLabel);

        add(panel);
        setVisible(true);
    }

    private void findASCIIValue() {
        String input = inputField.getText().trim();
        if (input.length() != 1) {
            JOptionPane.showMessageDialog(this, "Please enter a single character.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        char c = input.charAt(0);
        int asciiValue = (int) c;
        resultLabel.setText("ASCII Value of '" + c + "' is " + asciiValue);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ASCIIFinderGUI();
            }
        });
    }
}