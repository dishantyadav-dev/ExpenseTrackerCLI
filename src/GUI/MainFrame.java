package GUI;

import Model.Category;
import Model.Expense;
import Model.PaymentMethod;
import Service.ExpenseService;
import Storage.FileManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {

    private static final Color PRIMARY = new Color(63, 81, 181);
    private static final Color PRIMARY_DARK = new Color(40, 53, 147);
    private static final Color SIDEBAR = new Color(30, 34, 45);
    private static final Color SIDEBAR_HOVER = new Color(50, 55, 70);
    private static final Color ACCENT = new Color(0, 150, 136);
    private static final Color DANGER = new Color(229, 57, 53);
    private static final Color BG = new Color(245, 246, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT_DARK = new Color(33, 33, 33);

    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_NAV = new Font("Segoe UI", Font.PLAIN, 14);

    private final ExpenseService service;
    private final String dataFile = "expenses.txt";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel dashboardLabel;
    private DefaultTableModel searchTableModel;

    public MainFrame(ExpenseService service) {
        this.service = service;
        FileManager.loadExpenses(service, dataFile);

        setTitle("Expense Tracker");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);

        contentPanel.setBackground(BG);
        contentPanel.add(buildAddPanel(), "ADD");
        contentPanel.add(buildViewPanel(), "VIEW");
        contentPanel.add(buildDashboardPanel(), "DASHBOARD");
        contentPanel.add(buildBudgetPanel(), "BUDGET");
        contentPanel.add(buildSearchPanel(), "SEARCH");
        contentPanel.add(buildStatsPanel(), "STATS");
        add(contentPanel, BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                FileManager.saveExpenses(service, dataFile);
                System.exit(0);
            }
        });

        cardLayout.show(contentPanel, "DASHBOARD");
        refreshTable();
        refreshDashboard();
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("  Expense Tracker");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setBorder(new EmptyBorder(20, 5, 25, 5));
        sidebar.add(logo);

        sidebar.add(navButton("Dashboard", "DASHBOARD"));
        sidebar.add(navButton("Add Expense", "ADD"));
        sidebar.add(navButton("View Expenses", "VIEW"));
        sidebar.add(navButton("Search & Filter", "SEARCH"));
        sidebar.add(navButton("Statistics", "STATS"));
        sidebar.add(navButton("Budget", "BUDGET"));

        sidebar.add(Box.createVerticalGlue());

        JButton exitButton = new JButton("Save & Exit");
        exitButton.setFont(FONT_NAV);
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(DANGER);
        exitButton.setFocusPainted(false);
        exitButton.setBorderPainted(false);
        exitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        exitButton.setMaximumSize(new Dimension(200, 45));
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitButton.addActionListener(e -> {
            FileManager.saveExpenses(service, dataFile);
            System.exit(0);
        });
        sidebar.add(exitButton);

        return sidebar;
    }

    private JButton navButton(String text, String cardName) {
        JButton btn = new JButton("  " + text);
        btn.setFont(FONT_NAV);
        btn.setForeground(Color.WHITE);
        btn.setBackground(SIDEBAR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(SIDEBAR_HOVER); }
            public void mouseExited(MouseEvent e) { btn.setBackground(SIDEBAR); }
        });

        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            if (cardName.equals("VIEW")) refreshTable();
            if (cardName.equals("DASHBOARD")) refreshDashboard();
            if (cardName.equals("STATS")) refreshStats();
        });

        return btn;
    }

    private JButton styledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        Color hover = bgColor.darker();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bgColor); }
        });
        return btn;
    }

    private JTextField styledField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(FONT_LABEL);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(8, 10, 8, 10)));
        return field;
    }

    private JLabel styledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_DARK);
        return label;
    }

    private JPanel cardWrapper(JPanel card, int w, int h) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG);
        card.setPreferredSize(new Dimension(w, h));
        wrapper.add(card);
        return wrapper;
    }

    // ---------- Add Expense ----------
    private JPanel buildAddPanel() {
        JPanel card = new JPanel(new GridLayout(6, 2, 12, 14));
        card.setBackground(CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));

        JTextField amountField = styledField("");
        JComboBox<Category> categoryBox = new JComboBox<>(Category.values());
        categoryBox.setFont(FONT_LABEL);
        JTextField descField = styledField("");
        JTextField dateField = styledField(LocalDate.now().toString());
        JComboBox<PaymentMethod> paymentBox = new JComboBox<>(PaymentMethod.values());
        paymentBox.setFont(FONT_LABEL);
        JButton addButton = styledButton("Add Expense", ACCENT);

        card.add(styledLabel("Amount:")); card.add(amountField);
        card.add(styledLabel("Category:")); card.add(categoryBox);
        card.add(styledLabel("Description:")); card.add(descField);
        card.add(styledLabel("Date (yyyy-MM-dd):")); card.add(dateField);
        card.add(styledLabel("Payment Method:")); card.add(paymentBox);
        card.add(new JLabel()); card.add(addButton);

        addButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                Category category = (Category) categoryBox.getSelectedItem();
                String desc = descField.getText().trim();
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                PaymentMethod pm = (PaymentMethod) paymentBox.getSelectedItem();
                if (desc.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Description cannot be empty.");
                    return;
                }
                service.addExpense(amount, category, desc, date, pm);
                JOptionPane.showMessageDialog(this, "Expense added successfully!");
                amountField.setText("");
                descField.setText("");
                refreshTable();
                refreshDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return cardWrapper(card, 500, 340);
    }

    // ---------- View Expenses ----------
    private JPanel buildViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] columns = {"ID", "Date", "Amount", "Category", "Payment", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBackground(BG);
        JButton deleteButton = styledButton("Delete Selected", DANGER);
        JButton refreshButton = styledButton("Refresh", PRIMARY);

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a row first.");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            service.deleteExpense(id);
            refreshTable();
            refreshDashboard();
        });
        refreshButton.addActionListener(e -> refreshTable());

        bottom.add(deleteButton);
        bottom.add(refreshButton);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void styleTable(JTable t) {
        t.setFont(FONT_LABEL);
        t.setRowHeight(28);
        t.setSelectionBackground(PRIMARY.brighter());
        t.setGridColor(new Color(230, 230, 230));
        JTableHeader header = t.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY_DARK);
        header.setForeground(Color.WHITE);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Expense e : service.getAllExpenses()) {
            tableModel.addRow(new Object[]{e.getId(), e.getDate(), e.getAmount(),
                    e.getCategory(), e.getPaymentMethod(), e.getDescription()});
        }
    }

    private JPanel buildDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        dashboardLabel = new JLabel();
        dashboardLabel.setVerticalAlignment(SwingConstants.TOP);
        dashboardLabel.setOpaque(true);
        dashboardLabel.setBackground(CARD);
        dashboardLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(new JScrollPane(dashboardLabel), BorderLayout.CENTER);

        JButton refreshButton = styledButton("Refresh Dashboard", PRIMARY);
        refreshButton.addActionListener(e -> refreshDashboard());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.setBackground(BG);
        south.add(refreshButton);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshDashboard() {
        YearMonth current = YearMonth.now();
        StringBuilder sb = new StringBuilder("<html><div style='font-family:Segoe UI;padding:10px;'>");
        sb.append("<h1 style='color:#3F51B5;'>Dashboard</h1><p style='font-size:14px;'>");
        sb.append("<b>Total Expenses (all time):</b> Rs.").append(service.getTotalExpense()).append("<br>");
        sb.append("<b>Total Expenses (").append(current).append("):</b> Rs.")
                .append(service.getTotalForMonth(current)).append("<br>");
        sb.append("<b>Number of records:</b> ").append(service.getExpenseCount()).append("</p>");

        sb.append("<h3 style='color:#009688;'>Category-wise Summary</h3><p style='font-size:14px;'>");
        for (Map.Entry<Category, Double> entry : service.getCategoryWiseSummary().entrySet()) {
            sb.append(entry.getKey()).append(": Rs.").append(entry.getValue()).append("<br>");
        }
        sb.append("</p>");

        Double budget = service.getMonthlyBudget(current);
        sb.append("<h3 style='color:#3F51B5;'>Budget</h3><p style='font-size:14px;'>");
        if (budget != null) {
            Double remaining = service.getBudgetRemaining(current);
            sb.append("Budget: Rs.").append(budget).append("<br>Remaining: Rs.").append(remaining).append("<br>");
            if (remaining < 0) {
                sb.append("<span style='color:#E53935;font-weight:bold;'>WARNING: Over budget by Rs.")
                        .append(-remaining).append("!</span><br>");
            }
        } else {
            sb.append("No budget set for this month.<br>");
        }
        sb.append("</p></div></html>");
        dashboardLabel.setText(sb.toString());
    }

    private JPanel buildBudgetPanel() {
        JPanel card = new JPanel(new GridLayout(3, 2, 12, 14));
        card.setBackground(CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));

        JTextField monthField = styledField(YearMonth.now().toString());
        JTextField amountField = styledField("");
        JButton setButton = styledButton("Set Budget", ACCENT);

        card.add(styledLabel("Month (yyyy-MM):")); card.add(monthField);
        card.add(styledLabel("Budget Amount:")); card.add(amountField);
        card.add(new JLabel()); card.add(setButton);

        setButton.addActionListener(e -> {
            try {
                YearMonth ym = YearMonth.parse(monthField.getText().trim());
                double amount = Double.parseDouble(amountField.getText().trim());
                service.setMonthlyBudget(ym, amount);
                JOptionPane.showMessageDialog(this, "Budget set for " + ym);
                refreshDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
            }
        });

        return cardWrapper(card, 450, 190);
    }

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.setBackground(BG);

        JTextField keywordField = styledField("");
        keywordField.setPreferredSize(new Dimension(150, 32));
        JComboBox<Category> categoryFilter = new JComboBox<>(Category.values());
        JButton searchBtn = styledButton("Search", PRIMARY);
        JButton filterCatBtn = styledButton("Filter by Category", ACCENT);
        JButton clearBtn = styledButton("Clear", DANGER);

        top.add(styledLabel("Keyword:"));
        top.add(keywordField);
        top.add(searchBtn);
        top.add(categoryFilter);
        top.add(filterCatBtn);
        top.add(clearBtn);
        panel.add(top, BorderLayout.NORTH);

        String[] columns = {"ID", "Date", "Amount", "Category", "Payment", "Description"};
        searchTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable searchTable = new JTable(searchTableModel);
        styleTable(searchTable);
        panel.add(new JScrollPane(searchTable), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            List<Expense> results = service.searchByDescription(keywordField.getText().trim());
            populateSearchTable(results);
        });
        filterCatBtn.addActionListener(e -> {
            Category c = (Category) categoryFilter.getSelectedItem();
            populateSearchTable(service.filterByCategory(c));
        });
        clearBtn.addActionListener(e -> searchTableModel.setRowCount(0));

        return panel;
    }

    private void populateSearchTable(List<Expense> results) {
        searchTableModel.setRowCount(0);
        for (Expense e : results) {
            searchTableModel.addRow(new Object[]{e.getId(), e.getDate(), e.getAmount(),
                    e.getCategory(), e.getPaymentMethod(), e.getDescription()});
        }
    }

    private JLabel statsLabel;

    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        statsLabel = new JLabel();
        statsLabel.setVerticalAlignment(SwingConstants.TOP);
        statsLabel.setOpaque(true);
        statsLabel.setBackground(CARD);
        statsLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(new JScrollPane(statsLabel), BorderLayout.CENTER);

        return panel;
    }

    private void refreshStats() {
        StringBuilder sb = new StringBuilder("<html><div style='font-family:Segoe UI;padding:10px;font-size:14px;'>");
        sb.append("<h1 style='color:#3F51B5;'>Statistics</h1>");
        sb.append("<b>Total:</b> Rs.").append(service.getTotalExpense()).append("<br>");
        sb.append("<b>Average:</b> Rs.").append(String.format("%.2f", service.getAverageExpense())).append("<br>");
        sb.append("<b>Count:</b> ").append(service.getExpenseCount()).append("<br><br>");
        service.getHighestExpense().ifPresent(e -> sb.append("<b>Highest:</b> ").append(e.toString()).append("<br>"));
        service.getLowestExpense().ifPresent(e -> sb.append("<b>Lowest:</b> ").append(e.toString()).append("<br>"));
        sb.append("</div></html>");
        statsLabel.setText(sb.toString());
    }
}