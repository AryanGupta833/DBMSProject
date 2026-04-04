import java.sql.*;
import java.util.*;

public class PropertyService {

    public static void addProperty() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getIntegerInput("Enter Property ID");
            String address = InputUtil.getStringInput("Enter Address");
            String city = InputUtil.getStringInput("Enter City");
            String locality = InputUtil.getStringInput("Enter Locality");
            int size = InputUtil.getIntegerInput("Enter Size (sqft)");
            int bedrooms = InputUtil.getIntegerInput("Enter Bedrooms");
            int year = InputUtil.getIntegerInput("Enter Year Built");
            int agentId = InputUtil.getIntegerInput("Enter Agent ID");
            int ownerId = InputUtil.getIntegerInput("Enter Owner ID");

            String query = "INSERT INTO property VALUES (?, ?, ?, ?, ?, ?, ?, true, CURDATE(), ?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);
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
            System.out.println("Property added successfully");

        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void viewProperties() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT * FROM property";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            List<String> headers = Arrays.asList(
                    "ID", "Address", "City", "Locality",
                    "Size", "Bedrooms", "Year", "Available",
                    "Date", "Agent", "Owner"
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
                        String.valueOf(rs.getBoolean("availability_status")),
                        rs.getString("listing_date"),
                        String.valueOf(rs.getInt("agent_id")),
                        String.valueOf(rs.getInt("owner_id"))
                ));
            }

            TableUtil.printTable(headers, rows);

        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void updateAvailability() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getIntegerInput("Enter Property ID");
            int status = InputUtil.getIntegerInput("Enter 1 (Available) / 0 (Not Available)");

            String query = "UPDATE property SET availability_status=? WHERE property_id=?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setBoolean(1, status == 1);
            ps.setInt(2, id);

            ps.executeUpdate();
            System.out.println("Updated successfully");

        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void findPropertyById() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void updateProperty() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void deleteProperty() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void searchPropertyByCity() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void searchPropertyByLocality() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void filterByBedrooms() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void filterBySizeRange() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void filterByAvailability() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void countProperties() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void sortPropertiesByPrice() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void sortPropertiesBySize() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void checkPropertyExists() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }


}