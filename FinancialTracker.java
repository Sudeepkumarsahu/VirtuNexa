import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FinancialTracker {
    private JFrame frame;
    private JTextField amountField, categoryField, goalField;
    private JTextArea displayArea;
    private Connection conn;

    public FinancialTracker() {
        initializeDB();
        createGUI();
    }

    private void initializeDB() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:finances.db");
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS expenses (id INTEGER PRIMARY KEY, amount REAL, category TEXT, date TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS savings_goals (id INTEGER PRIMARY KEY, goal REAL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        frame = new JFrame("Financial Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        
        JPanel panel = new JPanel(new GridLayout(5, 2));
        amountField = new JTextField();
        categoryField = new JTextField();
        goalField = new JTextField();
        JButton addExpenseButton = new JButton("Add Expense");
        JButton setGoalButton = new JButton("Set Savings Goal");
        JButton viewExpensesButton = new JButton("View Expenses");
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        
        addExpenseButton.addActionListener(e -> addExpense());
        setGoalButton.addActionListener(e -> setSavingsGoal());
        viewExpensesButton.addActionListener(e -> viewExpenses());
        
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Savings Goal:"));
        panel.add(goalField);
        panel.add(addExpenseButton);
        panel.add(setGoalButton);
        panel.add(viewExpensesButton);
        
        frame.add(panel, BorderLayout.NORTH);
        frame.add(new JScrollPane(displayArea), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void addExpense() {
        String amount = amountField.getText();
        String category = categoryField.getText();
        if (amount.isEmpty() || category.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Both fields are required!");
            return;
        }
        try {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO expenses (amount, category, date) VALUES (?, ?, date('now'))");
            pstmt.setDouble(1, Double.parseDouble(amount));
            pstmt.setString(2, category);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Expense added!");
            amountField.setText("");
            categoryField.setText("");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setSavingsGoal() {
        String goal = goalField.getText();
        if (goal.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Goal amount is required!");
            return;
        }
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("DELETE FROM savings_goals"); // Replace old goal
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO savings_goals (goal) VALUES (?)");
            pstmt.setDouble(1, Double.parseDouble(goal));
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Savings goal set!");
            goalField.setText("");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewExpenses() {
        displayArea.setText("");
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM expenses");
            while (rs.next()) {
                displayArea.append("Amount: $" + rs.getDouble("amount") + " | Category: " + rs.getString("category") + " | Date: " + rs.getString("date") + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FinancialTracker::new);
    }
}
