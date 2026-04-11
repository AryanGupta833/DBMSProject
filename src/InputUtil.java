import java.util.Scanner;
import java.util.regex.Pattern;

public class InputUtil {

    static Scanner sc = new Scanner(System.in);


    public static int getIntegerInput(String message) {
        while (true) {
            try {
                System.out.println(message);
                return Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }


    public static String getStringInput(String message) {
        while (true) {
            System.out.println(message);
            String input = sc.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("Input cannot be empty.");
            }
        }
    }


    public static String getEmail(String message) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";

        while (true) {
            System.out.println(message);
            String email = sc.nextLine().trim();

            if (Pattern.matches(emailRegex, email)) {
                return email;
            } else {
                System.out.println("Invalid email format.");
            }
        }
    }


    public static String getPhone(String message) {
        while (true) {
            System.out.println(message);
            String phone = sc.nextLine().trim();

            if (phone.matches("\\d{10}")) {
            return phone;
        } else {
            System.out.println("Phone must be 10 digits.");
        }
    }
}


public static int getPositiveInt(String message) {
    while (true) {
        int value = InputUtil.getIntegerInput(message);
        if (value > 0) return value;

        System.out.println("Must be greater than 0.");
    }
}


public static int getIntInRange(String message, int min, int max) {
    while (true) {
        int value = InputUtil.getIntegerInput(message);

        if (value >= min && value <= max) {
            return value;
        } else {
            System.out.println("Enter value between \" + min + \" and " + max);
        }
    }
}


public static void pressEnterToContinue() {
    System.out.println("\nPress Enter to continue...");
            sc.nextLine();
    }
}
