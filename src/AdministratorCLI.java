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
                    "Find Client by ID",
                    "Update Client",
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

            int choice = InputUtil.getIntegerInput("Enter choice: ");

            switch (choice) {
                case 1 -> ClientService.addClient();
                case 2 -> ClientService.viewClient();
                case 3 -> ClientService.findClientById();
                case 4 -> ClientService.updateClient();
                case 5 -> ClientService.deleteClient();
                case 6 -> ClientService.assignRole();
                case 7 -> ClientService.searchClientByName();
                case 8 -> ClientService.countClients();
                case 9 -> ClientService.sortClientsByName();
                case 10 -> ClientService.sortClientsById();
                case 11 -> ClientService.filterClientsByRole();
                case 12 -> ClientService.removeClientRole();
                case 13 -> ClientService.updateClientRole();
                case 14 -> ClientService.checkClientExists();
                case 15 -> { return; }
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
                    "Find Agent by ID",
                    "Update Agent",
                    "Delete Agent",
                    "Search Agent by Name",
                    "Filter by Experience",
                    "Filter by Agency",
                    "Count Agents",
                    "Sort Agents by Experience",
                    "Check Agent Exists",
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("Enter choice: ");

            switch (choice) {
                case 1 -> AgentService.addAgent();
                case 2 -> AgentService.viewAgent();
                case 3 -> AgentService.findAgentById();
                case 4 -> AgentService.updateAgent();
                case 5 -> AgentService.deleteAgent();
                case 6 -> AgentService.searchAgentByName();
                case 7 -> AgentService.filterAgentsByExperience();
                case 8 -> AgentService.filterAgentsByAgency();
                case 9 -> AgentService.countAgency();
                case 10 -> AgentService.sortAgentsByExperience();
                case 11 -> AgentService.checkAgentExists();
                case 12 -> { return; }
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
                    "Find Property by ID",
                    "Update Property",
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

            int choice = InputUtil.getIntegerInput("Enter choice: ");

            switch (choice) {
                case 1 -> PropertyService.addProperty();
                case 2 -> PropertyService.viewProperties();
                case 3 -> PropertyService.updateAvailability();
                case 4 -> PropertyService.findPropertyById();
                case 5 -> PropertyService.updateProperty();
                case 6 -> PropertyService.deleteProperty();
                case 7 -> PropertyService.searchPropertyByCity();
                case 8 -> PropertyService.searchPropertyByLocality();
                case 9 -> PropertyService.filterByBedrooms();
                case 10 -> PropertyService.filterBySizeRange();
                case 11 -> PropertyService.filterByAvailability();
                case 12 -> PropertyService.countProperties();
                case 13 -> PropertyService.sortPropertiesByPrice();
                case 14 -> PropertyService.sortPropertiesBySize();
                case 15 -> PropertyService.checkPropertyExists();
                case 16 -> { return; }
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
                    "Find Sale by ID",
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

            int choice = InputUtil.getIntegerInput("Enter choice: ");

            switch (choice) {
                case 1 -> SalesService.RecordSale();
                case 2 -> SalesService.viewSales();
                case 3 -> SalesService.findSaleById();
                case 4 -> SalesService.deleteSale();
                case 5 -> SalesService.filterSalesByDate();
                case 6 -> SalesService.filterSalesByAgent();
                case 7 -> SalesService.filterSalesByProperty();
                case 8 -> SalesService.totalSalesAmount();
                case 9 -> SalesService.countSales();
                case 10 -> SalesService.sortSalesByAmount();
                case 11 -> SalesService.sortSalesByDate();
                case 12 -> { return; }
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
                    "Find Rent by ID",
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

            int choice = InputUtil.getIntegerInput("Enter choice: ");

            switch (choice) {
                case 1 -> RentService.recordRent();
                case 2 -> RentService.viewRent();
                case 3 -> RentService.findRentById();
                case 4 -> RentService.deleteRent();
                case 5 -> RentService.filterRentByDate();
                case 6 -> RentService.filterRentByClient();
                case 7 -> RentService.filterRentByProperty();
                case 8 -> RentService.totalRentAmount();
                case 9 -> RentService.countRents();
                case 10 -> RentService.sortRentByAmount();
                case 11 -> RentService.sortRentByDate();
                case 12 -> { return; }
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