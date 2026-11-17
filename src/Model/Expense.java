package Model;
import java.time.LocalDate;

public class Expense {
    private static int nextId = 1;

    private final int id;
    private double amount;
    private Category category;
    private String description;
    private LocalDate date;
    private PaymentMethod paymentMethod;

    public Expense(double amount, Category category, String description, LocalDate date, PaymentMethod paymentMethod)
    {
        this.id = nextId++;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.paymentMethod = paymentMethod;
        this.category = category;
    }

    public Expense(int id, double amount, String description, LocalDate date, Category category,
                   PaymentMethod paymentMethod)
    {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
        this.paymentMethod = paymentMethod;
        if (id >= nextId)
        {
            nextId = id + 1;
        }
    }

    public static void setNextId(int value) {
        nextId = value;
    }
    public int getId() { return id; }
    public double getAmount() { return amount; }
    public Category getCategory() { return category; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }

    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(Category category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    @Override
    public String toString()
    {
        return String.format("[%d] %-10s %-8.2f %-12s %-15s %s",
                id,date,amount,category,paymentMethod,description);
    }

    public String toFileString() {
        return id + "|" + amount + "|" + category + "|" + description + "|" + date + "|" + paymentMethod;
    }

}
