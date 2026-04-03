public class AdministratorCLI {
    public static void main(String[] args) {
        while (true){
            System.out.println("Admin Panel");
            System.out.println("1 Client");
            System.out.println("2 Agent");
            System.out.println("3 Property");
            System.out.println("4 Sales");
            System.out.println("5 Rent");
            System.out.println("6 Queries Demonstration");
            System.out.println("7 Exit");

            int choice=InputUtil.getIntegerInput("Enter choice");

            switch (choice){
                case 1->clientMenu();
                case 2->agentMenu();
                case 3->propertyMenu();
                case 4->salesMenu();
                case 5->rentMenu();
                case 6->queryMenu();
                case 7->{
                    System.out.println("Exit");
                    System.exit(0);
                }
                default -> System.out.println("Invalid");
            }
        }
    }
    static void clientMenu(){
        while(true){
            System.out.println("Client Menu");
            System.out.println("1 Add Client");
            System.out.println("2 View Client");
            System.out.println("3 Assign Role");
            System.out.println("4 Back");

            int choice=InputUtil.getIntegerInput("Enter choice");

            switch (choice){
                case 1->
                    ClientService.addClient();

                case 2->
                    ClientService.viewClient();

                case 3->
                    ClientService.assignRole();

                case 4->{
                    return ;
                }
                default -> System.out.println("Invalid");
            }
        }
    }
    static void agentMenu(){
        while(true){
            System.out.println("Agent Menu");
            System.out.println("1 Add Agent");
            System.out.println("2 View Agent");
            System.out.println("3 Back");

            int ch=InputUtil.getIntegerInput("Enter choice");

            switch (ch){
                case 1->{
                    AgentService.addAgent();
                }
                case 2->{
                    AgentService.viewAgent();
                }
                case 3->{
                   return ;
                }
                default -> System.out.println("Invalid");
            }
        }

    }
    static void propertyMenu(){
        while(true){
            System.out.println("Property Menu");
            System.out.println("1 Add Property");
            System.out.println("2 View Properties");
            System.out.println("3 Update Availability");
            System.out.println("4 Back");

            int ch=InputUtil.getIntegerInput("Enter Choice");
            switch (ch){
                case 1->{
                    PropertyService.addProperty();
                }
                case 2->{
PropertyService.viewProperties();                }
                case 3->{
                    PropertyService.updateAvailability();
                }
                case 4->{
                    return ;
                }
                default -> System.out.println("Invalid");
            }
        }

    }
    static void salesMenu(){
        while (true){
            System.out.println("Sales Menu");
            System.out.println("1 Record sale");
            System.out.println("2 View sales");
            System.out.println("3 Back");

            int ch=InputUtil.getIntegerInput("Enter choice");
            switch (ch){
                case 1->{
                    SalesService.RecordSale();
                }
                case 2->{
                    SalesService.viewSales();
                }
                case 3->{
                    return;
                }
                default -> System.out.println("Invalid");
            }
        }

    }
    static void rentMenu(){
        while (true){
            System.out.println("Rent Menu");
            System.out.println("1 Record Rent");
            System.out.println("2 View Rent record");
            System.out.println("3 Back");

            int ch=InputUtil.getIntegerInput("Enter choice");

            switch (ch){
                case 1->{
                    RentService.recordRent();
                }
                case 2->{
                    RentService.viewRent();
                }
                case 3->{
                    return;
                }
                default -> System.out.println("Invalid");
            }

        }

    }

    static void queryMenu(){
        while(true){
            System.out.println("Queries");
            System.out.println("1 List the houses in your city (for example Guwahati) that are built later than 2023 and are available for rent.");
            System.out.println("2 Find the addresses of the houses in your city costing between Rs.20,00,000 and Rs. 60,00,000.");
            System.out.println("3 Find the addresses of the houses for rent in G.S.Road (you can use the name of another locality if your city is\n" +
                    "different) with at least 2 bedrooms and costing less than Rs.15,000 per month.");
            System.out.println("4 Find the name of the agent who has sold the most property in the year 2023 by total amount in rupees.");
            System.out.println("5 For each agent, compute the average selling price of properties sold in 2018, and the average time the property\n" +
                    "was on the market. Note that this suggests use of date attributes in your design.");
            System.out.println("6 List the details of the most expensive houses and the houses with the highest rent, in the database.");
            System.out.println("7 Back");

            int ch=InputUtil.getIntegerInput("Enter choice");
            switch (ch){
                case 1->QueryService.query1();
                case 2->QueryService.query2();
                case 3->QueryService.query3();
                case 4->QueryService.query4();
                case 5->QueryService.query5();
                case 6->QueryService.query6();
                case 7->{return ;}
                default -> System.out.println("Invalid");
            }


        }
    }


}
