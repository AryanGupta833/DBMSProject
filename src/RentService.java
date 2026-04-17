import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RentService {

    // ─────────────────────────────────────────────────────────────────
    // COMMON JOIN & HEADERS (used by every listing method)
    // ─────────────────────────────────────────────────────────────────
    private static final String RENT_JOIN = """
        SELECT r.rent_id, r.rent_amount, r.rent_start_date, r.rent_end_date,
               r.tenant_id, r.property_id, r.agent_id,
               c.client_name AS tenant_name, c.client_phone AS tenant_phone,
               p.address AS property_address, p.city, p.locality,
               a.name AS agent_name
        FROM rent r
        JOIN client   c ON r.tenant_id   = c.client_id
        JOIN property p ON r.property_id = p.property_id
        JOIN agent    a ON r.agent_id    = a.agent_id
    """;

    private static final List<String> RENT_HEADERS = Arrays.asList(
            "Rent ID", "Prop ID", "Address", "City",
            "Tenant", "Agent", "Amount (₹)", "Start", "End"
    );

    private static List<String> rentRow(ResultSet rs) throws Exception {
        return Arrays.asList(
                String.valueOf(rs.getInt("rent_id")),
                String.valueOf(rs.getInt("property_id")),
                rs.getString("property_address"),
                rs.getString("city"),
                rs.getString("tenant_name"),
                rs.getString("agent_name"),
                String.format("%,d", rs.getLong("rent_amount")),
                rs.getString("rent_start_date"),
                rs.getString("rent_end_date")
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // HELPER: Show only Tenants (role = 'Tenant')
    // ─────────────────────────────────────────────────────────────────
    private static void showTenantsForSelection() throws Exception {
        Connection conn = DBConnection.getConnection();
        String query = """
            SELECT c.client_id, c.client_name, c.client_phone
            FROM client c
            JOIN client_role cr ON c.client_id = cr.client_id
            WHERE cr.role = 'Tenant'
            ORDER BY c.client_name
        """;
        ResultSet rs = conn.createStatement().executeQuery(query);
        List<String> headers = Arrays.asList("Client ID", "Name", "Phone");
        List<List<String>> rows = new ArrayList<>();
        while (rs.next()) {
            rows.add(Arrays.asList(
                    String.valueOf(rs.getInt("client_id")),
                    rs.getString("client_name"),
                    rs.getString("client_phone")
            ));
        }
        if (!rows.isEmpty()) {
            System.out.println("\n📋 Tenants:");
            TableUtil.printTable(headers, rows);
        } else {
            System.out.println("⚠  No clients with Tenant role found. You may still enter any client ID.");
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // HELPER: Show available properties for rent
    // ─────────────────────────────────────────────────────────────────
    private static void showPropertiesForRent() throws Exception {
        Connection conn = DBConnection.getConnection();
        String query = """
            SELECT p.property_id, p.address, p.city, p.locality,
                   p.bedrooms, p.size_sqft, p.availability_status,
                   pt.listing_type, pt.price
            FROM property p
            JOIN property_type pt ON p.property_id = pt.property_id
            WHERE p.availability_status = true
            AND pt.listing_type = 'Rent'
        """;
        PreparedStatement ps;
        if ("AGENT".equals(Session.role)) {
            query += " AND p.agent_id = ?";
            ps = conn.prepareStatement(query); ps.setInt(1, Session.userId);
        } else if ("AGENCY".equals(Session.role)) {
            query += " AND p.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            ps = conn.prepareStatement(query); ps.setInt(1, Session.agencyId);
        } else {
            ps = conn.prepareStatement(query);
        }
        ResultSet rs = ps.executeQuery();
        List<String> headers = Arrays.asList("Prop ID", "Address", "City", "Locality", "Beds", "Size (sqft)", "Listed Rent");
        List<List<String>> rows = new ArrayList<>();
        while (rs.next()) {
            String price = rs.getString("price") != null
                    ? "₹" + String.format("%,d", rs.getLong("price")) : "N/A";
            rows.add(Arrays.asList(
                    String.valueOf(rs.getInt("property_id")),
                    rs.getString("address"),
                    rs.getString("city"),
                    rs.getString("locality"),
                    String.valueOf(rs.getInt("bedrooms")),
                    String.valueOf(rs.getInt("size_sqft")),
                    price
            ));
        }
        if (!rows.isEmpty()) {
            System.out.println("\n📋 Available Properties for Rent:");
            TableUtil.printTable(headers, rows);
        } else {
            System.out.println("⚠  No available properties for rent found.");
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // HELPER: Show all properties (for filter selection)
    // ─────────────────────────────────────────────────────────────────
    private static void showAllPropertiesForSelection() throws Exception {
        Connection conn = DBConnection.getConnection();
        String baseQuery = """
            SELECT p.property_id, p.address, p.city, p.locality, p.bedrooms, p.availability_status
            FROM property p
        """;
        PreparedStatement ps;
        if ("AGENT".equals(Session.role)) {
            ps = conn.prepareStatement(baseQuery + " WHERE p.agent_id=? ORDER BY p.property_id");
            ps.setInt(1, Session.userId);
        } else if ("AGENCY".equals(Session.role)) {
            ps = conn.prepareStatement(baseQuery + " WHERE p.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?) ORDER BY p.property_id");
            ps.setInt(1, Session.agencyId);
        } else {
            ps = conn.prepareStatement(baseQuery + " ORDER BY p.property_id");
        }
        ResultSet rs = ps.executeQuery();
        List<String> headers = Arrays.asList("Prop ID", "Address", "City", "Beds", "Available");
        List<List<String>> rows = new ArrayList<>();
        while (rs.next()) {
            rows.add(Arrays.asList(
                    String.valueOf(rs.getInt("property_id")),
                    rs.getString("address"),
                    rs.getString("city"),
                    String.valueOf(rs.getInt("bedrooms")),
                    rs.getBoolean("availability_status") ? "Yes" : "No"
            ));
        }
        if (!rows.isEmpty()) {
            System.out.println("\n📋 Properties:");
            TableUtil.printTable(headers, rows);
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // HELPER: Show agents for selection
    // ─────────────────────────────────────────────────────────────────
    private static void showAgentsForSelection() throws Exception {
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps;
        if ("AGENCY".equals(Session.role)) {
            ps = conn.prepareStatement("SELECT agent_id, name, phone FROM agent WHERE agency_id=? ORDER BY name");
            ps.setInt(1, Session.agencyId);
        } else {
            ps = conn.prepareStatement("SELECT agent_id, name, phone FROM agent ORDER BY name");
        }
        ResultSet rs = ps.executeQuery();
        List<String> headers = Arrays.asList("Agent ID", "Name", "Phone");
        List<List<String>> rows = new ArrayList<>();
        while (rs.next()) rows.add(Arrays.asList(String.valueOf(rs.getInt("agent_id")), rs.getString("name"), rs.getString("phone")));
        if (!rows.isEmpty()) { System.out.println("\n📋 Agents:"); TableUtil.printTable(headers, rows); }
    }

    // ─────────────────────────────────────────────────────────────────
    // RECORD RENT
    // ─────────────────────────────────────────────────────────────────
    public static void recordRent() {
        try {
            Connection conn = DBConnection.getConnection();

            int rentId     = InputUtil.getPositiveInt("Enter Rent ID");
            int rentAmount = InputUtil.getPositiveInt("Enter Rent Amount");
            Date startDate = Date.valueOf(InputUtil.getStringInput("Enter Start Date (YYYY-MM-DD)"));
            Date endDate   = Date.valueOf(InputUtil.getStringInput("Enter End Date   (YYYY-MM-DD)"));

            // Show tenants first
            showTenantsForSelection();
            int tenantId = InputUtil.getPositiveInt("Enter Tenant ID");

            // Show available properties for rent
            showPropertiesForRent();
            int propertyId = InputUtil.getPositiveInt("Enter Property ID");

            int agentId;
            if ("AGENT".equals(Session.role)) {
                agentId = Session.userId;
            } else {
                showAgentsForSelection();
                agentId = InputUtil.getPositiveInt("Enter Agent ID");
            }

            // Access check
            String checkQuery = "SELECT availability_status FROM property WHERE property_id=?";
            if ("AGENT".equals(Session.role))        checkQuery += " AND agent_id=?";
            else if ("AGENCY".equals(Session.role))  checkQuery += " AND agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";

            PreparedStatement check = conn.prepareStatement(checkQuery);
            check.setInt(1, propertyId);
            if ("AGENT".equals(Session.role))        check.setInt(2, Session.userId);
            else if ("AGENCY".equals(Session.role))  check.setInt(2, Session.agencyId);

            ResultSet rs = check.executeQuery();
            if (!rs.next()) { System.out.println("❌ Property not found or access denied."); InputUtil.pressEnterToContinue(); return; }
            if (!rs.getBoolean("availability_status")) { System.out.println("❌ Property is not available for rent."); InputUtil.pressEnterToContinue(); return; }

            PreparedStatement ps = conn.prepareStatement("INSERT INTO rent VALUES (?,?,?,?,?,?,?)");
            ps.setInt(1, rentId);
            ps.setInt(2, rentAmount);
            ps.setDate(3, startDate);
            ps.setDate(4, endDate);
            ps.setInt(5, tenantId);
            ps.setInt(6, propertyId);
            ps.setInt(7, agentId);
            ps.executeUpdate();

            System.out.println(Color.GREEN + "✅ Rent recorded successfully!" + Color.RESET);

        } catch (Exception e) {
            System.out.println(Color.RED + "❌ Error: " + e.getMessage() + Color.RESET);
        }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // VIEW RENT
    // ─────────────────────────────────────────────────────────────────
    public static void viewRent() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = RENT_JOIN;
            PreparedStatement ps;
            if ("AGENT".equals(Session.role)) {
                query += " WHERE r.agent_id=? ORDER BY r.rent_start_date DESC";
                ps = conn.prepareStatement(query); ps.setInt(1, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?) ORDER BY r.rent_start_date DESC";
                ps = conn.prepareStatement(query); ps.setInt(1, Session.agencyId);
            } else {
                query += " ORDER BY r.rent_start_date DESC";
                ps = conn.prepareStatement(query);
            }
            ResultSet rs = ps.executeQuery();
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(rentRow(rs));
            if (rows.isEmpty()) System.out.println("❌ No rent records found.");
            else { System.out.println("\n📊 All Rent Records:"); TableUtil.printTable(RENT_HEADERS, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // FIND RENT BY ID — full detail
    // ─────────────────────────────────────────────────────────────────
    public static void findRentById() {
        try {
            Connection conn = DBConnection.getConnection();
            int id = InputUtil.getPositiveInt("Enter Rent ID");
            String query = RENT_JOIN + " WHERE r.rent_id=?";
            if ("AGENT".equals(Session.role))        query += " AND r.agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " AND r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            if ("AGENT".equals(Session.role))        ps.setInt(2, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(2, Session.agencyId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                List<List<String>> rows = new ArrayList<>();
                rows.add(rentRow(rs));
                System.out.println("\n📋 Rent Details:");
                TableUtil.printTable(RENT_HEADERS, rows);
            } else {
                System.out.println("❌ Rent not found or access denied.");
            }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // DELETE RENT
    // ─────────────────────────────────────────────────────────────────
    public static void deleteRent() {
        try {
            Connection conn = DBConnection.getConnection();
            int id = InputUtil.getPositiveInt("Enter Rent ID to delete");
            String checkQuery = "SELECT property_id FROM rent WHERE rent_id=?";
            if ("AGENT".equals(Session.role))        checkQuery += " AND agent_id=?";
            else if ("AGENCY".equals(Session.role))  checkQuery += " AND agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            PreparedStatement check = conn.prepareStatement(checkQuery);
            check.setInt(1, id);
            if ("AGENT".equals(Session.role))        check.setInt(2, Session.userId);
            else if ("AGENCY".equals(Session.role))  check.setInt(2, Session.agencyId);
            ResultSet rs = check.executeQuery();
            if (!rs.next()) { System.out.println("❌ Not found or access denied."); InputUtil.pressEnterToContinue(); return; }
            int propertyId = rs.getInt("property_id");
            String deleteQuery = "DELETE FROM rent WHERE rent_id=?";
            if ("AGENT".equals(Session.role))        deleteQuery += " AND agent_id=?";
            else if ("AGENCY".equals(Session.role))  deleteQuery += " AND agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            PreparedStatement ps = conn.prepareStatement(deleteQuery);
            ps.setInt(1, id);
            if ("AGENT".equals(Session.role))        ps.setInt(2, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(2, Session.agencyId);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                conn.prepareStatement("UPDATE property SET availability_status=true WHERE property_id=" + propertyId).executeUpdate();
                System.out.println(Color.GREEN + "✅ Rent deleted & property restored." + Color.RESET);
            } else System.out.println("❌ Delete failed.");
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // FILTER RENT BY DATE — full table
    // ─────────────────────────────────────────────────────────────────
    public static void filterRentByDate() {
        try {
            Connection conn = DBConnection.getConnection();
            String date = InputUtil.getStringInput("Enter Date from (YYYY-MM-DD)");
            String query = RENT_JOIN + " WHERE r.rent_start_date >= ?";
            if ("AGENT".equals(Session.role))        query += " AND r.agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " AND r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " ORDER BY r.rent_start_date DESC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDate(1, Date.valueOf(date));
            if ("AGENT".equals(Session.role))        ps.setInt(2, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(2, Session.agencyId);
            ResultSet rs = ps.executeQuery();
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(rentRow(rs));
            if (rows.isEmpty()) System.out.println("❌ No rent records found from " + date + " onwards.");
            else { System.out.println("\n📊 Rent Records from " + date + ":"); TableUtil.printTable(RENT_HEADERS, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // FILTER RENT BY PROPERTY — list properties first, full table
    // ─────────────────────────────────────────────────────────────────
    public static void filterRentByProperty() {
        try {
            Connection conn = DBConnection.getConnection();
            showAllPropertiesForSelection();
            int id = InputUtil.getPositiveInt("Enter Property ID");
            String query = RENT_JOIN + " WHERE r.property_id=?";
            if ("AGENT".equals(Session.role))        query += " AND r.agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " AND r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " ORDER BY r.rent_start_date DESC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            if ("AGENT".equals(Session.role))        ps.setInt(2, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(2, Session.agencyId);
            ResultSet rs = ps.executeQuery();
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(rentRow(rs));
            if (rows.isEmpty()) System.out.println("❌ No rent records found for Property ID " + id + ".");
            else { System.out.println("\n📊 Rent for Property ID " + id + ":"); TableUtil.printTable(RENT_HEADERS, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // FILTER RENT BY CLIENT — list tenants first, full table
    // ─────────────────────────────────────────────────────────────────
    public static void filterRentByClient() {
        try {
            Connection conn = DBConnection.getConnection();
            showTenantsForSelection();
            int id = InputUtil.getPositiveInt("Enter Tenant (Client) ID");
            String query = RENT_JOIN + " WHERE r.tenant_id=?";
            if ("AGENT".equals(Session.role))        query += " AND r.agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " AND r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " ORDER BY r.rent_start_date DESC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            if ("AGENT".equals(Session.role))        ps.setInt(2, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(2, Session.agencyId);
            ResultSet rs = ps.executeQuery();
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(rentRow(rs));
            if (rows.isEmpty()) System.out.println("❌ No rent records found for this client.");
            else { System.out.println("\n📊 Rent for Client ID " + id + ":"); TableUtil.printTable(RENT_HEADERS, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // RENT BY DATE RANGE — full table
    // ─────────────────────────────────────────────────────────────────
    public static void rentByDateRange() {
        try {
            Connection conn = DBConnection.getConnection();
            String start = InputUtil.getStringInput("Enter Start Date (YYYY-MM-DD)");
            String end   = InputUtil.getStringInput("Enter End Date   (YYYY-MM-DD)");
            String query = RENT_JOIN + " WHERE r.rent_start_date BETWEEN ? AND ?";
            if ("AGENT".equals(Session.role))        query += " AND r.agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " AND r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " ORDER BY r.rent_start_date DESC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDate(1, Date.valueOf(start)); ps.setDate(2, Date.valueOf(end));
            if ("AGENT".equals(Session.role))        ps.setInt(3, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(3, Session.agencyId);
            ResultSet rs = ps.executeQuery();
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(rentRow(rs));
            if (rows.isEmpty()) System.out.println("❌ No rent records found in this date range.");
            else { System.out.println("\n📊 Rent (" + start + " → " + end + "):"); TableUtil.printTable(RENT_HEADERS, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // TOTAL RENT AMOUNT
    // ─────────────────────────────────────────────────────────────────
    public static void totalRentAmount() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT SUM(rent_amount) AS total FROM rent";
            PreparedStatement ps;
            if ("AGENT".equals(Session.role)) { query += " WHERE agent_id=?"; ps = conn.prepareStatement(query); ps.setInt(1, Session.userId); }
            else if ("AGENCY".equals(Session.role)) { query += " WHERE agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)"; ps = conn.prepareStatement(query); ps.setInt(1, Session.agencyId); }
            else ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                List<String> h = Arrays.asList("Total Rent Collected");
                List<List<String>> r = new ArrayList<>();
                r.add(Arrays.asList("₹" + String.format("%,d", rs.getLong("total"))));
                TableUtil.printTable(h, r);
            }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // COUNT RENTS
    // ─────────────────────────────────────────────────────────────────
    public static void countRents() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT COUNT(*) AS total FROM rent";
            PreparedStatement ps;
            if ("AGENT".equals(Session.role)) { query += " WHERE agent_id=?"; ps = conn.prepareStatement(query); ps.setInt(1, Session.userId); }
            else if ("AGENCY".equals(Session.role)) { query += " WHERE agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)"; ps = conn.prepareStatement(query); ps.setInt(1, Session.agencyId); }
            else ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) System.out.println("Total Rent Records: " + rs.getInt("total"));
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // SORT RENT BY AMOUNT — full table
    // ─────────────────────────────────────────────────────────────────
    public static void sortRentByAmount() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = RENT_JOIN;
            PreparedStatement ps;
            if ("AGENT".equals(Session.role)) { query += " WHERE r.agent_id=? ORDER BY r.rent_amount DESC"; ps = conn.prepareStatement(query); ps.setInt(1, Session.userId); }
            else if ("AGENCY".equals(Session.role)) { query += " WHERE r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?) ORDER BY r.rent_amount DESC"; ps = conn.prepareStatement(query); ps.setInt(1, Session.agencyId); }
            else { query += " ORDER BY r.rent_amount DESC"; ps = conn.prepareStatement(query); }
            ResultSet rs = ps.executeQuery();
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(rentRow(rs));
            if (rows.isEmpty()) System.out.println("❌ No rent records found.");
            else { System.out.println("\n📊 Rent Sorted by Amount (High → Low):"); TableUtil.printTable(RENT_HEADERS, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // SORT RENT BY DATE — full table
    // ─────────────────────────────────────────────────────────────────
    public static void sortRentByDate() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = RENT_JOIN;
            PreparedStatement ps;
            if ("AGENT".equals(Session.role)) { query += " WHERE r.agent_id=? ORDER BY r.rent_start_date DESC"; ps = conn.prepareStatement(query); ps.setInt(1, Session.userId); }
            else if ("AGENCY".equals(Session.role)) { query += " WHERE r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?) ORDER BY r.rent_start_date DESC"; ps = conn.prepareStatement(query); ps.setInt(1, Session.agencyId); }
            else { query += " ORDER BY r.rent_start_date DESC"; ps = conn.prepareStatement(query); }
            ResultSet rs = ps.executeQuery();
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(rentRow(rs));
            if (rows.isEmpty()) System.out.println("❌ No rent records found.");
            else { System.out.println("\n📊 Rent Sorted by Date (Newest First):"); TableUtil.printTable(RENT_HEADERS, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // RENT SUMMARY
    // ─────────────────────────────────────────────────────────────────
    public static void rentSummary() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT COUNT(*) AS total, SUM(rent_amount) AS total_rent, AVG(rent_amount) AS avg_rent FROM rent";
            PreparedStatement ps;
            if ("AGENT".equals(Session.role)) { query += " WHERE agent_id=?"; ps = conn.prepareStatement(query); ps.setInt(1, Session.userId); }
            else if ("AGENCY".equals(Session.role)) { query += " WHERE agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)"; ps = conn.prepareStatement(query); ps.setInt(1, Session.agencyId); }
            else ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                List<String> h = Arrays.asList("Metric", "Value");
                List<List<String>> r = new ArrayList<>();
                r.add(Arrays.asList("Total Rentals",        String.valueOf(rs.getInt("total"))));
                r.add(Arrays.asList("Total Rent Collected", "₹" + String.format("%,d", rs.getLong("total_rent"))));
                r.add(Arrays.asList("Average Rent",         "₹" + String.format("%,d", rs.getLong("avg_rent"))));
                System.out.println("\n📊 Rent Summary:");
                TableUtil.printTable(h, r);
            }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // TOP TENANT — full detail table
    // ─────────────────────────────────────────────────────────────────
    public static void topTenant() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = """
                SELECT c.client_id, c.client_name, c.client_phone, c.client_email,
                       COUNT(r.rent_id) AS total_rents,
                       SUM(r.rent_amount) AS total_paid
                FROM rent r
                JOIN client c ON r.tenant_id = c.client_id
            """;
            if ("AGENT".equals(Session.role))        query += " WHERE r.agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " WHERE r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " GROUP BY c.client_id, c.client_name, c.client_phone, c.client_email ORDER BY total_rents DESC, total_paid DESC LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(query);
            if ("AGENT".equals(Session.role))        ps.setInt(1, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(1, Session.agencyId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                List<String> h = Arrays.asList("Client ID", "Name", "Phone", "Email", "Total Rentals", "Total Paid (₹)");
                List<List<String>> r = new ArrayList<>();
                r.add(Arrays.asList(
                        String.valueOf(rs.getInt("client_id")),
                        rs.getString("client_name"),
                        rs.getString("client_phone"),
                        rs.getString("client_email"),
                        String.valueOf(rs.getInt("total_rents")),
                        String.format("%,d", rs.getLong("total_paid"))
                ));
                System.out.println("\n🏆 Top Tenant:");
                TableUtil.printTable(h, r);
            } else System.out.println("❌ No tenant data found.");
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // TOP AGENT BY RENT REVENUE — table
    // ─────────────────────────────────────────────────────────────────
    public static void topAgentByRentRevenue() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = """
                SELECT a.agent_id, a.name, a.phone,
                       COUNT(r.rent_id) AS total_rentals,
                       SUM(r.rent_amount) AS total_revenue
                FROM rent r
                JOIN agent a ON r.agent_id = a.agent_id
            """;
            if ("AGENT".equals(Session.role))        query += " WHERE r.agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " WHERE r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " GROUP BY a.agent_id, a.name, a.phone ORDER BY total_revenue DESC LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(query);
            if ("AGENT".equals(Session.role))        ps.setInt(1, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(1, Session.agencyId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                List<String> h = Arrays.asList("Agent ID", "Name", "Phone", "Total Rentals", "Total Revenue (₹)");
                List<List<String>> r = new ArrayList<>();
                r.add(Arrays.asList(String.valueOf(rs.getInt("agent_id")), rs.getString("name"), rs.getString("phone"), String.valueOf(rs.getInt("total_rentals")), String.format("%,d", rs.getLong("total_revenue"))));
                System.out.println("\n🏆 Top Agent by Rent Revenue:");
                TableUtil.printTable(h, r);
            } else System.out.println("❌ No data found.");
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // RENT BY CITY — table
    // ─────────────────────────────────────────────────────────────────
    public static void rentByCity() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT p.city, COUNT(r.rent_id) AS total_rentals, SUM(r.rent_amount) AS total_rent FROM rent r JOIN property p ON r.property_id = p.property_id";
            if ("AGENT".equals(Session.role))        query += " WHERE r.agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " WHERE r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " GROUP BY p.city ORDER BY total_rent DESC";
            PreparedStatement ps = conn.prepareStatement(query);
            if ("AGENT".equals(Session.role))        ps.setInt(1, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(1, Session.agencyId);
            ResultSet rs = ps.executeQuery();
            List<String> h = Arrays.asList("City", "Total Rentals", "Total Rent (₹)");
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(Arrays.asList(rs.getString("city"), String.valueOf(rs.getInt("total_rentals")), String.format("%,d", rs.getLong("total_rent"))));
            if (rows.isEmpty()) System.out.println("❌ No data found.");
            else { System.out.println("\n📊 Rent by City:"); TableUtil.printTable(h, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // MONTHLY RENT REPORT — table
    // ─────────────────────────────────────────────────────────────────
    public static void monthlyRentReport() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT YEAR(rent_start_date) AS year, MONTH(rent_start_date) AS month, COUNT(*) AS total_rentals, SUM(rent_amount) AS total FROM rent";
            if ("AGENT".equals(Session.role))        query += " WHERE agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " WHERE agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " GROUP BY YEAR(rent_start_date), MONTH(rent_start_date) ORDER BY year DESC, month DESC";
            PreparedStatement ps = conn.prepareStatement(query);
            if ("AGENT".equals(Session.role))        ps.setInt(1, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(1, Session.agencyId);
            ResultSet rs = ps.executeQuery();
            List<String> h = Arrays.asList("Year", "Month", "Total Rentals", "Total Rent (₹)");
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(Arrays.asList(String.valueOf(rs.getInt("year")), String.valueOf(rs.getInt("month")), String.valueOf(rs.getInt("total_rentals")), String.format("%,d", rs.getLong("total"))));
            if (rows.isEmpty()) System.out.println("❌ No data found.");
            else { System.out.println("\n📊 Monthly Rent Report:"); TableUtil.printTable(h, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // HIGH VALUE RENTALS — full table
    // ─────────────────────────────────────────────────────────────────
    public static void highValueRentals() {
        try {
            Connection conn = DBConnection.getConnection();
            int amount = InputUtil.getPositiveInt("Enter minimum rent amount");
            String query = RENT_JOIN + " WHERE r.rent_amount >= ?";
            if ("AGENT".equals(Session.role))        query += " AND r.agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " AND r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " ORDER BY r.rent_amount DESC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, amount);
            if ("AGENT".equals(Session.role))        ps.setInt(2, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(2, Session.agencyId);
            ResultSet rs = ps.executeQuery();
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(rentRow(rs));
            if (rows.isEmpty()) System.out.println("❌ No high-value rentals found.");
            else { System.out.println("\n📊 High Value Rentals (≥ ₹" + String.format("%,d", (long) amount) + "):"); TableUtil.printTable(RENT_HEADERS, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // VACANT PROPERTIES — table
    // ─────────────────────────────────────────────────────────────────
    public static void vacantProperties() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = """
                SELECT p.property_id, p.address, p.city, p.locality, p.bedrooms, p.size_sqft
                FROM property p
                LEFT JOIN rent r ON p.property_id = r.property_id
                WHERE r.property_id IS NULL
            """;
            PreparedStatement ps;
            if ("AGENT".equals(Session.role)) { query += " AND p.agent_id=?"; ps = conn.prepareStatement(query); ps.setInt(1, Session.userId); }
            else if ("AGENCY".equals(Session.role)) { query += " AND p.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)"; ps = conn.prepareStatement(query); ps.setInt(1, Session.agencyId); }
            else ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            List<String> h = Arrays.asList("Prop ID", "Address", "City", "Locality", "Beds", "Size (sqft)");
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(Arrays.asList(String.valueOf(rs.getInt("property_id")), rs.getString("address"), rs.getString("city"), rs.getString("locality"), String.valueOf(rs.getInt("bedrooms")), String.valueOf(rs.getInt("size_sqft"))));
            if (rows.isEmpty()) System.out.println("❌ No vacant properties found.");
            else { System.out.println("\n📊 Vacant Properties (Never Rented):"); TableUtil.printTable(h, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // REPEAT TENANTS — table
    // ─────────────────────────────────────────────────────────────────
    public static void repeatTenants() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT c.client_id, c.client_name, c.client_phone, COUNT(r.rent_id) AS total_rents FROM rent r JOIN client c ON r.tenant_id = c.client_id";
            if ("AGENT".equals(Session.role))        query += " WHERE r.agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " WHERE r.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " GROUP BY c.client_id, c.client_name, c.client_phone HAVING COUNT(r.rent_id) > 1 ORDER BY total_rents DESC";
            PreparedStatement ps = conn.prepareStatement(query);
            if ("AGENT".equals(Session.role))        ps.setInt(1, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(1, Session.agencyId);
            ResultSet rs = ps.executeQuery();
            List<String> h = Arrays.asList("Client ID", "Name", "Phone", "Total Rentals");
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(Arrays.asList(String.valueOf(rs.getInt("client_id")), rs.getString("client_name"), rs.getString("client_phone"), String.valueOf(rs.getInt("total_rents"))));
            if (rows.isEmpty()) System.out.println("❌ No repeat tenants found.");
            else { System.out.println("\n📊 Repeat Tenants:"); TableUtil.printTable(h, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }
}