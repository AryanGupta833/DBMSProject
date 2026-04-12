import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SalesService {
    private static void showPropertiesForSelection() throws Exception {
        Connection conn = DBConnection.getConnection();

        String baseQuery = """
        SELECT p.property_id, p.city, p.locality, p.availability_status, p.agent_id, 
               pt.listing_type, pt.price 
        FROM property p 
        LEFT JOIN property_type pt ON p.property_id = pt.property_id
    """;

        PreparedStatement ps;

        if ("AGENT".equals(Session.role)) {
            ps = conn.prepareStatement(baseQuery + " WHERE p.agent_id = ?");
            ps.setInt(1, Session.userId);

        } else if ("AGENCY".equals(Session.role)) {
            ps = conn.prepareStatement(baseQuery + """
            WHERE p.agent_id IN (
                SELECT agent_id FROM agent WHERE agency_id = ?
            )
        """);
            ps.setInt(1, Session.agencyId);

        } else {
            ps = conn.prepareStatement(baseQuery);
        }

        ResultSet rs = ps.executeQuery();

        List<String> headers = Arrays.asList(
                "Property ID", "City", "Locality", "Available", "Agent ID", "Type", "Price"
        );
        List<List<String>> rows = new ArrayList<>();

        while (rs.next()) {
            rows.add(Arrays.asList(
                    String.valueOf(rs.getInt("property_id")),
                    rs.getString("city"),
                    rs.getString("locality"),
                    rs.getBoolean("availability_status") ? "Yes" : "No",
                    String.valueOf(rs.getInt("agent_id")),
                    rs.getString("listing_type") != null ? rs.getString("listing_type") : "N/A",
                    rs.getString("price") != null ? "₹" + String.format("%,d", rs.getInt("price")) : "N/A"
            ));
        }

        if (!rows.isEmpty()) {
            System.out.println("\n📋 Available Properties:");
            TableUtil.printTable(headers, rows);
        } else {
            System.out.println("❌ No properties available.");
        }
    }

    private static void showAgentsForSelection() throws Exception {
        Connection conn = DBConnection.getConnection();

        PreparedStatement ps;

        if ("AGENCY".equals(Session.role)) {
            ps = conn.prepareStatement("SELECT agent_id, name FROM agent WHERE agency_id = ?");
            ps.setInt(1, Session.agencyId);
        } else {
            ps = conn.prepareStatement("SELECT agent_id, name FROM agent");
        }

        ResultSet rs = ps.executeQuery();

        List<String> headers = Arrays.asList("Agent ID", "Name");
        List<List<String>> rows = new ArrayList<>();

        while (rs.next()) {
            rows.add(Arrays.asList(
                    String.valueOf(rs.getInt("agent_id")),
                    rs.getString("name")
            ));
        }

        if (!rows.isEmpty()) {
            System.out.println("\n📋 Available Agents:");
            TableUtil.printTable(headers, rows);
        } else {
            System.out.println("❌ No agents available.");
        }
    }

    private static void showClientsForSelection() throws Exception {
        Connection conn = DBConnection.getConnection();

        String query = """
            SELECT c.client_id, c.client_name, GROUP_CONCAT(cr.role SEPARATOR ', ') AS roles
            FROM client c
            LEFT JOIN client_role cr ON c.client_id = cr.client_id
            GROUP BY c.client_id, c.client_name
        """;

        ResultSet rs = conn.createStatement().executeQuery(query);

        List<String> headers = Arrays.asList("Client ID", "Name", "Roles");
        List<List<String>> rows = new ArrayList<>();

        while (rs.next()) {
            String roles = rs.getString("roles");
            rows.add(Arrays.asList(
                    String.valueOf(rs.getInt("client_id")),
                    rs.getString("client_name"),
                    roles != null ? roles : "No Role"
            ));
        }

        if (!rows.isEmpty()) {
            System.out.println("\n📋 Available Clients:");
            TableUtil.printTable(headers, rows);
        } else {
            System.out.println("❌ No clients available.");
        }
    }

    public static void viewSales(){
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
            SELECT s.sales_id, s.sales_price, s.sales_date,
                   b.client_name AS buyer, se.client_name AS seller,
                   a.name AS agent, p.address
            FROM sales s
            JOIN client b ON s.buyer_id = b.client_id
            JOIN client se ON s.seller_id = se.client_id
            JOIN agent a ON s.agent_id = a.agent_id
            JOIN property p ON s.property_id = p.property_id
        """;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE s.agent_id = ?";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.agencyId);

            } else { // ADMIN
                ps = conn.prepareStatement(query);
            }

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                System.out.println(rs.getInt("sales_id") + " | ₹" +
                        rs.getInt("sales_price") + " | " +
                        rs.getString("agent"));
            }

        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void topBuyerInfo() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
            SELECT 
                c.client_id, 
                c.client_name, 
                COUNT(s.sales_id) AS total_purchases,
                SUM(s.sales_price) AS total_investment
            FROM client c
            JOIN sales s ON c.client_id = s.buyer_id
        """;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE s.agent_id = ?";
                query += " GROUP BY c.client_id ORDER BY total_purchases DESC LIMIT 1";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                query += " GROUP BY c.client_id ORDER BY total_purchases DESC LIMIT 1";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.agencyId);

            } else { // ADMIN
                query += " GROUP BY c.client_id ORDER BY total_purchases DESC LIMIT 1";
                ps = conn.prepareStatement(query);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Top Buyer: " +
                        rs.getString("client_name") +
                        " | ₹" + rs.getLong("total_investment"));
            } else {
                System.out.println("No data");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void findSaleById() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Sales ID");

            String query = """
            SELECT s.sales_id, s.sales_price, s.sales_date,
                   b.client_name AS buyer,
                   se.client_name AS seller,
                   a.name AS agent,
                   p.address
            FROM sales s
            JOIN client b ON s.buyer_id = b.client_id
            JOIN client se ON s.seller_id = se.client_id
            JOIN agent a ON s.agent_id = a.agent_id
            JOIN property p ON s.property_id = p.property_id
            WHERE s.sales_id = ?
        """;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " AND s.agent_id = ?";
                ps = conn.prepareStatement(query);
                ps.setInt(1, id);
                ps.setInt(2, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " AND s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                ps = conn.prepareStatement(query);
                ps.setInt(1, id);
                ps.setInt(2, Session.agencyId);

            } else { // ADMIN
                ps = conn.prepareStatement(query);
                ps.setInt(1, id);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Sale: ₹" + rs.getInt("sales_price") +
                        " | Agent: " + rs.getString("agent"));
            } else {
                System.out.println("❌ Not found or access denied");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void deleteSale() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Sales ID to delete");

            String checkQuery = "SELECT property_id FROM sales WHERE sales_id=?";

            if ("AGENT".equals(Session.role)) {
                checkQuery += " AND agent_id=?";
            } else if ("AGENCY".equals(Session.role)) {
                checkQuery += " AND agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            }

            PreparedStatement ps1 = conn.prepareStatement(checkQuery);
            ps1.setInt(1, id);

            if ("AGENT".equals(Session.role)) {
                ps1.setInt(2, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                ps1.setInt(2, Session.agencyId);
            }

            ResultSet rs = ps1.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Not found or access denied");
                return;
            }

            int propertyId = rs.getInt("property_id");

            String deleteQuery = "DELETE FROM sales WHERE sales_id=?";

            if ("AGENT".equals(Session.role)) {
                deleteQuery += " AND agent_id=?";
            } else if ("AGENCY".equals(Session.role)) {
                deleteQuery += " AND agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            }

            PreparedStatement ps = conn.prepareStatement(deleteQuery);
            ps.setInt(1, id);

            if ("AGENT".equals(Session.role)) {
                ps.setInt(2, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                ps.setInt(2, Session.agencyId);
            }

            int rows = ps.executeUpdate();

            if (rows > 0) {
                PreparedStatement ps2 = conn.prepareStatement(
                        "UPDATE property SET availability_status=true WHERE property_id=?"
                );
                ps2.setInt(1, propertyId);
                ps2.executeUpdate();

                System.out.println("✅ Sale deleted & property restored");
            } else {
                System.out.println("❌ Delete failed");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void filterSalesByDate() {
        try {
            Connection conn = DBConnection.getConnection();

            String date = InputUtil.getStringInput("Enter Date (YYYY-MM-DD)");

            String query = """
            SELECT s.sales_id, s.sales_price, s.sales_date,
                   p.address, p.city,
                   a.name AS agent_name,
                   c.client_name AS buyer_name
            FROM sales s
            JOIN property p ON s.property_id = p.property_id
            JOIN agent a ON s.agent_id = a.agent_id
            JOIN client c ON s.buyer_id = c.client_id
            WHERE s.sales_date >= ?
        """;

            if ("AGENT".equals(Session.role)) {
                query += " AND s.agent_id=?";
            } else if ("AGENCY".equals(Session.role)) {
                query += " AND s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            }

            query += " ORDER BY s.sales_date DESC";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, date);

            if ("AGENT".equals(Session.role)) {
                ps.setInt(2, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                ps.setInt(2, Session.agencyId);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getInt("sales_id") + " | ₹" +
                        rs.getInt("sales_price") + " | " +
                        rs.getString("agent_name"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void filterSalesByAgent() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId;

            if ("AGENT".equals(Session.role)) {
                agentId = Session.userId;
            } else {
                agentId = InputUtil.getPositiveInt("Enter Agent ID");
            }

            String query = """
            SELECT s.sales_id, s.sales_price, s.sales_date,
                   p.address, p.city,
                   b.client_name AS buyer_name
            FROM sales s
            JOIN property p ON s.property_id = p.property_id
            JOIN client b ON s.buyer_id = b.client_id
            WHERE s.agent_id = ?
        """;

            if ("AGENCY".equals(Session.role)) {
                query += " AND s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            }

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, agentId);

            if ("AGENCY".equals(Session.role)) {
                ps.setInt(2, Session.agencyId);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getInt("sales_id") + " | ₹" +
                        rs.getInt("sales_price"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void filterSalesByProperty() {
        try {
            Connection conn = DBConnection.getConnection();

            int propertyId = InputUtil.getPositiveInt("Enter Property ID");

            String query = "SELECT * FROM sales WHERE property_id=?";

            if ("AGENT".equals(Session.role)) {
                query += " AND agent_id=?";
            } else if ("AGENCY".equals(Session.role)) {
                query += " AND agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            }

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, propertyId);

            if ("AGENT".equals(Session.role)) {
                ps.setInt(2, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                ps.setInt(2, Session.agencyId);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getInt("sales_id") + " | ₹" +
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

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE agent_id=?";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.agencyId);

            } else { // ADMIN
                ps = conn.prepareStatement(query);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Total Sales Amount: ₹" + rs.getInt("total"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void countSales() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT COUNT(*) AS total FROM sales";

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE agent_id=?";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.agencyId);

            } else {
                ps = conn.prepareStatement(query);
            }

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

            String query = "SELECT * FROM sales";

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE agent_id=?";
                query += " ORDER BY sales_price DESC";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                query += " ORDER BY sales_price DESC";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.agencyId);

            } else {
                query += " ORDER BY sales_price DESC";
                ps = conn.prepareStatement(query);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getInt("sales_id") + " | ₹" +
                        rs.getInt("sales_price"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void sortSalesByDate() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT * FROM sales";

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE agent_id=?";
                query += " ORDER BY sales_date DESC";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                query += " ORDER BY sales_date DESC";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.agencyId);

            } else {
                query += " ORDER BY sales_date DESC";
                ps = conn.prepareStatement(query);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("sales_date") +
                        " | " + rs.getInt("sales_id") +
                        " | ₹" + rs.getInt("sales_price"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void RecordSale(){
        try{
            Connection conn = DBConnection.getConnection();

            int salesId = InputUtil.getPositiveInt("Enter Sales ID");
            int salesPrice = InputUtil.getPositiveInt("Enter Sales Price");
            String salesDate = InputUtil.getStringInput("Enter Date (YYYY-MM-DD)");

            showClientsForSelection();
            int buyerId = InputUtil.getPositiveInt("Enter Buyer ID");

            int sellerId = InputUtil.getPositiveInt("Enter Seller ID");

            showPropertiesForSelection();
            int propertyId = InputUtil.getPositiveInt("Enter Property ID");

            int agentId;

            if ("AGENT".equals(Session.role)) {
                agentId = Session.userId;
            } else {
                agentId = InputUtil.getPositiveInt("Enter Agent ID");
            }

            // 🔥 ACCESS CHECK
            String checkQuery = "SELECT availability_status FROM property WHERE property_id=?";

            if ("AGENT".equals(Session.role)) {
                checkQuery += " AND agent_id=?";
            } else if ("AGENCY".equals(Session.role)) {
                checkQuery += " AND agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            }

            PreparedStatement check = conn.prepareStatement(checkQuery);
            check.setInt(1, propertyId);

            if ("AGENT".equals(Session.role)) {
                check.setInt(2, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                check.setInt(2, Session.agencyId);
            }

            ResultSet rs = check.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Access denied or property not found");
                return;
            }

            if (!rs.getBoolean("availability_status")) {
                System.out.println("❌ Already sold/rented");
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO sales VALUES (?, ?, ?, ?, ?, ?, ?)"
            );

            ps.setInt(1, salesId);
            ps.setInt(2, salesPrice);
            ps.setString(3, salesDate);
            ps.setInt(4, buyerId);
            ps.setInt(5, sellerId);
            ps.setInt(6, agentId);
            ps.setInt(7, propertyId);

            ps.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE property SET availability_status=false, owner_id=? WHERE property_id=?"
            );
            ps2.setInt(1, buyerId);
            ps2.setInt(2, propertyId);
            ps2.executeUpdate();

            System.out.println("✅ Sale recorded successfully!");

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

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE agent_id=?";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.agencyId);

            } else {
                ps = conn.prepareStatement(query);
            }

            ResultSet rs = ps.executeQuery();

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
            SELECT s.buyer_id, COUNT(*) AS total
            FROM sales s
        """;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE s.agent_id=?";
            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            }

            query += " GROUP BY s.buyer_id ORDER BY total DESC LIMIT 1";

            PreparedStatement ps = conn.prepareStatement(query);

            if ("AGENT".equals(Session.role)) {
                ps.setInt(1, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                ps.setInt(1, Session.agencyId);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int buyerId = rs.getInt("buyer_id");
                int total = rs.getInt("total");

                PreparedStatement ps2 = conn.prepareStatement(
                        "SELECT client_name FROM client WHERE client_id=?"
                );
                ps2.setInt(1, buyerId);

                ResultSet rs2 = ps2.executeQuery();

                if (rs2.next()) {
                    System.out.println("Top Buyer: " +
                            rs2.getString("client_name") +
                            " (" + total + " purchases)");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void salesByDateRange() {
        try {
            Connection conn = DBConnection.getConnection();

            String start = InputUtil.getStringInput("Enter Start Date (YYYY-MM-DD)");
            String end = InputUtil.getStringInput("Enter End Date (YYYY-MM-DD)");

            String query = "SELECT * FROM sales WHERE sales_date BETWEEN ? AND ?";

            if ("AGENT".equals(Session.role)) {
                query += " AND agent_id=?";
            } else if ("AGENCY".equals(Session.role)) {
                query += " AND agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            }

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, start);
            ps.setString(2, end);

            if ("AGENT".equals(Session.role)) {
                ps.setInt(3, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                ps.setInt(3, Session.agencyId);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                        rs.getInt("sales_id") + " | ₹" +
                                rs.getInt("sales_price") + " | " +
                                rs.getString("sales_date")
                );
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void topAgentBySalesRevenue() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
            SELECT a.name, SUM(s.sales_price) AS total_revenue
            FROM sales s
            JOIN agent a ON s.agent_id = a.agent_id
        """;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE s.agent_id=?";
            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            }

            query += " GROUP BY a.name ORDER BY total_revenue DESC LIMIT 1";

            PreparedStatement ps = conn.prepareStatement(query);

            if ("AGENT".equals(Session.role)) {
                ps.setInt(1, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                ps.setInt(1, Session.agencyId);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Top Agent: " +
                        rs.getString("name") +
                        " | ₹" + rs.getInt("total_revenue"));
            } else {
                System.out.println("No data found");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void salesByCity() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("\n[Query] Sales by City");

            String query = """
            SELECT p.city, SUM(s.sales_price) AS total_sales
            FROM sales s
            JOIN property p ON s.property_id = p.property_id
        """;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE s.agent_id=?";
                query += " GROUP BY p.city";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                query += " GROUP BY p.city";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.agencyId);

            } else {
                query += " GROUP BY p.city";
                ps = conn.prepareStatement(query);
            }

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                System.out.printf("%-20s %-15d%n",
                        rs.getString("city"),
                        rs.getInt("total_sales"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void monthlySalesReport() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("\n[Query] Monthly Sales Report");

            String query = """
            SELECT MONTH(sales_date) AS month,
                   COUNT(*) AS total_sales,
                   SUM(sales_price) AS revenue
            FROM sales
        """;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE agent_id=?";
                query += " GROUP BY MONTH(sales_date) ORDER BY month";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                query += " GROUP BY MONTH(sales_date) ORDER BY month";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.agencyId);

            } else {
                query += " GROUP BY MONTH(sales_date) ORDER BY month";
                ps = conn.prepareStatement(query);
            }

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                System.out.printf("%-10d %-15d %-15d%n",
                        rs.getInt("month"),
                        rs.getInt("total_sales"),
                        rs.getInt("revenue"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void highValueSales() {
        try {
            Connection conn = DBConnection.getConnection();

            int amount = InputUtil.getPositiveInt("Enter minimum amount");

            System.out.println("\n[Query] High Value Sales");

            String query = "SELECT * FROM sales WHERE sales_price >= ?";

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " AND agent_id=?";
                ps = conn.prepareStatement(query);
                ps.setInt(1, amount);
                ps.setInt(2, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " AND agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                ps = conn.prepareStatement(query);
                ps.setInt(1, amount);
                ps.setInt(2, Session.agencyId);

            } else {
                ps = conn.prepareStatement(query);
                ps.setInt(1, amount);
            }

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                System.out.printf("%-8d %-12d %-12s %-10d %-10d %-10d %-10d%n",
                        rs.getInt("sales_id"),
                        rs.getInt("sales_price"),
                        rs.getString("sales_date"),
                        rs.getInt("buyer_id"),
                        rs.getInt("seller_id"),
                        rs.getInt("agent_id"),
                        rs.getInt("property_id"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void unsoldProperties() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("\n[Query] Unsold Properties");

            String query = """
            SELECT p.property_id, p.address, p.city
            FROM property p
            LEFT JOIN sales s ON p.property_id = s.property_id
            WHERE s.property_id IS NULL
        """;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " AND p.agent_id=?";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " AND p.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.agencyId);

            } else {
                ps = conn.prepareStatement(query);
            }

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                System.out.printf("%-10d %-25s %-20s%n",
                        rs.getInt("property_id"),
                        rs.getString("address"),
                        rs.getString("city"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void repeatBuyers() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("\n[Query] Repeat Buyers");

            String query = """
            SELECT c.client_name, COUNT(s.sales_id) AS total_purchases
            FROM sales s
            JOIN client c ON s.buyer_id = c.client_id
        """;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE s.agent_id=?";
                query += " GROUP BY s.buyer_id HAVING COUNT(s.sales_id) > 1";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                query += " GROUP BY s.buyer_id HAVING COUNT(s.sales_id) > 1";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.agencyId);

            } else {
                query += " GROUP BY s.buyer_id HAVING COUNT(s.sales_id) > 1";
                ps = conn.prepareStatement(query);
            }

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                System.out.printf("%-25s %-15d%n",
                        rs.getString("client_name"),
                        rs.getInt("total_purchases"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }



}