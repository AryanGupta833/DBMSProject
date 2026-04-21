public class OfficeMenu {

    public static void start() {
        while (true) {
            AdministratorCLI.clearScreen();
            AdministratorCLI.showHeader();

            AdministratorCLI.printTableMenu("AGENCY PANEL", new String[]{

                    // 🔹 Property Management
                    "Add Property",
                    "View Properties",
                    "Find Property by ID",
                    "Update Property",
                    "Delete Property",

                    // 🔹 Listing Management
                    "Update Listing",
                    "Remove Listing",
                    "Relist Property",

                    // 🔹 Assignment
                    "Assign Agent to Property",

                    // 🔹 Property Search & Filters
                    "Search by City",
                    "Search by Locality",
                    "Filter by Bedrooms",
                    "Filter by Size Range",
                    "Filter by Availability",
                    "Filter by Price Range",
                    "Filter by Type",

                    // 🔹 Sorting
                    "Sort Properties by Price",
                    "Sort Properties by Size",

                    // 🔹 Agent Management
                    "Add Agent",
                    "View Agents",
                    "Find Agent by ID",
                    "Update Agent",
                    "Delete Agent",
                    "Search Agent by Name",
                    "Filter Agents by Experience",
                    "Filter Agents by Agency",
                    "Count Agents",
                    "Sort Agents by Experience",
                    "Check Agent Exists",

                    // 🔥 Agent Analytics
                    "Top Agent by Revenue",
                    "Agent Sales Count",
                    "Agent Revenue Breakdown",
                    "Agents With No Deals",
                    "Agents With No Sales",

                    // 🔥 Sales Analytics
                    "Top Agent by Sales Revenue",
                    "Sales by City",
                    "Monthly Sales Report",
                    "High Value Sales",
                    "Unsold Properties",
                    "Repeat Buyers",

                    // 🔥 Rent Analytics (NEW)
                    "Top Agent by Rent Revenue",
                    "Rent by City",
                    "Monthly Rent Report",
                    "High Value Rentals",
                    "Vacant Properties",
                    "Repeat Tenants",

                    // 🔹 Utility
                    "Count Properties",
                    "Check Property Exists",
                    "View All Deals",
                    "Bottom Performing Agent",
                    "Agents By City",
                    "Most Expensive Property",
                    "Average Property Price",
                    "Filter by Year Built",
                    "View Properties by AgentId",

                    // 🔹 Exit
                    "Back"
            });

            int choice = InputUtil.getIntegerInput("\nEnter choice: ");

            switch (choice) {

                // 🔹 Property Management
                case 1 -> PropertyService.addProperty();
                case 2 -> PropertyService.viewProperties();
                case 3 -> PropertyService.findPropertyById();
                case 4 -> PropertyService.updateProperty();
                case 5 -> PropertyService.deleteProperty();

                // 🔹 Listing Management
                case 6 -> PropertyService.updateListing();
                case 7 -> PropertyService.removeListing();
                case 8 -> PropertyService.relistProperty();

                // 🔹 Assignment
                case 9 -> PropertyService.assignAgentToProperty();

                // 🔹 Property Search & Filters
                case 10 -> PropertyService.searchPropertyByCity();
                case 11 -> PropertyService.searchPropertyByLocality();
                case 12 -> PropertyService.filterByBedrooms();
                case 13 -> PropertyService.filterBySizeRange();
                case 14 -> PropertyService.filterByAvailability();
                case 15 -> PropertyService.propertiesByPriceRange();
                case 16 -> PropertyService.propertiesByType();

                // 🔹 Sorting
                case 17 -> PropertyService.sortPropertiesByPrice();
                case 18 -> PropertyService.sortPropertiesBySize();

                // 🔹 Agent Management
                case 19 -> AgentService.addAgent();
                case 20 -> AgentService.viewAgent();
                case 21 -> AgentService.findAgentById();
                case 22 -> AgentService.updateAgent();
                case 23 -> AgentService.deleteAgent();
                case 24 -> AgentService.searchAgentByName();
                case 25 -> AgentService.filterAgentsByExperience();
                case 26 -> AgentService.filterAgentsByAgency();
                case 27 -> AgentService.countAgents();
                case 28 -> AgentService.sortAgentsByExperience();
                case 29 -> AgentService.checkAgentExists();

                // 🔥 Agent Analytics
                case 30 -> AgentService.topAgentByRevenue();
                case 31 -> AgentService.agentSalesCount();
                case 32 -> AgentService.agentRevenueBreakdown();
                case 33 -> AgentService.agentsWithNoDeals();
                case 34 -> AgentService.agentsWithNoSales();

                // 🔥 Sales Analytics
                case 35 -> SalesService.topAgentBySalesRevenue();
                case 36 -> SalesService.salesByCity();
                case 37 -> SalesService.monthlySalesReport();
                case 38 -> SalesService.highValueSales();
                case 39 -> SalesService.unsoldProperties();
                case 40 -> SalesService.repeatBuyers();

                // 🔥 Rent Analytics (NEW)
                case 41 -> RentService.topAgentByRentRevenue();
                case 42 -> RentService.rentByCity();
                case 43 -> RentService.monthlyRentReport();
                case 44 -> RentService.highValueRentals();
                case 45 -> RentService.vacantProperties();
                case 46 -> RentService.repeatTenants();

                // 🔹 Utility
                case 47 -> PropertyService.countProperties();
                case 48 -> PropertyService.checkPropertyExists();
                case 49 -> DealService.viewAllDeals();
                case 50 -> AgentService.bottomAgentByRevenue();
                case 51 -> AgentService.agentsByCity();
                case 52 -> PropertyService.mostExpensiveProperty();
                case 53 -> PropertyService.averagePropertyPrice();
                case 54 -> PropertyService.propertiesByYear();
                case 55 -> AgentService.viewAgentProperties();

                // 🔹 Exit
                case 56 -> {
                    return;
                }

                default -> System.out.println("Invalid choice.");
            }
        }
    }
}