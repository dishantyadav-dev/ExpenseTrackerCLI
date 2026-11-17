package UI;

import Model.Category;
import Model.Expense;
import Model.PaymentMethod;
import Service.ExpenseService;
import Storage.FileManager;
import Util.DateUtil;
import Util.InputValidator;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI {

    private final Scanner sc = new Scanner(System.in);
    private final ExpenseService service;
    private final String dataFile = "expenses.txt";

    public ConsoleUI(ExpenseService service) {
        this.service = service;
    }

    public void start() {
        FileManager.loadExpenses(service, dataFile);
        System.out.println("=== Welcome to Expense Tracker CLI ===");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = InputValidator.readInt(sc, "Choose an option: ");
            switch (choice) {
                case 1 -> addExpense();
                case 2 -> viewAllExpenses();
                case 3 -> updateExpense();
                case 4 -> deleteExpense();
                case 5 -> searchExpenses();
                case 6 -> sortExpenses();
                case 7 -> filterExpenses();
                case 8 -> showDashboard();
                case 9 -> showCategorySummary();
                case 10 -> setBudget();
                case 11 -> showStatistics();
                case 12 -> {
                    FileManager.saveExpenses(service, dataFile);
                    System.out.println("Data saved. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n===== MENU =====");
        System.out.println("1. Add Expense");
        System.out.println("2. View All Expenses");
        System.out.println("3. Update Expense");
        System.out.println("4. Delete Expense");
        System.out.println("5. Search Expenses");
        System.out.println("6. Sort Expenses");
        System.out.println("7. Filter Expenses");
        System.out.println("8. Dashboard");
        System.out.println("9. Category-wise Summary");
        System.out.println("10. Set/View Monthly Budget");
        System.out.println("11. Expense Statistics");
        System.out.println("12. Save & Exit");
    }

    private void addExpense() {
        double amount = InputValidator.readDouble(sc, "Enter amount: ");
        Category category = InputValidator.readEnum(sc, "Select category:", Category.class);
        String description = InputValidator.readNonEmptyString(sc, "Enter description: ");
        LocalDate date = InputValidator.readDate(sc, "Enter date");
        PaymentMethod pm = InputValidator.readEnum(sc, "Select payment method:", PaymentMethod.class);

        Expense e = service.addExpense(amount, category, description, date, pm);
        checkBudgetWarning(YearMonth.from(date));
        System.out.println("Expense added: " + e);
    }

    private void viewAllExpenses() {
        List<Expense> all = service.getAllExpenses();
        printExpenseList(all);
    }

    private void printExpenseList(List<Expense> list) {
        if (list.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }
        System.out.println("\nID   Date      Amount   Category     Payment       Description");
        for (Expense e : list) {
            System.out.println(e);
        }
        System.out.println("Total shown: " + list.size());
    }

    private void updateExpense() {
        int id = InputValidator.readInt(sc, "Enter expense ID to update: ");
        if (service.getExpenseById(id).isEmpty()) {
            System.out.println("Expense not found.");
            return;
        }
        System.out.println("Leave a field as-is by entering nothing where applicable.");
        System.out.print("New amount (or press enter to skip): ");
        String amtInput = sc.nextLine().trim();
        Double amount = amtInput.isEmpty() ? null : Double.parseDouble(amtInput);

        Category category = InputValidator.readEnum(sc, "Select new category:", Category.class);
        String description = InputValidator.readNonEmptyString(sc, "New description: ");
        LocalDate date = InputValidator.readDate(sc, "New date");
        PaymentMethod pm = InputValidator.readEnum(sc, "Select new payment method:", PaymentMethod.class);

        boolean updated = service.updateExpense(id, amount, category, description, date, pm);
        System.out.println(updated ? "Expense updated." : "Update failed.");
    }

    private void deleteExpense() {
        int id = InputValidator.readInt(sc, "Enter expense ID to delete: ");
        boolean deleted = service.deleteExpense(id);
        System.out.println(deleted ? "Expense deleted." : "Expense not found.");
    }

    private void searchExpenses() {
        String keyword = InputValidator.readNonEmptyString(sc, "Enter keyword to search description: ");
        printExpenseList(service.searchByDescription(keyword));
    }

    // ---------- Sort ----------
    private void sortExpenses() {
        System.out.println("Sort by: 1. Date  2. Amount");
        int by = InputValidator.readInt(sc, "Choose: ");
        System.out.println("Order: 1. Ascending  2. Descending");
        int order = InputValidator.readInt(sc, "Choose: ");
        boolean asc = order == 1;

        List<Expense> result = (by == 2) ? service.sortByAmount(asc) : service.sortByDate(asc);
        printExpenseList(result);
    }

    private void filterExpenses() {
        System.out.println("Filter by: 1. Category  2. Date Range  3. Payment Method");
        int choice = InputValidator.readInt(sc, "Choose: ");
        switch (choice) {
            case 1 -> {
                Category c = InputValidator.readEnum(sc, "Select category:", Category.class);
                printExpenseList(service.filterByCategory(c));
            }
            case 2 -> {
                LocalDate from = InputValidator.readDate(sc, "From date");
                LocalDate to = InputValidator.readDate(sc, "To date");
                printExpenseList(service.filterByDateRange(from, to));
            }
            case 3 -> {
                PaymentMethod pm = InputValidator.readEnum(sc, "Select payment method:", PaymentMethod.class);
                printExpenseList(service.filterByPaymentMethod(pm));
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    private void showDashboard() {
        YearMonth current = YearMonth.now();
        System.out.println("\n===== DASHBOARD =====");
        System.out.println("Total Expenses (all time): " + service.getTotalExpense());
        System.out.println("Total Expenses (" + current + "): " + service.getTotalForMonth(current));
        System.out.println("Number of records: " + service.getExpenseCount());

        Double budget = service.getMonthlyBudget(current);
        if (budget != null) {
            Double remaining = service.getBudgetRemaining(current);
            System.out.println("Budget for " + current + ": " + budget);
            System.out.println("Remaining: " + remaining);
            if (remaining < 0) {
                System.out.println("WARNING: You are over budget by " + (-remaining) + "!");
            }
        } else {
            System.out.println("No budget set for " + current + ".");
        }

        System.out.println("\nRecent expenses:");
        List<Expense> recent = service.sortByDate(false);
        printExpenseList(recent.size() > 5 ? recent.subList(0, 5) : recent);
    }

    private void showCategorySummary() {
        Map<Category, Double> summary = service.getCategoryWiseSummary();
        if (summary.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }
        System.out.println("\n===== CATEGORY-WISE SUMMARY =====");
        for (Map.Entry<Category, Double> entry : summary.entrySet()) {
            System.out.printf("%-15s : %.2f%n", entry.getKey(), entry.getValue());
        }
    }

    private void setBudget() {
        YearMonth ym = DateUtil.readYearMonth(sc, "Enter month for budget");
        Double existing = service.getMonthlyBudget(ym);
        if (existing != null) {
            System.out.println("Current budget for " + ym + ": " + existing);
        }
        double amount = InputValidator.readDouble(sc, "Enter budget amount: ");
        service.setMonthlyBudget(ym, amount);
        System.out.println("Budget set for " + ym + ": " + amount);
    }

    private void checkBudgetWarning(YearMonth ym) {
        if (service.isOverBudget(ym)) {
            double over = -service.getBudgetRemaining(ym);
            System.out.println("WARNING: You have exceeded your budget for " + ym + " by " + over + "!");
        }
    }

    private void showStatistics() {
        System.out.println("\n===== EXPENSE STATISTICS =====");
        System.out.println("Total: " + service.getTotalExpense());
        System.out.println("Average: " + String.format("%.2f", service.getAverageExpense()));
        service.getHighestExpense().ifPresent(e -> System.out.println("Highest: " + e));
        service.getLowestExpense().ifPresent(e -> System.out.println("Lowest: " + e));
        System.out.println("Count: " + service.getExpenseCount());
    }
}

