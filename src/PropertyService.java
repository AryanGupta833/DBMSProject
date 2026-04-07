import java.sql.*;
import java.util.*;

public class PropertyService {

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
            int agentId = InputUtil.getPositiveInt("Enter Agent ID");
            int ownerId = InputUtil.getPositiveInt("Enter Owner ID");

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
    }

    public static void viewProperties() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
                    SELECT p.property_id, p.city, p.locality,
                           p.bedrooms, p.size_sqft,
                           pt.listing_type, pt.price,
                           p.availability_status
                    FROM property p
                    LEFT JOIN property_type pt ON p.property_id = pt.property_id
                    """;

            ResultSet rs = conn.createStatement().executeQuery(query);

            List<String> headers = Arrays.asList(
                    "ID", "City", "Locality", "BHK", "Size",
                    "Type", "Price", "Available"
            );

            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        rs.getString("property_id"),
                        rs.getString("city"),
                        rs.getString("locality"),
                        rs.getString("bedrooms"),
                        rs.getString("size_sqft"),
                        rs.getString("listing_type"),
                        rs.getString("price"),
                        rs.getString("availability_status")
                ));
            }

            TableUtil.printTable(headers, rows);

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void updateAvailability() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Property ID");
            int status = InputUtil.getIntInRange("Enter 1 (Available) / 0 (Not Available)", 0, 1);

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE property SET availability_status=? WHERE property_id=?");

            ps.setBoolean(1, status == 1);
            ps.setInt(2, id);

            ps.executeUpdate();

            System.out.println("✅ Availability updated");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void findPropertyById() {
        try {
            Connection conn = DBConnection.getConnection();

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
                System.out.println("Available: " + rs.getBoolean("availability_status"));
                System.out.println("Listing Date: " + rs.getString("listing_date"));
                System.out.println("Agent ID: " + rs.getInt("agent_id"));
                System.out.println("Owner ID: " + rs.getInt("owner_id"));

                System.out.println("Listing Type: " + rs.getString("listing_type"));
                System.out.println("Price: " + rs.getInt("price"));

            } else {
                System.out.println("❌ Property not found");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void updateProperty() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Property ID");

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
                        ps = conn.prepareStatement(
                                "UPDATE property SET city=? WHERE property_id=?");
                        ps.setString(1, city);
                    }

                    case 2 -> {
                        String locality = InputUtil.getStringInput("Enter New Locality");
                        ps = conn.prepareStatement(
                                "UPDATE property SET locality=? WHERE property_id=?");
                        ps.setString(1, locality);
                    }

                    case 3 -> {
                        int size = InputUtil.getPositiveInt("Enter New Size");
                        ps = conn.prepareStatement(
                                "UPDATE property SET size_sqft=? WHERE property_id=?");
                        ps.setInt(1, size);
                    }

                    case 4 -> {
                        int bedrooms = InputUtil.getPositiveInt("Enter New Bedrooms");
                        ps = conn.prepareStatement(
                                "UPDATE property SET bedrooms=? WHERE property_id=?");
                        ps.setInt(1, bedrooms);
                    }

                    case 5 -> {
                        int agentId = InputUtil.getPositiveInt("Enter New Agent ID");
                        ps = conn.prepareStatement(
                                "UPDATE property SET agent_id=? WHERE property_id=?");
                        ps.setInt(1, agentId);
                    }

                    case 6 -> {
                        int ownerId = InputUtil.getPositiveInt("Enter New Owner ID");
                        ps = conn.prepareStatement(
                                "UPDATE property SET owner_id=? WHERE property_id=?");
                        ps.setInt(1, ownerId);
                    }
                }

                if (ps != null) {
                    ps.setInt(2, id);
                    int rows = ps.executeUpdate();

                    if (rows > 0)
                        System.out.println("✅ Updated successfully");
                    else
                        System.out.println("❌ Property not found");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void deleteProperty() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Property ID");

            PreparedStatement checkSales = conn.prepareStatement(
                    "SELECT 1 FROM sales WHERE property_id=?"
            );
            checkSales.setInt(1, id);
            ResultSet rs1 = checkSales.executeQuery();

            if (rs1.next()) {
                System.out.println("❌ Cannot delete: Property has been SOLD before");
                return;
            }

            PreparedStatement checkRent = conn.prepareStatement(
                    "SELECT 1 FROM rent WHERE property_id=?"
            );
            checkRent.setInt(1, id);
            ResultSet rs2 = checkRent.executeQuery();

            if (rs2.next()) {
                System.out.println("❌ Cannot delete: Property has been RENTED before");
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM property WHERE property_id=?"
            );
            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("✅ Property deleted successfully");
            else
                System.out.println("❌ Property not found");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void searchPropertyByCity() {
        try {
            Connection conn = DBConnection.getConnection();

            String city = InputUtil.getStringInput("Enter City");

            String query = """
                SELECT p.property_id, p.city, p.locality, p.size_sqft, p.bedrooms,
                       p.availability_status, pt.listing_type, pt.price
                FROM property p
                LEFT JOIN property_type pt ON p.property_id = pt.property_id
                WHERE p.city=?
                """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, city);

            ResultSet rs = ps.executeQuery();

            printPropertyTable(rs);

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void searchPropertyByLocality() {
        try {
            Connection conn = DBConnection.getConnection();

            String locality = InputUtil.getStringInput("Enter Locality");

            String query = """
                SELECT p.property_id, p.city, p.locality, p.size_sqft, p.bedrooms,
                       p.availability_status, pt.listing_type, pt.price
                FROM property p
                LEFT JOIN property_type pt ON p.property_id = pt.property_id
                WHERE p.locality=?
                """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, locality);

            ResultSet rs = ps.executeQuery();

            printPropertyTable(rs);

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void filterByBedrooms() {
        try {
            Connection conn = DBConnection.getConnection();

            int b = InputUtil.getPositiveInt("Enter Bedrooms");

            String query = """
                SELECT p.property_id, p.city, p.locality, p.size_sqft, p.bedrooms,
                       p.availability_status, pt.listing_type, pt.price
                FROM property p
                LEFT JOIN property_type pt ON p.property_id = pt.property_id
                WHERE p.bedrooms=?
                """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, b);

            ResultSet rs = ps.executeQuery();

            printPropertyTable(rs);

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void filterBySizeRange() {
        try {
            Connection conn = DBConnection.getConnection();

            int min = InputUtil.getPositiveInt("Min Size");
            int max = InputUtil.getPositiveInt("Max Size");

            String query = """
                SELECT p.property_id, p.city, p.locality, p.size_sqft, p.bedrooms,
                       p.availability_status, pt.listing_type, pt.price
                FROM property p
                LEFT JOIN property_type pt ON p.property_id = pt.property_id
                WHERE p.size_sqft BETWEEN ? AND ?
                """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, min);
            ps.setInt(2, max);

            ResultSet rs = ps.executeQuery();

            printPropertyTable(rs);

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void filterByAvailability() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
                SELECT p.property_id, p.city, p.locality, p.size_sqft, p.bedrooms,
                       p.availability_status, pt.listing_type, pt.price
                FROM property p
                LEFT JOIN property_type pt ON p.property_id = pt.property_id
                WHERE p.availability_status = true
                """;

            ResultSet rs = conn.createStatement().executeQuery(query);

            printPropertyTable(rs);

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    public static void countProperties() {
        try {
            Connection conn = DBConnection.getConnection();

            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT COUNT(*) FROM property");

            if (rs.next()) {
                System.out.println("Total Properties: " + rs.getInt(1));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void sortPropertiesByPrice() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = """
                    SELECT p.property_id, pt.price
                    FROM property p
                    JOIN property_type pt ON p.property_id = pt.property_id
                    ORDER BY pt.price DESC
                    """;

            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                System.out.println("Property " + rs.getInt("property_id") +
                        " → " + rs.getInt("price"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void sortPropertiesBySize() {
        try {
            Connection conn = DBConnection.getConnection();

            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT * FROM property ORDER BY size_sqft DESC");

            while (rs.next()) {
                System.out.println(
                        rs.getString("address") + " - " +
                                rs.getInt("size_sqft") + " sqft"
                );
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void checkPropertyExists() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getPositiveInt("Enter Property ID");

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM property WHERE property_id=?");
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
                System.out.println("✅ Property exists");
            else
                System.out.println("❌ Property does not exist");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public  static void printPropertyTable(ResultSet rs) throws Exception {

        List<String> headers = Arrays.asList(
                "ID", "City", "Locality", "Size", "BHK",
                "Available", "Type", "Price"
        );

        List<List<String>> rows = new ArrayList<>();

        while (rs.next()) {
            rows.add(Arrays.asList(
                    rs.getString("property_id"),
                    rs.getString("city"),
                    rs.getString("locality"),
                    rs.getString("size_sqft"),
                    rs.getString("bedrooms"),
                    rs.getString("availability_status"),
                    rs.getString("listing_type"),
                    rs.getString("price")
            ));
        }

        if (rows.isEmpty()) {
            System.out.println("❌ No results found");
        } else {
            TableUtil.printTable(headers, rows);
        }
    }

    public static void sellProperty() {
        try {
            Connection conn = DBConnection.getConnection();

            int propertyId = InputUtil.getPositiveInt("Enter Property ID");
            int buyerId = InputUtil.getPositiveInt("Enter Buyer ID");
            int sellerId = InputUtil.getPositiveInt("Enter Seller ID");
            int agentId = InputUtil.getPositiveInt("Enter Agent ID");
            int price = InputUtil.getPositiveInt("Enter Sale Price");

            // insert into sales
            PreparedStatement ps1 = conn.prepareStatement(
                    "INSERT INTO sales (sales_price, sales_date, buyer_id, seller_id, agent_id, property_id) VALUES (?, CURDATE(), ?, ?, ?, ?)"
            );

            ps1.setInt(1, price);
            ps1.setInt(2, buyerId);
            ps1.setInt(3, sellerId);
            ps1.setInt(4, agentId);
            ps1.setInt(5, propertyId);

            ps1.executeUpdate();

            // update property owner + availability
            PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE property SET availability_status=false, owner_id=? WHERE property_id=?"
            );

            ps2.setInt(1, buyerId);
            ps2.setInt(2, propertyId);
            ps2.executeUpdate();

            System.out.println("✅ Property sold successfully");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void rentProperty() {
        try {
            Connection conn = DBConnection.getConnection();

            int propertyId = InputUtil.getPositiveInt("Enter Property ID");
            int tenantId = InputUtil.getPositiveInt("Enter Tenant ID");
            int agentId = InputUtil.getPositiveInt("Enter Agent ID");
            int rentAmount = InputUtil.getPositiveInt("Enter Rent Amount");
            String startDate = InputUtil.getStringInput("Enter Start Date (YYYY-MM-DD)");
            String endDate = InputUtil.getStringInput("Enter End Date (YYYY-MM-DD)");

            // insert into rent
            PreparedStatement ps1 = conn.prepareStatement(
                    "INSERT INTO rent (rent_amount, rent_start_date, rent_end_date, tenant_id, property_id, agent_id) VALUES (?, ?, ?, ?, ?, ?)"
            );

            ps1.setInt(1, rentAmount);
            ps1.setString(2, startDate);
            ps1.setString(3, endDate);
            ps1.setInt(4, tenantId);
            ps1.setInt(5, propertyId);
            ps1.setInt(6, agentId);

            ps1.executeUpdate();

            // mark unavailable
            PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE property SET availability_status=false WHERE property_id=?"
            );

            ps2.setInt(1, propertyId);
            ps2.executeUpdate();

            System.out.println("✅ Property rented successfully");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void relistProperty() {
        try {
            Connection conn = DBConnection.getConnection();

            int propertyId = InputUtil.getPositiveInt("Enter Property ID");
            String type = InputUtil.getStringInput("Enter Listing Type (Sale/Rent)");
            int price = InputUtil.getPositiveInt("Enter Price");

            // make property available again
            PreparedStatement ps1 = conn.prepareStatement(
                    "UPDATE property SET availability_status=true WHERE property_id=?"
            );
            ps1.setInt(1, propertyId);
            ps1.executeUpdate();

            // check if listing exists
            PreparedStatement check = conn.prepareStatement(
                    "SELECT * FROM property_type WHERE property_id=? AND listing_type=?"
            );
            check.setInt(1, propertyId);
            check.setString(2, type);

            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                // update existing listing
                PreparedStatement ps2 = conn.prepareStatement(
                        "UPDATE property_type SET price=? WHERE property_id=? AND listing_type=?"
                );
                ps2.setInt(1, price);
                ps2.setInt(2, propertyId);
                ps2.setString(3, type);
                ps2.executeUpdate();

            } else {
                // insert new listing
                PreparedStatement ps3 = conn.prepareStatement(
                        "INSERT INTO property_type VALUES (?, ?, ?)"
                );
                ps3.setString(1, type);
                ps3.setInt(2, price);
                ps3.setInt(3, propertyId);
                ps3.executeUpdate();
            }

            System.out.println("✅ Property relisted successfully");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void removeListing() {
        try {
            Connection conn = DBConnection.getConnection();

            int propertyId = InputUtil.getPositiveInt("Enter Property ID");

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE property SET availability_status=false WHERE property_id=?"
            );

            ps.setInt(1, propertyId);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("✅ Property removed from listing");
            else
                System.out.println("❌ Property not found");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void updateListing() {
        try {
            Connection conn = DBConnection.getConnection();

            int propertyId = InputUtil.getPositiveInt("Enter Property ID");
            String type = InputUtil.getStringInput("Enter Listing Type (Sale/Rent)");
            int price = InputUtil.getPositiveInt("Enter New Price");

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE property_type SET price=? WHERE property_id=? AND listing_type=?"
            );

            ps.setInt(1, price);
            ps.setInt(2, propertyId);
            ps.setString(3, type);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("✅ Listing updated");
            else
                System.out.println("❌ Listing not found");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }




}