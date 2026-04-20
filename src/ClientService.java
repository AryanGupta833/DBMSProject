import java.sql.*;
import java.util.*;

public class ClientService {

    // Helper method to show clients before asking for ID input
    static void showClientsForSelection() throws Exception {
        Connection conn = DBConnection.getConnection();
        ResultSet rs = conn.createStatement().executeQuery("SELECT client_id, client_name, client_phone FROM client");

        List<String> headers = Arrays.asList("ID", "Name", "Phone");
        List<List<String>> rows = new ArrayList<>();

        while (rs.next()) {
            rows.add(Arrays.asList(
                    String.valueOf(rs.getInt("client_id")),
                    rs.getString("client_name"),
                    rs.getString("client_phone")
            ));
        }

        if (!rows.isEmpty()) {
            System.out.println("\n📋 Available Clients:");
            TableUtil.printTable(headers, rows);
        } else {
            System.out.println("❌ No clients available.");
        }
    }

    public static void addClient() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Client ID");

            // --- CHECK 1: Ensure Client ID doesn't already exist ---
            PreparedStatement checkPs = conn.prepareStatement("SELECT 1 FROM client WHERE client_id = ?");
            checkPs.setInt(1, id);
            if (checkPs.executeQuery().next()) {
                System.out.println("❌ A client with ID " + id + " already exists!");
                InputUtil.pressEnterToContinue();
                return; // Stop execution here to save the user from filling out the rest
            }

            String name = InputUtil.getStringInput("Enter Name");
            String phone = InputUtil.getPhone("Enter Phone (10 digits)");
            String email = InputUtil.getEmail("Enter Email");
            String address = InputUtil.getStringInput("Enter Address");

            // --- CHECK 2: Explicit confirmation before modifying the database ---
            System.out.println();
            if (!InputUtil.confirm("Are you sure you want to add " + name + " as a new client?")) {
                System.out.println("⚠️ Client addition cancelled.");
                InputUtil.pressEnterToContinue();
                return;
            }

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
        InputUtil.pressEnterToContinue();
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

            if (rows.isEmpty()) {
                System.out.println("❌ No clients found");
            } else {
                System.out.println("\n📋 All Clients:");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void assignRole() {
        try {
            showClientsForSelection();
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
                InputUtil.pressEnterToContinue();
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
        InputUtil.pressEnterToContinue();
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
                System.out.println("\n🔍 Client Details:");
                System.out.println("ID: " + rs.getInt("client_id"));
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
        InputUtil.pressEnterToContinue();
    }

    public static void updateClient() {
        try {
            showClientsForSelection();
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
        InputUtil.pressEnterToContinue();
    }

    public static void searchClientByName() {
        try {
            Connection conn = DBConnection.getConnection();

            String name = InputUtil.getStringInput("Enter Name to search");

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM client WHERE client_name LIKE ?");
            ps.setString(1, "%" + name + "%");

            ResultSet rs = ps.executeQuery();

            List<String> headers = Arrays.asList("ID", "Name", "Phone", "Email", "Address");
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
                System.out.println("❌ Client not found matching: " + name);
            } else {
                System.out.println("\n🔍 Search Results:");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void countClients() {
        try {
            Connection conn = DBConnection.getConnection();

            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT COUNT(*) FROM client");

            if (rs.next()) {
                System.out.println("📊 Total Clients: " + rs.getInt(1));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void sortClientsByName() {
        try {
            Connection conn = DBConnection.getConnection();

            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT client_id, client_name, client_phone FROM client ORDER BY client_name");

            List<String> headers = Arrays.asList("ID", "Name", "Phone");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("client_id")),
                        rs.getString("client_name"),
                        rs.getString("client_phone")
                ));
            }

            System.out.println("\n📊 Clients Sorted By Name:");
            TableUtil.printTable(headers, rows);

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void sortClientsById() {
        try {
            Connection conn = DBConnection.getConnection();

            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT client_id, client_name, client_phone FROM client ORDER BY client_id");

            List<String> headers = Arrays.asList("ID", "Name", "Phone");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("client_id")),
                        rs.getString("client_name"),
                        rs.getString("client_phone")
                ));
            }

            System.out.println("\n📊 Clients Sorted By ID:");
            TableUtil.printTable(headers, rows);

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
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
                System.out.println("\n📋 Clients filtered by role (" + role + "):");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void removeClientRole() {
        try {
            showClientsForSelection();
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
                InputUtil.pressEnterToContinue();
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
        InputUtil.pressEnterToContinue();
    }

    public static void updateClientRole() {
        try {
            showClientsForSelection();
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Client ID");
            String role;
            while (true) {
                role = InputUtil.getStringInput("Enter Role to Add (Buyer/Seller/Tenant)");

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
        InputUtil.pressEnterToContinue();
    }

    public static void checkClientExists() {
        try {
            showClientsForSelection();
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
        InputUtil.pressEnterToContinue();
    }

    public static void viewClientWithRoles() {
        try {
            Connection conn = DBConnection.getConnection();

            // BUG FIX: use GROUP_CONCAT so each client appears exactly once
            // with all their roles in a single column — no manual map juggling.
            String query = """
                SELECT c.client_id,
                       c.client_name,
                       COALESCE(GROUP_CONCAT(cr.role ORDER BY cr.role SEPARATOR ', '), 'No Role') AS roles
                FROM client c
                LEFT JOIN client_role cr ON c.client_id = cr.client_id
                GROUP BY c.client_id, c.client_name
                ORDER BY c.client_id
            """;

            ResultSet rs = conn.createStatement().executeQuery(query);

            List<String> headers = Arrays.asList("Client ID", "Name", "Assigned Roles");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("client_id")),
                        rs.getString("client_name"),
                        rs.getString("roles")
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No clients found.");
            } else {
                System.out.println("\n\uD83D\uDCCB Clients With Roles:");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void viewClientTransactions() {
        try {
            showClientsForSelection();
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Client ID");

            System.out.println("\n--- PURCHASES ---");

            PreparedStatement ps1 = conn.prepareStatement(
                    "SELECT property_id, sales_price, sales_date FROM sales WHERE buyer_id=?"
            );
            ps1.setInt(1, id);

            ResultSet rs1 = ps1.executeQuery();
            List<String> purchaseHeaders = Arrays.asList("Property ID", "Price", "Date");
            List<List<String>> purchaseRows = new ArrayList<>();

            while (rs1.next()) {
                purchaseRows.add(Arrays.asList(
                        String.valueOf(rs1.getInt("property_id")),
                        "₹" + String.format("%,d", rs1.getInt("sales_price")),
                        rs1.getString("sales_date")
                ));
            }

            if (purchaseRows.isEmpty()) {
                System.out.println("No purchases found");
            } else {
                TableUtil.printTable(purchaseHeaders, purchaseRows);
            }

            System.out.println("\n--- RENTALS ---");

            PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT property_id, rent_amount, rent_start_date FROM rent WHERE tenant_id=?"
            );
            ps2.setInt(1, id);

            ResultSet rs2 = ps2.executeQuery();
            List<String> rentHeaders = Arrays.asList("Property ID", "Monthly Rent", "Start Date");
            List<List<String>> rentRows = new ArrayList<>();

            while (rs2.next()) {
                rentRows.add(Arrays.asList(
                        String.valueOf(rs2.getInt("property_id")),
                        "₹" + String.format("%,d", rs2.getInt("rent_amount")),
                        rs2.getString("rent_start_date")
                ));
            }

            if (rentRows.isEmpty()) {
                System.out.println("No rentals found");
            } else {
                TableUtil.printTable(rentHeaders, rentRows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void deleteClient() {
        try {
            showClientsForSelection();
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
                InputUtil.pressEnterToContinue();
                return;
            }

            // check rent
            PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT 1 FROM rent WHERE tenant_id=?"
            );
            ps2.setInt(1, id);

            if (ps2.executeQuery().next()) {
                System.out.println("❌ Cannot delete: Client involved in rent");
                InputUtil.pressEnterToContinue();
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
        InputUtil.pressEnterToContinue();
    }

    public static void clientSummary() {
        try {
            showClientsForSelection();
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
                InputUtil.pressEnterToContinue();
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
        InputUtil.pressEnterToContinue();
    }

    public static void topBuyerByPurchases() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("\n[Query] Top Buyer by Purchases");

            String query = """
                SELECT c.client_id, c.client_name, COUNT(s.sales_id) as total_purchases
                FROM client c
                JOIN sales s ON c.client_id = s.buyer_id
                GROUP BY c.client_id, c.client_name
                ORDER BY total_purchases DESC
                LIMIT 1
            """;

            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next()) {
                System.out.println("🏆 Top Buyer: " + rs.getString("client_name") + " (ID: " + rs.getInt("client_id") + ")");
                System.out.println("Total Purchases: " + rs.getInt("total_purchases"));
            } else {
                System.out.println("❌ No sales data found to determine top buyer.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void repeatClients() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("\n[Query] Repeat Clients");

            String query = """
                SELECT client_id, client_name, total_transactions
                FROM (
                    SELECT c.client_id, c.client_name,
                           (SELECT COUNT(*) FROM sales WHERE buyer_id = c.client_id OR seller_id = c.client_id) +
                           (SELECT COUNT(*) FROM rent WHERE tenant_id = c.client_id) as total_transactions
                    FROM client c
                ) AS trans
                WHERE total_transactions > 1
                ORDER BY total_transactions DESC
            """;

            ResultSet rs = conn.createStatement().executeQuery(query);

            List<String> headers = Arrays.asList("Client ID", "Name", "Total Transactions");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("client_id")),
                        rs.getString("client_name"),
                        String.valueOf(rs.getInt("total_transactions"))
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No repeat clients found.");
            } else {
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void clientRevenueContribution() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("\n[Query] Client Revenue Contribution");
            System.out.println("Calculating total transaction value generated by buyers...");

            String query = """
                SELECT c.client_id, c.client_name, COALESCE(SUM(s.sales_price), 0) as total_revenue
                FROM client c
                JOIN sales s ON c.client_id = s.buyer_id
                GROUP BY c.client_id, c.client_name
                ORDER BY total_revenue DESC
            """;

            ResultSet rs = conn.createStatement().executeQuery(query);

            List<String> headers = Arrays.asList("Client ID", "Name", "Total Spent/Generated");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("client_id")),
                        rs.getString("client_name"),
                        "₹" + String.format("%,d", rs.getLong("total_revenue"))
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No revenue data found.");
            } else {
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void clientsWithNoTransactions() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("\n[Query] Clients With No Transactions");

            String query = """
                SELECT c.client_id, c.client_name, c.client_phone, c.client_email
                FROM client c
                WHERE NOT EXISTS (SELECT 1 FROM sales WHERE buyer_id = c.client_id OR seller_id = c.client_id)
                  AND NOT EXISTS (SELECT 1 FROM rent WHERE tenant_id = c.client_id)
            """;

            ResultSet rs = conn.createStatement().executeQuery(query);

            List<String> headers = Arrays.asList("ID", "Name", "Phone", "Email");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("client_id")),
                        rs.getString("client_name"),
                        rs.getString("client_phone"),
                        rs.getString("client_email")
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("✅ All clients have been involved in at least one transaction.");
            } else {
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }
}