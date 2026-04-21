import java.util.Scanner;

public class ContributionUtil {

    public static void showMenu() {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n===== Contribution Menu =====");
            System.out.println("1. Adarsh Yadav (2401012)");
            System.out.println("2. Aryan Gupta (2401048)");
            System.out.println("3. Ayush Patel (2401058)");
            System.out.println("4. Gaikar Hitesh Sunil (2401079)");
            System.out.println("5. Exit");
            System.out.print("Select a member: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1->
                    displayContributionByRoll(2401012);

                case 2->
                    displayContributionByRoll(2401048);
                case 3->
                    displayContributionByRoll(2401058);

                case 4->
                    displayContributionByRoll(2401079);

                case 5 -> {
                    System.out.println("Exiting...");
                    return ;
                }

                default->
                    System.out.println("Invalid choice! Try again.");
            }

        } while (choice != 5);

        sc.close();
    }

    public static void displayContributionByRoll(int roll) {

        System.out.println("\n===== Contribution Details =====\n");

        if (roll == 2401012) {

            System.out.println("Adarsh Yadav (2401012):");
            System.out.println("Adarsh Yadav played a crucial and foundational role in the development of the ");
            System.out.println("Real Estate Management System. He was primarily responsible for designing ");
            System.out.println("the overall business logic and defining detailed workflows for core operations ");
            System.out.println("such as property buying, selling, and renting. His work ensured that all ");
            System.out.println("transactions followed real-world constraints and maintained logical consistency ");
            System.out.println("throughout the system. ");
            System.out.println("In the database design phase, he created and structured all tables with a well-");
            System.out.println("defined schema, carefully incorporating primary keys and foreign key constraints ");
            System.out.println("to enforce referential integrity. He ensured that the database followed proper ");
            System.out.println("normalization techniques to eliminate redundancy and improve efficiency. ");
            System.out.println("Additionally, he contributed to the development of utility components such as ");
            System.out.println("TableUtil, which enhanced the readability and formatting of command-line ");
            System.out.println("outputs. He also assisted in implementing the login system, participated in ");
            System.out.println("data feeding for realistic testing, conducted thorough system testing, and ");
            System.out.println("worked on improving the overall user interface and interaction flow.\n");

        } else if (roll == 2401048) {

            System.out.println("Aryan Gupta (2401048):");
            System.out.println("Aryan Gupta played a significant role in both the conceptual design and ");
            System.out.println("technical implementation of the system. During the ER modeling phase, he ");
            System.out.println("defined important system assumptions and performed detailed cardinality and ");
            System.out.println("participation mapping to ensure that relationships between entities were ");
            System.out.println("accurately represented. ");
            System.out.println("In the database implementation phase, he designed and implemented a wide ");
            System.out.println("range of indexes that significantly improved query performance and optimized ");
            System.out.println("data retrieval operations. He was also responsible for implementing database ");
            System.out.println("connectivity using JDBC through a dedicated DBConnection class, enabling ");
            System.out.println("smooth communication between the application and the database. ");
            System.out.println("Furthermore, he designed and implemented a secure login system incorporating ");
            System.out.println("role-based authentication (RBAC) and data isolation mechanisms, ensuring that ");
            System.out.println("users could only access authorized functionalities. He also developed a modular ");
            System.out.println("service-based architecture using interfaces for Agent, Agency, and Admin ");
            System.out.println("modules, which enhanced scalability, maintainability, and separation of concerns.\n");

        } else if (roll == 2401058) {

            System.out.println("Ayush Patel (2401058):");
            System.out.println("Ayush Patel made important contributions to both the structural design and ");
            System.out.println("implementation of the system. He was responsible for designing and structuring ");
            System.out.println("the ER diagram, ensuring that all entities, attributes, and relationships were ");
            System.out.println("logically consistent and correctly mapped. ");
            System.out.println("During the database phase, he implemented several triggers to enforce critical ");
            System.out.println("business rules and maintain strict data integrity. These triggers ensured ");
            System.out.println("validations such as preventing invalid transactions, maintaining correct ");
            System.out.println("ownership records, and ensuring consistency in sales and rental operations. ");
            System.out.println("In the application layer, he developed SQL queries for key service modules ");
            System.out.println("including Client, Property, and Agent services. His work covered complete ");
            System.out.println("CRUD operations along with optimized data retrieval techniques. ");
            System.out.println("He also contributed significantly to testing and validation, ensuring that ");
            System.out.println("all modules worked correctly under various scenarios and edge cases.\n");

        } else if (roll == 2401079) {

            System.out.println("Gaikar Hitesh Sunil (2401079):");
            System.out.println("Gaikar Hitesh Sunil contributed extensively to the data modeling and ");
            System.out.println("implementation phases of the project. During the initial design stage, he ");
            System.out.println("identified key entities and their attributes, ensuring that the database ");
            System.out.println("schema was properly normalized and free from redundancy. ");
            System.out.println("In the implementation phase, he performed large-scale data feeding by inserting ");
            System.out.println("structured and diverse sample data across all tables, simulating real-world ");
            System.out.println("scenarios. This data was crucial for testing queries, validating triggers, ");
            System.out.println("and analyzing system behavior. ");
            System.out.println("He also implemented SQL queries for Sales, Rent, and advanced Query Service ");
            System.out.println("modules, including analytical and reporting functionalities such as revenue ");
            System.out.println("analysis, transaction summaries, and performance evaluation. ");
            System.out.println("Additionally, he actively participated in system testing and debugging, ");
            System.out.println("ensuring smooth integration and proper functioning of all system components.\n");
        }

        System.out.println("================================\n");
    }

}