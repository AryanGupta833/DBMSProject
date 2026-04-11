import java.sql.*;
import java.util.*;

public class AgentService {
    private static int resolveAgentId() {
        if ("AGENT".equals(Session.role)) {
            return Session.userId; // 🔥 auto
        } else {
            return InputUtil.getPositiveInt("Enter Agent ID"); // admin/office
        }
    }

    public static void addAgent() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Agent ID");
            String name = InputUtil.getStringInput("Enter Name");
            String phone = InputUtil.getPhone("Enter Phone (10 digits)");
            String email = InputUtil.getEmail("Enter Email");
            int exp = InputUtil.getPositiveInt("Enter Experience (years)");
            int agencyId = InputUtil.getPositiveInt("Enter Agency ID");

            String query = "INSERT INTO agent VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setInt(5, exp);
            ps.setInt(6, agencyId);

            ps.executeUpdate();
            System.out.println("✅ Agent added successfully");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void viewAgent() {
        try {
            Connection conn = DBConnection.getConnection();

            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM agent");

            List<String> headers = Arrays.asList(
                    "ID", "Name", "Phone", "Email", "Experience", "Agency ID"
            );

            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("agent_id")),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        String.valueOf(rs.getInt("experience_year")),
                        String.valueOf(rs.getInt("agency_id"))
                ));
            }

            TableUtil.printTable(headers, rows);

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void findAgentById() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Agent ID");

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM agent WHERE agent_id=?");
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\nAgent Details:");
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Phone: " + rs.getString("phone"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Experience: " + rs.getInt("experience_year"));
            } else {
                System.out.println("❌ Agent not found");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void updateAgent() {
        try {
            Connection conn = DBConnection.getConnection();

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
    }

    public static void deleteAgent() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Agent ID");

            PreparedStatement ps1 = conn.prepareStatement(
                    "SELECT 1 FROM property WHERE agent_id=?"
            );
            ps1.setInt(1, id);

            if (ps1.executeQuery().next()) {
                System.out.println("❌ Cannot delete: Agent assigned to properties");
                return;
            }

            PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT 1 FROM sales WHERE agent_id=?"
            );
            ps2.setInt(1, id);

            if (ps2.executeQuery().next()) {
                System.out.println("❌ Cannot delete: Agent involved in sales");
                return;
            }

            PreparedStatement ps3 = conn.prepareStatement(
                    "SELECT 1 FROM rent WHERE agent_id=?"
            );
            ps3.setInt(1, id);

            if (ps3.executeQuery().next()) {
                System.out.println("❌ Cannot delete: Agent involved in rent");
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM agent WHERE agent_id=?"
            );
            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("✅ Deleted successfully");
            else
                System.out.println("❌ Agent not found");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void searchAgentByName() {
        try {
            Connection conn = DBConnection.getConnection();

            String name = InputUtil.getStringInput("Enter name to search");

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM agent WHERE name LIKE ?");
            ps.setString(1, "%" + name + "%");

            ResultSet rs = ps.executeQuery();


            if (rs.next()) {
                System.out.println("\nAgent Details:");
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Phone: " + rs.getString("phone"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Experience: " + rs.getInt("experience_year"));
            } else {
                System.out.println("❌ Agent not found");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void filterAgentsByExperience() {
        try {
            Connection conn = DBConnection.getConnection();

            int exp = InputUtil.getPositiveInt("Enter minimum experience");

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM agent WHERE experience_year >= ?");
            ps.setInt(1, exp);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                        rs.getString("name") + " (" +
                                rs.getInt("experience_year") + " years)"
                );
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void filterAgentsByAgency() {
        try {
            Connection conn = DBConnection.getConnection();

            int agencyId = InputUtil.getPositiveInt("Enter Agency ID");

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM agent WHERE agency_id=?");
            ps.setInt(1, agencyId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void countAgents() {
        try {
            Connection conn = DBConnection.getConnection();

            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT COUNT(*) FROM agent");

            if (rs.next()) {
                System.out.println("Total Agents: " + rs.getInt(1));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void sortAgentsByExperience() {
        try {
            Connection conn = DBConnection.getConnection();

            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT * FROM agent ORDER BY experience_year DESC");

            while (rs.next()) {
                System.out.println(
                        rs.getString("name") + " - " +
                                rs.getInt("experience_year") + " years"
                );
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void checkAgentExists() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Agent ID");

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM agent WHERE agent_id=?");
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
                System.out.println("✅ Agent exists");
            else
                System.out.println("❌ Agent does not exist");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
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
                System.out.println("Total Deals Handled: " + rs.getInt("total_deals"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void agentSummary() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();


            // basic info
            PreparedStatement ps1 = conn.prepareStatement(
                    "SELECT name, experience_year FROM agent WHERE agent_id=?"
            );
            ps1.setInt(1, agentId);

            ResultSet rs1 = ps1.executeQuery();

            if (!rs1.next()) {
                System.out.println("❌ Agent not found");
                return;
            }

            String name = rs1.getString("name");
            int exp = rs1.getInt("experience_year");

            // properties managed
            PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT COUNT(*) FROM property WHERE agent_id=?"
            );
            ps2.setInt(1, agentId);
            ResultSet rs2 = ps2.executeQuery();
            rs2.next();
            int properties = rs2.getInt(1);

            // sales
            PreparedStatement ps3 = conn.prepareStatement(
                    "SELECT COUNT(*) FROM sales WHERE agent_id=?"
            );
            ps3.setInt(1, agentId);
            ResultSet rs3 = ps3.executeQuery();
            rs3.next();
            int sales = rs3.getInt(1);

            // rentals
            PreparedStatement ps4 = conn.prepareStatement(
                    "SELECT COUNT(*) FROM rent WHERE agent_id=?"
            );
            ps4.setInt(1, agentId);
            ResultSet rs4 = ps4.executeQuery();
            rs4.next();
            int rentals = rs4.getInt(1);

            System.out.println("\n--- AGENT SUMMARY ---");
            System.out.println("Name: " + name);
            System.out.println("Experience: " + exp + " years");
            System.out.println("Properties Managed: " + properties);
            System.out.println("Total Sales: " + sales);
            System.out.println("Total Rentals: " + rentals);

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void topAgent() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
            SELECT agent_id, COUNT(*) AS total
            FROM (
                SELECT agent_id FROM sales
                UNION ALL
                SELECT agent_id FROM rent
            ) AS deals
            GROUP BY agent_id
            ORDER BY total DESC
            LIMIT 1
        """;

            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next()) {
                int agentId = rs.getInt("agent_id");
                int total = rs.getInt("total");

                PreparedStatement ps = conn.prepareStatement(
                        "SELECT name FROM agent WHERE agent_id=?"
                );
                ps.setInt(1, agentId);

                ResultSet rs2 = ps.executeQuery();

                if (rs2.next()) {
                    System.out.println("🏆 Top Agent: " +
                            rs2.getString("name") +
                            " (" + total + " deals)");
                }
            } else {
                System.out.println("❌ No data found");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    public static void viewAgentProperties() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();


            PreparedStatement ps = conn.prepareStatement("""
            SELECT p.property_id, p.city, pt.listing_type, pt.price
            FROM property p
            LEFT JOIN property_type pt ON p.property_id = pt.property_id
            WHERE p.agent_id=?
        """);

            ps.setInt(1, agentId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                        "Property " + rs.getInt("property_id") +
                                " | " + rs.getString("city") +
                                " | " + rs.getString("listing_type") +
                                " | ₹" + rs.getInt("price")
                );
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void agentRevenue() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();


            System.out.println("\n[Query] Agent Revenue");
            System.out.println("Calculating total revenue for Agent ID: " + agentId);

            // TODO: SQL implementation

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void agentActiveListings() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();


            System.out.println("\n[Query] Active Listings");
            System.out.println("Fetching properties managed by Agent ID: " + agentId);

            // TODO: SQL implementation

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void agentDealBreakdown() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();


            System.out.println("\n[Query] Deal Breakdown");
            System.out.println("Analyzing sales and rentals for Agent ID: " + agentId);

            // TODO: SQL implementation

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void agentPortfolioValue() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();


            System.out.println("\n[Query] Portfolio Value");
            System.out.println("Calculating total property value for Agent ID: " + agentId);

            // TODO: SQL implementation

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void bottomAgentByRevenue() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("\n[Query] Bottom Agent by Revenue");
            System.out.println("Finding lowest earning agent...");

            // TODO: SQL implementation

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void agentsWithNoDeals() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("\n[Query] Agents With No Deals");
            System.out.println("Listing agents with no transactions...");

            // TODO: SQL implementation

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }


    public static void agentSuccessRate() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();


            System.out.println("\n[Query] Agent Success Rate");
            System.out.println("Calculating success rate for Agent ID: " + agentId);

            // TODO: SQL implementation

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }


    public static void agentDealsHistory() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();


            System.out.println("\n[Query] Agent Deals History");
            System.out.println("Fetching all sales and rentals handled by Agent ID: " + agentId);

            // TODO: SQL (sales + rent join / union)

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }


    public static void agentsByCity() {
        try {
            Connection conn = DBConnection.getConnection();

            String city = InputUtil.getStringInput("Enter City");

            System.out.println("\n[Query] Agents in City");
            System.out.println("Finding agents handling properties in: " + city);

            // TODO: SQL (JOIN property)

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void agentWorkload() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId = resolveAgentId();


            System.out.println("\n[Query] Agent Workload");
            System.out.println("Analyzing workload for Agent ID: " + agentId);

            // TODO: SQL (properties + deals count)

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }


    public static void topAgentByRevenue() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("\n[Query] Top Agent by Revenue");
            System.out.println("Fetching agent with highest total sales revenue...");

            // TODO: SQL (JOIN + SUM + GROUP BY)

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void agentSalesCount() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("\n[Query] Agent Sales Count");
            System.out.println("Counting total sales handled by each agent...");

            // TODO: SQL

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void agentRevenueBreakdown() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("\n[Query] Agent Revenue Breakdown");
            System.out.println("Fetching revenue generated by each agent...");

            // TODO: SQL

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }


    public static void agentsWithNoSales() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("\n[Query] Agents With No Sales");
            System.out.println("Fetching agents who have not made any sales...");

            // TODO: SQL (LEFT JOIN / NOT EXISTS)

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }








}