public class AgentMenu {

    public static void start() {
        while (true) {
            AdministratorCLI.clearScreen();
            AdministratorCLI.showHeader();

            AdministratorCLI.printTableMenu("AGENT PANEL", new String[]{

                    // 🔹 Property Actions
                    "View Properties",
                    "Search Property (City)",
                    "Search Property by Locality",
                    "Filter by Price Range",
                    "Filter by Type",
                    "Filter by Bedrooms",
                    "Sort Properties by Price",
                    "Sort Properties by Size",



                    // 🔹 Client Actions
                    "Add Client",
                    "View  Clients",

                    // 🔹 Sales Transactions
                    "Record Sales",
                    "View My Deals",
                    "Deals by Date Range (My Deals)",
                    "Total Deal Value (My Deals)",

                    // 🔹 Rent Management (NEW 🔥)
                    "Record Rent",
                    "View Rent",
                    "Find Rent by ID",
                    "Filter Rent by Date",
                    "Filter Rent by Property",
                    "Filter Rent by Client",
                    "Rent by Date Range",

                    // 🔹 Rent Analytics (SELF)
                    "Total Rent Amount",
                    "Count Rents",
                    "Sort Rent by Amount",
                    "Sort Rent by Date",
                    "Rent Summary",
                    "Top Tenant",

                    // 🔹 Agent Analytics (SELF ONLY)
                    "My Performance",
                    "My Summary",
                    "My Revenue",
                    "My Active Listings",
                    "My Deal Breakdown",
                    "My Portfolio Value",
                    "My Success Rate",
                    "My Deals History",
                    "My Workload",
                    "My Most Expensive Property",
                    "My Average Property Price",
                    "Find Property by Id",

                    // 🔹 Exit
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("\nEnter choice: ");

            switch (choice) {

                // 🔹 Property
                case 1 -> PropertyService.viewProperties();
                case 2 -> PropertyService.searchPropertyByCity();
                case 3 -> PropertyService.searchPropertyByLocality();
                case 4 -> PropertyService.propertiesByPriceRange();
                case 5 -> PropertyService.propertiesByType();
                case 6 -> PropertyService.filterByBedrooms();
                case 7 -> PropertyService.sortPropertiesByPrice();
                case 8 -> PropertyService.sortPropertiesBySize();
                //case 9 -> AgentService.viewAgentProperties();

                // 🔹 Client
                case 10 -> ClientService.addClient();
                case 11 -> ClientService.viewClient();

                // 🔹 Sales
                case 12 -> PropertyService.sellProperty();
                case 13 -> DealService.dealsByAgent();
                case 14 -> DealService.dealsByDateRange();
                case 15 -> DealService.totalDealValue();

                // 🔥 Rent Management
                case 16 -> RentService.recordRent();
                case 17 -> RentService.viewRent();
                case 18 -> RentService.findRentById();
                case 19 -> RentService.filterRentByDate();
                case 20 -> RentService.filterRentByProperty();
                case 21 -> RentService.filterRentByClient();
                case 22 -> RentService.rentByDateRange();

                // 🔥 Rent Analytics
                case 23 -> RentService.totalRentAmount();
                case 24 -> RentService.countRents();
                case 25 -> RentService.sortRentByAmount();
                case 26 -> RentService.sortRentByDate();
                case 27 -> RentService.rentSummary();
                case 28 -> RentService.topTenant();

                // 🔹 Agent Analytics
                case 29 -> AgentService.agentPerformance();
                case 30 -> AgentService.agentSummary();
                case 31 -> AgentService.agentRevenue();
                case 32 -> AgentService.agentActiveListings();
                case 33 -> AgentService.agentDealBreakdown();
                case 34 -> AgentService.agentPortfolioValue();
                case 35 -> AgentService.agentSuccessRate();
                case 36 -> AgentService.agentDealsHistory();
                case 37 -> AgentService.agentWorkload();
                case 38 -> PropertyService.mostExpensiveProperty();
                case 39 -> PropertyService.averagePropertyPrice();
                case 40 -> PropertyService.findPropertyById();
                case 41 -> AgentService.makePropertyAvailable();
                case 42 -> AgentService.assignRole();

                // 🔹 Exit
                case 43 -> {
                    return;
                }

                default -> System.out.println("Invalid choice.");
            }
        }
    }
}

//assign role
//make property available
