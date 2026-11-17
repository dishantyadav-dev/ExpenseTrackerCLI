package Util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class DateUtil {

    private static final DateTimeFormatter YM_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    public static YearMonth readYearMonth(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt + " (yyyy-MM, or 'current'): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("current")) {
                return YearMonth.now();
            }
            try {
                return YearMonth.parse(input, YM_FORMAT);
            } catch (Exception e) {
                System.out.println("Invalid format. Try again.");
            }
        }
    }

    public static String format(LocalDate date) {
        return date.format(InputValidator.DATE_FORMAT);
    }
}