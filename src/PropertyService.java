import java.sql.*;
import java.util.*;

public class PropertyService {

    // --- HELPER METHODS FOR UI ---
    private static void showPropertiesForSelection() throws Exception {
        Connection conn = DBConnection.getConnection();

        String query = """
                SELECT p.property_id, p.city, p.locality, p.availability_status, p.agent_id, 
                       pt.listing_type, pt.price 
                FROM property p 
                LEFT JOIN property_type pt ON p.property_id = pt.property_id
                """;

        ResultSet rs = conn.createStatement().executeQuery(query);

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
        ResultSet rs = conn.createStatement().executeQuery("SELECT agent_id, name FROM agent");
        List<String> headers = Arrays.asList("Agent ID", "Name");
        List<List<String>> rows = new ArrayList<>();
        while (rs.next()) {
            rows.add(Arrays.asList(String.valueOf(rs.getInt("agent_id")), rs.getString("name")));
        }
        if (!rows.isEmpty()) {
            System.out.println("\n📋 Available Agents:");
            TableUtil.printTable(headers, rows);
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

            String query = """
                    SELECT p.property_id, p.city, p.locality,
                           p.bedrooms, p.size_sqft,
                           pt.listing_type, pt.price,
                           p.availability_status, p.agent_id
                    FROM property p
                    LEFT JOIN property_type pt ON p.property_id = pt.property_id
                    """;

            ResultSet rs = conn.createStatement().executeQuery(query);

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

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE property SET availability_status=? WHERE property_id=?");

            ps.setBoolean(1, status == 1);
            ps.setInt(2, id);

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
                LEFT JOIN property_type pt
                ON p.property_id = pt.property_id
                WHERE p.property_id = ?
                """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

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
                System.out.println("Listing Date: " + rs.getString("listing_date"));
                System.out.println("Agent ID: " + rs.getInt("agent_id"));
                System.out.println("Owner ID: " + rs.getInt("owner_id"));

                if (rs.getString("listing_type") != null) {
                    System.out.println("Listing Type: " + rs.getString("listing_type"));
                    System.out.println("Price: ₹" + String.format("%,d", rs.getInt("price")));
                }

            } else {
                System.out.println("❌ Property not found");
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

                switch (choice) {
                    case 1 -> {
                        String city = InputUtil.getStringInput("Enter New City");
                        ps = conn.prepareStatement("UPDATE property SET city=? WHERE property_id=?");
                        ps.setString(1, city);
                    }
                    case 2 -> {
                        String locality = InputUtil.getStringInput("Enter New Locality");
                        ps = conn.prepareStatement("UPDATE property SET locality=? WHERE property_id=?");
                        ps.setString(1, locality);
                    }
                    case 3 -> {
                        int size = InputUtil.getPositiveInt("Enter New Size (sqft)");
                        ps = conn.prepareStatement("UPDATE property SET size_sqft=? WHERE property_id=?");
                        ps.setInt(1, size);
                    }
                    case 4 -> {
                        int bedrooms = InputUtil.getPositiveInt("Enter New Bedrooms");
                        ps = conn.prepareStatement("UPDATE property SET bedrooms=? WHERE property_id=?");
                        ps.setInt(1, bedrooms);
                    }
                    case 5 -> {
                        showAgentsForSelection();
                        int agentId = InputUtil.getPositiveInt("Enter New Agent ID");
                        ps = conn.prepareStatement("UPDATE property SET agent_id=? WHERE property_id=?");
                        ps.setInt(1, agentId);
                    }
                    case 6 -> {
                        showClientsForSelection();
                        int ownerId = InputUtil.getPositiveInt("Enter New Owner ID");
                        ps = conn.prepareStatement("UPDATE property SET owner_id=? WHERE property_id=?");
                        ps.setInt(1, ownerId);
                    }
                }

                if (ps != null) {
                    ps.setInt(2, id);
                    int rows = ps.executeUpdate();

                    if (rows > 0) System.out.println("✅ Updated successfully");
                    else System.out.println("❌ Property not found");
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
            ResultSet rs1 = checkSales.executeQuery();

            if (rs1.next()) {
                System.out.println("❌ Cannot delete: Property has been SOLD before");
                InputUtil.pressEnterToContinue();
                return;
            }

            PreparedStatement checkRent = conn.prepareStatement("SELECT 1 FROM rent WHERE property_id=?");
            checkRent.setInt(1, id);
            ResultSet rs2 = checkRent.executeQuery();

            if (rs2.next()) {
                System.out.println("❌ Cannot delete: Property has been RENTED before");
                InputUtil.pressEnterToContinue();
                return;
            }

            PreparedStatement ps = conn.prepareStatement("DELETE FROM property WHERE property_id=?");
            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) System.out.println("✅ Property deleted successfully");
            else System.out.println("❌ Property not found");

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

            showClientsForSelection();
            int buyerId = InputUtil.getPositiveInt("Enter Buyer ID");
            int sellerId = InputUtil.getPositiveInt("Enter Seller ID");

            showAgentsForSelection();
            int agentId = InputUtil.getPositiveInt("Enter Agent ID");

            int salesId = InputUtil.getPositiveInt("Enter New Sales ID (Must be unique)");
            int price = InputUtil.getPositiveInt("Enter Final Sale Price");

            // Insert into sales
            PreparedStatement ps1 = conn.prepareStatement(
                    "INSERT INTO sales (sales_id, sales_price, sales_date, buyer_id, seller_id, agent_id, property_id) VALUES (?, ?, CURDATE(), ?, ?, ?, ?)"
            );

            ps1.setInt(1, salesId);
            ps1.setInt(2, price);
            ps1.setInt(3, buyerId);
            ps1.setInt(4, sellerId);
            ps1.setInt(5, agentId);
            ps1.setInt(6, propertyId);
            ps1.executeUpdate();

            // Update property owner + availability
            PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE property SET availability_status=0, owner_id=? WHERE property_id=?"
            );

            ps2.setInt(1, buyerId);
            ps2.setInt(2, propertyId);
            ps2.executeUpdate();

            System.out.println("✅ Property sold successfully");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void rentProperty() {
        try {
            Connection conn = DBConnection.getConnection();

            showPropertiesForSelection();
            int propertyId = InputUtil.getPositiveInt("Enter Property ID");

            showClientsForSelection();
            int tenantId = InputUtil.getPositiveInt("Enter Tenant ID");

            showAgentsForSelection();
            int agentId = InputUtil.getPositiveInt("Enter Agent ID");

            int rentId = InputUtil.getPositiveInt("Enter New Rent ID (Must be unique)");
            int rentAmount = InputUtil.getPositiveInt("Enter Monthly Rent Amount");
            String startDate = InputUtil.getStringInput("Enter Start Date (YYYY-MM-DD)");
            String endDate = InputUtil.getStringInput("Enter End Date (YYYY-MM-DD)");

            // Insert into rent
            PreparedStatement ps1 = conn.prepareStatement(
                    "INSERT INTO rent (rent_id, rent_amount, rent_start_date, rent_end_date, tenant_id, property_id, agent_id) VALUES (?, ?, ?, ?, ?, ?, ?)"
            );

            ps1.setInt(1, rentId);
            ps1.setInt(2, rentAmount);
            ps1.setString(3, startDate);
            ps1.setString(4, endDate);
            ps1.setInt(5, tenantId);
            ps1.setInt(6, propertyId);
            ps1.setInt(7, agentId);
            ps1.executeUpdate();

            // Mark unavailable
            PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE property SET availability_status=0 WHERE property_id=?"
            );

            ps2.setInt(1, propertyId);
            ps2.executeUpdate();

            System.out.println("✅ Property rented successfully");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
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

            String query = "SELECT p.*, pt.listing_type, pt.price FROM property p LEFT JOIN property_type pt ON p.property_id = pt.property_id WHERE p.city=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, city);
            System.out.println("\n🔍 Properties in " + city + ":");
            printPropertyTable(ps.executeQuery());
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    public static void searchPropertyByLocality() {
        try {
            Connection conn = DBConnection.getConnection();
            String locality = InputUtil.getStringInput("Enter Locality");

            String query = "SELECT p.*, pt.listing_type, pt.price FROM property p LEFT JOIN property_type pt ON p.property_id = pt.property_id WHERE p.locality=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, locality);
            System.out.println("\n🔍 Properties in " + locality + ":");
            printPropertyTable(ps.executeQuery());
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    public static void filterByBedrooms() {
        try {
            Connection conn = DBConnection.getConnection();
            int b = InputUtil.getPositiveInt("Enter Bedrooms");

            String query = "SELECT p.*, pt.listing_type, pt.price FROM property p LEFT JOIN property_type pt ON p.property_id = pt.property_id WHERE p.bedrooms=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, b);
            System.out.println("\n🛏️ Properties with " + b + " Bedrooms:");
            printPropertyTable(ps.executeQuery());
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    public static void filterBySizeRange() {
        try {
            Connection conn = DBConnection.getConnection();
            int min = InputUtil.getPositiveInt("Min Size (sqft)");
            int max = InputUtil.getPositiveInt("Max Size (sqft)");

            String query = "SELECT p.*, pt.listing_type, pt.price FROM property p LEFT JOIN property_type pt ON p.property_id = pt.property_id WHERE p.size_sqft BETWEEN ? AND ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, min);
            ps.setInt(2, max);
            System.out.println("\n📏 Properties between " + min + " and " + max + " sqft:");
            printPropertyTable(ps.executeQuery());
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    public static void filterByAvailability() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT p.*, pt.listing_type, pt.price FROM property p LEFT JOIN property_type pt ON p.property_id = pt.property_id WHERE p.availability_status = 1";
            System.out.println("\n✅ Available Properties:");
            printPropertyTable(conn.createStatement().executeQuery(query));
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    public static void countProperties() {
        try {
            Connection conn = DBConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM property");
            if (rs.next()) System.out.println("📊 Total Properties in System: " + rs.getInt(1));
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    public static void sortPropertiesByPrice() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT p.*, pt.listing_type, pt.price FROM property p LEFT JOIN property_type pt ON p.property_id = pt.property_id ORDER BY pt.price DESC";
            System.out.println("\n📉 Properties Sorted by Price (High to Low):");
            printPropertyTable(conn.createStatement().executeQuery(query));
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    public static void sortPropertiesBySize() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT p.*, pt.listing_type, pt.price FROM property p LEFT JOIN property_type pt ON p.property_id = pt.property_id ORDER BY p.size_sqft DESC";
            System.out.println("\n📉 Properties Sorted by Size (Largest to Smallest):");
            printPropertyTable(conn.createStatement().executeQuery(query));
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    public static void checkPropertyExists() {
        try {
            Connection conn = DBConnection.getConnection();
            showPropertiesForSelection();
            int id = InputUtil.getPositiveInt("Enter Property ID");

            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM property WHERE property_id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) System.out.println("✅ Property exists");
            else System.out.println("❌ Property does not exist");
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    // --- IMPLEMENTED TODOs ---

    public static void mostExpensiveProperty() {
        try {
            Connection conn = DBConnection.getConnection();
            System.out.println("\n[Query] Most Expensive Property");

            String query = """
                SELECT p.property_id, p.address, p.city, p.agent_id, pt.listing_type, pt.price
                FROM property p
                JOIN property_type pt ON p.property_id = pt.property_id
                ORDER BY pt.price DESC LIMIT 1
            """;

            ResultSet rs = conn.createStatement().executeQuery(query);
            if (rs.next()) {
                System.out.println("🏆 Most Expensive Property:");
                System.out.println("Property ID: " + rs.getInt("property_id"));
                System.out.println("Location: " + rs.getString("address") + ", " + rs.getString("city"));
                System.out.println("Type: " + rs.getString("listing_type"));
                System.out.println("Price: ₹" + String.format("%,d", rs.getLong("price")));
                System.out.println("Agent ID: " + rs.getInt("agent_id"));
            } else {
                System.out.println("❌ No properties with listed prices found.");
            }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    public static void propertiesByPriceRange() {
        try {
            Connection conn = DBConnection.getConnection();
            int min = InputUtil.getPositiveInt("Enter Min Price");
            int max = InputUtil.getPositiveInt("Enter Max Price");

            System.out.println("\n[Query] Properties between ₹" + min + " and ₹" + max);
            String query = "SELECT p.*, pt.listing_type, pt.price FROM property p JOIN property_type pt ON p.property_id = pt.property_id WHERE pt.price BETWEEN ? AND ? ORDER BY pt.price";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, min);
            ps.setInt(2, max);

            printPropertyTable(ps.executeQuery());
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    public static void propertiesByType() {
        try {
            Connection conn = DBConnection.getConnection();
            String type = InputUtil.getStringInput("Enter Type (Sale/Rent)");

            System.out.println("\n[Query] " + type + " Properties:");
            String query = "SELECT p.*, pt.listing_type, pt.price FROM property p JOIN property_type pt ON p.property_id = pt.property_id WHERE pt.listing_type = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, type);

            printPropertyTable(ps.executeQuery());
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    public static void propertiesByYear() {
        try {
            Connection conn = DBConnection.getConnection();
            int year = InputUtil.getPositiveInt("Enter Year Built");

            System.out.println("\n[Query] Properties Built in " + year + ":");
            String query = "SELECT p.*, pt.listing_type, pt.price FROM property p LEFT JOIN property_type pt ON p.property_id = pt.property_id WHERE p.year_built = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, year);

            printPropertyTable(ps.executeQuery());
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    public static void averagePropertyPrice() {
        try {
            Connection conn = DBConnection.getConnection();
            System.out.println("\n[Query] Average Property Prices by Type");

            String query = "SELECT listing_type, AVG(price) as avg_price FROM property_type GROUP BY listing_type";
            ResultSet rs = conn.createStatement().executeQuery(query);

            List<String> headers = Arrays.asList("Listing Type", "Average Price");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        rs.getString("listing_type"),
                        "₹" + String.format("%,d", rs.getLong("avg_price"))
                ));
            }

            if (rows.isEmpty()) System.out.println("❌ No pricing data found.");
            else TableUtil.printTable(headers, rows);

        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }

    public static void assignAgentToProperty() {
        try {
            Connection conn = DBConnection.getConnection();

            showPropertiesForSelection();
            int propertyId = InputUtil.getPositiveInt("Enter Property ID");

            showAgentsForSelection();
            int agentId = InputUtil.getPositiveInt("Enter Agent ID to Assign");

            PreparedStatement ps = conn.prepareStatement("UPDATE property SET agent_id=? WHERE property_id=?");
            ps.setInt(1, agentId);
            ps.setInt(2, propertyId);

            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("✅ Agent " + agentId + " successfully assigned to Property " + propertyId);
            else System.out.println("❌ Property not found");

        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        InputUtil.pressEnterToContinue();
    }
}