import java.sql.*;
import java.util.Scanner;

public class RentService {
    static Scanner sc = new Scanner(System.in);

    static void printHeader() {
        System.out.printf("\n%-8s %-12s %-12s %-12s %-10s %-12s %-10s%n",
                "ID","Amount","Start","End","Tenant","Property","Agent");
        System.out.println("--------------------------------------------------------------------------");
    }

    static void printRow(ResultSet rs) throws Exception {
        System.out.printf("%-8d %-12d %-12s %-12s %-10d %-12d %-10d%n",
                rs.getInt("rent_id"),
                rs.getInt("rent_amount"),
                rs.getString("rent_start_date"),
                rs.getString("rent_end_date"),
                rs.getInt("tenant_id"),
                rs.getInt("property_id"),
                rs.getInt("agent_id"));
    }

    public static void recordRent(){
        try{
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Rent ID: ");
            int rentId = sc.nextInt();

            System.out.print("Enter Rent Amount: ");
            int rentAmount = sc.nextInt();

            System.out.print("Enter Start Date (YYYY-MM-DD): ");
            String startDate = sc.next();

            System.out.print("Enter End Date (YYYY-MM-DD): ");
            String endDate = sc.next();

            System.out.print("Enter Tenant ID: ");
            int tenantId = sc.nextInt();

            System.out.print("Enter Property ID: ");
            int propertyId = sc.nextInt();

            System.out.print("Enter Agent ID: ");
            int agentId = sc.nextInt();

            // 🔥 Check availability
            PreparedStatement check = conn.prepareStatement(
                    "SELECT availability_status FROM property WHERE property_id=?"
            );
            check.setInt(1, propertyId);

            ResultSet rs = check.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Property not found");
                return;
            }

            if (!rs.getBoolean("availability_status")) {
                System.out.println("❌ Property already occupied");
                return;
            }

            // insert rent
            String q = "INSERT INTO rent VALUES (?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(q);

            ps.setInt(1, rentId);
            ps.setInt(2, rentAmount);
            ps.setString(3, startDate);
            ps.setString(4, endDate);
            ps.setInt(5, tenantId);
            ps.setInt(6, propertyId);
            ps.setInt(7, agentId);

            ps.executeUpdate();

            // 🔥 mark unavailable
            PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE property SET availability_status=false WHERE property_id=?"
            );
            ps2.setInt(1, propertyId);
            ps2.executeUpdate();

            System.out.println("✅ Rent Recorded Successfully");

        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void viewRent(){
        try{
            Connection conn = DBConnection.getConnection();
            String q = "SELECT * FROM rent";

            PreparedStatement ps = conn.prepareStatement(q);
            ResultSet rs = ps.executeQuery();

            printHeader();

            boolean found = false;
            while(rs.next()){
                printRow(rs);
                found = true;
            }

            if(!found){
                System.out.println("No records found.");
            }

        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void findRentById() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Rent ID: ");
            int id = sc.nextInt();

            String query = "SELECT * FROM rent WHERE rent_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            printHeader();

            if (rs.next()) {
                printRow(rs);
            } else {
                System.out.println("❌ No rent found with ID " + id);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void deleteRent() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Rent ID to delete: ");
            int id = sc.nextInt();

            // get property id
            PreparedStatement ps1 = conn.prepareStatement(
                    "SELECT property_id FROM rent WHERE rent_id=?"
            );
            ps1.setInt(1, id);

            ResultSet rs = ps1.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ ID not found");
                return;
            }

            int propertyId = rs.getInt("property_id");

            // delete
            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM rent WHERE rent_id=?"
            );
            ps.setInt(1, id);
            ps.executeUpdate();

            // 🔥 restore availability
            PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE property SET availability_status=true WHERE property_id=?"
            );
            ps2.setInt(1, propertyId);
            ps2.executeUpdate();

            System.out.println("✅ Rent deleted and property available");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void filterRentByDate() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Start Date (YYYY-MM-DD): ");
            String date = sc.next();

            String q = "SELECT * FROM rent WHERE rent_start_date >= ?";
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setString(1, date);

            ResultSet rs = ps.executeQuery();

            printHeader();

            boolean found = false;
            while(rs.next()){
                printRow(rs);
                found = true;
            }

            if(!found){
                System.out.println("No records found.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void filterRentByClient() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Tenant ID: ");
            int id = sc.nextInt();

            String q = "SELECT * FROM rent WHERE tenant_id=?";
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            printHeader();

            boolean found = false;
            while(rs.next()){
                printRow(rs);
                found = true;
            }

            if(!found){
                System.out.println("No records found.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void filterRentByProperty() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Property ID: ");
            int id = sc.nextInt();

            String q = "SELECT * FROM rent WHERE property_id=?";
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            printHeader();

            boolean found = false;
            while(rs.next()){
                printRow(rs);
                found = true;
            }

            if(!found){
                System.out.println("No records found.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void totalRentAmount() {
        try {
            Connection conn = DBConnection.getConnection();

            String q = "SELECT SUM(rent_amount) AS total FROM rent";
            ResultSet rs = conn.prepareStatement(q).executeQuery();

            if(rs.next()){
                System.out.println("Total Rent: " + rs.getInt("total"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void countRents() {
        try {
            Connection conn = DBConnection.getConnection();

            String q = "SELECT COUNT(*) AS total FROM rent";
            ResultSet rs = conn.prepareStatement(q).executeQuery();

            if(rs.next()){
                System.out.println("Total Records: " + rs.getInt("total"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void sortRentByAmount() {
        try {
            Connection conn = DBConnection.getConnection();

            String q = "SELECT * FROM rent ORDER BY rent_amount DESC";
            ResultSet rs = conn.prepareStatement(q).executeQuery();

            printHeader();

            while(rs.next()){
                printRow(rs);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void sortRentByDate() {
        try {
            Connection conn = DBConnection.getConnection();

            String q = "SELECT * FROM rent ORDER BY rent_start_date DESC";
            ResultSet rs = conn.prepareStatement(q).executeQuery();

            printHeader();

            while(rs.next()){
                printRow(rs);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void rentSummary() {
        try {
            Connection conn = DBConnection.getConnection();

            String q = """
            SELECT COUNT(*) AS total,
                   SUM(rent_amount) AS total_rent,
                   AVG(rent_amount) AS avg_rent
            FROM rent
        """;

            ResultSet rs = conn.createStatement().executeQuery(q);

            if (rs.next()) {
                System.out.println("\n--- RENT SUMMARY ---");
                System.out.println("Total Rentals: " + rs.getInt("total"));
                System.out.println("Total Rent Collected: ₹" + rs.getInt("total_rent"));
                System.out.println("Average Rent: ₹" + rs.getInt("avg_rent"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    public static void topTenant() {
        try {
            Connection conn = DBConnection.getConnection();

            String q = """
            SELECT tenant_id, COUNT(*) AS total
            FROM rent
            GROUP BY tenant_id
            ORDER BY total DESC
            LIMIT 1
        """;

            ResultSet rs = conn.createStatement().executeQuery(q);

            if (rs.next()) {
                int id = rs.getInt("tenant_id");
                int total = rs.getInt("total");

                PreparedStatement ps = conn.prepareStatement(
                        "SELECT client_name FROM client WHERE client_id=?"
                );
                ps.setInt(1, id);

                ResultSet rs2 = ps.executeQuery();

                if (rs2.next()) {
                    System.out.println("🏆 Top Tenant: " +
                            rs2.getString("client_name") +
                            " (" + total + " rentals)");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    public static void rentByDateRange() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Start Date: ");
            String start = sc.next();

            System.out.print("Enter End Date: ");
            String end = sc.next();

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM rent WHERE rent_start_date BETWEEN ? AND ?"
            );

            ps.setString(1, start);
            ps.setString(2, end);

            ResultSet rs = ps.executeQuery();

            printHeader();

            while (rs.next()) {
                printRow(rs);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}