public class AdministratorCLI {

    public static void main(String[] args) {
        while (true) {
            clearScreen();
            showHeader();

            printTableMenu("ADMIN PANEL", new String[]{
                    "Client Management",
                    "Agent Management",
                    "Property Management",
                    "Sales",
                    "Rent",
                    "Queries",
                    "Exit"
            });

            int choice = InputUtil.getIntegerInput("\nEnter choice: ");

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

    public static void showHeader() {
        System.out.println("\n==============================================================");
        System.out.println("            REAL ESTATE MANAGEMENT SYSTEM");
        System.out.println("==============================================================");
    }

    public static void printTableMenu(String title, String[] options) {
        int cols = 3;

        int maxLen = title.length();
        for (int i = 0; i < options.length; i++) {
            String text = (i + 1) + ". " + options[i];
            if (text.length() > maxLen) {
                maxLen = text.length();
            }
        }

        int colWidth = maxLen + 2;
        int totalInnerWidth = (colWidth * cols) + (cols - 1);

        System.out.println("╔" + "═".repeat(totalInnerWidth) + "╗");

        System.out.printf("║%-" + totalInnerWidth + "s║%n", centerText(title, totalInnerWidth));

        System.out.println("╠" + "═".repeat(colWidth) + "╦"
                + "═".repeat(colWidth) + "╦"
                + "═".repeat(colWidth) + "╣");

        int index = 0;
        int rows = (int) Math.ceil(options.length / (double) cols);

        for (int r = 0; r < rows; r++) {
            String[] row = new String[cols];

            for (int c = 0; c < cols; c++) {
                if (index < options.length) {
                    row[c] = " " + (index + 1) + ". " + options[index++];
                } else {
                    row[c] = "";
                }
            }

            System.out.printf("║%-" + colWidth + "s║%-" + colWidth + "s║%-" + colWidth + "s║%n",
                    row[0], row[1], row[2]);
        }

        System.out.println("╚" + "═".repeat(colWidth) + "╩"
                + "═".repeat(colWidth) + "╩"
                + "═".repeat(colWidth) + "╝");
    }

    public static String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }

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

    static void clientMenu() {
        while (true) {
            clearScreen();
            showHeader();

            printTableMenu("CLIENT MENU", new String[]{
                    "Add Client",
                    "View Clients",
                    "Find Client by ID",
                    "Update Client",

                    "View Client with Roles",        // NEW
                    "Client Transaction History",    // NEW
                    "Client Summary",                // NEW

                    "Delete Client",
                    "Assign Role",
                    "Search Client by Name",
                    "Count Clients",
                    "Sort Clients by Name",
                    "Sort Clients by ID",
                    "Filter Clients by Role",
                    "Remove Client Role",
                    "Update Client Role",
                    "Check Client Exists",
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("\nEnter choice: ");

            switch (choice) {
                case 1 -> ClientService.addClient();
                case 2 -> ClientService.viewClient();
                case 3 -> ClientService.findClientById();
                case 4 -> ClientService.updateClient();

                case 5 -> ClientService.viewClientWithRoles();      // NEW
                case 6 -> ClientService.viewClientTransactions();   // NEW
                case 7 -> ClientService.clientSummary();            // NEW

                case 8 -> ClientService.deleteClient();
                case 9 -> ClientService.assignRole();
                case 10 -> ClientService.searchClientByName();
                case 11 -> ClientService.countClients();
                case 12 -> ClientService.sortClientsByName();
                case 13 -> ClientService.sortClientsById();
                case 14 -> ClientService.filterClientsByRole();
                case 15 -> ClientService.removeClientRole();
                case 16 -> ClientService.updateClientRole();
                case 17 -> ClientService.checkClientExists();
                case 18 -> { return; }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void agentMenu() {
        while (true) {
            clearScreen();
            showHeader();

            printTableMenu("AGENT MENU", new String[]{
                    "Add Agent",
                    "View Agents",
                    "Find Agent by ID",
                    "Update Agent",

                    "Agent Performance",        // NEW
                    "Agent Summary",            // NEW
                    "Top Performing Agent",     // NEW
                    "View Agent Properties",    // NEW

                    "Delete Agent",
                    "Search Agent by Name",
                    "Filter by Experience",
                    "Filter by Agency",
                    "Count Agents",
                    "Sort Agents by Experience",
                    "Check Agent Exists",
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("\nEnter choice: ");

            switch (choice) {
                case 1 -> AgentService.addAgent();
                case 2 -> AgentService.viewAgent();
                case 3 -> AgentService.findAgentById();
                case 4 -> AgentService.updateAgent();

                case 5 -> AgentService.agentPerformance();     // NEW
                case 6 -> AgentService.agentSummary();         // NEW
                case 7 -> AgentService.topAgent();             // NEW
                case 8 -> AgentService.viewAgentProperties();  // NEW

                case 9 -> AgentService.deleteAgent();
                case 10 -> AgentService.searchAgentByName();
                case 11 -> AgentService.filterAgentsByExperience();
                case 12 -> AgentService.filterAgentsByAgency();
                case 13 -> AgentService.countAgency();
                case 14 -> AgentService.sortAgentsByExperience();
                case 15 -> AgentService.checkAgentExists();
                case 16 -> { return; }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void propertyMenu() {
        while (true) {
            clearScreen();
            showHeader();

            printTableMenu("PROPERTY MENU", new String[]{
                    "Add Property",
                    "View Properties",
                    "Update Availability",
                    "Find Property by ID",
                    "Update Property",

                    "Update Listing",          // NEW
                    "Remove Listing",          // NEW
                    "Sell Property",           // NEW
                    "Rent Property",           // NEW
                    "Relist Property",         // NEW

                    "Delete Property",
                    "Search by City",
                    "Search by Locality",
                    "Filter by Bedrooms",
                    "Filter by Size Range",
                    "Filter by Availability",
                    "Count Properties",
                    "Sort Properties by Price",
                    "Sort Properties by Size",
                    "Check Property Exists",
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("\nEnter choice: ");

            switch (choice) {
                case 1 -> PropertyService.addProperty();
                case 2 -> PropertyService.viewProperties();
                case 3 -> PropertyService.updateAvailability();
                case 4 -> PropertyService.findPropertyById();
                case 5 -> PropertyService.updateProperty();

                case 6 -> PropertyService.updateListing();     // NEW
                case 7 -> PropertyService.removeListing();     // NEW
                case 8 -> PropertyService.sellProperty();      // NEW
                case 9 -> PropertyService.rentProperty();      // NEW
                case 10 -> PropertyService.relistProperty();   // NEW

                case 11 -> PropertyService.deleteProperty();
                case 12 -> PropertyService.searchPropertyByCity();
                case 13 -> PropertyService.searchPropertyByLocality();
                case 14 -> PropertyService.filterByBedrooms();
                case 15 -> PropertyService.filterBySizeRange();
                case 16 -> PropertyService.filterByAvailability();
                case 17 -> PropertyService.countProperties();
                case 18 -> PropertyService.sortPropertiesByPrice();
                case 19 -> PropertyService.sortPropertiesBySize();
                case 20 -> PropertyService.checkPropertyExists();
                case 21 -> { return; }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void salesMenu() {
        while (true) {
            clearScreen();
            showHeader();

            printTableMenu("SALES MENU", new String[]{
                    "Record Sale",
                    "View Sales",
                    "Find Sale by ID",

                    "Sales Summary",          // NEW
                    "Top Buyer",              // NEW
                    "Sales by Date Range",    // NEW

                    "Delete Sale",
                    "Filter Sales by Date",
                    "Filter Sales by Agent",
                    "Filter Sales by Property",
                    "Total Sales Amount",
                    "Count Sales",
                    "Sort Sales by Amount",
                    "Sort Sales by Date",
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("\nEnter choice: ");

            switch (choice) {
                case 1 -> SalesService.RecordSale();
                case 2 -> SalesService.viewSales();
                case 3 -> SalesService.findSaleById();

                case 4 -> SalesService.salesSummary();      // NEW
                case 5 -> SalesService.topBuyerInfo();          // NEW
                case 6 -> SalesService.salesByDateRange();  // NEW

                case 7 -> SalesService.deleteSale();
                case 8 -> SalesService.filterSalesByDate();
                case 9 -> SalesService.filterSalesByAgent();
                case 10 -> SalesService.filterSalesByProperty();
                case 11 -> SalesService.totalSalesAmount();
                case 12 -> SalesService.countSales();
                case 13 -> SalesService.sortSalesByAmount();
                case 14 -> SalesService.sortSalesByDate();
                case 15 -> { return; }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void rentMenu() {
        while (true) {
            clearScreen();
            showHeader();

            printTableMenu("RENT MENU", new String[]{
                    "Record Rent",
                    "View Rent Records",
                    "Find Rent by ID",

                    "Rent Summary",          // NEW
                    "Top Tenant",            // NEW
                    "Rent by Date Range",    // NEW

                    "Delete Rent",
                    "Filter Rent by Date",
                    "Filter Rent by Client",
                    "Filter Rent by Property",
                    "Total Rent Amount",
                    "Count Rents",
                    "Sort Rent by Amount",
                    "Sort Rent by Date",
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("\nEnter choice: ");

            switch (choice) {
                case 1 -> RentService.recordRent();
                case 2 -> RentService.viewRent();
                case 3 -> RentService.findRentById();

                case 4 -> RentService.rentSummary();       // NEW
                case 5 -> RentService.topTenant();         // NEW
                case 6 -> RentService.rentByDateRange();   // NEW

                case 7 -> RentService.deleteRent();
                case 8 -> RentService.filterRentByDate();
                case 9 -> RentService.filterRentByClient();
                case 10 -> RentService.filterRentByProperty();
                case 11 -> RentService.totalRentAmount();
                case 12 -> RentService.countRents();
                case 13 -> RentService.sortRentByAmount();
                case 14 -> RentService.sortRentByDate();
                case 15 -> { return; }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void queryMenu() {
        while (true) {
            clearScreen();
            showHeader();

            printTableMenu("PROPERTY ANALYSIS MENU", new String[]{
                    " Find Available Properties (City + Year)",
                    " Find Properties by Price Range",
                    " Find Rental Properties (Locality + BHK + Rent)",
                    " Top Selling Agent (Year-wise)",
                    " Agent Performance (Avg Price & Selling Time)",
                    " Most Expensive & Highest Rent Property",
                    " Run Custom SQL Query",
                    " Back"
            });

            int choice = InputUtil.getIntegerInput("\nEnter choice: ");

            try {
                switch (choice) {
                    case 1 -> QueryService.query1();
                    case 2 -> QueryService.query2();
                    case 3 -> QueryService.query3();
                    case 4 -> QueryService.query4();
                    case 5 -> QueryService.query5();
                    case 6 -> QueryService.query6();
                    case 7 -> QueryService.runCustomQuery();
                    case 8 -> { return; }
                    default -> System.out.println("❌ Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }

            System.out.println("\nPress Enter to continue...");
            InputUtil.sc.nextLine();
        }
    }
}