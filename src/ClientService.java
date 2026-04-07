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

            String name = InputUtil.getStringInput("New Name");
            String phone = InputUtil.getPhone("New Phone");
            String email = InputUtil.getEmail("New Email");
            String address = InputUtil.getStringInput("New Address");

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE client SET client_name=?, client_phone=?, client_email=?, client_address=? WHERE client_id=?"
            );

            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, email);
            ps.setString(4, address);
            ps.setInt(5, id);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("✅ Updated successfully");
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

            String role = InputUtil.getStringInput("Enter Role");

            PreparedStatement ps = conn.prepareStatement("""
                    SELECT c.client_name
                    FROM client c
                    JOIN client_role cr ON c.client_id = cr.client_id
                    WHERE cr.role = ?
                    """);

            ps.setString(1, role);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("client_name"));
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
    public static void viewClientWithRoles() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
            SELECT c.client_id, c.client_name, cr.role
            FROM client c
            LEFT JOIN client_role cr ON c.client_id = cr.client_id
            ORDER BY c.client_id
        """;

            ResultSet rs = conn.createStatement().executeQuery(query);

            Map<Integer, List<String>> map = new LinkedHashMap<>();

            while (rs.next()) {
                int id = rs.getInt("client_id");
                String name = rs.getString("client_name");
                String role = rs.getString("role");

                map.putIfAbsent(id, new ArrayList<>());
                if (role != null) map.get(id).add(role);

                map.get(id).add(0, name); // store name at index 0
            }

            for (var entry : map.entrySet()) {
                List<String> data = entry.getValue();
                String name = data.get(0);
                List<String> roles = data.subList(1, data.size());

                System.out.println(name + " → " +
                        (roles.isEmpty() ? "No Role" : String.join(", ", roles)));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    public static void viewClientTransactions() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Client ID");

            System.out.println("\n--- PURCHASES ---");

            PreparedStatement ps1 = conn.prepareStatement(
                    "SELECT property_id, sales_price, sales_date FROM sales WHERE buyer_id=?"
            );
            ps1.setInt(1, id);

            ResultSet rs1 = ps1.executeQuery();

            boolean found = false;

            while (rs1.next()) {
                found = true;
                System.out.println(
                        "Property: " + rs1.getInt("property_id") +
                                ", Price: " + rs1.getInt("sales_price") +
                                ", Date: " + rs1.getString("sales_date")
                );
            }

            if (!found) System.out.println("No purchases found");

            System.out.println("\n--- RENTALS ---");

            PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT property_id, rent_amount, rent_start_date FROM rent WHERE tenant_id=?"
            );
            ps2.setInt(1, id);

            ResultSet rs2 = ps2.executeQuery();

            found = false;

            while (rs2.next()) {
                found = true;
                System.out.println(
                        "Property: " + rs2.getInt("property_id") +
                                ", Rent: " + rs2.getInt("rent_amount") +
                                ", Start: " + rs2.getString("rent_start_date")
                );
            }

            if (!found) System.out.println("No rentals found");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    public static void deleteClient() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Client ID");

            // check sales
            PreparedStatement ps1 = conn.prepareStatement(
                    "SELECT 1 FROM sales WHERE buyer_id=? OR seller_id=?"
            );
            ps1.setInt(1, id);
            ps1.setInt(2, id);

            if (ps1.executeQuery().next()) {
                System.out.println("❌ Cannot delete: Client involved in sales");
                return;
            }

            // check rent
            PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT 1 FROM rent WHERE tenant_id=?"
            );
            ps2.setInt(1, id);

            if (ps2.executeQuery().next()) {
                System.out.println("❌ Cannot delete: Client involved in rent");
                return;
            }

            // delete
            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM client WHERE client_id=?"
            );
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
    public static void clientSummary() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Client ID");

            // name
            PreparedStatement ps1 = conn.prepareStatement(
                    "SELECT client_name FROM client WHERE client_id=?"
            );
            ps1.setInt(1, id);

            ResultSet rs1 = ps1.executeQuery();

            if (!rs1.next()) {
                System.out.println("❌ Client not found");
                return;
            }

            String name = rs1.getString("client_name");

            // roles
            PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT role FROM client_role WHERE client_id=?"
            );
            ps2.setInt(1, id);

            ResultSet rs2 = ps2.executeQuery();
            List<String> roles = new ArrayList<>();

            while (rs2.next()) {
                roles.add(rs2.getString("role"));
            }

            // purchases
            PreparedStatement ps3 = conn.prepareStatement(
                    "SELECT COUNT(*) FROM sales WHERE buyer_id=?"
            );
            ps3.setInt(1, id);

            ResultSet rs3 = ps3.executeQuery();
            rs3.next();
            int purchases = rs3.getInt(1);

            // rentals
            PreparedStatement ps4 = conn.prepareStatement(
                    "SELECT COUNT(*) FROM rent WHERE tenant_id=?"
            );
            ps4.setInt(1, id);

            ResultSet rs4 = ps4.executeQuery();
            rs4.next();
            int rentals = rs4.getInt(1);

            System.out.println("\n--- CLIENT SUMMARY ---");
            System.out.println("Name: " + name);
            System.out.println("Roles: " + (roles.isEmpty() ? "None" : String.join(", ", roles)));
            System.out.println("Total Purchases: " + purchases);
            System.out.println("Total Rentals: " + rentals);

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}