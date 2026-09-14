package com.intellifind.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

public class InputValidator {
    // Improvement 1: uuuu ensures strict proleptic calendar parsing
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("dd-MM-uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    public static String readNonEmptyString(Scanner sc, String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Field cannot be left blank. Please re-enter.");
            }
        } while (input.isEmpty());
        return input;
    }

    // Improvement 3: Bounded string length check for descriptions
    public static String readStringWithMaxLength(Scanner sc, String prompt, int maxLength) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Field cannot be left blank.");
            } else if (input.length() > maxLength) {
                System.out.printf("Input must not exceed %d characters (currently %d).%n", maxLength, input.length());
            } else {
                return input;
            }
        }
    }

    // Improvement 2: Yes/No boolean parser
    public static boolean readYesNo(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) return true;
            if (input.equals("n") || input.equals("no")) return false;
            System.out.println("Please enter Y (Yes) or N (No).");
        }
    }

    public static LocalDate readValidDate(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = sc.nextLine().trim();
            try {
                return LocalDate.parse(raw, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Use valid calendar DD-MM-YYYY (e.g. 10-09-2026).");
            }
        }
    }

    public static int readIntInRange(Scanner sc, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String raw = sc.nextLine().trim();
            try {
                int val = Integer.parseInt(raw);
                if (val >= min && val <= max) return val;
                System.out.printf("Please select an option between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Digits only.");
            }
        }
    }
}