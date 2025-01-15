/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpenseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/expense_manager";
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = ""; // Replace with your MySQL password
    private static Connection connection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenseManager::initialize);
    }

    private static void initialize() {
        try {
            setupDatabase();
            createMainFrame();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error initializing application: " + e.getMessage());
        }
    }

    private static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        String createTableSQL = "CREATE TABLE IF NOT EXISTS expenses ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "amount DOUBLE NOT NULL,"
                + "category VARCHAR(255) NOT NULL,"
                + "date DATE NOT NULL,"
                + "description TEXT"
                + ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    private static void createMainFrame() {
        JFrame frame = new JFrame("Expense Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel(new BorderLayout());

        // Clock Display
        JLabel clockLabel = new JLabel();
        clockLabel.setHorizontalAlignment(SwingConstants.CENTER);
        clockLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(clockLabel, BorderLayout.NORTH);

        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            clockLabel.setText("Current Time: " + sdf.format(new Date()));
        });
        timer.start();

        // Table to Display Expenses
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Amount", "Category", "Date", "Description"}, 0);
        JTable table = new JTable(tableModel);
        loadExpenses(tableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons for Actions
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Expense");
        JButton editButton = new JButton("Edit Expense");
        JButton deleteButton = new JButton("Delete Expense");
        JButton exportButton = new JButton("Export to Excel");

        addButton.addActionListener(e -> addExpense(tableModel));
        editButton.addActionListener(e -> editExpense(tableModel, table));
        deleteButton.addActionListener(e -> deleteExpense(tableModel, table));
        exportButton.addActionListener(e -> exportToExcel());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void loadExpenses(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        String query = "SELECT * FROM expenses";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getDate("date"),
                        rs.getString("description")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addExpense(DefaultTableModel tableModel) {
        JTextField amountField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        JTextField descriptionField = new JTextField();

        int result = JOptionPane.showConfirmDialog(null, new Object[]{
                        "Amount:", amountField,
                        "Category:", categoryField,
                        "Date (yyyy-MM-dd):", dateField,
                        "Description:", descriptionField},
                "Add Expense", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String insertSQL = "INSERT INTO expenses (amount, category, date, description) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setDouble(1, Double.parseDouble(amountField.getText()));
                pstmt.setString(2, categoryField.getText());
                pstmt.setDate(3, java.sql.Date.valueOf(dateField.getText()));

//                pstmt.setDate(3, Date.valueOf(dateField.getText()));
                pstmt.setString(4, descriptionField.getText());
                pstmt.executeUpdate();
                loadExpenses(tableModel);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void editExpense(DefaultTableModel tableModel, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a row to edit.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        JTextField amountField = new JTextField(tableModel.getValueAt(selectedRow, 1).toString());
        JTextField categoryField = new JTextField(tableModel.getValueAt(selectedRow, 2).toString());
        JTextField dateField = new JTextField(tableModel.getValueAt(selectedRow, 3).toString());
        JTextField descriptionField = new JTextField(tableModel.getValueAt(selectedRow, 4).toString());

        int result = JOptionPane.showConfirmDialog(null, new Object[]{
                        "Amount:", amountField,
                        "Category:", categoryField,
                        "Date (yyyy-MM-dd):", dateField,
                        "Description:", descriptionField},
                "Edit Expense", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String updateSQL = "UPDATE expenses SET amount = ?, category = ?, date = ?, description = ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
                pstmt.setDouble(1, Double.parseDouble(amountField.getText()));
                pstmt.setString(2, categoryField.getText());
                pstmt.setDate(3, java.sql.Date.valueOf(dateField.getText()));

//                pstmt.setDate(3, Date.valueOf(dateField.getText()));
                pstmt.setString(4, descriptionField.getText());
                pstmt.setInt(5, id);
                pstmt.executeUpdate();
                loadExpenses(tableModel);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void deleteExpense(DefaultTableModel tableModel, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a row to delete.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this expense?", "Delete Expense", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String deleteSQL = "DELETE FROM expenses WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                loadExpenses(tableModel);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void exportToExcel() {
        // Export logic using Apache POI can be implemented here
        JOptionPane.showMessageDialog(null, "Export to Excel functionality coming soon!");
    }
}
