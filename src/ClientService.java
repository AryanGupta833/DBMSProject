import java.sql.*;
import java.util.*;

public class ClientService {

    public static void addClient() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Client ID");
            String name = InputUtil.getStringInput("Enter Name");
            String phone = InputUtil.getPhone("Enter Phone (10 digits)");
            String email = InputUtil.getEmail("Enter Email");
            String address = InputUtil.getStringInput("Enter Address");

            String query = "INSERT INTO client VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setString(5, address);

            ps.executeUpdate();
            System.out.println("✅ Client added successfully");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void viewClient() {
        try {
            Connection conn = DBConnection.getConnection();

            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM client");

            List<String> headers = Arrays.asList(
                    "ID", "Name", "Phone", "Email", "Address"
            );

            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("client_id")),
                        rs.getString("client_name"),
                        rs.getString("client_phone"),
                        rs.getString("client_email"),
                        rs.getString("client_address")
                ));
            }

            TableUtil.printTable(headers, rows);

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void assignRole() {
        try {
            Connection conn = DBConnection.getConnection();

            int clientId = InputUtil.getPositiveInt("Enter Client ID");

            String role;
            while (true) {
                role = InputUtil.getStringInput("Enter Role (Buyer/Seller/Tenant)");

                if (role.equalsIgnoreCase("Buyer") ||
                        role.equalsIgnoreCase("Seller") ||
                        role.equalsIgnoreCase("Tenant")) {

                    role = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
                    break;
                } else {
                    System.out.println("❌ Invalid role! Only Buyer, Seller, Tenant allowed.");
                }
            }

            PreparedStatement check = conn.prepareStatement(
                    "SELECT 1 FROM client_role WHERE client_id=? AND role=?"
            );
            check.setInt(1, clientId);
            check.setString(2, role);

            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                System.out.println("⚠️ Role already exists for this client");
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO client_role VALUES (?, ?)"
            );

            ps.setInt(1, clientId);
            ps.setString(2, role);

            ps.executeUpdate();

            System.out.println("✅ Role assigned successfully");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void findClientById() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Client ID");

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM client WHERE client_id=?");
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\nClient Details:");
                System.out.println("Name: " + rs.getString("client_name"));
                System.out.println("Phone: " + rs.getString("client_phone"));
                System.out.println("Email: " + rs.getString("client_email"));
                System.out.println("Address: " + rs.getString("client_address"));
            } else {
                System.out.println("❌ Client not found");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void updateClient() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Client ID");

            while (true) {
                System.out.println("\n--- Update Client Menu ---");
                System.out.println("1. Update Name");
                System.out.println("2. Update Phone");
                System.out.println("3. Update Email");
                System.out.println("4. Update Address");
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
                        query = "UPDATE client SET client_name=? WHERE client_id=?";
                        ps = conn.prepareStatement(query);
                        ps.setString(1, name);
                        ps.setInt(2, id);
                        break;

                    case 2:
                        String phone = InputUtil.getPhone("New Phone");
                        query = "UPDATE client SET client_phone=? WHERE client_id=?";
                        ps = conn.prepareStatement(query);
                        ps.setString(1, phone);
                        ps.setInt(2, id);
                        break;

                    case 3:
                        String email = InputUtil.getEmail("New Email");
                        query = "UPDATE client SET client_email=? WHERE client_id=?";
                        ps = conn.prepareStatement(query);
                        ps.setString(1, email);
                        ps.setInt(2, id);
                        break;

                    case 4:
                        String address = InputUtil.getStringInput("New Address");
                        query = "UPDATE client SET client_address=? WHERE client_id=?";
                        ps = conn.prepareStatement(query);
                        ps.setString(1, address);
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
                    System.out.println("❌ Client not found");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void deleteClient() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Client ID");

            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM client WHERE client_id=?");

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("✅ Deleted successfully");
            else
                System.out.println("❌ Client not found");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void searchClientByName() {
        try {
            Connection conn = DBConnection.getConnection();

            String name = InputUtil.getStringInput("Enter Name to search");

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM client WHERE client_name LIKE ?");
            ps.setString(1, "%" + name + "%");

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\nClient Details:");
                System.out.println("Name: " + rs.getString("client_name"));
                System.out.println("Phone: " + rs.getString("client_phone"));
                System.out.println("Email: " + rs.getString("client_email"));
                System.out.println("Address: " + rs.getString("client_address"));
            } else {
                System.out.println("❌ Client not found");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void countClients() {
        try {
            Connection conn = DBConnection.getConnection();

            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT COUNT(*) FROM client");

            if (rs.next()) {
                System.out.println("Total Clients: " + rs.getInt(1));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void sortClientsByName() {
        try {
            Connection conn = DBConnection.getConnection();

            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT * FROM client ORDER BY client_name");

            while (rs.next()) {
                System.out.println(rs.getString("client_name"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void sortClientsById() {
        try {
            Connection conn = DBConnection.getConnection();

            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT * FROM client ORDER BY client_id");

            while (rs.next()) {
                System.out.println(rs.getString("client_name"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void filterClientsByRole() {
        try {
            Connection conn = DBConnection.getConnection();

            String role;

            while (true) {
                role = InputUtil.getStringInput("Enter Role (Buyer / Seller / Tenant)");

                if (role.equalsIgnoreCase("Buyer") ||
                        role.equalsIgnoreCase("Seller") ||
                        role.equalsIgnoreCase("Tenant")) {
                    role = role.substring(0,1).toUpperCase() + role.substring(1).toLowerCase();
                    break;
                } else {
                    System.out.println("❌ Invalid role. Allowed: Buyer, Seller, Tenant");
                }
            }

            PreparedStatement ps = conn.prepareStatement("""
                SELECT c.client_id, c.client_name, c.client_phone,
                       c.client_email, c.client_address
                FROM client c
                JOIN client_role cr ON c.client_id = cr.client_id
                WHERE cr.role = ?
                """);

            ps.setString(1, role);

            ResultSet rs = ps.executeQuery();

            List<String> headers = Arrays.asList(
                    "ID", "Name", "Phone", "Email", "Address"
            );

            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("client_id")),
                        rs.getString("client_name"),
                        rs.getString("client_phone"),
                        rs.getString("client_email"),
                        rs.getString("client_address")
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No clients found for role: " + role);
            } else {
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void removeClientRole() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Client ID");

            PreparedStatement fetch = conn.prepareStatement(
                    "SELECT role FROM client_role WHERE client_id=?"
            );
            fetch.setInt(1, id);

            ResultSet rs = fetch.executeQuery();

            List<String> roles = new ArrayList<>();

            while (rs.next()) {
                roles.add(rs.getString("role"));
            }

            if (roles.isEmpty()) {
                System.out.println("❌ No roles found for this client");
                return;
            }

            System.out.println("Roles for this client:");
            for (String r : roles) {
                System.out.println("- " + r);
            }

            String role;
            while (true) {
                role = InputUtil.getStringInput("Enter role to delete");

                if (role.equalsIgnoreCase("Buyer") ||
                        role.equalsIgnoreCase("Seller") ||
                        role.equalsIgnoreCase("Tenant")) {

                    role = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();

                    if (roles.contains(role)) break;

                    System.out.println("❌ Client does not have this role");
                } else {
                    System.out.println("❌ Invalid role! Only Buyer/Seller/Tenant allowed.");
                }
            }

            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM client_role WHERE client_id=? AND role=?"
            );

            ps.setInt(1, id);
            ps.setString(2, role);

            ps.executeUpdate();

            System.out.println("✅ Role removed successfully");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void updateClientRole() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Client ID");
            String role;
            while (true) {
                role = InputUtil.getStringInput("Enter Role (Buyer/Seller/Tenant)");

                if (role.equalsIgnoreCase("Buyer") ||
                        role.equalsIgnoreCase("Seller") ||
                        role.equalsIgnoreCase("Tenant")) {

                    role = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
                    break;
                } else {
                    System.out.println("❌ Invalid role! Only Buyer, Seller, Tenant allowed.");
                }
            }

            PreparedStatement check = conn.prepareStatement(
                    "SELECT 1 FROM client_role WHERE client_id=? AND role=?"
            );
            check.setInt(1, id);
            check.setString(2, role);

            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                System.out.println("⚠️ Role already exists for this client");
            } else {
                PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO client_role VALUES (?, ?)"
                );

                insert.setInt(1, id);
                insert.setString(2, role);

                insert.executeUpdate();

                System.out.println("✅ Role added successfully");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void checkClientExists() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Client ID");

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM client WHERE client_id=?");

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
                System.out.println("✅ Client exists");
            else
                System.out.println("❌ Client does not exist");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}