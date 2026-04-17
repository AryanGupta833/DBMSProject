import java.sql.*;
import java.util.*;

public class AgentService {

    private static int resolveAgentId() throws Exception {
        if ("AGENT".equals(Session.role)) {
            return Session.userId; // Auto-resolve for logged-in agent
        } else {
            // Show agents first, then ask for ID
            showAgentsForSelection();
            return InputUtil.getPositiveInt("Enter Agent ID");
        }
    }
    private static void showAgenciesForSelection() throws Exception {
        Connection conn = DBConnection.getConnection();
        ResultSet rs = conn.createStatement().executeQuery("SELECT agency_id, agency_name, agency_city FROM enterprise");

        List<String> headers = Arrays.asList("Agency ID", "Agency Name", "City");
        List<List<String>> rows = new ArrayList<>();

        while (rs.next()) {
            rows.add(Arrays.asList(
                    String.valueOf(rs.getInt("agency_id")),
                    rs.getString("agency_name"),
                    rs.getString("agency_city")
            ));
        }

        if (!rows.isEmpty()) {
            System.out.println("\n🏢 Available Agencies:");
            TableUtil.printTable(headers, rows);
        } else {
            System.out.println("❌ No agencies available. Please add an agency first.");
        }
    }

    private static void showAgentsForSelection() throws Exception {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps;
            if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement("SELECT agent_id, name, phone, experience_year FROM agent WHERE agency_id=?");
                ps.setInt(1, Session.agencyId);
            } else {
                ps = conn.prepareStatement("SELECT agent_id, name, phone, experience_year FROM agent");
            }

            ResultSet rs = ps.executeQuery();

            List<String> headers = Arrays.asList("ID", "Name", "Phone", "Experience");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("agent_id")),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getInt("experience_year") + " yrs"
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No agents available");
            } else {
                System.out.println("\n📋 Available Agents:");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void addAgent() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Agent ID");

            // Check if agent ID already exists
            PreparedStatement checkAgent = conn.prepareStatement("SELECT 1 FROM agent WHERE agent_id = ?");
            checkAgent.setInt(1, id);
            if (checkAgent.executeQuery().next()) {
                System.out.println("❌ Agent ID already exists!");
                InputUtil.pressEnterToContinue();
                return;
            }

            String name = InputUtil.getStringInput("Enter Name");
            String phone = InputUtil.getPhone("Enter Phone (10 digits)");
            String email = InputUtil.getEmail("Enter Email");
            int exp = InputUtil.getPositiveInt("Enter Experience (years)");

            // Show agencies before asking for Agency ID
            showAgenciesForSelection();
            int agencyId = InputUtil.getPositiveInt("Enter Agency ID");

            // Check if agency exists
            PreparedStatement checkAgency = conn.prepareStatement("SELECT 1 FROM enterprise WHERE agency_id = ?");
            checkAgency.setInt(1, agencyId);
            if (!checkAgency.executeQuery().next()) {
                System.out.println("❌ Agency ID does not exist!");
                InputUtil.pressEnterToContinue();
                return;
            }

            // The schema requires a password column
            String password = InputUtil.getMaskedInput("Enter Password (or press Enter for default 'agent123'): ");
            if (password.isEmpty()) password = "agent123";

            System.out.println();
            if (!InputUtil.confirm("Are you sure you want to add " + name + " as a new agent?")) {
                System.out.println("⚠️ Agent addition cancelled.");
                InputUtil.pressEnterToContinue();
                return;
            }

            // Specify exact columns to fix the column count mismatch error
            String query = "INSERT INTO agent (agent_id, name, phone, email, experience_year, agency_id, password) VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setInt(5, exp);
            ps.setInt(6, agencyId);
            ps.setString(7, password);

            ps.executeUpdate();
            System.out.println("✅ Agent added successfully");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void viewAgent() {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps;

            if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement("SELECT * FROM agent WHERE agency_id=?");
                ps.setInt(1, Session.agencyId);
            } else {
                ps = conn.prepareStatement("SELECT * FROM agent");
            }

            ResultSet rs = ps.executeQuery();

            List<String> headers = Arrays.asList("ID", "Name", "Phone", "Email", "Experience", "Agency ID");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("agent_id")),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getInt("experience_year") + " yrs",
                        String.valueOf(rs.getInt("agency_id"))
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No agents found");
            } else {
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void findAgentById() {
        try {
            Connection conn = DBConnection.getConnection();

            showAgentsForSelection();
            int id = InputUtil.getPositiveInt("Enter Agent ID");

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM agent WHERE agent_id=?");
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n🔍 Agent Details:");
                System.out.println("ID: " + rs.getInt("agent_id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Phone: " + rs.getString("phone"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Experience: " + rs.getInt("experience_year") + " years");
                System.out.println("Agency ID: " + rs.getInt("agency_id"));
            } else {
                System.out.println("❌ Agent not found");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void updateAgent() {
        try {
            Connection conn = DBConnection.getConnection();

            showAgentsForSelection();
            int id = InputUtil.getPositiveInt("Enter Agent ID");

            while (true) {
                System.out.println("\n--- Update Menu ---");
                System.out.println("1. Update Name");
                System.out.println("2. Update Phone");
                System.out.println("3. Update Email");
                System.out.println("4. Update Experience");
                System.out.println("5. Exit");

                int choice = InputUtil.getPositiveInt("Enter choice");

                if (choice == 5) {
                    System.out.println("Exiting update...");
                    break;
                }

                String query = "";
                PreparedStatement ps = null;

                switch (choice) {
                    case 1:
                        String name = InputUtil.getStringInput("New Name");
                        query = "UPDATE agent SET name=? WHERE agent_id=?";
                        ps = conn.prepareStatement(query);
                        ps.setString(1, name);
                        ps.setInt(2, id);
                        break;

                    case 2:
                        String phone = InputUtil.getPhone("New Phone");
                        query = "UPDATE agent SET phone=? WHERE agent_id=?";
                        ps = conn.prepareStatement(query);
                        ps.setString(1, phone);
                        ps.setInt(2, id);
                        break;

                    case 3:
                        String email = InputUtil.getEmail("New Email");
                        query = "UPDATE agent SET email=? WHERE agent_id=?";
                        ps = conn.prepareStatement(query);
                        ps.setString(1, email);
                        ps.setInt(2, id);
                        break;

                    case 4:
                        int exp = InputUtil.getPositiveInt("New Experience");
                        query = "UPDATE agent SET experience_year=? WHERE agent_id=?";
                        ps = conn.prepareStatement(query);
                        ps.setInt(1, exp);
                        ps.setInt(2, id);
                        break;

                    default:
                        System.out.println("Invalid choice");
                        continue;
                }

                int rows = ps.executeUpdate();

                if (rows > 0)
                    System.out.println("✅ Updated successfully");
                else
                    System.out.println("❌ Agent not found");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void deleteAgent() {
        try {
            Connection conn = DBConnection.getConnection();

            showAgentsForSelection();
            int id = InputUtil.getPositiveInt("Enter Agent ID");

            PreparedStatement ps1 = conn.prepareStatement("SELECT 1 FROM property WHERE agent_id=?");
            ps1.setInt(1, id);

            if (ps1.executeQuery().next()) {
                System.out.println("❌ Cannot delete: Agent assigned to properties");
                InputUtil.pressEnterToContinue();
                return;
            }

            PreparedStatement ps2 = conn.prepareStatement("SELECT 1 FROM sales WHERE agent_id=?");
            ps2.setInt(1, id);

            if (ps2.executeQuery().next()) {
                System.out.println("❌ Cannot delete: Agent involved in sales");
                InputUtil.pressEnterToContinue();
                return;
            }

            PreparedStatement ps3 = conn.prepareStatement("SELECT 1 FROM rent WHERE agent_id=?");
            ps3.setInt(1, id);

            if (ps3.executeQuery().next()) {
                System.out.println("❌ Cannot delete: Agent involved in rent");
                InputUtil.pressEnterToContinue();
                return;
            }

            PreparedStatement ps = conn.prepareStatement("DELETE FROM agent WHERE agent_id=?");
            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("✅ Deleted successfully");
            else
                System.out.println("❌ Agent not found");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void searchAgentByName() {
        try {
            Connection conn = DBConnection.getConnection();

            String name = InputUtil.getStringInput("Enter name to search");

            PreparedStatement ps;

            if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement(
                        "SELECT * FROM agent WHERE name LIKE ? AND agency_id=?"
                );
                ps.setString(1, "%" + name + "%");
                ps.setInt(2, Session.agencyId);
            } else {
                ps = conn.prepareStatement(
                        "SELECT * FROM agent WHERE name LIKE ?"
                );
                ps.setString(1, "%" + name + "%");
            }            ps.setString(1, "%" + name + "%");

            ResultSet rs = ps.executeQuery();

            List<String> headers = Arrays.asList("ID", "Name", "Phone", "Email", "Experience");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("agent_id")),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getInt("experience_year") + " yrs"
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No agents found matching: " + name);
            } else {
                System.out.println("\n🔍 Search Results:");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void filterAgentsByExperience() {
        try {
            Connection conn = DBConnection.getConnection();

            int exp = InputUtil.getPositiveInt("Enter minimum experience (years)");

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM agent WHERE experience_year >= ? ORDER BY experience_year DESC");
            ps.setInt(1, exp);

            ResultSet rs = ps.executeQuery();

            List<String> headers = Arrays.asList("ID", "Name", "Phone", "Experience");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("agent_id")),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getInt("experience_year") + " yrs"
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No agents found with " + exp + "+ years experience");
            } else {
                System.out.println("\n📊 Agents with " + exp + "+ years experience:");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void filterAgentsByAgency() {
        try {
            Connection conn = DBConnection.getConnection();

            int agencyId;

            if ("AGENCY".equals(Session.role)) {
                agencyId = Session.agencyId;
            } else {
                showAgenciesForSelection(); // Show agencies first
                agencyId = InputUtil.getPositiveInt("Enter Agency ID");
            }

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM agent WHERE agency_id=?");
            ps.setInt(1, agencyId);

            ResultSet rs = ps.executeQuery();

            List<String> headers = Arrays.asList("ID", "Name", "Phone", "Email", "Experience");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("agent_id")),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getInt("experience_year") + " yrs"
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No agents found for Agency ID: " + agencyId);
            } else {
                System.out.println("\n🏢 Agents for Agency ID " + agencyId + ":");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void countAgents() {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps;

            if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement("SELECT COUNT(*) FROM agent WHERE agency_id=?");
                ps.setInt(1, Session.agencyId);
            } else {
                ps = conn.prepareStatement("SELECT COUNT(*) FROM agent");
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("📊 Total Agents: " + rs.getInt(1));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void sortAgentsByExperience() {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps;

            if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement(
                        "SELECT agent_id, name, experience_year FROM agent WHERE agency_id=? ORDER BY experience_year DESC"
                );
                ps.setInt(1, Session.agencyId);
            } else {
                ps = conn.prepareStatement(
                        "SELECT agent_id, name, experience_year FROM agent ORDER BY experience_year DESC"
                );
            }

            ResultSet rs = ps.executeQuery();
            List<String> headers = Arrays.asList("ID", "Name", "Experience");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("agent_id")),
                        rs.getString("name"),
                        rs.getInt("experience_year") + " yrs"
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No agents found");
            } else {
                System.out.println("\n📊 Agents sorted by Experience:");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void checkAgentExists() {
        try {
            Connection conn = DBConnection.getConnection();

            showAgentsForSelection();
            int id = InputUtil.getPositiveInt("Enter Agent ID");

            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM agent WHERE agent_id=?");
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
                System.out.println("✅ Agent exists");
            else
                System.out.println("❌ Agent does not exist");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void agentPerformance() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();

            PreparedStatement ps = conn.prepareStatement("""
                    SELECT
                        (SELECT COUNT(*) FROM sales WHERE agent_id=?) +
                        (SELECT COUNT(*) FROM rent WHERE agent_id=?) AS total_deals
                """);

            ps.setInt(1, agentId);
            ps.setInt(2, agentId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n📈 Agent Performance:");
                System.out.println("Total Deals Handled: " + rs.getInt("total_deals"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void agentSummary() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();

            // FIXED: s.sale_id changed to s.sales_id
            String query = """
                SELECT
                    a.name,
                    a.experience_year,
                    COUNT(DISTINCT p.property_id) as properties,
                    COUNT(DISTINCT s.sales_id) as sales,
                    COUNT(DISTINCT r.rent_id) as rentals
                FROM agent a
                LEFT JOIN property p ON a.agent_id = p.agent_id
                LEFT JOIN sales s ON a.agent_id = s.agent_id
                LEFT JOIN rent r ON a.agent_id = r.agent_id
                WHERE a.agent_id = ?
                GROUP BY a.agent_id, a.name, a.experience_year
                """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, agentId);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Agent not found");
                InputUtil.pressEnterToContinue();
                return;
            }

            System.out.println("\n📋 AGENT SUMMARY");
            System.out.println("================");
            System.out.println("Name: " + rs.getString("name"));
            System.out.println("Experience: " + rs.getInt("experience_year") + " years");
            System.out.println("Properties Managed: " + rs.getInt("properties"));
            System.out.println("Total Sales: " + rs.getInt("sales"));
            System.out.println("Total Rentals: " + rs.getInt("rentals"));

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void topAgent() {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps;

            if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement("""
                SELECT a.agent_id, a.name, COUNT(*) AS total
                FROM agent a
                JOIN (
                    SELECT agent_id FROM sales
                    UNION ALL
                    SELECT agent_id FROM rent
                ) AS deals ON a.agent_id = deals.agent_id
                WHERE a.agency_id = ?
                GROUP BY a.agent_id, a.name
                ORDER BY total DESC
                LIMIT 1
                """);
                ps.setInt(1, Session.agencyId);
            } else {
                ps = conn.prepareStatement("""
                SELECT a.agent_id, a.name, COUNT(*) AS total
                FROM agent a
                JOIN (
                    SELECT agent_id FROM sales
                    UNION ALL
                    SELECT agent_id FROM rent
                ) AS deals ON a.agent_id = deals.agent_id
                GROUP BY a.agent_id, a.name
                ORDER BY total DESC
                LIMIT 1
                """);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n🏆 TOP PERFORMING AGENT:");
                List<String> headers = Arrays.asList("Agent ID", "Name", "Total Deals");
                List<List<String>> rows = new ArrayList<>();
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("agent_id")),
                        rs.getString("name"),
                        String.valueOf(rs.getInt("total"))
                ));
                TableUtil.printTable(headers, rows);
            } else {
                System.out.println("❌ No data found");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void viewAgentProperties() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();

            PreparedStatement ps = conn.prepareStatement("""
                    SELECT p.property_id, p.city, p.locality, p.availability_status, pt.listing_type, pt.price
                    FROM property p
                    LEFT JOIN property_type pt ON p.property_id = pt.property_id
                    WHERE p.agent_id=?
                    """);

            ps.setInt(1, agentId);

            ResultSet rs = ps.executeQuery();

            List<String> headers = Arrays.asList("Property ID", "City", "Locality", "Status", "Type", "Price");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                String status = rs.getBoolean("availability_status") ? Color.GREEN + "Available" + Color.RESET : Color.RED + "Unavailable" + Color.RESET;
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("property_id")),
                        rs.getString("city"),
                        rs.getString("locality"),
                        status,
                        rs.getString("listing_type") == null ? "N/A" : rs.getString("listing_type"),
                        rs.getObject("price") == null ? "N/A" : "₹" + String.format("%,d", rs.getInt("price"))
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No properties found for this agent");
            } else {
                System.out.println("\n🏠 Properties Managed:");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void agentRevenue() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();

            // FIXED: changed 'price' to 'sales_price'
            PreparedStatement ps = conn.prepareStatement("SELECT SUM(sales_price) as total_revenue FROM sales WHERE agent_id=?");
            ps.setInt(1, agentId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long revenue = rs.getLong("total_revenue");

                System.out.println("\n💰 AGENT REVENUE");
                System.out.println("================");
                System.out.println("Total Revenue from Sales: ₹" + String.format("%,d", revenue));
            } else {
                System.out.println("❌ No revenue data found");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void agentActiveListings() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();

            PreparedStatement ps = conn.prepareStatement("""
                    SELECT p.property_id, p.city, p.locality, p.address, pt.listing_type, pt.price
                    FROM property p
                    LEFT JOIN property_type pt ON p.property_id = pt.property_id
                    WHERE p.agent_id=? AND p.availability_status = true
                    """);

            ps.setInt(1, agentId);

            ResultSet rs = ps.executeQuery();

            List<String> headers = Arrays.asList("Property ID", "City", "Locality", "Address", "Type", "Price");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("property_id")),
                        rs.getString("city"),
                        rs.getString("locality"),
                        rs.getString("address"),
                        rs.getString("listing_type"),
                        "₹" + String.format("%,d", rs.getInt("price"))
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No active listings found for this agent");
            } else {
                System.out.println("\n📋 Active Listings:");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void agentDealBreakdown() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();

            PreparedStatement ps = conn.prepareStatement("""
                    SELECT
                        (SELECT COUNT(*) FROM sales WHERE agent_id=?) AS sales_count,
                        (SELECT COUNT(*) FROM rent WHERE agent_id=?) AS rent_count
                """);

            ps.setInt(1, agentId);
            ps.setInt(2, agentId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int salesCount = rs.getInt("sales_count");
                int rentCount = rs.getInt("rent_count");

                List<String> headers = Arrays.asList("Deal Type", "Count");
                List<List<String>> rows = new ArrayList<>();

                rows.add(Arrays.asList("Sales", String.valueOf(salesCount)));
                rows.add(Arrays.asList("Rentals", String.valueOf(rentCount)));
                rows.add(Arrays.asList("TOTAL", String.valueOf(salesCount + rentCount)));

                System.out.println("\n📊 Deal Breakdown:");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void agentPortfolioValue() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();

            PreparedStatement ps = conn.prepareStatement("""
                    SELECT SUM(pt.price) as total_value
                    FROM property p
                    JOIN property_type pt ON p.property_id = pt.property_id
                    WHERE p.agent_id=?
                    """);

            ps.setInt(1, agentId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long totalValue = rs.getLong("total_value");

                System.out.println("\n💼 PORTFOLIO VALUE");
                System.out.println("==================");
                System.out.println("Total Property Value: ₹" + String.format("%,d", totalValue));
            } else {
                System.out.println("❌ No portfolio data found");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void agentSuccessRate() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();

            PreparedStatement ps = conn.prepareStatement("""
                    SELECT
                        COUNT(DISTINCT p.property_id) as properties_managed,
                        (SELECT COUNT(*) FROM sales WHERE agent_id=?) +
                        (SELECT COUNT(*) FROM rent WHERE agent_id=?) as total_deals
                    FROM property p
                    WHERE p.agent_id=?
                """);

            ps.setInt(1, agentId);
            ps.setInt(2, agentId);
            ps.setInt(3, agentId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int propertiesManaged = rs.getInt("properties_managed");
                int totalDeals = rs.getInt("total_deals");

                System.out.println("\n📈 SUCCESS RATE");
                System.out.println("===============");
                System.out.println("Properties Managed: " + propertiesManaged);
                System.out.println("Total Deals Closed: " + totalDeals);

                if (propertiesManaged > 0) {
                    double successRate = (double) totalDeals / propertiesManaged * 100;
                    System.out.printf("Success Rate: %.2f%%\n", successRate);
                } else {
                    System.out.println("Success Rate: N/A (No properties managed)");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void agentDealsHistory() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();

            // FIXED: sale_id -> sales_id, sale_date -> sales_date, price -> sales_price
            // FIXED: rent_date -> rent_start_date, monthly_rent -> rent_amount
            PreparedStatement ps = conn.prepareStatement("""
                    SELECT 'Sale' as deal_type, s.sales_id as deal_id, s.property_id,
                           s.buyer_id as customer_id, s.sales_date as deal_date, s.sales_price as amount
                    FROM sales s
                    WHERE s.agent_id = ?
                    UNION ALL
                    SELECT 'Rent' as deal_type, r.rent_id as deal_id, r.property_id,
                           r.tenant_id as customer_id, r.rent_start_date as deal_date, r.rent_amount as amount
                    FROM rent r
                    WHERE r.agent_id = ?
                    ORDER BY deal_date DESC
                """);

            ps.setInt(1, agentId);
            ps.setInt(2, agentId);

            ResultSet rs = ps.executeQuery();

            List<String> headers = Arrays.asList("Type", "Deal ID", "Property ID", "Customer ID", "Date", "Amount");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        rs.getString("deal_type"),
                        String.valueOf(rs.getInt("deal_id")),
                        String.valueOf(rs.getInt("property_id")),
                        String.valueOf(rs.getInt("customer_id")),
                        rs.getString("deal_date"),
                        "₹" + String.format("%,d", rs.getInt("amount"))
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No deal history found for this agent");
            } else {
                System.out.println("\n📜 Deals History:");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void agentWorkload() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();

            PreparedStatement ps = conn.prepareStatement("""
                    SELECT
                        COUNT(DISTINCT p.property_id) as properties_managed,
                        (SELECT COUNT(*) FROM sales WHERE agent_id=?) +
                        (SELECT COUNT(*) FROM rent WHERE agent_id=?) as total_deals
                    FROM property p
                    WHERE p.agent_id=?
                """);

            ps.setInt(1, agentId);
            ps.setInt(2, agentId);
            ps.setInt(3, agentId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                List<String> headers = Arrays.asList("Metric", "Count");
                List<List<String>> rows = new ArrayList<>();

                rows.add(Arrays.asList("Properties Managed", String.valueOf(rs.getInt("properties_managed"))));
                rows.add(Arrays.asList("Total Deals Handled", String.valueOf(rs.getInt("total_deals"))));

                System.out.println("\n💼 Agent Workload:");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void bottomAgentByRevenue() {
        try {
            Connection conn = DBConnection.getConnection();

            // FIXED: s.price -> s.sales_price
            PreparedStatement ps;

            if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement("""
        SELECT a.agent_id, a.name, COALESCE(SUM(s.sales_price), 0) as total_revenue
        FROM agent a
        LEFT JOIN sales s ON a.agent_id = s.agent_id
        WHERE a.agency_id = ?
        GROUP BY a.agent_id, a.name
        ORDER BY total_revenue ASC
        LIMIT 1
    """);
                ps.setInt(1, Session.agencyId);
            } else {
                ps = conn.prepareStatement("""
        SELECT a.agent_id, a.name, COALESCE(SUM(s.sales_price), 0) as total_revenue
        FROM agent a
        LEFT JOIN sales s ON a.agent_id = s.agent_id
        GROUP BY a.agent_id, a.name
        ORDER BY total_revenue ASC
        LIMIT 1
    """);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n📉 LOWEST EARNING AGENT:");
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Total Revenue: ₹" + String.format("%,d", rs.getLong("total_revenue")));
            } else {
                System.out.println("❌ No agent data found");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void agentsWithNoDeals() {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps;

            if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement("""
                SELECT a.agent_id, a.name, a.phone, a.experience_year
                FROM agent a
                LEFT JOIN sales s ON a.agent_id = s.agent_id
                LEFT JOIN rent r ON a.agent_id = r.agent_id
                WHERE s.sales_id IS NULL AND r.rent_id IS NULL
                AND a.agency_id = ?
            """);
                ps.setInt(1, Session.agencyId);
            } else {
                ps = conn.prepareStatement("""
                SELECT a.agent_id, a.name, a.phone, a.experience_year
                FROM agent a
                LEFT JOIN sales s ON a.agent_id = s.agent_id
                LEFT JOIN rent r ON a.agent_id = r.agent_id
                WHERE s.sales_id IS NULL AND r.rent_id IS NULL
            """);
            }

            ResultSet rs = ps.executeQuery();

            List<String> headers = Arrays.asList("ID", "Name", "Phone", "Experience");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("agent_id")),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getInt("experience_year") + " yrs"
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("✅ All agents have been involved in deals");
            } else {
                System.out.println("\n⚠️ Agents With No Deals:");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void agentsByCity() {
        try {
            Connection conn = DBConnection.getConnection();

            String city = InputUtil.getStringInput("Enter City");

            PreparedStatement ps;

            if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement("""
                    SELECT DISTINCT a.agent_id, a.name, a.phone, COUNT(p.property_id) as property_count
                    FROM agent a
                    JOIN property p ON a.agent_id = p.agent_id
                    WHERE p.city = ? AND a.agency_id = ?
                    GROUP BY a.agent_id, a.name, a.phone
                    """);
                ps.setString(1, city);
                ps.setInt(2, Session.agencyId);
            } else {
                ps = conn.prepareStatement("""
                    SELECT DISTINCT a.agent_id, a.name, a.phone, COUNT(p.property_id) as property_count
                    FROM agent a
                    JOIN property p ON a.agent_id = p.agent_id
                    WHERE p.city = ?
                    GROUP BY a.agent_id, a.name, a.phone
                    """);
                ps.setString(1, city);
            }

            ResultSet rs = ps.executeQuery();

            List<String> headers = Arrays.asList("ID", "Name", "Phone", "Properties in " + city);
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("agent_id")),
                        rs.getString("name"),
                        rs.getString("phone"),
                        String.valueOf(rs.getInt("property_count"))
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No agents found in " + city);
            } else {
                System.out.println("\n🏙️ Agents in " + city + ":");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void topAgentByRevenue() {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps;

            if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement("""
                SELECT a.agent_id, a.name, SUM(s.sales_price) as total_revenue
                FROM agent a
                JOIN sales s ON a.agent_id = s.agent_id
                WHERE a.agency_id = ?
                GROUP BY a.agent_id, a.name
                ORDER BY total_revenue DESC
                LIMIT 1
                """);
                ps.setInt(1, Session.agencyId);
            } else {
                ps = conn.prepareStatement("""
                SELECT a.agent_id, a.name, SUM(s.sales_price) as total_revenue
                FROM agent a
                JOIN sales s ON a.agent_id = s.agent_id
                GROUP BY a.agent_id, a.name
                ORDER BY total_revenue DESC
                LIMIT 1
                """);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n🏆 TOP AGENT BY REVENUE:");
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Total Revenue: ₹" + String.format("%,d", rs.getLong("total_revenue")));
            } else {
                System.out.println("❌ No sales data found");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void agentSalesCount() {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps;

            if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement("""
                SELECT a.agent_id, a.name, COUNT(s.sales_id) AS sales_count
                FROM agent a
                LEFT JOIN sales s ON a.agent_id = s.agent_id
                WHERE a.agency_id = ?
                GROUP BY a.agent_id, a.name
                ORDER BY sales_count DESC
            """);
                ps.setInt(1, Session.agencyId);
            } else {
                ps = conn.prepareStatement("""
                SELECT a.agent_id, a.name, COUNT(s.sales_id) AS sales_count
                FROM agent a
                LEFT JOIN sales s ON a.agent_id = s.agent_id
                GROUP BY a.agent_id, a.name
                ORDER BY sales_count DESC
            """);
            }

            ResultSet rs = ps.executeQuery();

            List<String> headers = Arrays.asList("ID", "Name", "Sales Count");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("agent_id")),
                        rs.getString("name"),
                        String.valueOf(rs.getInt("sales_count"))
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No agent data found");
            } else {
                System.out.println("\n📊 Agent Sales Count:");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void agentRevenueBreakdown() {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps;

            if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement("""
                SELECT a.agent_id, a.name, COALESCE(SUM(s.sales_price), 0) AS total_revenue
                FROM agent a
                LEFT JOIN sales s ON a.agent_id = s.agent_id
                WHERE a.agency_id = ?
                GROUP BY a.agent_id, a.name
                ORDER BY total_revenue DESC
            """);
                ps.setInt(1, Session.agencyId);
            } else {
                ps = conn.prepareStatement("""
                SELECT a.agent_id, a.name, COALESCE(SUM(s.sales_price), 0) AS total_revenue
                FROM agent a
                LEFT JOIN sales s ON a.agent_id = s.agent_id
                GROUP BY a.agent_id, a.name
                ORDER BY total_revenue DESC
            """);
            }

            ResultSet rs = ps.executeQuery();

            List<String> headers = Arrays.asList("ID", "Name", "Total Revenue");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("agent_id")),
                        rs.getString("name"),
                        "₹" + String.format("%,d", rs.getLong("total_revenue"))
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No agent data found");
            } else {
                System.out.println("\n💰 Agent Revenue Breakdown:");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void agentsWithNoSales() {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps;

            if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement("""
                SELECT a.agent_id, a.name, a.phone, a.experience_year
                FROM agent a
                LEFT JOIN sales s ON a.agent_id = s.agent_id
                WHERE s.sales_id IS NULL AND a.agency_id = ?
            """);
                ps.setInt(1, Session.agencyId);
            } else {
                ps = conn.prepareStatement("""
                SELECT a.agent_id, a.name, a.phone, a.experience_year
                FROM agent a
                LEFT JOIN sales s ON a.agent_id = s.agent_id
                WHERE s.sales_id IS NULL
            """);
            }

            ResultSet rs = ps.executeQuery();

            List<String> headers = Arrays.asList("ID", "Name", "Phone", "Experience");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("agent_id")),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getInt("experience_year") + " yrs"
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("✅ All agents have made at least one sale");
            } else {
                System.out.println("\n⚠️ Agents With No Sales:");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void makePropertyAvailable() {

    }


      public static void assignRole() {

     }
}