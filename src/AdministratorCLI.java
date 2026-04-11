import java.util.Scanner;

public class AdministratorCLI {

    public static void start() {
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

                    // 🔹 CRUD
                    "Add Client",
                    "View Clients",
                    "Find Client by ID",
                    "Update Client",

                    // 🔹 Client Insights
                    "View Client with Roles",
                    "Client Transaction History",
                    "Client Summary",

                    // 🔥 Client Analytics (NEW)
                    "Top Buyer by Purchases",
                    "Repeat Clients",
                    "Client Revenue Contribution",
                    "Clients With No Transactions",

                    // 🔹 Management
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

                    // 🔹 Exit
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("\nEnter choice: ");

            switch (choice) {

                // 🔹 CRUD
                case 1 -> ClientService.addClient();
                case 2 -> ClientService.viewClient();
                case 3 -> ClientService.findClientById();
                case 4 -> ClientService.updateClient();

                // 🔹 Insights
                case 5 -> ClientService.viewClientWithRoles();
                case 6 -> ClientService.viewClientTransactions();
                case 7 -> ClientService.clientSummary();

                // 🔥 Analytics
                case 8 -> ClientService.topBuyerByPurchases();
                case 9 -> ClientService.repeatClients();
                case 10 -> ClientService.clientRevenueContribution();
                case 11 -> ClientService.clientsWithNoTransactions();

                // 🔹 Management
                case 12 -> ClientService.deleteClient();
                case 13 -> ClientService.assignRole();
                case 14 -> ClientService.searchClientByName();
                case 15 -> ClientService.countClients();
                case 16 -> ClientService.sortClientsByName();
                case 17 -> ClientService.sortClientsById();
                case 18 -> ClientService.filterClientsByRole();
                case 19 -> ClientService.removeClientRole();
                case 20 -> ClientService.updateClientRole();
                case 21 -> ClientService.checkClientExists();

                // 🔹 Exit
                case 22 -> { return; }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void agentMenu() {
        while (true) {
            clearScreen();
            showHeader();

            printTableMenu("AGENT MENU", new String[]{

                    // 🔹 CRUD
                    "Add Agent",
                    "View Agents",
                    "Find Agent by ID",
                    "Update Agent",

                    // 🔹 Analytics (Core)
                    "Agent Performance",
                    "Agent Summary",
                    "Top Performing Agent",
                    "Bottom Performing Agent",
                    "View Agent Properties",

                    // 🔹 Advanced Queries
                    "Agent Revenue",
                    "Active Listings",
                    "Deal Breakdown",
                    "Portfolio Value",
                    "Success Rate",
                    "Deals History",
                    "Agents By City",
                    "Agent Workload",

                    // 🔥 NEW ADVANCED ANALYTICS
                    "Top Agent by Revenue",
                    "Agent Sales Count",
                    "Agent Revenue Breakdown",
                    "Agents With No Deals",
                    "Agents With No Sales",

                    // 🔹 Utility
                    "Delete Agent",
                    "Search Agent by Name",
                    "Filter by Experience",
                    "Filter by Agency",
                    "Count Agents",
                    "Sort Agents by Experience",
                    "Check Agent Exists",

                    // 🔹 Exit
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("\nEnter choice: ");

            switch (choice) {

                // 🔹 CRUD
                case 1 -> AgentService.addAgent();
                case 2 -> AgentService.viewAgent();
                case 3 -> AgentService.findAgentById();
                case 4 -> AgentService.updateAgent();

                // 🔹 Core Analytics
                case 5 -> AgentService.agentPerformance();
                case 6 -> AgentService.agentSummary();
                case 7 -> AgentService.topAgent();
                case 8 -> AgentService.bottomAgentByRevenue();
                case 9 -> AgentService.viewAgentProperties();

                // 🔹 Advanced Queries
                case 10 -> AgentService.agentRevenue();
                case 11 -> AgentService.agentActiveListings();
                case 12 -> AgentService.agentDealBreakdown();
                case 13 -> AgentService.agentPortfolioValue();
                case 14 -> AgentService.agentSuccessRate();
                case 15 -> AgentService.agentDealsHistory();
                case 16 -> AgentService.agentsByCity();
                case 17 -> AgentService.agentWorkload();

                // 🔥 NEW ADVANCED ANALYTICS
                case 18 -> AgentService.topAgentByRevenue();
                case 19 -> AgentService.agentSalesCount();
                case 20 -> AgentService.agentRevenueBreakdown();
                case 21 -> AgentService.agentsWithNoDeals();
                case 22 -> AgentService.agentsWithNoSales();

                // 🔹 Utility
                case 23 -> AgentService.deleteAgent();
                case 24 -> AgentService.searchAgentByName();
                case 25 -> AgentService.filterAgentsByExperience();
                case 26 -> AgentService.filterAgentsByAgency();
                case 27 -> AgentService.countAgents();
                case 28 -> AgentService.sortAgentsByExperience();
                case 29 -> AgentService.checkAgentExists();

                // 🔹 Exit
                case 30 -> { return; }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void propertyMenu() {
        while (true) {
            clearScreen();
            showHeader();

            printTableMenu("PROPERTY MENU", new String[]{

                    // 🔹 CRUD
                    "Add Property",
                    "View Properties",
                    "Update Availability",
                    "Find Property by ID",
                    "Update Property",

                    // 🔹 Listing & Transactions
                    "Update Listing",
                    "Remove Listing",
                    "Sell Property",
                    "Rent Property",
                    "Relist Property",

                    // 🔹 Search & Filters
                    "Search by City",
                    "Search by Locality",
                    "Filter by Bedrooms",
                    "Filter by Size Range",
                    "Filter by Availability",
                    "Filter by Price Range",
                    "Filter by Type",
                    "Filter by Year Built",

                    // 🔹 Analytics (🔥 important)
                    "Most Expensive Property",
                    "Average Property Price",

                    // 🔹 Deal Insights (🔥 NEW)
                    "View All Deals",
                    "Deals by Date Range",
                    "Total Deal Value",

                    // 🔹 Utility
                    "Delete Property",
                    "Count Properties",
                    "Sort Properties by Price",
                    "Sort Properties by Size",
                    "Check Property Exists",

                    // 🔹 Exit
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("\nEnter choice: ");

            switch (choice) {

                // 🔹 CRUD
                case 1 -> PropertyService.addProperty();
                case 2 -> PropertyService.viewProperties();
                case 3 -> PropertyService.updateAvailability();
                case 4 -> PropertyService.findPropertyById();
                case 5 -> PropertyService.updateProperty();

                // 🔹 Listing & Transactions
                case 6 -> PropertyService.updateListing();
                case 7 -> PropertyService.removeListing();
                case 8 -> PropertyService.sellProperty();
                case 9 -> PropertyService.rentProperty();
                case 10 -> PropertyService.relistProperty();

                // 🔹 Search & Filters
                case 11 -> PropertyService.searchPropertyByCity();
                case 12 -> PropertyService.searchPropertyByLocality();
                case 13 -> PropertyService.filterByBedrooms();
                case 14 -> PropertyService.filterBySizeRange();
                case 15 -> PropertyService.filterByAvailability();
                case 16 -> PropertyService.propertiesByPriceRange();
                case 17 -> PropertyService.propertiesByType();
                case 18 -> PropertyService.propertiesByYear();

                // 🔹 Analytics
                case 19 -> PropertyService.mostExpensiveProperty();
                case 20 -> PropertyService.averagePropertyPrice();

                // 🔹 Deal Insights
                case 21 -> DealService.viewAllDeals();
                case 22 -> DealService.dealsByDateRange();
                case 23 -> DealService.totalDealValue();

                // 🔹 Utility
                case 24 -> PropertyService.deleteProperty();
                case 25 -> PropertyService.countProperties();
                case 26 -> PropertyService.sortPropertiesByPrice();
                case 27 -> PropertyService.sortPropertiesBySize();
                case 28 -> PropertyService.checkPropertyExists();

                // 🔹 Exit
                case 29 -> { return; }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void salesMenu() {
        while (true) {
            clearScreen();
            showHeader();

            printTableMenu("SALES MENU", new String[]{
                    // 🔹 Core
                    "Record Sale",
                    "View Sales",
                    "Find Sale by ID",

                    // 🔹 Basic Analytics
                    "Sales Summary",
                    "Top Buyer",
                    "Sales by Date Range",

                    // 🔹 Filters
                    "Filter Sales by Date",
                    "Filter Sales by Agent",
                    "Filter Sales by Property",

                    // 🔹 Aggregates
                    "Total Sales Amount",
                    "Count Sales",

                    // 🔹 Sorting
                    "Sort Sales by Amount",
                    "Sort Sales by Date",

                    // 🔥 Advanced Sales Analytics
                    "Top Agent by Sales Revenue",
                    "Sales by City",
                    "Monthly Sales Report",
                    "High Value Sales",
                    "Unsold Properties",
                    "Repeat Buyers",

                    // 🔹 Utility
                    "Delete Sale",

                    // 🔹 Exit
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("\nEnter choice: ");

            switch (choice) {

                // 🔹 Core
                case 1 -> SalesService.RecordSale();
                case 2 -> SalesService.viewSales();
                case 3 -> SalesService.findSaleById();

                // 🔹 Basic Analytics
                case 4 -> SalesService.salesSummary();
                case 5 -> SalesService.topBuyerInfo();
                case 6 -> SalesService.salesByDateRange();

                // 🔹 Filters
                case 7 -> SalesService.filterSalesByDate();
                case 8 -> SalesService.filterSalesByAgent();
                case 9 -> SalesService.filterSalesByProperty();

                // 🔹 Aggregates
                case 10 -> SalesService.totalSalesAmount();
                case 11 -> SalesService.countSales();

                // 🔹 Sorting
                case 12 -> SalesService.sortSalesByAmount();
                case 13 -> SalesService.sortSalesByDate();

                // 🔥 Advanced Sales Analytics
                case 14 -> SalesService.topAgentBySalesRevenue();
                case 15 -> SalesService.salesByCity();
                case 16 -> SalesService.monthlySalesReport();
                case 17 -> SalesService.highValueSales();
                case 18 -> SalesService.unsoldProperties();
                case 19 -> SalesService.repeatBuyers();

                // 🔹 Utility
                case 20 -> SalesService.deleteSale();

                // 🔹 Exit
                case 21 -> { return; }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void rentMenu() {
        while (true) {
            clearScreen();
            showHeader();

            printTableMenu("RENT MENU", new String[]{
                    // 🔹 Core
                    "Record Rent",
                    "View Rent Records",
                    "Find Rent by ID",

                    // 🔹 Basic Analytics
                    "Rent Summary",
                    "Top Tenant",
                    "Rent by Date Range",

                    // 🔹 Filters
                    "Filter Rent by Date",
                    "Filter Rent by Client",
                    "Filter Rent by Property",

                    // 🔹 Aggregates
                    "Total Rent Amount",
                    "Count Rents",

                    // 🔹 Sorting
                    "Sort Rent by Amount",
                    "Sort Rent by Date",

                    // 🔥 Advanced Rent Analytics
                    "Top Agent by Rent Revenue",
                    "Rent by City",
                    "Monthly Rent Report",
                    "High Value Rentals",
                    "Vacant Properties",
                    "Repeat Tenants",

                    // 🔹 Utility
                    "Delete Rent",

                    // 🔹 Exit
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("\nEnter choice: ");

            switch (choice) {

                // 🔹 Core
                case 1 -> RentService.recordRent();
                case 2 -> RentService.viewRent();
                case 3 -> RentService.findRentById();

                // 🔹 Basic Analytics
                case 4 -> RentService.rentSummary();
                case 5 -> RentService.topTenant();
                case 6 -> RentService.rentByDateRange();

                // 🔹 Filters
                case 7 -> RentService.filterRentByDate();
                case 8 -> RentService.filterRentByClient();
                case 9 -> RentService.filterRentByProperty();

                // 🔹 Aggregates
                case 10 -> RentService.totalRentAmount();
                case 11 -> RentService.countRents();

                // 🔹 Sorting
                case 12 -> RentService.sortRentByAmount();
                case 13 -> RentService.sortRentByDate();

                // 🔥 Advanced Rent Analytics
                case 14 -> RentService.topAgentByRentRevenue();
                case 15 -> RentService.rentByCity();
                case 16 -> RentService.monthlyRentReport();
                case 17 -> RentService.highValueRentals();
                case 18 -> RentService.vacantProperties();
                case 19 -> RentService.repeatTenants();

                // 🔹 Utility
                case 20 -> RentService.deleteRent();

                // 🔹 Exit
                case 21 -> { return; }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void queryMenu() {
        while (true) {
            clearScreen();
            showHeader();

            printTableMenu("PROPERTY ANALYSIS MENU", new String[]{
                    "1. Find Available Properties (City + Year)",
                    "2. Find Properties by Price Range",
                    "3. Find Rental Properties (Locality + BHK + Rent)",
                    "4. Top Selling Agent (Year-wise)",
                    "5. Agent Performance (Avg Price & Selling Time)",
                    "6. Most Expensive & Highest Rent Property",
                    "7. Run Custom SQL Query",
                    "8. Back"
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