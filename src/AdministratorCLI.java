public class AdministratorCLI {

    public static void main(String[] args) {
        while (true) {
            clearScreen();
            printMenu("ADMIN PANEL", new String[]{
                    "Client Management",
                    "Agent Management",
                    "Property Management",
                    "Sales",
                    "Rent",
                    "Queries",
                    "Exit"
            });

            int choice = InputUtil.getIntegerInput("Enter choice: ");

            switch (choice) {
                case 1 -> clientMenu();
                case 2 -> agentMenu();
                case 3 -> propertyMenu();
                case 4 -> salesMenu();
                case 5 -> rentMenu();
                case 6 -> queryMenu();
                case 7 -> {
                    System.out.println("Exiting application...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ================= COMMON UI METHODS =================

    public static void printMenu(String title, String[] options) {
        System.out.println("\n========================================");
        System.out.printf(" %-38s%n", title);
        System.out.println("========================================");

        for (int i = 0; i < options.length; i++) {
            System.out.printf(" %d. %s%n", i + 1, options[i]);
        }

        System.out.println("========================================");
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // ================= CLIENT MENU =================

    static void clientMenu() {
        while (true) {
            clearScreen();
            printMenu("CLIENT MENU", new String[]{
                    "Add Client",
                    "View Clients",
                    "Assign Role",
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("Enter choice: ");

            switch (choice) {
                case 1 -> ClientService.addClient();
                case 2 -> ClientService.viewClient();
                case 3 -> ClientService.assignRole();
                case 4 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ================= AGENT MENU =================

    static void agentMenu() {
        while (true) {
            clearScreen();
            printMenu("AGENT MENU", new String[]{
                    "Add Agent",
                    "View Agents",
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("Enter choice: ");

            switch (choice) {
                case 1 -> AgentService.addAgent();
                case 2 -> AgentService.viewAgent();
                case 3 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ================= PROPERTY MENU =================

    static void propertyMenu() {
        while (true) {
            clearScreen();
            printMenu("PROPERTY MENU", new String[]{
                    "Add Property",
                    "View Properties",
                    "Update Availability",
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("Enter choice: ");

            switch (choice) {
                case 1 -> PropertyService.addProperty();
                case 2 -> PropertyService.viewProperties();
                case 3 -> PropertyService.updateAvailability();
                case 4 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ================= SALES MENU =================

    static void salesMenu() {
        while (true) {
            clearScreen();
            printMenu("SALES MENU", new String[]{
                    "Record Sale",
                    "View Sales",
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("Enter choice: ");

            switch (choice) {
                case 1 -> SalesService.RecordSale();
                case 2 -> SalesService.viewSales();
                case 3 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ================= RENT MENU =================

    static void rentMenu() {
        while (true) {
            clearScreen();
            printMenu("RENT MENU", new String[]{
                    "Record Rent",
                    "View Rent Records",
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("Enter choice: ");

            switch (choice) {
                case 1 -> RentService.recordRent();
                case 2 -> RentService.viewRent();
                case 3 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ================= QUERY MENU =================

    static void queryMenu() {
        while (true) {
            clearScreen();
            printMenu("QUERY MENU", new String[]{
                    "Houses in city after 2023 available for rent",
                    "Houses between 20L and 60L",
                    "2BHK rent under 15000 in locality",
                    "Top selling agent in 2023",
                    "Average selling price & market time",
                    "Most expensive & highest rent houses",
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("Enter choice: ");

            switch (choice) {
                case 1 -> QueryService.query1();
                case 2 -> QueryService.query2();
                case 3 -> QueryService.query3();
                case 4 -> QueryService.query4();
                case 5 -> QueryService.query5();
                case 6 -> QueryService.query6();
                case 7 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}