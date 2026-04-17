import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SalesService {

    // ─────────────────────────────────────────────────────────────────
    // HELPER: Full sale table (used by every listing method)
    // ─────────────────────────────────────────────────────────────────
    private static final String SALE_JOIN = """
        SELECT s.sales_id, s.property_id, p.address, p.city,
               b.client_name AS buyer,  b.client_id AS buyer_id,
               se.client_name AS seller, se.client_id AS seller_id,
               a.name AS agent, s.agent_id,
               s.sales_price, s.sales_date
        FROM sales s
        JOIN client b  ON s.buyer_id  = b.client_id
        JOIN client se ON s.seller_id = se.client_id
        JOIN agent  a  ON s.agent_id  = a.agent_id
        JOIN property p ON s.property_id = p.property_id
    """;

    private static final List<String> SALE_HEADERS = Arrays.asList(
            "Sale ID", "Prop ID", "Address", "City",
            "Buyer", "Seller", "Agent", "Amount (₹)", "Date"
    );

    private static List<String> saleRow(ResultSet rs) throws Exception {
        return Arrays.asList(
                String.valueOf(rs.getInt("sales_id")),
                String.valueOf(rs.getInt("property_id")),
                rs.getString("address"),
                rs.getString("city"),
                rs.getString("buyer"),
                rs.getString("seller"),
                rs.getString("agent"),
                String.format("%,d", rs.getLong("sales_price")),
                rs.getString("sales_date")
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // HELPER: Show only Buyers (role = 'Buyer')
    // ─────────────────────────────────────────────────────────────────
    private static void showBuyersForSelection() throws Exception {
        Connection conn = DBConnection.getConnection();
        String query = """
            SELECT c.client_id, c.client_name, c.client_phone
            FROM client c
            JOIN client_role cr ON c.client_id = cr.client_id
            WHERE cr.role = 'Buyer'
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
            System.out.println("\n📋 Buyers:");
            TableUtil.printTable(headers, rows);
        } else {
            System.out.println("⚠  No clients with Buyer role found. You may still enter any client ID.");
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // HELPER: Show only Sellers (role = 'Seller')
    // ─────────────────────────────────────────────────────────────────
    private static void showSellersForSelection() throws Exception {
        Connection conn = DBConnection.getConnection();
        String query = """
            SELECT c.client_id, c.client_name, c.client_phone
            FROM client c
            JOIN client_role cr ON c.client_id = cr.client_id
            JOIN property p ON c.client_id = p.owner_id
            WHERE cr.role = 'Seller'
            and p.availability_status = true
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
            System.out.println("\n📋 Sellers:");
            TableUtil.printTable(headers, rows);
        } else {
            System.out.println("⚠  No clients with Seller role found. You may still enter any client ID.");
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // HELPER: Show properties that belong to a specific seller (owner)
    // ─────────────────────────────────────────────────────────────────
    private static void showPropertiesForSeller(int sellerId) throws Exception {
        Connection conn = DBConnection.getConnection();
        String query = """
            SELECT p.property_id, p.address, p.city, p.locality,
                   p.bedrooms, p.size_sqft, p.availability_status,
                   pt.listing_type, pt.price
            FROM property p
            LEFT JOIN property_type pt ON p.property_id = pt.property_id
                   AND pt.listing_type = 'Sale'
            WHERE p.owner_id = ? AND p.availability_status = true
        """;
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, sellerId);
        ResultSet rs = ps.executeQuery();
        List<String> headers = Arrays.asList(
                "Prop ID", "Address", "City", "Locality", "Beds", "Size (sqft)", "Listed Price"
        );
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
            System.out.println("\n📋 Properties owned by this seller (available for sale):");
            TableUtil.printTable(headers, rows);
        } else {
            System.out.println("⚠  No available properties found for this seller.");
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // HELPER: List all agents (for filter-by-agent selection)
    // ─────────────────────────────────────────────────────────────────
    private static void showAgentsForSelection() throws Exception {
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps;
        if ("AGENCY".equals(Session.role)) {
            ps = conn.prepareStatement("SELECT agent_id, name, phone FROM agent WHERE agency_id = ? ORDER BY name");
            ps.setInt(1, Session.agencyId);
        } else {
            ps = conn.prepareStatement("SELECT agent_id, name, phone FROM agent ORDER BY name");
        }
        ResultSet rs = ps.executeQuery();
        List<String> headers = Arrays.asList("Agent ID", "Name", "Phone");
        List<List<String>> rows = new ArrayList<>();
        while (rs.next()) {
            rows.add(Arrays.asList(
                    String.valueOf(rs.getInt("agent_id")),
                    rs.getString("name"),
                    rs.getString("phone")
            ));
        }
        if (!rows.isEmpty()) {
            System.out.println("\n📋 Agents:");
            TableUtil.printTable(headers, rows);
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // HELPER: List all properties (for filter-by-property selection)
    // ─────────────────────────────────────────────────────────────────
    private static void showAllPropertiesForSelection() throws Exception {
        Connection conn = DBConnection.getConnection();
        String query = """
            SELECT p.property_id, p.address, p.city, p.locality,
                   p.bedrooms, p.availability_status, pt.listing_type, pt.price
            FROM property p
            LEFT JOIN property_type pt ON p.property_id = pt.property_id
               AND pt.listing_type = 'Sale'
            ORDER BY p.property_id
        """;
        PreparedStatement ps;
        if ("AGENT".equals(Session.role)) {
            query = query.replace("ORDER BY", "WHERE p.agent_id = ? ORDER BY");
            ps = conn.prepareStatement(query);
            ps.setInt(1, Session.userId);
        } else if ("AGENCY".equals(Session.role)) {
            query = query.replace("ORDER BY", "WHERE p.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?) ORDER BY");
            ps = conn.prepareStatement(query);
            ps.setInt(1, Session.agencyId);
        } else {
            ps = conn.prepareStatement(query);
        }
        ResultSet rs = ps.executeQuery();
        List<String> headers = Arrays.asList("Prop ID", "Address", "City", "Beds", "Available", "Listed Price");
        List<List<String>> rows = new ArrayList<>();
        while (rs.next()) {
            String price = rs.getString("price") != null
                    ? "₹" + String.format("%,d", rs.getLong("price")) : "N/A";
            rows.add(Arrays.asList(
                    String.valueOf(rs.getInt("property_id")),
                    rs.getString("address"),
                    rs.getString("city"),
                    String.valueOf(rs.getInt("bedrooms")),
                    rs.getBoolean("availability_status") ? "Yes" : "No",
                    price
            ));
        }
        if (!rows.isEmpty()) {
            System.out.println("\n📋 Properties:");
            TableUtil.printTable(headers, rows);
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // RECORD SALE
    // ─────────────────────────────────────────────────────────────────
    public static void RecordSale() {
        try {
            Connection conn = DBConnection.getConnection();

            int salesId    = InputUtil.getPositiveInt("Enter Sales ID");
            int salesPrice = InputUtil.getPositiveInt("Enter Sales Price");
            String salesDate = InputUtil.getStringInput("Enter Date (YYYY-MM-DD)");

            // Show sellers first, then ask for seller
            showSellersForSelection();
            int sellerId = InputUtil.getPositiveInt("Enter Seller ID");

            // Show only properties owned by that seller
            showPropertiesForSeller(sellerId);
            int propertyId = InputUtil.getPositiveInt("Enter Property ID");

            // Show buyers
            showBuyersForSelection();
            int buyerId = InputUtil.getPositiveInt("Enter Buyer ID");

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
            if (!rs.next()) { System.out.println("❌ Access denied or property not found"); InputUtil.pressEnterToContinue(); return; }
            if (!rs.getBoolean("availability_status")) { System.out.println("❌ Property is not available for sale"); InputUtil.pressEnterToContinue(); return; }

            PreparedStatement ps = conn.prepareStatement("INSERT INTO sales VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, salesId);
            ps.setInt(2, salesPrice);
            ps.setString(3, salesDate);
            ps.setInt(4, buyerId);
            ps.setInt(5, sellerId);
            ps.setInt(6, agentId);
            ps.setInt(7, propertyId);
            ps.executeUpdate();

            System.out.println(Color.GREEN + "✅ Sale recorded successfully!" + Color.RESET);

        } catch (Exception e) {
            System.out.println(Color.RED + "❌ Error: " + e.getMessage() + Color.RESET);
        }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // VIEW SALES
    // ─────────────────────────────────────────────────────────────────
    public static void viewSales() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = SALE_JOIN;
            PreparedStatement ps;
            if ("AGENT".equals(Session.role)) {
                query += " WHERE s.agent_id = ? ORDER BY s.sales_date DESC";
                ps = conn.prepareStatement(query); ps.setInt(1, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?) ORDER BY s.sales_date DESC";
                ps = conn.prepareStatement(query); ps.setInt(1, Session.agencyId);
            } else {
                query += " ORDER BY s.sales_date DESC";
                ps = conn.prepareStatement(query);
            }
            ResultSet rs = ps.executeQuery();
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(saleRow(rs));
            if (rows.isEmpty()) System.out.println("❌ No sales records found.");
            else { System.out.println("\n📊 All Sales:"); TableUtil.printTable(SALE_HEADERS, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // FIND SALE BY ID — full detail
    // ─────────────────────────────────────────────────────────────────
    public static void findSaleById() {
        try {
            Connection conn = DBConnection.getConnection();
            int id = InputUtil.getPositiveInt("Enter Sales ID");

            String query = SALE_JOIN + " WHERE s.sales_id = ?";
            if ("AGENT".equals(Session.role))        query += " AND s.agent_id = ?";
            else if ("AGENCY".equals(Session.role))  query += " AND s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            if ("AGENT".equals(Session.role))        ps.setInt(2, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(2, Session.agencyId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                List<List<String>> rows = new ArrayList<>();
                rows.add(saleRow(rs));
                System.out.println("\n📋 Sale Details:");
                TableUtil.printTable(SALE_HEADERS, rows);
            } else {
                System.out.println("❌ Sale not found or access denied.");
            }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // TOP BUYER — proper table
    // ─────────────────────────────────────────────────────────────────
    public static void topBuyer() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
                SELECT c.client_id, c.client_name, c.client_phone, c.client_email,
                       COUNT(s.sales_id) AS total_purchases,
                       SUM(s.sales_price) AS total_investment
                FROM client c
                JOIN sales s ON c.client_id = s.buyer_id
            """;

            if ("AGENT".equals(Session.role))        query += " WHERE s.agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " WHERE s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";

            query += " GROUP BY c.client_id, c.client_name, c.client_phone, c.client_email ORDER BY total_purchases DESC, total_investment DESC LIMIT 1";

            PreparedStatement ps = conn.prepareStatement(query);
            if ("AGENT".equals(Session.role))        ps.setInt(1, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(1, Session.agencyId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                List<String> headers = Arrays.asList("Client ID", "Name", "Phone", "Email", "Purchases", "Total Invested (₹)");
                List<List<String>> rows = new ArrayList<>();
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("client_id")),
                        rs.getString("client_name"),
                        rs.getString("client_phone"),
                        rs.getString("client_email"),
                        String.valueOf(rs.getInt("total_purchases")),
                        String.format("%,d", rs.getLong("total_investment"))
                ));
                System.out.println("\n🏆 Top Buyer:");
                TableUtil.printTable(headers, rows);
            } else {
                System.out.println("❌ No buyer data found.");
            }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // TOP BUYER INFO (alias kept for backward compatibility)
    // ─────────────────────────────────────────────────────────────────
    public static void topBuyerInfo() { topBuyer(); }

    // ─────────────────────────────────────────────────────────────────
    // SALES BY DATE RANGE — full table
    // ─────────────────────────────────────────────────────────────────
    public static void salesByDateRange() {
        try {
            Connection conn = DBConnection.getConnection();
            String start = InputUtil.getStringInput("Enter Start Date (YYYY-MM-DD)");
            String end   = InputUtil.getStringInput("Enter End Date   (YYYY-MM-DD)");

            String query = SALE_JOIN + " WHERE s.sales_date BETWEEN ? AND ?";
            if ("AGENT".equals(Session.role))        query += " AND s.agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " AND s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " ORDER BY s.sales_date DESC";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, start); ps.setString(2, end);
            if ("AGENT".equals(Session.role))        ps.setInt(3, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(3, Session.agencyId);

            ResultSet rs = ps.executeQuery();
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(saleRow(rs));
            if (rows.isEmpty()) System.out.println("❌ No sales found in this date range.");
            else { System.out.println("\n📊 Sales (" + start + " → " + end + "):"); TableUtil.printTable(SALE_HEADERS, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // FILTER SALES BY DATE — full table
    // ─────────────────────────────────────────────────────────────────
    public static void filterSalesByDate() {
        try {
            Connection conn = DBConnection.getConnection();
            String date = InputUtil.getStringInput("Enter Date from (YYYY-MM-DD)");

            String query = SALE_JOIN + " WHERE s.sales_date >= ?";
            if ("AGENT".equals(Session.role))        query += " AND s.agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " AND s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " ORDER BY s.sales_date DESC";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, date);
            if ("AGENT".equals(Session.role))        ps.setInt(2, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(2, Session.agencyId);

            ResultSet rs = ps.executeQuery();
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(saleRow(rs));
            if (rows.isEmpty()) System.out.println("❌ No sales found from " + date + " onwards.");
            else { System.out.println("\n📊 Sales from " + date + ":"); TableUtil.printTable(SALE_HEADERS, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // FILTER SALES BY AGENT — list agents first, then full table
    // ─────────────────────────────────────────────────────────────────
    public static void filterSalesByAgent() {
        try {
            Connection conn = DBConnection.getConnection();
            int agentId;
            if ("AGENT".equals(Session.role)) {
                agentId = Session.userId;
            } else {
                showAgentsForSelection();
                agentId = InputUtil.getPositiveInt("Enter Agent ID");
            }

            String query = SALE_JOIN + " WHERE s.agent_id = ?";
            if ("AGENCY".equals(Session.role)) query += " AND s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " ORDER BY s.sales_date DESC";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, agentId);
            if ("AGENCY".equals(Session.role)) ps.setInt(2, Session.agencyId);

            ResultSet rs = ps.executeQuery();
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(saleRow(rs));
            if (rows.isEmpty()) System.out.println("❌ No sales found for this agent.");
            else { System.out.println("\n📊 Sales by Agent ID " + agentId + ":"); TableUtil.printTable(SALE_HEADERS, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // FILTER SALES BY PROPERTY — list properties first, then full table
    // ─────────────────────────────────────────────────────────────────
    public static void filterSalesByProperty() {
        try {
            Connection conn = DBConnection.getConnection();
            showAllPropertiesForSelection();
            int propertyId = InputUtil.getPositiveInt("Enter Property ID");

            String query = SALE_JOIN + " WHERE s.property_id = ?";
            if ("AGENT".equals(Session.role))        query += " AND s.agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " AND s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " ORDER BY s.sales_date DESC";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, propertyId);
            if ("AGENT".equals(Session.role))        ps.setInt(2, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(2, Session.agencyId);

            ResultSet rs = ps.executeQuery();
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(saleRow(rs));
            if (rows.isEmpty()) System.out.println("❌ No sales found for property ID " + propertyId + ".");
            else { System.out.println("\n📊 Sales for Property ID " + propertyId + ":"); TableUtil.printTable(SALE_HEADERS, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // SORT SALES BY AMOUNT — full table
    // ─────────────────────────────────────────────────────────────────
    public static void sortSalesByAmount() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = SALE_JOIN;
            PreparedStatement ps;
            if ("AGENT".equals(Session.role)) {
                query += " WHERE s.agent_id=? ORDER BY s.sales_price DESC";
                ps = conn.prepareStatement(query); ps.setInt(1, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?) ORDER BY s.sales_price DESC";
                ps = conn.prepareStatement(query); ps.setInt(1, Session.agencyId);
            } else {
                query += " ORDER BY s.sales_price DESC";
                ps = conn.prepareStatement(query);
            }
            ResultSet rs = ps.executeQuery();
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(saleRow(rs));
            if (rows.isEmpty()) System.out.println("❌ No sales found.");
            else { System.out.println("\n📊 Sales Sorted by Amount (High → Low):"); TableUtil.printTable(SALE_HEADERS, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // SORT SALES BY DATE — full table
    // ─────────────────────────────────────────────────────────────────
    public static void sortSalesByDate() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = SALE_JOIN;
            PreparedStatement ps;
            if ("AGENT".equals(Session.role)) {
                query += " WHERE s.agent_id=? ORDER BY s.sales_date DESC";
                ps = conn.prepareStatement(query); ps.setInt(1, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                query += " WHERE s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?) ORDER BY s.sales_date DESC";
                ps = conn.prepareStatement(query); ps.setInt(1, Session.agencyId);
            } else {
                query += " ORDER BY s.sales_date DESC";
                ps = conn.prepareStatement(query);
            }
            ResultSet rs = ps.executeQuery();
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(saleRow(rs));
            if (rows.isEmpty()) System.out.println("❌ No sales found.");
            else { System.out.println("\n📊 Sales Sorted by Date (Newest First):"); TableUtil.printTable(SALE_HEADERS, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // DELETE SALE
    // ─────────────────────────────────────────────────────────────────
    public static void deleteSale() {
        try {
            Connection conn = DBConnection.getConnection();
            int id = InputUtil.getPositiveInt("Enter Sales ID to delete");

            String checkQuery = "SELECT property_id FROM sales WHERE sales_id=?";
            if ("AGENT".equals(Session.role))        checkQuery += " AND agent_id=?";
            else if ("AGENCY".equals(Session.role))  checkQuery += " AND agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";

            PreparedStatement ps1 = conn.prepareStatement(checkQuery);
            ps1.setInt(1, id);
            if ("AGENT".equals(Session.role))        ps1.setInt(2, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps1.setInt(2, Session.agencyId);

            ResultSet rs = ps1.executeQuery();
            if (!rs.next()) { System.out.println("❌ Not found or access denied"); InputUtil.pressEnterToContinue(); return; }

            int propertyId = rs.getInt("property_id");
            String deleteQuery = "DELETE FROM sales WHERE sales_id=?";
            if ("AGENT".equals(Session.role))        deleteQuery += " AND agent_id=?";
            else if ("AGENCY".equals(Session.role))  deleteQuery += " AND agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";

            PreparedStatement ps = conn.prepareStatement(deleteQuery);
            ps.setInt(1, id);
            if ("AGENT".equals(Session.role))        ps.setInt(2, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(2, Session.agencyId);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                conn.prepareStatement("UPDATE property SET availability_status=true WHERE property_id=" + propertyId).executeUpdate();
                System.out.println(Color.GREEN + "✅ Sale deleted & property restored." + Color.RESET);
            } else {
                System.out.println("❌ Delete failed.");
            }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // TOTAL SALES AMOUNT
    // ─────────────────────────────────────────────────────────────────
    public static void totalSalesAmount() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT SUM(sales_price) AS total FROM sales";
            PreparedStatement ps;
            if ("AGENT".equals(Session.role)) { query += " WHERE agent_id=?"; ps = conn.prepareStatement(query); ps.setInt(1, Session.userId); }
            else if ("AGENCY".equals(Session.role)) { query += " WHERE agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)"; ps = conn.prepareStatement(query); ps.setInt(1, Session.agencyId); }
            else ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                List<String> h = Arrays.asList("Total Sales Amount");
                List<List<String>> r = new ArrayList<>();
                r.add(Arrays.asList("₹" + String.format("%,d", rs.getLong("total"))));
                TableUtil.printTable(h, r);
            }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // COUNT SALES
    // ─────────────────────────────────────────────────────────────────
    public static void countSales() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT COUNT(*) AS total FROM sales";
            PreparedStatement ps;
            if ("AGENT".equals(Session.role)) { query += " WHERE agent_id=?"; ps = conn.prepareStatement(query); ps.setInt(1, Session.userId); }
            else if ("AGENCY".equals(Session.role)) { query += " WHERE agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)"; ps = conn.prepareStatement(query); ps.setInt(1, Session.agencyId); }
            else ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) System.out.println("Total Sales Count: " + rs.getInt("total"));
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // SALES SUMMARY
    // ─────────────────────────────────────────────────────────────────
    public static void salesSummary() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT COUNT(*) AS total_sales, SUM(sales_price) AS total_revenue, AVG(sales_price) AS avg_price FROM sales";
            PreparedStatement ps;
            if ("AGENT".equals(Session.role)) { query += " WHERE agent_id=?"; ps = conn.prepareStatement(query); ps.setInt(1, Session.userId); }
            else if ("AGENCY".equals(Session.role)) { query += " WHERE agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)"; ps = conn.prepareStatement(query); ps.setInt(1, Session.agencyId); }
            else ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                List<String> h = Arrays.asList("Metric", "Value");
                List<List<String>> r = new ArrayList<>();
                r.add(Arrays.asList("Total Sales",    String.valueOf(rs.getInt("total_sales"))));
                r.add(Arrays.asList("Total Revenue",  "₹" + String.format("%,d", rs.getLong("total_revenue"))));
                r.add(Arrays.asList("Average Price",  "₹" + String.format("%,d", rs.getLong("avg_price"))));
                System.out.println("\n📊 Sales Summary:");
                TableUtil.printTable(h, r);
            }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // TOP AGENT BY SALES REVENUE — table
    // ─────────────────────────────────────────────────────────────────
    public static void topAgentBySalesRevenue() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = """
                SELECT a.agent_id, a.name, a.phone,
                       COUNT(s.sales_id) AS total_sales,
                       SUM(s.sales_price) AS total_revenue
                FROM sales s
                JOIN agent a ON s.agent_id = a.agent_id
            """;
            if ("AGENT".equals(Session.role))        query += " WHERE s.agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " WHERE s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " GROUP BY a.agent_id, a.name, a.phone ORDER BY total_revenue DESC LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(query);
            if ("AGENT".equals(Session.role))        ps.setInt(1, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(1, Session.agencyId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                List<String> h = Arrays.asList("Agent ID", "Name", "Phone", "Total Sales", "Total Revenue (₹)");
                List<List<String>> r = new ArrayList<>();
                r.add(Arrays.asList(
                        String.valueOf(rs.getInt("agent_id")),
                        rs.getString("name"), rs.getString("phone"),
                        String.valueOf(rs.getInt("total_sales")),
                        String.format("%,d", rs.getLong("total_revenue"))
                ));
                System.out.println("\n🏆 Top Agent by Sales Revenue:");
                TableUtil.printTable(h, r);
            } else System.out.println("❌ No data found.");
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // SALES BY CITY — table
    // ─────────────────────────────────────────────────────────────────
    public static void salesByCity() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = """
                SELECT p.city, COUNT(s.sales_id) AS total_sales, SUM(s.sales_price) AS total_revenue
                FROM sales s
                JOIN property p ON s.property_id = p.property_id
            """;
            if ("AGENT".equals(Session.role))        query += " WHERE s.agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " WHERE s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " GROUP BY p.city ORDER BY total_revenue DESC";
            PreparedStatement ps = conn.prepareStatement(query);
            if ("AGENT".equals(Session.role))        ps.setInt(1, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(1, Session.agencyId);
            ResultSet rs = ps.executeQuery();
            List<String> h = Arrays.asList("City", "Total Sales", "Total Revenue (₹)");
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(Arrays.asList(rs.getString("city"), String.valueOf(rs.getInt("total_sales")), String.format("%,d", rs.getLong("total_revenue"))));
            if (rows.isEmpty()) System.out.println("❌ No data found.");
            else { System.out.println("\n📊 Sales by City:"); TableUtil.printTable(h, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // MONTHLY SALES REPORT — table
    // ─────────────────────────────────────────────────────────────────
    public static void monthlySalesReport() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT MONTH(sales_date) AS month, YEAR(sales_date) AS year, COUNT(*) AS total_sales, SUM(sales_price) AS revenue FROM sales";
            if ("AGENT".equals(Session.role))        query += " WHERE agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " WHERE agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " GROUP BY YEAR(sales_date), MONTH(sales_date) ORDER BY year DESC, month DESC";
            PreparedStatement ps = conn.prepareStatement(query);
            if ("AGENT".equals(Session.role))        ps.setInt(1, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(1, Session.agencyId);
            ResultSet rs = ps.executeQuery();
            List<String> h = Arrays.asList("Year", "Month", "Total Sales", "Revenue (₹)");
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(Arrays.asList(String.valueOf(rs.getInt("year")), String.valueOf(rs.getInt("month")), String.valueOf(rs.getInt("total_sales")), String.format("%,d", rs.getLong("revenue"))));
            if (rows.isEmpty()) System.out.println("❌ No data found.");
            else { System.out.println("\n📊 Monthly Sales Report:"); TableUtil.printTable(h, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // HIGH VALUE SALES — full table
    // ─────────────────────────────────────────────────────────────────
    public static void highValueSales() {
        try {
            Connection conn = DBConnection.getConnection();
            int amount = InputUtil.getPositiveInt("Enter minimum sale amount");
            String query = SALE_JOIN + " WHERE s.sales_price >= ?";
            if ("AGENT".equals(Session.role))        query += " AND s.agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " AND s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " ORDER BY s.sales_price DESC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, amount);
            if ("AGENT".equals(Session.role))        ps.setInt(2, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(2, Session.agencyId);
            ResultSet rs = ps.executeQuery();
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(saleRow(rs));
            if (rows.isEmpty()) System.out.println("❌ No high-value sales found.");
            else { System.out.println("\n📊 High Value Sales (≥ ₹" + String.format("%,d", (long) amount) + "):"); TableUtil.printTable(SALE_HEADERS, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // UNSOLD PROPERTIES — table
    // ─────────────────────────────────────────────────────────────────
    public static void unsoldProperties() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = """
                SELECT p.property_id, p.address, p.city, p.locality, p.bedrooms, p.size_sqft
                FROM property p
                LEFT JOIN sales s ON p.property_id = s.property_id
                WHERE s.property_id IS NULL
            """;
            PreparedStatement ps;
            if ("AGENT".equals(Session.role)) { query += " AND p.agent_id=?"; ps = conn.prepareStatement(query); ps.setInt(1, Session.userId); }
            else if ("AGENCY".equals(Session.role)) { query += " AND p.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)"; ps = conn.prepareStatement(query); ps.setInt(1, Session.agencyId); }
            else ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            List<String> h = Arrays.asList("Prop ID", "Address", "City", "Locality", "Beds", "Size (sqft)");
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(Arrays.asList(String.valueOf(rs.getInt("property_id")), rs.getString("address"), rs.getString("city"), rs.getString("locality"), String.valueOf(rs.getInt("bedrooms")), String.valueOf(rs.getInt("size_sqft"))));
            if (rows.isEmpty()) System.out.println("❌ All properties have been sold.");
            else { System.out.println("\n📊 Unsold Properties:"); TableUtil.printTable(h, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // ─────────────────────────────────────────────────────────────────
    // REPEAT BUYERS — table
    // ─────────────────────────────────────────────────────────────────
    public static void repeatBuyers() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = """
                SELECT c.client_id, c.client_name, c.client_phone, COUNT(s.sales_id) AS total_purchases
                FROM sales s
                JOIN client c ON s.buyer_id = c.client_id
            """;
            if ("AGENT".equals(Session.role))        query += " WHERE s.agent_id=?";
            else if ("AGENCY".equals(Session.role))  query += " WHERE s.agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            query += " GROUP BY c.client_id, c.client_name, c.client_phone HAVING COUNT(s.sales_id) > 1 ORDER BY total_purchases DESC";
            PreparedStatement ps = conn.prepareStatement(query);
            if ("AGENT".equals(Session.role))        ps.setInt(1, Session.userId);
            else if ("AGENCY".equals(Session.role))  ps.setInt(1, Session.agencyId);
            ResultSet rs = ps.executeQuery();
            List<String> h = Arrays.asList("Client ID", "Name", "Phone", "Total Purchases");
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) rows.add(Arrays.asList(String.valueOf(rs.getInt("client_id")), rs.getString("client_name"), rs.getString("client_phone"), String.valueOf(rs.getInt("total_purchases"))));
            if (rows.isEmpty()) System.out.println("❌ No repeat buyers found.");
            else { System.out.println("\n📊 Repeat Buyers:"); TableUtil.printTable(h, rows); }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }
}