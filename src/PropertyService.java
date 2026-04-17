import java.sql.*;
import java.util.*;

public class PropertyService {

    private static void showPropertiesForSelection() throws Exception {
        Connection conn = DBConnection.getConnection();

        String baseQuery = """
        SELECT 
            p.property_id, 
            p.city, 
            p.locality, 
            p.availability_status, 
            p.agent_id,
            p.owner_id,
            pt.listing_type, 
            pt.price,
            c.client_name AS owner_name
        FROM property p 
        LEFT JOIN property_type pt ON p.property_id = pt.property_id
        LEFT JOIN client c ON p.owner_id = c.client_id
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
                "Property ID", "City", "Locality", "Available",
                "Agent ID", "Owner ID", "Owner Name", "Type", "Price"
        );

        List<List<String>> rows = new ArrayList<>();

        while (rs.next()) {
            rows.add(Arrays.asList(
                    String.valueOf(rs.getInt("property_id")),
                    rs.getString("city"),
                    rs.getString("locality"),
                    rs.getBoolean("availability_status") ? "Yes" : "No",
                    String.valueOf(rs.getInt("agent_id")),
                    String.valueOf(rs.getInt("owner_id")),
                    rs.getString("owner_name") != null ? rs.getString("owner_name") : "N/A",
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

    // --- MAIN METHODS ---

    public static void addProperty() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Property ID");
            String address = InputUtil.getStringInput("Enter Address");
            String city = InputUtil.getStringInput("Enter City");
            String locality = InputUtil.getStringInput("Enter Locality");
            int size = InputUtil.getPositiveInt("Enter Size (sqft)");
            int bedrooms = InputUtil.getPositiveInt("Enter Bedrooms");
            int year = InputUtil.getPositiveInt("Enter Year Built");

            showAgentsForSelection();
            int agentId = InputUtil.getPositiveInt("Enter Agent ID");

            showClientsForSelection();
            int ownerId = InputUtil.getPositiveInt("Enter Owner ID (Client ID)");

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO property VALUES (?, ?, ?, ?, ?, ?, ?, true, CURDATE(), ?, ?)");

            ps.setInt(1, id);
            ps.setString(2, address);
            ps.setString(3, city);
            ps.setString(4, locality);
            ps.setInt(5, size);
            ps.setInt(6, bedrooms);
            ps.setInt(7, year);
            ps.setInt(8, agentId);
            ps.setInt(9, ownerId);

            ps.executeUpdate();

            String type = InputUtil.getStringInput("Enter Listing Type (Sale/Rent)");
            int price = InputUtil.getPositiveInt("Enter Price");

            PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT INTO property_type VALUES (?, ?, ?)");
            ps2.setString(1, type);
            ps2.setInt(2, price);
            ps2.setInt(3, id);

            ps2.executeUpdate();

            System.out.println("✅ Property added successfully");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void viewProperties() {
        try {
            Connection conn = DBConnection.getConnection();

            String baseQuery = """
    SELECT p.property_id, p.city, p.locality,
           p.bedrooms, p.size_sqft,
           pt.listing_type, pt.price,
           p.availability_status, p.agent_id
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
                    "ID", "City", "Locality", "BHK", "Size",
                    "Type", "Price", "Available", "Agent ID"
            );

            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        rs.getString("property_id"),
                        rs.getString("city"),
                        rs.getString("locality"),
                        rs.getString("bedrooms"),
                        rs.getString("size_sqft") + " sqft",
                        rs.getString("listing_type"),
                        rs.getString("price") != null ? "₹" + String.format("%,d", rs.getInt("price")) : "N/A",
                        rs.getBoolean("availability_status") ? "Yes" : "No",
                        String.valueOf(rs.getInt("agent_id"))
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No properties found.");
            } else {
                System.out.println("\n🏠 All Properties:");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void updateAvailability() {
        try {
            Connection conn = DBConnection.getConnection();
            showPropertiesForSelection();

            int id = InputUtil.getPositiveInt("Enter Property ID");
            int status = InputUtil.getIntInRange("Enter 1 (Available) / 0 (Not Available)", 0, 1);

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                ps = conn.prepareStatement(
                        "UPDATE property SET availability_status=? WHERE property_id=? AND agent_id=?"
                );
                ps.setBoolean(1, status == 1);
                ps.setInt(2, id);
                ps.setInt(3, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement("""
        UPDATE property 
        SET availability_status=? 
        WHERE property_id=? 
        AND agent_id IN (
            SELECT agent_id FROM agent WHERE agency_id=?
        )
    """);
                ps.setBoolean(1, status == 1);
                ps.setInt(2, id);
                ps.setInt(3, Session.agencyId);

            } else {
                ps = conn.prepareStatement(
                        "UPDATE property SET availability_status=? WHERE property_id=?"
                );
                ps.setBoolean(1, status == 1);
                ps.setInt(2, id);
            }

            int rows = ps.executeUpdate();
            if(rows > 0) System.out.println("✅ Availability updated");
            else System.out.println("❌ Property not found");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void findPropertyById() {
        try {
            Connection conn = DBConnection.getConnection();
            showPropertiesForSelection();

            int id = InputUtil.getPositiveInt("Enter Property ID");

            String query = """
            SELECT p.*, pt.listing_type, pt.price
            FROM property p
            LEFT JOIN property_type pt ON p.property_id = pt.property_id
            WHERE p.property_id = ?
        """;

            if ("AGENT".equals(Session.role)) {
                query += " AND p.agent_id = ?";
            } else if ("AGENCY".equals(Session.role)) {
                query += """
                AND p.agent_id IN (
                    SELECT agent_id FROM agent WHERE agency_id = ?
                )
            """;
            }

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            if ("AGENT".equals(Session.role)) {
                ps.setInt(2, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                ps.setInt(2, Session.agencyId);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n🏠 Property Details:");
                System.out.println("ID: " + rs.getInt("property_id"));
                System.out.println("Address: " + rs.getString("address"));
                System.out.println("City: " + rs.getString("city"));
                System.out.println("Locality: " + rs.getString("locality"));
                System.out.println("Size: " + rs.getInt("size_sqft") + " sqft");
                System.out.println("Bedrooms: " + rs.getInt("bedrooms"));
                System.out.println("Year Built: " + rs.getString("year_built"));
                System.out.println("Available: " + (rs.getBoolean("availability_status") ? "Yes" : "No"));
                System.out.println("Agent ID: " + rs.getInt("agent_id"));
                System.out.println("Owner ID: " + rs.getInt("owner_id"));

                if (rs.getString("listing_type") != null) {
                    System.out.println("Type: " + rs.getString("listing_type"));
                    System.out.println("Price: ₹" + String.format("%,d", rs.getInt("price")));
                }

            } else {
                System.out.println("❌ Property not found or access denied");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void updateProperty() {
        try {
            Connection conn = DBConnection.getConnection();
            showPropertiesForSelection();

            int id = InputUtil.getPositiveInt("Enter Property ID to Update");

            while (true) {
                System.out.println("\n--- Update Property Menu ---");
                System.out.println("1. Update City");
                System.out.println("2. Update Locality");
                System.out.println("3. Update Size");
                System.out.println("4. Update Bedrooms");
                System.out.println("5. Update Agent ID");
                System.out.println("6. Update Owner ID");
                System.out.println("0. Exit");

                int choice = InputUtil.getIntInRange("Enter choice", 0, 6);

                if (choice == 0) {
                    System.out.println("✅ Update finished");
                    break;
                }

                PreparedStatement ps = null;
                String query = "";

                switch (choice) {
                    case 1 -> {
                        String city = InputUtil.getStringInput("Enter New City");
                        query = "UPDATE property SET city=? WHERE property_id=?";
                        ps = conn.prepareStatement(query);
                        ps.setString(1, city);
                    }
                    case 2 -> {
                        String locality = InputUtil.getStringInput("Enter New Locality");
                        query = "UPDATE property SET locality=? WHERE property_id=?";
                        ps = conn.prepareStatement(query);
                        ps.setString(1, locality);
                    }
                    case 3 -> {
                        int size = InputUtil.getPositiveInt("Enter New Size (sqft)");
                        query = "UPDATE property SET size_sqft=? WHERE property_id=?";
                        ps = conn.prepareStatement(query);
                        ps.setInt(1, size);
                    }
                    case 4 -> {
                        int bedrooms = InputUtil.getPositiveInt("Enter New Bedrooms");
                        query = "UPDATE property SET bedrooms=? WHERE property_id=?";
                        ps = conn.prepareStatement(query);
                        ps.setInt(1, bedrooms);
                    }
                    case 5 -> {
                        if ("AGENT".equals(Session.role)) {
                            System.out.println("❌ Agents cannot reassign properties");
                            continue;
                        }
                        showAgentsForSelection();
                        int agentId = InputUtil.getPositiveInt("Enter New Agent ID");

                        query = "UPDATE property SET agent_id=? WHERE property_id=?";
                        ps = conn.prepareStatement(query);
                        ps.setInt(1, agentId);
                    }
                    case 6 -> {
                        showClientsForSelection();
                        int ownerId = InputUtil.getPositiveInt("Enter New Owner ID");

                        query = "UPDATE property SET owner_id=? WHERE property_id=?";
                        ps = conn.prepareStatement(query);
                        ps.setInt(1, ownerId);
                    }
                }

                if (ps != null) {

                    // 🔥 RBAC FILTER
                    if ("AGENT".equals(Session.role)) {
                        query += " AND agent_id=?";
                    } else if ("AGENCY".equals(Session.role)) {
                        query += " AND agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
                    }

                    ps = conn.prepareStatement(query);
                    // Re-set values (important)
                    switch (choice) {
                        case 1 -> ps.setString(1, InputUtil.getStringInput("Enter New City"));
                        case 2 -> ps.setString(1, InputUtil.getStringInput("Enter New Locality"));
                        case 3 -> ps.setInt(1, InputUtil.getPositiveInt("Enter New Size"));
                        case 4 -> ps.setInt(1, InputUtil.getPositiveInt("Enter New Bedrooms"));
                    }

                    ps.setInt(2, id);

                    if ("AGENT".equals(Session.role)) {
                        ps.setInt(3, Session.userId);
                    } else if ("AGENCY".equals(Session.role)) {
                        ps.setInt(3, Session.agencyId);
                    }

                    int rows = ps.executeUpdate();

                    if (rows > 0) System.out.println("✅ Updated successfully");
                    else System.out.println("❌ Access denied or property not found");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void deleteProperty() {
        try {
            Connection conn = DBConnection.getConnection();
            showPropertiesForSelection();

            int id = InputUtil.getPositiveInt("Enter Property ID to Delete");

            PreparedStatement checkSales = conn.prepareStatement("SELECT 1 FROM sales WHERE property_id=?");
            checkSales.setInt(1, id);
            if (checkSales.executeQuery().next()) {
                System.out.println("❌ Cannot delete: Property has been SOLD");
                return;
            }

            PreparedStatement checkRent = conn.prepareStatement("SELECT 1 FROM rent WHERE property_id=?");
            checkRent.setInt(1, id);
            if (checkRent.executeQuery().next()) {
                System.out.println("❌ Cannot delete: Property has been RENTED");
                return;
            }

            String query = "DELETE FROM property WHERE property_id=?";

            if ("AGENT".equals(Session.role)) {
                query += " AND agent_id=?";
            } else if ("AGENCY".equals(Session.role)) {
                query += " AND agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            }

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            if ("AGENT".equals(Session.role)) {
                ps.setInt(2, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                ps.setInt(2, Session.agencyId);
            }

            int rows = ps.executeUpdate();

            if (rows > 0) System.out.println("✅ Property deleted");
            else System.out.println("❌ Access denied or not found");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void sellProperty() {
        try {
            Connection conn = DBConnection.getConnection();

            showPropertiesForSelection();
            int propertyId = InputUtil.getPositiveInt("Enter Property ID");

            // 🔥 VALIDATE ACCESS
            String checkQuery = "SELECT 1 FROM property WHERE property_id=?";
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

            if (!check.executeQuery().next()) {
                System.out.println("❌ Access denied");
                return;
            }

            showClientsForSelection();
            int buyerId = InputUtil.getPositiveInt("Enter Buyer ID");
            int sellerId = InputUtil.getPositiveInt("Enter Seller ID");

            int agentId = Session.userId; // 🔥 auto assign

            int salesId = InputUtil.getPositiveInt("Enter Sales ID");
            int price = InputUtil.getPositiveInt("Enter Sale Price");

            PreparedStatement ps1 = conn.prepareStatement(
                    "INSERT INTO sales VALUES (?, ?, CURDATE(), ?, ?, ?, ?)"
            );

            ps1.setInt(1, salesId);
            ps1.setInt(2, price);
            ps1.setInt(3, buyerId);
            ps1.setInt(4, sellerId);
            ps1.setInt(5, agentId);
            ps1.setInt(6, propertyId);
            ps1.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE property SET availability_status=0, owner_id=? WHERE property_id=?"
            );
            ps2.setInt(1, buyerId);
            ps2.setInt(2, propertyId);
            ps2.executeUpdate();

            System.out.println("✅ Property sold");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void rentProperty() {
        try {
            Connection conn = DBConnection.getConnection();

            showPropertiesForSelection();
            int propertyId = InputUtil.getPositiveInt("Enter Property ID");

            String checkQuery = "SELECT 1 FROM property WHERE property_id=?";
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

            if (!check.executeQuery().next()) {
                System.out.println("❌ Access denied");
                return;
            }

            showClientsForSelection();
            int tenantId = InputUtil.getPositiveInt("Enter Tenant ID");

            int agentId = Session.userId;

            int rentId = InputUtil.getPositiveInt("Enter Rent ID");
            int amount = InputUtil.getPositiveInt("Enter Rent Amount");
            String start = InputUtil.getStringInput("Start Date");
            String end = InputUtil.getStringInput("End Date");

            PreparedStatement ps1 = conn.prepareStatement(
                    "INSERT INTO rent VALUES (?, ?, ?, ?, ?, ?, ?)"
            );

            ps1.setInt(1, rentId);
            ps1.setInt(2, amount);
            ps1.setString(3, start);
            ps1.setString(4, end);
            ps1.setInt(5, tenantId);
            ps1.setInt(6, propertyId);
            ps1.setInt(7, agentId);
            ps1.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE property SET availability_status=0 WHERE property_id=?"
            );
            ps2.setInt(1, propertyId);
            ps2.executeUpdate();

            System.out.println("✅ Property rented");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void relistProperty() {
        try {
            Connection conn = DBConnection.getConnection();
            showPropertiesForSelection();

            int propertyId = InputUtil.getPositiveInt("Enter Property ID");
            String type = InputUtil.getStringInput("Enter Listing Type (Sale/Rent)");
            int price = InputUtil.getPositiveInt("Enter Price");

            PreparedStatement ps1 = conn.prepareStatement("UPDATE property SET availability_status=1 WHERE property_id=?");
            ps1.setInt(1, propertyId);
            ps1.executeUpdate();

            PreparedStatement check = conn.prepareStatement("SELECT * FROM property_type WHERE property_id=? AND listing_type=?");
            check.setInt(1, propertyId);
            check.setString(2, type);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                PreparedStatement ps2 = conn.prepareStatement("UPDATE property_type SET price=? WHERE property_id=? AND listing_type=?");
                ps2.setInt(1, price);
                ps2.setInt(2, propertyId);
                ps2.setString(3, type);
                ps2.executeUpdate();
            } else {
                PreparedStatement ps3 = conn.prepareStatement("INSERT INTO property_type VALUES (?, ?, ?)");
                ps3.setString(1, type);
                ps3.setInt(2, price);
                ps3.setInt(3, propertyId);
                ps3.executeUpdate();
            }

            System.out.println("✅ Property relisted successfully");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void removeListing() {
        try {
            Connection conn = DBConnection.getConnection();
            showPropertiesForSelection();

            int propertyId = InputUtil.getPositiveInt("Enter Property ID");

            PreparedStatement ps = conn.prepareStatement("UPDATE property SET availability_status=0 WHERE property_id=?");
            ps.setInt(1, propertyId);

            int rows = ps.executeUpdate();

            if (rows > 0) System.out.println("✅ Property removed from listing");
            else System.out.println("❌ Property not found");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void updateListing() {
        try {
            Connection conn = DBConnection.getConnection();
            showPropertiesForSelection();

            int propertyId = InputUtil.getPositiveInt("Enter Property ID");
            String type = InputUtil.getStringInput("Enter Listing Type (Sale/Rent)");
            int price = InputUtil.getPositiveInt("Enter New Price");

            PreparedStatement ps = conn.prepareStatement("UPDATE property_type SET price=? WHERE property_id=? AND listing_type=?");
            ps.setInt(1, price);
            ps.setInt(2, propertyId);
            ps.setString(3, type);

            int rows = ps.executeUpdate();

            if (rows > 0) System.out.println("✅ Listing updated");
            else System.out.println("❌ Listing not found");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    // --- SEARCH / FILTER / SORT / METRICS ---

    public static void printPropertyTable(ResultSet rs) throws Exception {
        List<String> headers = Arrays.asList("ID", "City", "Locality", "Size", "BHK", "Available", "Type", "Price", "Agent ID");
        List<List<String>> rows = new ArrayList<>();

        while (rs.next()) {
            rows.add(Arrays.asList(
                    rs.getString("property_id"),
                    rs.getString("city"),
                    rs.getString("locality"),
                    rs.getString("size_sqft") + " sqft",
                    rs.getString("bedrooms"),
                    rs.getBoolean("availability_status") ? "Yes" : "No",
                    rs.getString("listing_type"),
                    rs.getString("price") != null ? "₹" + String.format("%,d", rs.getInt("price")) : "N/A",
                    String.valueOf(rs.getInt("agent_id"))
            ));
        }

        if (rows.isEmpty()) System.out.println("❌ No results found");
        else TableUtil.printTable(headers, rows);
    }

    public static void searchPropertyByCity() {
        try {
            Connection conn = DBConnection.getConnection();
            String city = InputUtil.getStringInput("Enter City");

            String baseQuery = """
                SELECT p.*, pt.listing_type, pt.price 
                FROM property p 
                LEFT JOIN property_type pt ON p.property_id = pt.property_id 
                WHERE p.city = ?
            """;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                ps = conn.prepareStatement(baseQuery + " AND p.agent_id = ?");
                ps.setString(1, city);
                ps.setInt(2, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement(baseQuery + """
                    AND p.agent_id IN (
                        SELECT agent_id FROM agent WHERE agency_id = ?
                    )
                """);
                ps.setString(1, city);
                ps.setInt(2, Session.agencyId);

            } else {
                ps = conn.prepareStatement(baseQuery);
                ps.setString(1, city);
            }

            ResultSet rs = ps.executeQuery();

            // Showing all table attributes
            List<String> headers = Arrays.asList(
                    "ID", "Address", "City", "Locality", "Size(sqft)", "Beds",
                    "Built", "Available", "Listing Date", "Agent ID", "Owner ID", "Type", "Price"
            );
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("property_id")),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("locality"),
                        String.valueOf(rs.getInt("size_sqft")),
                        String.valueOf(rs.getInt("bedrooms")),
                        rs.getString("year_built"),
                        rs.getBoolean("availability_status") ? "Yes" : "No",
                        rs.getString("listing_date"),
                        String.valueOf(rs.getInt("agent_id")),
                        String.valueOf(rs.getInt("owner_id")),
                        rs.getString("listing_type") != null ? rs.getString("listing_type") : "N/A",
                        rs.getString("price") != null ? "₹" + String.format("%,d", rs.getInt("price")) : "N/A"
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No properties found in the city: " + city);
            } else {
                System.out.println("\n🔍 Full Property Details in " + city + ":");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void searchPropertyByLocality() {
        try {
            Connection conn = DBConnection.getConnection();
            String locality = InputUtil.getStringInput("Enter Locality");

            String query = """
                SELECT p.*, pt.listing_type, pt.price 
                FROM property p 
                LEFT JOIN property_type pt ON p.property_id = pt.property_id 
                WHERE p.locality = ?
            """;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                ps = conn.prepareStatement(query + " AND p.agent_id = ?");
                ps.setString(1, locality);
                ps.setInt(2, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement(query + """
                    AND p.agent_id IN (
                        SELECT agent_id FROM agent WHERE agency_id = ?
                    )
                """);
                ps.setString(1, locality);
                ps.setInt(2, Session.agencyId);

            } else {
                ps = conn.prepareStatement(query);
                ps.setString(1, locality);
            }

            ResultSet rs = ps.executeQuery();

            // Showing all table attributes
            List<String> headers = Arrays.asList(
                    "ID", "Address", "City", "Locality", "Size(sqft)", "Beds",
                    "Built", "Available", "Listing Date", "Agent ID", "Owner ID", "Type", "Price"
            );
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("property_id")),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("locality"),
                        String.valueOf(rs.getInt("size_sqft")),
                        String.valueOf(rs.getInt("bedrooms")),
                        rs.getString("year_built"),
                        rs.getBoolean("availability_status") ? "Yes" : "No",
                        rs.getString("listing_date"),
                        String.valueOf(rs.getInt("agent_id")),
                        String.valueOf(rs.getInt("owner_id")),
                        rs.getString("listing_type") != null ? rs.getString("listing_type") : "N/A",
                        rs.getString("price") != null ? "₹" + String.format("%,d", rs.getInt("price")) : "N/A"
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No properties found in this locality.");
            } else {
                System.out.println("\n🔍 Full Property Details in " + locality + ":");
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void filterByBedrooms() {
        try {
            Connection conn = DBConnection.getConnection();
            int bedrooms = InputUtil.getPositiveInt("Enter Bedrooms");

            String baseQuery = "SELECT * FROM property p WHERE p.bedrooms = ?";
            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                ps = conn.prepareStatement(baseQuery + " AND p.agent_id = ?");
                ps.setInt(1, bedrooms);
                ps.setInt(2, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement(baseQuery + """
                AND p.agent_id IN (
                    SELECT agent_id FROM agent WHERE agency_id = ?
                )
            """);
                ps.setInt(1, bedrooms);
                ps.setInt(2, Session.agencyId);

            } else {
                ps = conn.prepareStatement(baseQuery);
                ps.setInt(1, bedrooms);
            }

            ResultSet rs = ps.executeQuery();

            System.out.println("\n🛏️ Properties with " + bedrooms + " Bedrooms:\n");

            boolean found = false;

            while (rs.next()) {
                found = true;

                System.out.println("Property ID: " + rs.getInt("property_id"));
                System.out.println("Bedrooms   : " + rs.getInt("bedrooms"));
                System.out.println("--------------------------------------");
            }

            if (!found) {
                System.out.println("⚠️ No properties found.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        InputUtil.pressEnterToContinue();
    }

    public static void filterBySizeRange() {
        try {
            Connection conn = DBConnection.getConnection();

            int min = InputUtil.getPositiveInt("Enter Min Size");
            int max = InputUtil.getPositiveInt("Enter Max Size");

            String query = "SELECT * FROM property p WHERE p.size_sqft BETWEEN ? AND ?";

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                ps = conn.prepareStatement(query + " AND p.agent_id = ?");
                ps.setInt(1, min);
                ps.setInt(2, max);
                ps.setInt(3, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement(query + """
                AND p.agent_id IN (
                    SELECT agent_id FROM agent WHERE agency_id = ?
                )
            """);
                ps.setInt(1, min);
                ps.setInt(2, max);
                ps.setInt(3, Session.agencyId);

            } else {
                ps = conn.prepareStatement(query);
                ps.setInt(1, min);
                ps.setInt(2, max);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getInt("property_id") + " | " + rs.getInt("size_sqft"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void filterByAvailability() {
        try {
            Connection conn = DBConnection.getConnection();

            String status = InputUtil.getStringInput("Enter Availability");

            String query = "SELECT * FROM property p WHERE p.availability_status = ?";

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                ps = conn.prepareStatement(query + " AND p.agent_id = ?");
                ps.setString(1, status);
                ps.setInt(2, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement(query + """
                AND p.agent_id IN (
                    SELECT agent_id FROM agent WHERE agency_id = ?
                )
            """);
                ps.setString(1, status);
                ps.setInt(2, Session.agencyId);

            } else {
                ps = conn.prepareStatement(query);
                ps.setString(1, status);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getInt("property_id") + " | " + rs.getString("availability_status"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void countProperties() {
        try {
            Connection conn = DBConnection.getConnection();
            String baseQuery = "SELECT COUNT(*) FROM property p";

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
            ResultSet rs=ps.executeQuery();
            if (rs.next()) System.out.println("📊 Total Properties in System: " + rs.getInt(1));
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    public static void sortPropertiesByPrice() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
            SELECT p.property_id, pt.price
            FROM property p
            JOIN property_type pt ON p.property_id = pt.property_id
        """;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                ps = conn.prepareStatement(query + " WHERE p.agent_id = ? ORDER BY pt.price DESC");
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement(query + """
                WHERE p.agent_id IN (
                    SELECT agent_id FROM agent WHERE agency_id = ?
                )
                ORDER BY pt.price DESC
            """);
                ps.setInt(1, Session.agencyId);

            } else {
                ps = conn.prepareStatement(query + " ORDER BY pt.price DESC");
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getInt("property_id") + " | ₹" + rs.getInt("price"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void sortPropertiesBySize() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT property_id, size_sqft FROM property p";

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                ps = conn.prepareStatement(query + " WHERE p.agent_id = ? ORDER BY size_sqft DESC");
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement(query + """
                WHERE p.agent_id IN (
                    SELECT agent_id FROM agent WHERE agency_id = ?
                )
                ORDER BY size_sqft DESC
            """);
                ps.setInt(1, Session.agencyId);

            } else {
                ps = conn.prepareStatement(query + " ORDER BY size_sqft DESC");
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getInt("property_id") + " | " + rs.getInt("size_sqft"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void checkPropertyExists() {
        try {
            Connection conn = DBConnection.getConnection();
            showPropertiesForSelection();

            int id = InputUtil.getPositiveInt("Enter Property ID");

            String query = "SELECT 1 FROM property p WHERE p.property_id = ?";

            if ("AGENT".equals(Session.role)) {
                query += " AND p.agent_id = ?";
            } else if ("AGENCY".equals(Session.role)) {
                query += """
                AND p.agent_id IN (
                    SELECT agent_id FROM agent WHERE agency_id = ?
                )
            """;
            }

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            if ("AGENT".equals(Session.role)) {
                ps.setInt(2, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                ps.setInt(2, Session.agencyId);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) System.out.println("✅ Property exists");
            else System.out.println("❌ Property not found or access denied");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    // --- IMPLEMENTED TODOs ---

    public static void mostExpensiveProperty() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
            SELECT p.property_id, pt.price
            FROM property p
            JOIN property_type pt ON p.property_id = pt.property_id
        """;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                ps = conn.prepareStatement(query + " WHERE p.agent_id = ? ORDER BY pt.price DESC LIMIT 1");
                ps.setInt(1, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement(query + """
                WHERE p.agent_id IN (
                    SELECT agent_id FROM agent WHERE agency_id = ?
                )
                ORDER BY pt.price DESC LIMIT 1
            """);
                ps.setInt(1, Session.agencyId);

            } else {
                ps = conn.prepareStatement(query + " ORDER BY pt.price DESC LIMIT 1");
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Most Expensive Property: " +
                        rs.getInt("property_id") + " | ₹" + rs.getInt("price"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void propertiesByPriceRange() {
        try {
            Connection conn = DBConnection.getConnection();

            int min = InputUtil.getPositiveInt("Min Price");
            int max = InputUtil.getPositiveInt("Max Price");

            String query = """
            SELECT p.property_id, pt.price
            FROM property p
            JOIN property_type pt ON p.property_id = pt.property_id
            WHERE pt.price BETWEEN ? AND ?
        """;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                ps = conn.prepareStatement(query + " AND p.agent_id = ?");
                ps.setInt(1, min);
                ps.setInt(2, max);
                ps.setInt(3, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement(query + """
                AND p.agent_id IN (
                    SELECT agent_id FROM agent WHERE agency_id = ?
                )
            """);
                ps.setInt(1, min);
                ps.setInt(2, max);
                ps.setInt(3, Session.agencyId);

            } else {
                ps = conn.prepareStatement(query);
                ps.setInt(1, min);
                ps.setInt(2, max);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getInt("property_id") + " | ₹" + rs.getInt("price"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void propertiesByType() {
        try {
            Connection conn = DBConnection.getConnection();

            String type = InputUtil.getStringInput("Enter Type");

            String query = """
            SELECT p.property_id, pt.listing_type
            FROM property p
            JOIN property_type pt ON p.property_id = pt.property_id
            WHERE pt.listing_type = ?
        """;

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                ps = conn.prepareStatement(query + " AND p.agent_id = ?");
                ps.setString(1, type);
                ps.setInt(2, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement(query + """
                AND p.agent_id IN (
                    SELECT agent_id FROM agent WHERE agency_id = ?
                )
            """);
                ps.setString(1, type);
                ps.setInt(2, Session.agencyId);

            } else {
                ps = conn.prepareStatement(query);
                ps.setString(1, type);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getInt("property_id") + " | " + rs.getString("listing_type"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void propertiesByYear() {
        try {
            Connection conn = DBConnection.getConnection();

            int year = InputUtil.getPositiveInt("Enter Year");

            String query = "SELECT * FROM property p WHERE p.year_built = ?";

            PreparedStatement ps;

            if ("AGENT".equals(Session.role)) {
                ps = conn.prepareStatement(query + " AND p.agent_id = ?");
                ps.setInt(1, year);
                ps.setInt(2, Session.userId);

            } else if ("AGENCY".equals(Session.role)) {
                ps = conn.prepareStatement(query + """
                AND p.agent_id IN (
                    SELECT agent_id FROM agent WHERE agency_id = ?
                )
            """);
                ps.setInt(1, year);
                ps.setInt(2, Session.agencyId);

            } else {
                ps = conn.prepareStatement(query);
                ps.setInt(1, year);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getInt("property_id") + " | " + rs.getInt("year_built"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void averagePropertyPrice() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
            SELECT pt.listing_type, AVG(pt.price) as avg_price
            FROM property p
            JOIN property_type pt ON p.property_id = pt.property_id
        """;

            if ("AGENT".equals(Session.role)) {
                query += " WHERE p.agent_id = ?";
            } else if ("AGENCY".equals(Session.role)) {
                query += """
                WHERE p.agent_id IN (
                    SELECT agent_id FROM agent WHERE agency_id = ?
                )
            """;
            }

            query += " GROUP BY pt.listing_type";

            PreparedStatement ps = conn.prepareStatement(query);

            if ("AGENT".equals(Session.role)) {
                ps.setInt(1, Session.userId);
            } else if ("AGENCY".equals(Session.role)) {
                ps.setInt(1, Session.agencyId);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("listing_type") +
                        " | Avg Price: ₹" + String.format("%,d", rs.getLong("avg_price")));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    public static void assignAgentToProperty() {
        try {
            Connection conn = DBConnection.getConnection();

            showPropertiesForSelection();
            int propertyId = InputUtil.getPositiveInt("Enter Property ID");

            showAgentsForSelection();
            int agentId = InputUtil.getPositiveInt("Enter Agent ID to Assign");

            if ("AGENT".equals(Session.role)) {
                System.out.println("❌ Agents cannot reassign properties");
                return;
            }

            String query = "UPDATE property SET agent_id=? WHERE property_id=?";

            if ("AGENCY".equals(Session.role)) {
                query += " AND agent_id IN (SELECT agent_id FROM agent WHERE agency_id=?)";
            }

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, agentId);
            ps.setInt(2, propertyId);

            if ("AGENCY".equals(Session.role)) {
                ps.setInt(3, Session.agencyId);
            }

            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("✅ Agent " + agentId + " successfully assigned to Property " + propertyId);
            else System.out.println("❌ Property not found");

        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }
}