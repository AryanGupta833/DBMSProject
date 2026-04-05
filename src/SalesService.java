import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class SalesService {

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

            // Update property availability
            String updateQuery = "UPDATE property SET availability_status = false WHERE property_id = ?";
            PreparedStatement ps2 = conn.prepareStatement(updateQuery);
            ps2.setInt(1, propertyId);
            ps2.executeUpdate();

            System.out.println("✅ Sale recorded successfully!");

        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

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

    public static void findSaleById() {
        try {
            Connection conn = DBConnection.getConnection();
            Scanner sc = new Scanner(System.in);

            System.out.print("Enter Sales ID: ");
            int id = sc.nextInt();

            String query = "SELECT * FROM sales WHERE sales_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("ID: " + rs.getInt("sales_id"));
                System.out.println("Price: " + rs.getInt("sales_price"));
                System.out.println("Date: " + rs.getString("sales_date"));
            } else {
                System.out.println("Sale not found!");
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

            String query = "DELETE FROM sales WHERE sales_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Sale deleted!");
            } else {
                System.out.println("Sale not found!");
            }

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

            String query = "SELECT * FROM sales WHERE sales_date = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, date);

            ResultSet rs = ps.executeQuery();

            System.out.println("ID | Price | Date");
            System.out.println("------------------------");

            while (rs.next()) {
                System.out.println(rs.getInt("sales_id") + " | " +
                        rs.getInt("sales_price") + " | " +
                        rs.getString("sales_date"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void filterSalesByAgent() {
        try {
            Connection conn = DBConnection.getConnection();
            Scanner sc = new Scanner(System.in);

            System.out.print("Enter Agent ID: ");
            int agentId = sc.nextInt();

            String query = "SELECT * FROM sales WHERE agent_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, agentId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getInt("sales_id") + " | " +
                        rs.getInt("sales_price"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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

            while (rs.next()) {
                System.out.println(rs.getInt("sales_id") + " | " +
                        rs.getInt("sales_price"));
            }

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

            while (rs.next()) {
                System.out.println(rs.getInt("sales_id") + " | " +
                        rs.getInt("sales_price"));
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

            while (rs.next()) {
                System.out.println(rs.getInt("sales_id") + " | " +
                        rs.getString("sales_date"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}