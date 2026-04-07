import java.sql.*;
import java.util.*;

public class AgentService {

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

            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM agent WHERE agent_id=?");

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

    public static void countAgency() {
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
}