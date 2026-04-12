import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RentService {

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


    static Scanner sc = new Scanner(System.in);

    // 🔥 COMMON JOIN QUERY (USED EVERYWHERE)
    static final String RENT_JOIN = """
        SELECT r.*, 
               c.client_name AS tenant_name,
               p.address AS property_address,
               a.name AS agent_name
        FROM rent r
        JOIN client c ON r.tenant_id = c.client_id
        JOIN property p ON r.property_id = p.property_id
        JOIN agent a ON r.agent_id = a.agent_id
    """;

    // ---------------- PRINT ----------------

    static void printHeader() {
        System.out.printf("\n%-5s %-10s %-12s %-12s %-15s %-20s %-15s%n",
                "ID","Amount","Start","End","Tenant","Property","Agent");
        System.out.println("----------------------------------------------------------------------------------------");
    }

    static void printRow(ResultSet rs) throws Exception {
        System.out.printf("%-5d %-10d %-12s %-12s %-15s %-20s %-15s%n",
                rs.getInt("rent_id"),
                rs.getInt("rent_amount"),
                rs.getString("rent_start_date"),
                rs.getString("rent_end_date"),
                rs.getString("tenant_name"),
                rs.getString("property_address"),
                rs.getString("agent_name"));
    }

    // ---------------- CREATE ----------------

    public static void recordRent(){
        try{
            Connection conn = DBConnection.getConnection();

            int rentId = InputUtil.getPositiveInt("Enter Rent ID");
            int rentAmount = InputUtil.getPositiveInt("Enter Rent Amount");

            Date startDate = Date.valueOf(InputUtil.getStringInput("Enter Start Date (YYYY-MM-DD)"));
            Date endDate = Date.valueOf(InputUtil.getStringInput("Enter End Date (YYYY-MM-DD)"));

            showClientsForSelection();
            int tenantId = InputUtil.getPositiveInt("Enter Tenant ID");

            showPropertiesForSelection();
            int propertyId = InputUtil.getPositiveInt("Enter Property ID");

            int agentId;

            // 🔥 AUTO ASSIGN AGENT
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
                System.out.println("❌ Property not found or access denied");
                return;
            }

            if (!rs.getBoolean("availability_status")) {
                System.out.println("❌ Property already occupied");
                return;
            }

            // ✅ INSERT
            String q = "INSERT INTO rent VALUES (?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(q);

            ps.setInt(1, rentId);
            ps.setInt(2, rentAmount);
            ps.setDate(3, startDate);
            ps.setDate(4, endDate);
            ps.setInt(5, tenantId);
            ps.setInt(6, propertyId);
            ps.setInt(7, agentId);

            ps.executeUpdate();

            System.out.println("✅ Rent Recorded Successfully");

        } catch (Exception e){
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ---------------- READ ----------------

    public static void viewRent(){
        try{
            Connection conn = DBConnection.getConnection();

            String query = RENT_JOIN;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE r.agent_id=?";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.agencyId);

            } else {
                ps = conn.prepareStatement(query);
            }

            ResultSet rs = ps.executeQuery();

            printHeader();

            boolean found = false;
            while(rs.next()){
                printRow(rs);
                found = true;
            }

            if(!found){
                System.out.println("No records found.");
            }

        } catch (Exception e){
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void findRentById() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Rent ID");

            String query = RENT_JOIN + " WHERE r.rent_id=?";

            if ("AGENT".equals(Session.role)) {
                query += " AND r.agent_id=?";
            } else if ("AGENCY".equals(Session.role)) {
                query += " AND r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            }

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            if ("AGENT".equals(Session.role)) {
                ps.setInt(2, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                ps.setInt(2, Session.agencyId);
            }

            ResultSet rs = ps.executeQuery();

            printHeader();

            if (rs.next()) {
                printRow(rs);
            } else {
                System.out.println("❌ Not found or access denied");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ---------------- DELETE ----------------

    public static void deleteRent() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Rent ID to delete");

            // 🔥 ACCESS CHECK
            String checkQuery = "SELECT property_id FROM rent WHERE rent_id=?";

            if ("AGENT".equals(Session.role)) {
                checkQuery += " AND agent_id=?";
            } else if ("AGENCY".equals(Session.role)) {
                checkQuery += " AND agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            }

            PreparedStatement check = conn.prepareStatement(checkQuery);
            check.setInt(1, id);

            if ("AGENT".equals(Session.role)) {
                check.setInt(2, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                check.setInt(2, Session.agencyId);
            }

            ResultSet rs = check.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Not found or access denied");
                return;
            }

            int propertyId = rs.getInt("property_id");

            // 🔥 DELETE
            String deleteQuery = "DELETE FROM rent WHERE rent_id=?";

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
                // 🔥 RESTORE PROPERTY AVAILABILITY
                PreparedStatement ps2 = conn.prepareStatement(
                        "UPDATE property SET availability_status=true WHERE property_id=?"
                );
                ps2.setInt(1, propertyId);
                ps2.executeUpdate();

                System.out.println("✅ Rent deleted & property restored");
            } else {
                System.out.println("❌ Delete failed");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ---------------- FILTER ----------------

    public static void filterRentByDate() {
        try {
            Connection conn = DBConnection.getConnection();

            Date date = Date.valueOf(
                    InputUtil.getStringInput("Enter Start Date (YYYY-MM-DD)")
            );

            String query = RENT_JOIN + " WHERE r.rent_start_date >= ?";

            if ("AGENT".equals(Session.role)) {
                query += " AND r.agent_id=?";
            } else if ("AGENCY".equals(Session.role)) {
                query += " AND r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            }

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDate(1, date);

            if ("AGENT".equals(Session.role)) {
                ps.setInt(2, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                ps.setInt(2, Session.agencyId);
            }

            ResultSet rs = ps.executeQuery();

            printHeader();

            boolean found = false;
            while(rs.next()){
                printRow(rs);
                found = true;
            }

            if(!found){
                System.out.println("No records found.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void filterRentByProperty() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Property ID");

            String query = RENT_JOIN + " WHERE r.property_id=?";

            if ("AGENT".equals(Session.role)) {
                query += " AND r.agent_id=?";
            } else if ("AGENCY".equals(Session.role)) {
                query += " AND r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            }

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            if ("AGENT".equals(Session.role)) {
                ps.setInt(2, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                ps.setInt(2, Session.agencyId);
            }

            ResultSet rs = ps.executeQuery();

            printHeader();

            boolean found = false;
            while(rs.next()){
                printRow(rs);
                found = true;
            }

            if(!found){
                System.out.println("No records found.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void filterRentByClient() {
        try {
            Connection conn = DBConnection.getConnection();

            int clientId = InputUtil.getPositiveInt("Enter Client ID");

            String query = RENT_JOIN + " WHERE r.tenant_id=?";

            if ("AGENT".equals(Session.role)) {
                query += " AND r.agent_id=?";
            } else if ("AGENCY".equals(Session.role)) {
                query += " AND r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            }

            query += " ORDER BY r.rent_start_date DESC";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, clientId);

            if ("AGENT".equals(Session.role)) {
                ps.setInt(2, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                ps.setInt(2, Session.agencyId);
            }

            ResultSet rs = ps.executeQuery();

            printHeader();

            boolean found = false;
            while(rs.next()){
                printRow(rs);
                found = true;
            }

            if(!found){
                System.out.println("No records found.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void rentByDateRange() {
        try {
            Connection conn = DBConnection.getConnection();

            Date start = Date.valueOf(
                    InputUtil.getStringInput("Enter Start Date (YYYY-MM-DD)")
            );
            Date end = Date.valueOf(
                    InputUtil.getStringInput("Enter End Date (YYYY-MM-DD)")
            );

            String query = RENT_JOIN + " WHERE r.rent_start_date BETWEEN ? AND ?";

            if ("AGENT".equals(Session.role)) {
                query += " AND r.agent_id=?";
            } else if ("AGENCY".equals(Session.role)) {
                query += " AND r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            }

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDate(1, start);
            ps.setDate(2, end);

            if ("AGENT".equals(Session.role)) {
                ps.setInt(3, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                ps.setInt(3, Session.agencyId);
            }

            ResultSet rs = ps.executeQuery();

            printHeader();

            boolean found = false;
            while (rs.next()) {
                printRow(rs);
                found = true;
            }

            if(!found){
                System.out.println("No records found.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ---------------- ANALYTICS ----------------

    public static void totalRentAmount() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT SUM(rent_amount) AS total FROM rent";

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

            if(rs.next()){
                System.out.println("Total Rent: ₹" + rs.getInt("total"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void countRents() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT COUNT(*) AS total FROM rent";

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

            if(rs.next()){
                System.out.println("Total Records: " + rs.getInt("total"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void sortRentByAmount() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = RENT_JOIN;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE r.agent_id=? ORDER BY r.rent_amount DESC";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?) ORDER BY r.rent_amount DESC";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.agencyId);

            } else {
                query += " ORDER BY r.rent_amount DESC";
                ps = conn.prepareStatement(query);
            }

            ResultSet rs = ps.executeQuery();

            printHeader();

            while(rs.next()){
                printRow(rs);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void sortRentByDate() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = RENT_JOIN;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE r.agent_id=? ORDER BY r.rent_start_date DESC";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?) ORDER BY r.rent_start_date DESC";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.agencyId);

            } else {
                query += " ORDER BY r.rent_start_date DESC";
                ps = conn.prepareStatement(query);
            }

            ResultSet rs = ps.executeQuery();

            printHeader();

            while(rs.next()){
                printRow(rs);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void rentSummary() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
            SELECT COUNT(*) AS total,
                   SUM(rent_amount) AS total_rent,
                   AVG(rent_amount) AS avg_rent
            FROM rent
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
                System.out.println("\n--- RENT SUMMARY ---");
                System.out.println("Total Rentals: " + rs.getInt("total"));
                System.out.println("Total Rent Collected: ₹" + rs.getInt("total_rent"));
                System.out.println("Average Rent: ₹" + rs.getInt("avg_rent"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void topTenant() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
            SELECT tenant_id, COUNT(*) AS total
            FROM rent
        """;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE agent_id=?";
                query += " GROUP BY tenant_id ORDER BY total DESC LIMIT 1";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                query += " GROUP BY tenant_id ORDER BY total DESC LIMIT 1";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.agencyId);

            } else {
                query += " GROUP BY tenant_id ORDER BY total DESC LIMIT 1";
                ps = conn.prepareStatement(query);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("tenant_id");
                int total = rs.getInt("total");

                PreparedStatement ps2 = conn.prepareStatement(
                        "SELECT client_name FROM client WHERE client_id=?"
                );
                ps2.setInt(1, id);

                ResultSet rs2 = ps2.executeQuery();

                if (rs2.next()) {
                    System.out.println("🏆 Top Tenant: " +
                            rs2.getString("client_name") +
                            " (" + total + " rentals)");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void topAgentByRentRevenue() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
            SELECT a.name, SUM(r.rent_amount) AS total_revenue
            FROM rent r
            JOIN agent a ON r.agent_id = a.agent_id
        """;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE r.agent_id=?";
                query += " GROUP BY a.name ORDER BY total_revenue DESC LIMIT 1";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                query += " GROUP BY a.name ORDER BY total_revenue DESC LIMIT 1";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.agencyId);

            } else {
                query += " GROUP BY a.name ORDER BY total_revenue DESC LIMIT 1";
                ps = conn.prepareStatement(query);
            }

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                System.out.println("Top Agent: " +
                        rs.getString("name") +
                        " | ₹" + rs.getInt("total_revenue"));
            } else {
                System.out.println("No data found.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void rentByCity() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
            SELECT p.city, SUM(r.rent_amount) AS total_rent
            FROM rent r
            JOIN property p ON r.property_id = p.property_id
        """;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE r.agent_id=?";
                query += " GROUP BY p.city";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
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
                        rs.getInt("total_rent"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void monthlyRentReport() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
            SELECT MONTH(rent_start_date) AS month,
                   SUM(rent_amount) AS total
            FROM rent
        """;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE agent_id=?";
                query += " GROUP BY MONTH(rent_start_date) ORDER BY month";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                query += " GROUP BY MONTH(rent_start_date) ORDER BY month";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.agencyId);

            } else {
                query += " GROUP BY MONTH(rent_start_date) ORDER BY month";
                ps = conn.prepareStatement(query);
            }

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                System.out.printf("%-10d %-15d%n",
                        rs.getInt("month"),
                        rs.getInt("total"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void highValueRentals() {
        try {
            Connection conn = DBConnection.getConnection();

            int amount = InputUtil.getPositiveInt("Enter minimum rent amount");

            String query = "SELECT * FROM rent WHERE rent_amount >= ?";

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
                System.out.printf("%-8d %-12d %-12s %-12s %-10d %-12d %-10d%n",
                        rs.getInt("rent_id"),
                        rs.getInt("rent_amount"),
                        rs.getString("rent_start_date"),
                        rs.getString("rent_end_date"),
                        rs.getInt("tenant_id"),
                        rs.getInt("property_id"),
                        rs.getInt("agent_id"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void vacantProperties() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
            SELECT p.property_id, p.address, p.city
            FROM property p
            LEFT JOIN rent r ON p.property_id = r.property_id
            WHERE r.property_id IS NULL
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
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void repeatTenants() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
            SELECT c.client_name, COUNT(r.rent_id) AS total_rents
            FROM rent r
            JOIN client c ON r.tenant_id = c.client_id
        """;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE r.agent_id=?";
                query += " GROUP BY r.tenant_id HAVING COUNT(r.rent_id) > 1";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                query += " GROUP BY r.tenant_id HAVING COUNT(r.rent_id) > 1";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Session.agencyId);

            } else {
                query += " GROUP BY r.tenant_id HAVING COUNT(r.rent_id) > 1";
                ps = conn.prepareStatement(query);
            }

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                System.out.printf("%-25s %-15d%n",
                        rs.getString("client_name"),
                        rs.getInt("total_rents"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}