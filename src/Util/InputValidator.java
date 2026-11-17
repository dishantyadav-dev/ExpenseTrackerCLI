package Util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class InputValidator {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value <= 0) {
                    System.out.println("Amount must be positive. Try again.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    public static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    public static String readNonEmptyString(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("This field cannot be empty. Try again.");
        }
    }

    public static LocalDate readDate(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt + " (yyyy-MM-dd, or 'today'): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("today")) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(input, DATE_FORMAT);
            } catch (Exception e) {
                System.out.println("Invalid date format. Try again.");
            }
        }
    }

    public static <T extends Enum<T>> T readEnum(Scanner sc, String prompt, Class<T> enumClass) {
        T[] values = enumClass.getEnumConstants();
        while (true) {
            System.out.println(prompt);
            for (int i = 0; i < values.length; i++) {
                System.out.println("  " + (i + 1) + ". " + values[i]);
            }
            System.out.print("Choose (1-" + values.length + "): ");
            String input = sc.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= values.length) {
                    return values[choice - 1];
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Invalid choice. Try again.");
        }
    }
}