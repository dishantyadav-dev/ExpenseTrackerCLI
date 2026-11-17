package Storage;

import Model.Category;
import Model.Expense;
import Model.PaymentMethod;
import Service.ExpenseService;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

public class FileManager {

    public static void saveExpenses(ExpenseService service, String filePath) {
        List<Expense> expenses = service.getAllExpenses();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Expense e : expenses) {
                bw.write(e.toFileString());
                bw.newLine();
            }
        } catch (IOException ex) {
            System.out.println("Error saving expenses: " + ex.getMessage());
        }
    }

    public static void loadExpenses(ExpenseService service, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                try {
                    Expense e = parseLine(line);
                    service.addLoadedExpense(e);
                } catch (Exception ex) {
                    System.out.println("Skipping corrupted line: " + line);
                }
            }
        } catch (IOException ex) {
            System.out.println("Error loading expenses: " + ex.getMessage());
        }
    }

    private static Expense parseLine(String line) {
        String[] parts = line.split("\\|", -1);
        int id = Integer.parseInt(parts[0]);
        double amount = Double.parseDouble(parts[1]);
        Category category = Category.valueOf(parts[2]);
        String description = parts[3];
        LocalDate date = LocalDate.parse(parts[4]);
        PaymentMethod paymentMethod = PaymentMethod.valueOf(parts[5]);
        return new Expense(id, amount, description, date, category, paymentMethod);
    }
}