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
                    "View Clients",

                    // 🔹 Rent Management
                    "Record Rent",
                    "View Rent",
                    "Find Rent by ID",
                    "Filter Rent by Date",
                    "Filter Rent by Property",
                    "Filter Rent by Client",
                    "Rent by Date Range",

                    // 🔹 Rent Analytics
                    "Total Rent Amount",
                    "Count Rents",
                    "Sort Rent by Amount",
                    "Sort Rent by Date",
                    "Rent Summary",
                    "Top Tenant",

                    // 🔹 Agent Analytics
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
                    "Find Property by ID",
                    "Make Property Available",
                    "Assign Role",

                    // 🔹 Sales (Agent)
                    "Record Sale",
                    "View My Sales",
                    "Find Sale by ID",
                    "Delete Sale",

                    // 🔹 Sales Filters
                    "Sales by Date Range",
                    "Sales by Property",
                    "High Value Sales",

                    // 🔹 Sales Analytics
                    "Total Sales Amount",
                    "Total Sales Count",
                    "Sales Summary",
                    "Top Buyer",
                    "Repeat Buyers",

                    // 🔹 Sales Sorting
                    "Sort Sales by Amount",
                    "Sort Sales by Date",

                    // 🔹 Sales Insights
                    "Sales by City",
                    "Monthly Sales Report",
                    "Unsold Properties",

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

                // 🔹 Client
                case 9 -> ClientService.addClient();
                case 10 -> ClientService.viewClient();

                // 🔹 Rent Management
                case 11 -> RentService.recordRent();
                case 12 -> RentService.viewRent();
                case 13 -> RentService.findRentById();
                case 14 -> RentService.filterRentByDate();
                case 15 -> RentService.filterRentByProperty();
                case 16 -> RentService.filterRentByClient();
                case 17 -> RentService.rentByDateRange();

                // 🔹 Rent Analytics
                case 18 -> RentService.totalRentAmount();
                case 19 -> RentService.countRents();
                case 20 -> RentService.sortRentByAmount();
                case 21 -> RentService.sortRentByDate();
                case 22 -> RentService.rentSummary();
                case 23 -> RentService.topTenant();

                // 🔹 Agent Analytics
                case 24 -> AgentService.agentPerformance();
                case 25 -> AgentService.agentSummary();
                case 26 -> AgentService.agentRevenue();
                case 27 -> AgentService.agentActiveListings();
                case 28 -> AgentService.agentDealBreakdown();
                case 29 -> AgentService.agentPortfolioValue();
                case 30 -> AgentService.agentSuccessRate();
                case 31 -> AgentService.agentDealsHistory();
                case 32 -> AgentService.agentWorkload();
                case 33 -> PropertyService.mostExpensiveProperty();
                case 34 -> PropertyService.averagePropertyPrice();
                case 35 -> PropertyService.findPropertyById();
                case 36 -> AgentService.makePropertyAvailable();
                case 37 -> AgentService.assignRole();

                // 🔹 Sales
                case 38 -> SalesService.RecordSale();
                case 39 -> SalesService.viewSales();
                case 40 -> SalesService.findSaleById();
                case 41 -> SalesService.deleteSale();

                // 🔹 Sales Filters
                case 42 -> SalesService.salesByDateRange();
                case 43 -> SalesService.filterSalesByProperty();
                case 44 -> SalesService.highValueSales();

                // 🔹 Sales Analytics
                case 45 -> SalesService.totalSalesAmount();
                case 46 -> SalesService.countSales();
                case 47 -> SalesService.salesSummary();
                case 48 -> SalesService.topBuyer();
                case 49 -> SalesService.repeatBuyers();

                // 🔹 Sales Sorting
                case 50 -> SalesService.sortSalesByAmount();
                case 51 -> SalesService.sortSalesByDate();

                // 🔹 Sales Insights
                case 52 -> SalesService.salesByCity();
                case 53 -> SalesService.monthlySalesReport();
                case 54 -> SalesService.unsoldProperties();

                // 🔹 Exit
                case 55 -> { return; }

                default -> System.out.println("Invalid choice.");
            }
        }
    }
}

//assign role
//make property available
