import java.util.Scanner;
import java.util.regex.Pattern;

public class InputUtil {

    static Scanner sc = new Scanner(System.in);

    // ----------------------------------------------------------------
    // Basic input helpers
    // ----------------------------------------------------------------

    public static int getIntegerInput(String message) {
        while (true) {
            try {
                System.out.print(Color.CYAN + message + Color.RESET + " ");
                String line = sc.nextLine().trim();
                return Integer.parseInt(line);
            } catch (Exception e) {
                System.out.println(Color.RED + "   ⚠  Invalid number. Please try again." + Color.RESET);
            }
        }
    }

    public static String getStringInput(String message) {
        while (true) {
            System.out.print(Color.CYAN + message + Color.RESET + " ");
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println(Color.RED + "   ⚠  Input cannot be empty." + Color.RESET);
        }
    }

    /** Like getStringInput but allows blank (returns "" if user hits Enter). */
    public static String getOptionalStringInput(String message) {
        System.out.print(Color.CYAN + message + Color.RESET + " ");
        return sc.nextLine().trim();
    }

    public static String getEmail(String message) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        while (true) {
            System.out.print(Color.CYAN + message + Color.RESET + " ");
            String email = sc.nextLine().trim();
            if (Pattern.matches(emailRegex, email)) return email;
            System.out.println(Color.RED + "   ⚠  Invalid email format (e.g. user@example.com)." + Color.RESET);
        }
    }

    public static String getPhone(String message) {
        while (true) {
            System.out.print(Color.CYAN + message + Color.RESET + " ");
            String phone = sc.nextLine().trim();
            if (phone.matches("\\d{10}")) return phone;
            System.out.println(Color.RED + "   ⚠  Phone must be exactly 10 digits." + Color.RESET);
        }
    }

    public static int getPositiveInt(String message) {
        while (true) {
            int value = getIntegerInput(message);
            if (value > 0) return value;
            System.out.println(Color.RED + "   ⚠  Value must be greater than 0." + Color.RESET);
        }
    }

    public static int getIntInRange(String message, int min, int max) {
        while (true) {
            int value = getIntegerInput(message);
            if (value >= min && value <= max) return value;
            // BUG FIX: was broken string concat "\"+ min +\" and" + max
            System.out.println(Color.RED + "   ⚠  Enter a value between " + min + " and " + max + "." + Color.RESET);
        }
    }

    // ----------------------------------------------------------------
    // Password / masked input
    // ----------------------------------------------------------------

    /**
     * Reads a password without echoing characters.
     * Falls back to plain Scanner if no console is available
     * (e.g. when running inside an IDE).
     */
    public static String getMaskedInput(String message) {
        System.out.print(Color.CYAN + message + Color.RESET);
        java.io.Console console = System.console();
        if (console != null) {
            char[] chars = console.readPassword();
            return chars == null ? "" : new String(chars);
        } else {
            // IDE fallback — input is visible but code still works
            return sc.nextLine().trim();
        }
    }

    // ----------------------------------------------------------------
    // Confirmation helpers
    // ----------------------------------------------------------------

    /** Returns true if user types y / Y / yes / YES. */
    public static boolean confirm(String message) {
        System.out.print(Color.YELLOW + message + " [y/N]: " + Color.RESET);
        String ans = sc.nextLine().trim().toLowerCase();
        return ans.equals("y") || ans.equals("yes");
    }

    // ----------------------------------------------------------------
    // Misc
    // ----------------------------------------------------------------

    public static void pressEnterToContinue() {
        System.out.print(Color.DIM + "\n  Press Enter to continue..." + Color.RESET);
        sc.nextLine();
    }
}