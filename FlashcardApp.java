import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FlashcardApp {
    private JFrame frame;
    private JTextField questionField, answerField;
    private JTextArea displayArea;
    private Connection conn;

    public FlashcardApp() {
        initializeDB();
        createGUI();
    }

    private void initializeDB() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:flashcards.db");
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS flashcards (id INTEGER PRIMARY KEY, question TEXT, answer TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        frame = new JFrame("Flashcard App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        
        JPanel panel = new JPanel(new GridLayout(4, 1));
        questionField = new JTextField();
        answerField = new JTextField();
        JButton addButton = new JButton("Add Flashcard");
        JButton viewButton = new JButton("View Flashcards");
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        
        addButton.addActionListener(e -> addFlashcard());
        viewButton.addActionListener(e -> viewFlashcards());
        
        panel.add(new JLabel("Question:"));
        panel.add(questionField);
        panel.add(new JLabel("Answer:"));
        panel.add(answerField);
        panel.add(addButton);
        panel.add(viewButton);
        
        frame.add(panel, BorderLayout.NORTH);
        frame.add(new JScrollPane(displayArea), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void addFlashcard() {
        String question = questionField.getText();
        String answer = answerField.getText();
        if (question.isEmpty() || answer.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Both fields are required!");
            return;
        }
        try {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO flashcards (question, answer) VALUES (?, ?)");
            pstmt.setString(1, question);
            pstmt.setString(2, answer);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Flashcard added!");
            questionField.setText("");
            answerField.setText("");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewFlashcards() {
        displayArea.setText("");
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM flashcards");
            while (rs.next()) {
                displayArea.append("Q: " + rs.getString("question") + "\nA: " + rs.getString("answer") + "\n\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FlashcardApp::new);
    }
}
