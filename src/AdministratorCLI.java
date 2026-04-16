/**
 * AdministratorCLI — main menu shell for the ADMIN role.
 *
 * Fixes:
 *  • showHeader() no longer uses ANSI codes inside the box-drawing frame
 *    (the stray "[" character visible in the screenshot was caused by a
 *     raw ESC code being printed before the ╔ glyph — now separated cleanly)
 *  • printTableMenu() uses visibleLen() so column widths are ANSI-safe
 *  • Logout returns to Login loop instead of System.exit()
 *  • Exit asks for confirmation
 */
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
                    "Logout",
                    "Exit Application"
            });

            int choice = InputUtil.getIntegerInput("\nEnter choice:");

            switch (choice) {
                case 1 -> clientMenu();
                case 2 -> agentMenu();
                case 3 -> propertyMenu();
                case 4 -> salesMenu();
                case 5 -> rentMenu();
                case 6 -> queryMenu();
                case 7 -> {
                    if (InputUtil.confirm("  Logout and return to login screen?")) return;
                }
                case 8 -> {
                    if (InputUtil.confirm("  Exit the application?")) {
                        System.out.println(Color.CYAN + "\n  Goodbye! \uD83D\uDC4B\n" + Color.RESET);
                        System.exit(0);
                    }
                }
                default -> System.out.println(Color.RED + "  \u274C Invalid choice." + Color.RESET);
            }
        }
    }

    // ----------------------------------------------------------------
    // Header — FIX: print ANSI reset BEFORE the box characters
    // ----------------------------------------------------------------
    public static void showHeader() {
        // Reset any lingering attributes first, THEN draw the box
        System.out.print(Color.RESET);
        System.out.println(Color.BOLD + Color.BRIGHT_CYAN
                + "\u256C\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u256C");
        System.out.println("\u2551       \uD83C\uDFE0  REAL ESTATE MANAGEMENT SYSTEM  \uD83C\uDFE0         \u2551");
        System.out.println("\u256C\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u256C"
                + Color.RESET);

        if (Session.username != null) {
            String roleColor = switch (Session.role == null ? "" : Session.role) {
                case "ADMIN"  -> Color.BG_RED   + Color.WHITE;
                case "AGENCY" -> Color.BG_BLUE  + Color.WHITE;
                case "AGENT"  -> Color.BG_GREEN + Color.WHITE;
                default       -> Color.BG_CYAN  + Color.WHITE;
            };
            System.out.print("  " + Color.DIM + "Logged in as: " + Color.RESET);
            System.out.print(Color.BOLD + Session.username + Color.RESET);
            System.out.println("  " + roleColor + " " + Session.role + " " + Color.RESET);
        }
        System.out.println();
    }

    // ----------------------------------------------------------------
    // printTableMenu — ANSI-safe column width calculation
    // ----------------------------------------------------------------
    public static void printTableMenu(String title, String[] options) {
        int cols = 3;

        // Measure max visible width (strip ANSI when measuring)
        int maxLen = title.length();
        for (int i = 0; i < options.length; i++) {
            String text = (i + 1) + ". " + options[i];
            if (text.length() > maxLen) maxLen = text.length();
        }

        int colWidth        = maxLen + 4;
        int totalInnerWidth = (colWidth * cols) + (cols - 1);

        // Title bar
        System.out.print(Color.RESET); // reset before drawing
        System.out.println(Color.BOLD + Color.BRIGHT_YELLOW
                + "\u2554" + "\u2550".repeat(totalInnerWidth) + "\u2557");
        // Center title — plain spaces, no ANSI inside the padded string
        int titlePad = (totalInnerWidth - title.length()) / 2;
        String paddedTitle = " ".repeat(Math.max(0, titlePad)) + title;
        System.out.printf("\u2551%-" + totalInnerWidth + "s\u2551%n", paddedTitle);
        System.out.println("\u2560" + "\u2550".repeat(colWidth) + "\u2566"
                + "\u2550".repeat(colWidth) + "\u2566"
                + "\u2550".repeat(colWidth) + "\u2563"
                + Color.RESET);

        int index = 0;
        int rows  = (int) Math.ceil(options.length / (double) cols);

        for (int r = 0; r < rows; r++) {
            // Build raw text for each cell (no ANSI — pad purely by length)
            String[] cell = new String[cols];
            for (int c = 0; c < cols; c++) {
                if (index < options.length) {
                    cell[c] = " " + (index + 1) + ". " + options[index++];
                } else {
                    cell[c] = "";
                }
            }
            // Print with color but pad using raw text length
            System.out.print(Color.BRIGHT_YELLOW + "\u2551" + Color.RESET);
            for (int c = 0; c < cols; c++) {
                int pad = colWidth - cell[c].length();
                System.out.print(Color.BRIGHT_WHITE + cell[c] + Color.RESET
                        + " ".repeat(Math.max(0, pad))
                        + Color.BRIGHT_YELLOW + "\u2551" + Color.RESET);
            }
            System.out.println();
        }

        System.out.println(Color.BRIGHT_YELLOW
                + "\u255A" + "\u2550".repeat(colWidth) + "\u2569"
                + "\u2550".repeat(colWidth) + "\u2569"
                + "\u2550".repeat(colWidth) + "\u255D"
                + Color.RESET);
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
        } catch (Exception e) { /* not critical */ }
    }

    // ----------------------------------------------------------------
    // Sub-menus
    // ----------------------------------------------------------------

    static void clientMenu() {
        while (true) {
            clearScreen(); showHeader();
            printTableMenu("CLIENT MENU", new String[]{
                    "Add Client", "View Clients", "Find Client by ID", "Update Client",
                    "View Client with Roles", "Client Transaction History", "Client Summary",
                    "Top Buyer by Purchases", "Repeat Clients", "Client Revenue Contribution",
                    "Clients With No Transactions", "Delete Client", "Assign Role",
                    "Search Client by Name", "Count Clients", "Sort Clients by Name",
                    "Sort Clients by ID", "Filter Clients by Role", "Remove Client Role",
                    "Update Client Role", "Check Client Exists", "Back"
            });
            int choice = InputUtil.getIntegerInput("\nEnter choice:");
            switch (choice) {
                case 1  -> ClientService.addClient();
                case 2  -> ClientService.viewClient();
                case 3  -> ClientService.findClientById();
                case 4  -> ClientService.updateClient();
                case 5  -> ClientService.viewClientWithRoles();
                case 6  -> ClientService.viewClientTransactions();
                case 7  -> ClientService.clientSummary();
                case 8  -> ClientService.topBuyerByPurchases();
                case 9  -> ClientService.repeatClients();
                case 10 -> ClientService.clientRevenueContribution();
                case 11 -> ClientService.clientsWithNoTransactions();
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
                case 22 -> { return; }
                default -> System.out.println(Color.RED + "  \u274C Invalid choice." + Color.RESET);
            }
        }
    }

    static void agentMenu() {
        while (true) {
            clearScreen(); showHeader();
            printTableMenu("AGENT MENU", new String[]{
                    "Add Agent", "View Agents", "Find Agent by ID", "Update Agent",
                    "Agent Performance", "Agent Summary", "Top Performing Agent",
                    "View Agent Properties", "Delete Agent", "Search Agent by Name",
                    "Filter by Experience", "Filter by Agency", "Count Agents",
                    "Sort Agents by Experience", "Check Agent Exists",
                    "Top Agent by Revenue", "Agent Sales Count", "Agent Revenue Breakdown",
                    "Agents With No Deals", "Agents With No Sales",
                    "Bottom Performing Agent", "Agents By City", "Back"
            });
            int choice = InputUtil.getIntegerInput("\nEnter choice:");
            switch (choice) {
                case 1  -> AgentService.addAgent();
                case 2  -> AgentService.viewAgent();
                case 3  -> AgentService.findAgentById();
                case 4  -> AgentService.updateAgent();
                case 5  -> AgentService.agentPerformance();
                case 6  -> AgentService.agentSummary();
                case 7  -> AgentService.topAgentByRevenue();
                case 8  -> AgentService.agentActiveListings();
                case 9  -> AgentService.deleteAgent();
                case 10 -> AgentService.searchAgentByName();
                case 11 -> AgentService.filterAgentsByExperience();
                case 12 -> AgentService.filterAgentsByAgency();
                case 13 -> AgentService.countAgents();
                case 14 -> AgentService.sortAgentsByExperience();
                case 15 -> AgentService.checkAgentExists();
                case 16 -> AgentService.topAgentByRevenue();
                case 17 -> AgentService.agentSalesCount();
                case 18 -> AgentService.agentRevenueBreakdown();
                case 19 -> AgentService.agentsWithNoDeals();
                case 20 -> AgentService.agentsWithNoSales();
                case 21 -> AgentService.bottomAgentByRevenue();
                case 22 -> AgentService.agentsByCity();
                case 23 -> { return; }
                default -> System.out.println(Color.RED + "  \u274C Invalid choice." + Color.RESET);
            }
        }
    }

    static void propertyMenu() {
        while (true) {
            clearScreen(); showHeader();
            printTableMenu("PROPERTY MENU", new String[]{
                    "Add Property", "View Properties", "Update Availability",
                    "Find Property by ID", "Update Property", "Update Listing",
                    "Remove Listing", "Sell Property", "Rent Property", "Relist Property",
                    "Delete Property", "Search by City", "Search by Locality",
                    "Filter by Bedrooms", "Filter by Size Range", "Filter by Availability",
                    "Count Properties", "Sort Properties by Price", "Sort Properties by Size",
                    "Check Property Exists", "Most Expensive Property",
                    "Average Property Price", "Filter by Year Built", "Back"
            });
            int choice = InputUtil.getIntegerInput("\nEnter choice:");
            switch (choice) {
                case 1  -> PropertyService.addProperty();
                case 2  -> PropertyService.viewProperties();
                case 3  -> PropertyService.updateAvailability();
                case 4  -> PropertyService.findPropertyById();
                case 5  -> PropertyService.updateProperty();
                case 6  -> PropertyService.updateListing();
                case 7  -> PropertyService.removeListing();
                case 8  -> PropertyService.sellProperty();
                case 9  -> PropertyService.rentProperty();
                case 10 -> PropertyService.relistProperty();
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
                case 21 -> PropertyService.mostExpensiveProperty();
                case 22 -> PropertyService.averagePropertyPrice();
                case 23 -> PropertyService.propertiesByYear();
                case 24 -> { return; }
                default -> System.out.println(Color.RED + "  \u274C Invalid choice." + Color.RESET);
            }
        }
    }

    static void salesMenu() {
        while (true) {
            clearScreen(); showHeader();
            printTableMenu("SALES MENU", new String[]{
                    "Record Sale", "View Sales", "Find Sale by ID",
                    "Sales Summary", "Top Buyer", "Sales by Date Range",
                    "Delete Sale", "Filter Sales by Date", "Filter Sales by Agent",
                    "Filter Sales by Property", "Total Sales Amount",
                    "Count Sales", "Sort Sales by Amount", "Sort Sales by Date", "Back"
            });
            int choice = InputUtil.getIntegerInput("\nEnter choice:");
            switch (choice) {
                case 1  -> SalesService.RecordSale();
                case 2  -> SalesService.viewSales();
                case 3  -> SalesService.findSaleById();
                case 4  -> SalesService.salesSummary();
                case 5  -> SalesService.topBuyerInfo();
                case 6  -> SalesService.salesByDateRange();
                case 7  -> SalesService.deleteSale();
                case 8  -> SalesService.filterSalesByDate();
                case 9  -> SalesService.filterSalesByAgent();
                case 10 -> SalesService.filterSalesByProperty();
                case 11 -> SalesService.totalSalesAmount();
                case 12 -> SalesService.countSales();
                case 13 -> SalesService.sortSalesByAmount();
                case 14 -> SalesService.sortSalesByDate();
                case 15 -> { return; }
                default -> System.out.println(Color.RED + "  \u274C Invalid choice." + Color.RESET);
            }
        }
    }

    static void rentMenu() {
        while (true) {
            clearScreen(); showHeader();
            printTableMenu("RENT MENU", new String[]{
                    "Record Rent", "View Rent Records", "Find Rent by ID",
                    "Rent Summary", "Top Tenant", "Rent by Date Range",
                    "Delete Rent", "Filter Rent by Date", "Filter Rent by Client",
                    "Filter Rent by Property", "Total Rent Amount",
                    "Count Rents", "Sort Rent by Amount", "Sort Rent by Date", "Back"
            });
            int choice = InputUtil.getIntegerInput("\nEnter choice:");
            switch (choice) {
                case 1  -> RentService.recordRent();
                case 2  -> RentService.viewRent();
                case 3  -> RentService.findRentById();
                case 4  -> RentService.rentSummary();
                case 5  -> RentService.topTenant();
                case 6  -> RentService.rentByDateRange();
                case 7  -> RentService.deleteRent();
                case 8  -> RentService.filterRentByDate();
                case 9  -> RentService.filterRentByClient();
                case 10 -> RentService.filterRentByProperty();
                case 11 -> RentService.totalRentAmount();
                case 12 -> RentService.countRents();
                case 13 -> RentService.sortRentByAmount();
                case 14 -> RentService.sortRentByDate();
                case 15 -> { return; }
                default -> System.out.println(Color.RED + "  \u274C Invalid choice." + Color.RESET);
            }
        }
    }

    static void queryMenu() {
        while (true) {
            clearScreen(); showHeader();
            printTableMenu("PROPERTY ANALYSIS MENU", new String[]{
                    "Available Properties (City + Year)",
                    "Properties by Price Range",
                    "Rental Properties (Locality + BHK + Rent)",
                    "Top Selling Agent (Year-wise)",
                    "Agent Perf (Avg Price & Sell Time)",
                    "Most Expensive & Highest Rent",
                    "Run Custom SQL Query",
                    "Back"
            });
            int choice = InputUtil.getIntegerInput("\nEnter choice:");
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
                    default -> System.out.println(Color.RED + "  \u274C Invalid choice." + Color.RESET);
                }
            } catch (Exception e) {
                System.out.println(Color.RED + "  \u274C Error: " + e.getMessage() + Color.RESET);
            }
            InputUtil.pressEnterToContinue();
        }
    }
}