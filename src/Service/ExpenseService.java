package Service;

import Model.Category;
import Model.Expense;
import Model.PaymentMethod;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class ExpenseService {

    private final List<Expense> expenses = new ArrayList<>();
    private final Map<YearMonth, Double> monthlyBudgets = new HashMap<>();


    public Expense addExpense(double amount, Category category, String description,
                              LocalDate date, PaymentMethod paymentMethod) {
        Expense e = new Expense(amount, category, description, date, paymentMethod);
        expenses.add(e);
        return e;
    }

    public void addLoadedExpense(Expense e) {
        expenses.add(e);
    }

    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    public Optional<Expense> getExpenseById(int id) {
        return expenses.stream().filter(e -> e.getId() == id).findFirst();
    }

    public boolean updateExpense(int id, Double amount, Category category, String description,
                                 LocalDate date, PaymentMethod paymentMethod) {
        Optional<Expense> opt = getExpenseById(id);
        if (opt.isEmpty()) return false;
        Expense e = opt.get();
        if (amount != null) e.setAmount(amount);
        if (category != null) e.setCategory(category);
        if (description != null && !description.isBlank()) e.setDescription(description);
        if (date != null) e.setDate(date);
        if (paymentMethod != null) e.setPaymentMethod(paymentMethod);
        return true;
    }

    public boolean deleteExpense(int id) {
        return expenses.removeIf(e -> e.getId() == id);
    }

    // ---------- Search / Sort / Filter ----------

    public List<Expense> searchByDescription(String keyword) {
        String k = keyword.toLowerCase();
        return expenses.stream()
                .filter(e -> e.getDescription().toLowerCase().contains(k))
                .collect(Collectors.toList());
    }

    public List<Expense> filterByCategory(Category category) {
        return expenses.stream()
                .filter(e -> e.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<Expense> filterByDateRange(LocalDate from, LocalDate to) {
        return expenses.stream()
                .filter(e -> !e.getDate().isBefore(from) && !e.getDate().isAfter(to))
                .collect(Collectors.toList());
    }

    public List<Expense> filterByPaymentMethod(PaymentMethod pm) {
        return expenses.stream()
                .filter(e -> e.getPaymentMethod() == pm)
                .collect(Collectors.toList());
    }

    public List<Expense> sortByDate(boolean ascending) {
        Comparator<Expense> cmp = Comparator.comparing(Expense::getDate);
        if (!ascending) cmp = cmp.reversed();
        return expenses.stream().sorted(cmp).collect(Collectors.toList());
    }

    public List<Expense> sortByAmount(boolean ascending) {
        Comparator<Expense> cmp = Comparator.comparingDouble(Expense::getAmount);
        if (!ascending) cmp = cmp.reversed();
        return expenses.stream().sorted(cmp).collect(Collectors.toList());
    }


    public double getTotalExpense() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    public Map<Category, Double> getCategoryWiseSummary() {
        return expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)));
    }

    public double getTotalForMonth(YearMonth ym) {
        return expenses.stream()
                .filter(e -> YearMonth.from(e.getDate()).equals(ym))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public double getAverageExpense() {
        return expenses.isEmpty() ? 0 : getTotalExpense() / expenses.size();
    }

    public Optional<Expense> getHighestExpense() {
        return expenses.stream().max(Comparator.comparingDouble(Expense::getAmount));
    }

    public Optional<Expense> getLowestExpense() {
        return expenses.stream().min(Comparator.comparingDouble(Expense::getAmount));
    }

    public int getExpenseCount() {
        return expenses.size();
    }


    public void setMonthlyBudget(YearMonth ym, double amount) {
        monthlyBudgets.put(ym, amount);
    }

    public Double getMonthlyBudget(YearMonth ym) {
        return monthlyBudgets.get(ym);
    }

    public Double getBudgetRemaining(YearMonth ym) {
        Double budget = monthlyBudgets.get(ym);
        if (budget == null) return null;
        return budget - getTotalForMonth(ym);
    }

    public boolean isOverBudget(YearMonth ym) {
        Double remaining = getBudgetRemaining(ym);
        return remaining != null && remaining < 0;
    }
}