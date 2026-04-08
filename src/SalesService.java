import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class SalesService {

    public static void viewSales(){
        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT s.sales_id, s.sales_price, s.sales_date, " +
                    "b.client_name AS buyer, se.client_name AS seller, " +
                    "a.name AS agent, p.address " +
                    "FROM sales s " +
                    "JOIN client b ON s.buyer_id = b.client_id " +
                    "JOIN client se ON s.seller_id = se.client_id " +
                    "JOIN agent a ON s.agent_id = a.agent_id " +
                    "JOIN property p ON s.property_id = p.property_id";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.printf("%-10s %-12s %-12s %-20s %-20s %-20s %-25s%n",
                    "ID","Price","Date","Buyer","Seller","Agent","Property");
            System.out.println("-----------------------------------------------------------------------------------------------");

            while(rs.next()){
                System.out.printf("%-10d %-12d %-12s %-20s %-20s %-20s %-25s%n",
                        rs.getInt("sales_id"),
                        rs.getInt("sales_price"),
                        rs.getString("sales_date"),
                        rs.getString("buyer"),
                        rs.getString("seller"),
                        rs.getString("agent"),
                        rs.getString("address")
                );
            }

        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void topBuyerInfo() {
        try {
            Connection conn = DBConnection.getConnection();

            // One query to rule them all: Join client, sales, and property
            String query = """
            SELECT 
                c.client_id, 
                c.client_name, 
                c.client_email, 
                c.client_phone, 
                c.client_address,
                COUNT(s.sales_id) AS total_purchases,
                SUM(s.sales_price) AS total_investment,
                (SELECT city FROM property p 
                 JOIN sales s2 ON p.property_id = s2.property_id 
                 WHERE s2.buyer_id = c.client_id 
                 GROUP BY city ORDER BY COUNT(*) DESC LIMIT 1) AS favorite_city
            FROM client c
            JOIN sales s ON c.client_id = s.buyer_id
            GROUP BY c.client_id
            ORDER BY total_purchases DESC, total_investment DESC
            LIMIT 1
        """;

            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next()) {
                System.out.println("==========================================");
                System.out.println(" TOP BUYER PROFILE");
                System.out.println("==========================================");
                System.out.println("Name:           " + rs.getString("client_name"));
                System.out.println("Client ID:      " + rs.getInt("client_id"));
                System.out.println("Contact:        " + rs.getString("client_email"));
                System.out.println("Phone:          " + rs.getString("client_phone"));
                System.out.println("Home Location:  " + rs.getString("client_address"));
                System.out.println("------------------------------------------");
                System.out.println("Total Purchases: " + rs.getInt("total_purchases"));
                System.out.println("Total Invested:  ₹" + rs.getLong("total_investment"));
                System.out.println("Preferred City: " + rs.getString("favorite_city"));
                System.out.println("==========================================");
            } else {
                System.out.println("No sales data found.");
            }

        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    public static void findSaleById() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Sales ID: ");
            int id = InputUtil.sc.nextInt();

            String query =
                    "SELECT s.sales_id, s.sales_price, s.sales_date, " +
                            "b.client_name AS buyer, " +
                            "se.client_name AS seller, " +
                            "a.name AS agent, " +
                            "p.address " +
                            "FROM sales s " +
                            "JOIN client b ON s.buyer_id = b.client_id " +
                            "JOIN client se ON s.seller_id = se.client_id " +
                            "JOIN agent a ON s.agent_id = a.agent_id " +
                            "JOIN property p ON s.property_id = p.property_id " +
                            "WHERE s.sales_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n========== SALE DETAILS ==========");
                System.out.println("ID       : " + rs.getInt("sales_id"));
                System.out.println("Price    : " + rs.getInt("sales_price"));
                System.out.println("Date     : " + rs.getString("sales_date"));
                System.out.println("Buyer    : " + rs.getString("buyer"));
                System.out.println("Seller   : " + rs.getString("seller"));
                System.out.println("Agent    : " + rs.getString("agent"));
                System.out.println("Property : " + rs.getString("address"));
                System.out.println("=================================");
            } else {
                System.out.println(" Sale not found!");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void deleteSale() {
        try {
            Connection conn = DBConnection.getConnection();
            Scanner sc = new Scanner(System.in);

            System.out.print("Enter Sales ID to delete: ");
            int id = sc.nextInt();

            // get property id
            PreparedStatement ps1 = conn.prepareStatement(
                    "SELECT property_id FROM sales WHERE sales_id=?"
            );
            ps1.setInt(1, id);

            ResultSet rs = ps1.executeQuery();

            if (!rs.next()) {
                System.out.println("Sale not found!");
                return;
            }

            int propertyId = rs.getInt("property_id");


            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM sales WHERE sales_id=?"
            );
            ps.setInt(1, id);

            ps.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE property SET availability_status=true WHERE property_id=?"
            );
            ps2.setInt(1, propertyId);
            ps2.executeUpdate();

            System.out.println(" Sale deleted and property restored");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void filterSalesByDate() {
        try {
            Connection conn = DBConnection.getConnection();
            Scanner sc = new Scanner(System.in);

            System.out.print("Enter Date (YYYY-MM-DD): ");
            String date = sc.next();

            String query = """
        SELECT 
            s.sales_id, 
            s.sales_price, 
            s.sales_date,
            p.address,
            p.city,
            a.name AS agent_name,
            c.client_name AS buyer_name
        FROM sales s
        JOIN property p ON s.property_id = p.property_id
        JOIN agent a ON s.agent_id = a.agent_id
        JOIN client c ON s.buyer_id = c.client_id
        WHERE s.sales_date >= ?
        ORDER BY s.sales_date DESC
        """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, date);

            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- Sales After: " + date + " ---");

            System.out.printf("%-5s %-12s %-12s %-30s %-15s %-15s%n",
                    "ID","Price","Date","Property","Agent","Buyer");

            System.out.println("-----------------------------------------------------------------------------------------------------");

            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                System.out.printf("%-5d ₹%-11d %-12s %-30s %-15s %-15s%n",
                        rs.getInt("sales_id"),
                        rs.getInt("sales_price"),
                        rs.getString("sales_date"),
                        rs.getString("address") + ", " + rs.getString("city"),
                        rs.getString("agent_name"),
                        rs.getString("buyer_name"));
            }

            if (!hasData) {
                System.out.println("No sales records found after this date.");
            }

            System.out.println("-----------------------------------------------------------------------------------------------------");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void filterSalesByAgent() {
        try {
            Connection conn = DBConnection.getConnection();
            Scanner sc = new Scanner(System.in);

            System.out.print(" Enter Agent ID to generate report: ");
            int agentId = sc.nextInt();

            // Comprehensive query to get Agent name, Property details, and Client names
            String query = """
            SELECT 
                a.name AS agent_name,
                s.sales_id, 
                s.sales_price, 
                s.sales_date,
                p.address, 
                p.city,
                b.client_name AS buyer_name,
                sel.client_name AS seller_name
            FROM sales s
            JOIN agent a ON s.agent_id = a.agent_id
            JOIN property p ON s.property_id = p.property_id
            JOIN client b ON s.buyer_id = b.client_id
            JOIN client sel ON s.seller_id = sel.client_id
            WHERE s.agent_id = ?
            ORDER BY s.sales_date DESC
        """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, agentId);
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            double totalVolume = 0;
            int dealCount = 0;

            while (rs.next()) {
                if (!found) {
                    System.out.println("\n SALES REPORT FOR: " + rs.getString("agent_name").toUpperCase());
                    System.out.println("------------------------------------------------------------------------------------------------");
                    System.out.printf("%-10s | %-12s | %-15s | %-20s | %-15s\n",
                            "ID", "Price", "Date", "Property Address", "Buyer");
                    System.out.println("------------------------------------------------------------------------------------------------");
                    found = true;
                }

                int price = rs.getInt("sales_price");
                totalVolume += price;
                dealCount++;

                System.out.printf("%-10d | ₹%-11d | %-15s | %-20s | %-15s\n",
                        rs.getInt("sales_id"),
                        price,
                        rs.getDate("sales_date"),
                        rs.getString("address") + ", " + rs.getString("city"),
                        rs.getString("buyer_name"));
            }



        } catch (Exception e) {
            System.out.println(" Error generating agent report: " + e.getMessage());
        }
    }
    public static void filterSalesByProperty() {
        try {
            Connection conn = DBConnection.getConnection();
            Scanner sc = new Scanner(System.in);

            System.out.print("Enter Property ID: ");
            int propertyId = sc.nextInt();

            String query = "SELECT * FROM sales WHERE property_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, propertyId);

            ResultSet rs = ps.executeQuery();

            // Improved Print Statements
            System.out.println("\n--- Sales Results for Property ID: " + propertyId + " ---");
            System.out.printf("%-10s | %-15s%n", "SALE ID", "PRICE");
            System.out.println("------------------------------------");

            while (rs.next()) {
                // Using printf to align columns and format the price
                System.out.printf("%-10d | %,-14d%n",
                        rs.getInt("sales_id"),
                        rs.getInt("sales_price"));
            }
            System.out.println("------------------------------------");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void totalSalesAmount() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT SUM(sales_price) AS total FROM sales";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Total Sales Amount: " + rs.getInt("total"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void countSales() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT COUNT(*) AS total FROM sales";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Total Sales: " + rs.getInt("total"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void sortSalesByAmount() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT * FROM sales ORDER BY sales_price DESC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- Sales Sorted by Price (Highest First) ---");
            System.out.printf("%-10s | %-12s | %-15s%n", "SALE ID", "PRICE", "DATE");
            System.out.println("----------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-10d | %,-11d | %-15s%n",
                        rs.getInt("sales_id"),
                        rs.getInt("sales_price"),
                        rs.getString("sales_date"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void sortSalesByDate() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT * FROM sales ORDER BY sales_date DESC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- Sales Sorted by Date (Recent First) ---");
            System.out.printf("%-12s | %-10s | %-12s%n", "DATE", "SALE ID", "PRICE");
            System.out.println("----------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-12s | %-10d | %,-11d%n",
                        rs.getString("sales_date"),
                        rs.getInt("sales_id"),
                        rs.getInt("sales_price"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void RecordSale(){
        try{
            Connection conn = DBConnection.getConnection();
            Scanner sc = new Scanner(System.in);

            System.out.print("Enter Sales ID: ");
            int salesId = sc.nextInt();

            System.out.print("Enter Sales Price: ");
            int salesPrice = sc.nextInt();

            System.out.print("Enter Sales Date (YYYY-MM-DD): ");
            String salesDate = sc.next();

            System.out.print("Enter Buyer ID: ");
            int buyerId = sc.nextInt();

            System.out.print("Enter Seller ID: ");
            int sellerId = sc.nextInt();

            System.out.print("Enter Agent ID: ");
            int agentId = sc.nextInt();

            System.out.print("Enter Property ID: ");
            int propertyId = sc.nextInt();


            PreparedStatement check = conn.prepareStatement(
                    "SELECT availability_status FROM property WHERE property_id=?"
            );
            check.setInt(1, propertyId);

            ResultSet rs = check.executeQuery();

            if (!rs.next()) {
                System.out.println(" Property not found");
                return;
            }

            if (!rs.getBoolean("availability_status")) {
                System.out.println(" Property already sold/rented");
                return;
            }


            String query = "INSERT INTO sales (sales_id, sales_price, sales_date, buyer_id, seller_id, agent_id, property_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setInt(1, salesId);
            ps.setInt(2, salesPrice);
            ps.setString(3, salesDate);
            ps.setInt(4, buyerId);
            ps.setInt(5, sellerId);
            ps.setInt(6, agentId);
            ps.setInt(7, propertyId);

            ps.executeUpdate();


            PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE property SET availability_status=false WHERE property_id=?"
            );
            ps2.setInt(1, propertyId);
            ps2.executeUpdate();


            PreparedStatement ps3 = conn.prepareStatement(
                    "UPDATE property SET owner_id=? WHERE property_id=?"
            );
            ps3.setInt(1, buyerId);
            ps3.setInt(2, propertyId);
            ps3.executeUpdate();

            System.out.println("Sale recorded successfully!");

        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void salesSummary() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
            SELECT COUNT(*) AS total_sales,
                   SUM(sales_price) AS total_revenue,
                   AVG(sales_price) AS avg_price
            FROM sales
        """;

            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next()) {
                System.out.println("\n--- SALES SUMMARY ---");
                System.out.println("Total Sales: " + rs.getInt("total_sales"));
                System.out.println("Total Revenue: ₹" + rs.getInt("total_revenue"));
                System.out.println("Average Sale Price: ₹" + rs.getInt("avg_price"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void topBuyer() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
            SELECT buyer_id, COUNT(*) AS total
            FROM sales
            GROUP BY buyer_id
            ORDER BY total DESC
            LIMIT 1
        """;

            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next()) {
                int buyerId = rs.getInt("buyer_id");
                int total = rs.getInt("total");

                PreparedStatement ps = conn.prepareStatement(
                        "SELECT client_name FROM client WHERE client_id=?"
                );
                ps.setInt(1, buyerId);

                ResultSet rs2 = ps.executeQuery();

                if (rs2.next()) {
                    System.out.println("Top Buyer: " +
                            rs2.getString("client_name") +
                            " (" + total + " purchases)");
                }
            }

        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }
    public static void salesByDateRange() {
        try {
            Connection conn = DBConnection.getConnection();
            Scanner sc = new Scanner(System.in);

            System.out.print("Enter Start Date (YYYY-MM-DD): ");
            String start = sc.next();

            System.out.print("Enter End Date (YYYY-MM-DD): ");
            String end = sc.next();

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM sales WHERE sales_date BETWEEN ? AND ?"
            );

            ps.setString(1, start);
            ps.setString(2, end);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                        rs.getInt("sales_id") + " | " +
                                rs.getInt("sales_price") + " | " +
                                rs.getString("sales_date")
                );
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}