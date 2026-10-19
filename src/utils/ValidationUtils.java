package utils;

import java.util.Scanner;

public class ValidationUtils {

    private static final String RETRY = "Enter again: ";

    public static int readInt(Scanner scanner, String message) {

        String prompt = message;

        while (true) {

            System.out.print(prompt);

            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            }

            scanner.nextLine();
            System.out.println("Error: Invalid input. Please enter a number.\n");
            prompt = RETRY;
        }
    }

    public static int readPositiveInt(Scanner scanner, String message, String fieldName) {

        String prompt = message;

        while (true) {
            int value = readInt(scanner, prompt);

            if (value > 0) {
                return value;
            }
            System.out.println("Error: " + fieldName + " must be greater than 0.\n");
            prompt = RETRY;
        }
    }

    public static int readNonNegativeInt(Scanner scanner, String message, String fieldName) {

        String prompt = message;

        while (true) {
            int value = readInt(scanner, prompt);

            if (value >= 0) {
                return value;
            }
            System.out.println("Error: " + fieldName + " cannot be negative.\n");
            prompt = RETRY;
        }
    }

    public static double readPositiveDouble(Scanner scanner, String message, String fieldName) {

        String prompt = message;

        while (true) {
            double value = readDouble(scanner, prompt);

            if (value > 0) {
                return value;
            }
            System.out.println("Error: " + fieldName + " must be greater than 0.\n");
            prompt = RETRY;
        }
    }

    public static double readDouble(Scanner scanner, String message) {

        String prompt = message;

        while (true) {

            System.out.print(prompt);

            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a valid number.\n");
                prompt = RETRY;
            }
        }
    }

    public static String readText(Scanner scanner, String message, String fieldName) {

        String prompt = message;

        while (true) {

            System.out.print(prompt);
            String text = scanner.nextLine().trim();

            if (!text.isEmpty()) {
                return text;
            }
            System.out.println("Error: " + fieldName + " cannot be empty.\n");
            prompt = RETRY;
        }
    }

    // The "OrKeep" methods return the current value when the user just presses Enter.

    public static String readTextOrKeep(Scanner scanner, String message, String current) {

        System.out.print(message);
        String text = scanner.nextLine().trim();

        return text.isEmpty() ? current : text;
    }

    public static int readPositiveIntOrKeep(Scanner scanner, String message, String fieldName,
                                            int current) {

        String prompt = message;

        while (true) {

            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return current;
            }
            try {
                int value = Integer.parseInt(input);
                if (value > 0) {
                    return value;
                }
                System.out.println("Error: " + fieldName + " must be greater than 0.\n");
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a number.\n");
            }
            prompt = RETRY;
        }
    }

    public static double readPositiveDoubleOrKeep(Scanner scanner, String message, String fieldName,
                                                  double current) {

        String prompt = message;

        while (true) {

            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return current;
            }
            try {
                double value = Double.parseDouble(input);
                if (value > 0) {
                    return value;
                }
                System.out.println("Error: " + fieldName + " must be greater than 0.\n");
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a valid number.\n");
            }
            prompt = RETRY;
        }
    }

    public static String readEmail(Scanner scanner, String message) {

        String prompt = message;

        while (true) {
            String email = readText(scanner, prompt, "Email");

            if (email.contains("@") && email.indexOf('@') < email.lastIndexOf('.')
                    && !email.contains(" ")) {
                return email;
            }
            System.out.println("Error: Please enter a valid email address.\n");
            prompt = RETRY;
        }
    }

    public static int readId(Scanner scanner, String message, String prefix) {

        String prompt = message;

        while (true) {

            System.out.print(prompt);
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.startsWith(prefix)) {
                input = input.substring(prefix.length());
            }

            try {
                int id = Integer.parseInt(input);
                if (id >= 0) {
                    return id;
                }
            } catch (NumberFormatException e) {
                // handled by the error message below
            }

            System.out.println(
                    "Error: Invalid input. Please enter a valid numeric or prefixed ID (e.g., "
                            + prefix + "001).\n"
            );
            prompt = RETRY;
        }
    }

}
