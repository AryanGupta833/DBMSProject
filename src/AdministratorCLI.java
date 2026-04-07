import java.util.Scanner;

public class AdministratorCLI {

    public static void main(String[] args) {
        while (true) {
            clearScreen();
            showHeader();

            printTableMenu("ADMIN PANEL", new String[]{
                    "Clients",
                    "Agents",
                    "Property Management",
                    "Sales",
                    "Rent",
                    "Queries",
                    "Exit"
            });

            // Replaced custom InputUtil with a standard scanner placeholder for compilation
            // Ensure you use your InputUtil.getIntegerInput in your actual code
            int choice = getIntegerInput("\nEnter choice: ");

            switch (choice) {
                case 1 -> clientMenu();
                case 2 -> agentMenu();
                case 3 -> propertyMenu();
                case 4 -> salesMenu();
                case 5 -> rentMenu();
                case 6 -> queryMenu();
                case 7 -> System.exit(0);
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ================= HEADER =================
    public static void showHeader() {
        System.out.println("\n==============================================================");
        System.out.println("            REAL ESTATE MANAGEMENT SYSTEM");
        System.out.println("==============================================================");
    }

    // ================= STABLE 3-COLUMN TABLE =================
    public static void printTableMenu(String title, String[] options) {
        int cols = 3;

        // 1. Find max length to ensure all columns fit the longest text
        int maxLen = title.length();
        for (int i = 0; i < options.length; i++) {
            // Include space for "XX. " prefix
            String text = (i + 1) + ". " + options[i];
            if (text.length() > maxLen) {
                maxLen = text.length();
            }
        }

        // 2. Calculate Exact Widths
        // +2 for aesthetic left/right padding inside the table cells
        int colWidth = maxLen + 2;
        // 3 columns + 2 internal vertical dividers (║)
        int totalInnerWidth = (colWidth * cols) + (cols - 1);

        // 3. Draw Top Border
        System.out.println("╔" + "═".repeat(totalInnerWidth) + "╗");

        // 4. Draw Centered Title
        System.out.printf("║%-" + totalInnerWidth + "s║%n", centerText(title, totalInnerWidth));

        // 5. Draw Middle Divider (Splitting into 3 columns)
        System.out.println("╠" + "═".repeat(colWidth) + "╦"
                + "═".repeat(colWidth) + "╦"
                + "═".repeat(colWidth) + "╣");

        // 6. Draw Content Rows
        int index = 0;
        int rows = (int) Math.ceil(options.length / (double) cols);

        for (int r = 0; r < rows; r++) {
            String[] row = new String[cols];

            for (int c = 0; c < cols; c++) {
                if (index < options.length) {
                    // Added a leading space so text doesn't touch the left line
                    row[c] = " " + (index + 1) + ". " + options[index++];
                } else {
                    row[c] = ""; // Empty cell if we run out of options
                }
            }

            // Print the 3 columns strictly bound by colWidth
            System.out.printf("║%-" + colWidth + "s║%-" + colWidth + "s║%-" + colWidth + "s║%n",
                    row[0], row[1], row[2]);
        }

        // 7. Draw Bottom Border
        System.out.println("╚" + "═".repeat(colWidth) + "╩"
                + "═".repeat(colWidth) + "╩"
                + "═".repeat(colWidth) + "╝");
    }

    // ================= CENTER TEXT =================
    public static String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        // String.format will handle the right-side padding dynamically in printTableMenu
        return " ".repeat(Math.max(0, padding)) + text;
    }

    // ================= CLEAR SCREEN =================
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("Error clearing screen");
        }
    }

    // ================= MOCK INPUT (Replace with InputUtil) =================
    private static int getIntegerInput(String prompt) {
        System.out.print(prompt);
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. " + prompt);
            scanner.next();
        }
        return scanner.nextInt();
    }

    // ================= MENUS =================
    // Note: Replaced your Service calls with print statements for immediate testing.
    // Revert these back to your actual Service classes.

    static void clientMenu() {
        while (true) {
            clearScreen();
            showHeader();
            printTableMenu("CLIENT MENU", new String[]{"Add Client", "View Clients", "Assign Role", "Back"});
            int choice = getIntegerInput("\nEnter choice: ");
            switch (choice) {
                case 1 -> System.out.println("Executing: ClientService.addClient()");
                case 2 -> System.out.println("Executing: ClientService.viewClient()");
                case 3 -> System.out.println("Executing: ClientService.assignRole()");
                case 4 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void agentMenu() {
        while (true) {
            clearScreen();
            showHeader();
            printTableMenu("AGENT MENU", new String[]{"Add Agent", "View Agents", "Back"});
            int choice = getIntegerInput("\nEnter choice: ");
            switch (choice) {
                case 1 -> System.out.println("Executing: AgentService.addAgent()");
                case 2 -> System.out.println("Executing: AgentService.viewAgent()");
                case 3 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void propertyMenu() {
        while (true) {
            clearScreen();
            showHeader();
            printTableMenu("PROPERTY MENU", new String[]{"Add Property", "View Properties", "Update Availability", "Back"});
            int choice = getIntegerInput("\nEnter choice: ");
            switch (choice) {
                case 1 -> System.out.println("Executing: PropertyService.addProperty()");
                case 2 -> System.out.println("Executing: PropertyService.viewProperties()");
                case 3 -> System.out.println("Executing: PropertyService.updateAvailability()");
                case 4 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void salesMenu() {
        while (true) {
            clearScreen();
            showHeader();
            printTableMenu("SALES MENU", new String[]{"Record Sale", "View Sales", "Back"});
            int choice = getIntegerInput("\nEnter choice: ");
            switch (choice) {
                case 1 -> System.out.println("Executing: SalesService.RecordSale()");
                case 2 -> System.out.println("Executing: SalesService.viewSales()");
                case 3 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void rentMenu() {
        while (true) {
            clearScreen();
            showHeader();
            printTableMenu("RENT MENU", new String[]{"Record Rent", "View Rent Records", "Back"});
            int choice = getIntegerInput("\nEnter choice: ");
            switch (choice) {
                case 1 -> System.out.println("Executing: RentService.recordRent()");
                case 2 -> System.out.println("Executing: RentService.viewRent()");
                case 3 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void queryMenu() {
        while (true) {
            clearScreen();
            showHeader();
            printTableMenu("QUERY DASHBOARD", new String[]{
                    "Rent Houses After 2023", "Price Between 20L and 60L",
                    "2BHK Under 15000", "Top Selling Agent",
                    "Average Price and Market Time", "Most Expensive and High Rent Houses", "Back"
            });
            int choice = getIntegerInput("\nEnter choice: ");
            switch (choice) {
                case 1 -> System.out.println("Executing: QueryService.query1()");
                case 2 -> System.out.println("Executing: QueryService.query2()");
                case 3 -> System.out.println("Executing: QueryService.query3()");
                case 4 -> System.out.println("Executing: QueryService.query4()");
                case 5 -> System.out.println("Executing: QueryService.query5()");
                case 6 -> System.out.println("Executing: QueryService.query6()");
                case 7 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}