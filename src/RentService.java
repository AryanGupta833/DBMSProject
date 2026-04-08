import java.sql.*;
import java.util.Scanner;

public class RentService {

    static Scanner sc = new Scanner(System.in);

    // 🔥 COMMON JOIN QUERY (USED EVERYWHERE)
    static final String RENT_JOIN = """
        SELECT r.*, 
               c.client_name AS tenant_name,
               p.address AS property_address,
               a.name AS agent_name
        FROM rent r
        JOIN client c ON r.tenant_id = c.client_id
        JOIN property p ON r.property_id = p.property_id
        JOIN agent a ON r.agent_id = a.agent_id
    """;

    // ---------------- PRINT ----------------

    static void printHeader() {
        System.out.printf("\n%-5s %-10s %-12s %-12s %-15s %-20s %-15s%n",
                "ID","Amount","Start","End","Tenant","Property","Agent");
        System.out.println("----------------------------------------------------------------------------------------");
    }

    static void printRow(ResultSet rs) throws Exception {
        System.out.printf("%-5d %-10d %-12s %-12s %-15s %-20s %-15s%n",
                rs.getInt("rent_id"),
                rs.getInt("rent_amount"),
                rs.getString("rent_start_date"),
                rs.getString("rent_end_date"),
                rs.getString("tenant_name"),
                rs.getString("property_address"),
                rs.getString("agent_name"));
    }

    // ---------------- CREATE ----------------

    public static void recordRent(){
        try{
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Rent ID: ");
            int rentId = sc.nextInt();

            System.out.print("Enter Rent Amount: ");
            int rentAmount = sc.nextInt();

            System.out.print("Enter Start Date (YYYY-MM-DD): ");
            Date startDate = Date.valueOf(sc.next());

            System.out.print("Enter End Date (YYYY-MM-DD): ");
            Date endDate = Date.valueOf(sc.next());

            System.out.print("Enter Tenant ID: ");
            int tenantId = sc.nextInt();

            System.out.print("Enter Property ID: ");
            int propertyId = sc.nextInt();

            System.out.print("Enter Agent ID: ");
            int agentId = sc.nextInt();

            // ✅ Check availability
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

            // ✅ Insert rent (Trigger will handle availability)
            String q = "INSERT INTO rent VALUES (?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(q);

            ps.setInt(1, rentId);
            ps.setInt(2, rentAmount);
            ps.setDate(3, startDate);
            ps.setDate(4, endDate);
            ps.setInt(5, tenantId);
            ps.setInt(6, propertyId);
            ps.setInt(7, agentId);

            ps.executeUpdate();

            System.out.println("✅ Rent Recorded Successfully");

        } catch (Exception e){
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ---------------- READ ----------------

    public static void viewRent(){
        try{
            Connection conn = DBConnection.getConnection();
            String q = RENT_JOIN;

            ResultSet rs = conn.createStatement().executeQuery(q);

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
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void findRentById() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Rent ID: ");
            int id = sc.nextInt();

            String q = RENT_JOIN + " WHERE r.rent_id=?";
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            printHeader();

            if (rs.next()) {
                printRow(rs);
            } else {
                System.out.println("❌ No rent found with ID " + id);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ---------------- DELETE ----------------

    public static void deleteRent() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Rent ID to delete: ");
            int id = sc.nextInt();

            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM rent WHERE rent_id=?"
            );
            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Rent deleted");
            } else {
                System.out.println("❌ ID not found");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ---------------- FILTER ----------------

    public static void filterRentByDate() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Start Date (YYYY-MM-DD): ");
            Date date = Date.valueOf(sc.next());

            String q = RENT_JOIN + " WHERE r.rent_start_date >= ?";
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setDate(1, date);

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
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void filterRentByProperty() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Property ID: ");
            int id = sc.nextInt();

            String q = RENT_JOIN + " WHERE r.property_id=?";
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
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void filterRentByClient() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Client ID: ");
            int clientId = sc.nextInt();

            String q = RENT_JOIN + " WHERE r.tenant_id=? ORDER BY r.rent_start_date DESC";
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setInt(1, clientId);

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
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void rentByDateRange() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Start Date: ");
            Date start = Date.valueOf(sc.next());

            System.out.print("Enter End Date: ");
            Date end = Date.valueOf(sc.next());

            String q = RENT_JOIN + " WHERE r.rent_start_date BETWEEN ? AND ?";
            PreparedStatement ps = conn.prepareStatement(q);

            ps.setDate(1, start);
            ps.setDate(2, end);

            ResultSet rs = ps.executeQuery();

            printHeader();

            while (rs.next()) {
                printRow(rs);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ---------------- ANALYTICS ----------------

    public static void totalRentAmount() {
        try {
            Connection conn = DBConnection.getConnection();

            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT SUM(rent_amount) AS total FROM rent");

            if(rs.next()){
                System.out.println("Total Rent: ₹" + rs.getInt("total"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void countRents() {
        try {
            Connection conn = DBConnection.getConnection();

            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT COUNT(*) AS total FROM rent");

            if(rs.next()){
                System.out.println("Total Records: " + rs.getInt("total"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void sortRentByAmount() {
        try {
            Connection conn = DBConnection.getConnection();

            String q = RENT_JOIN + " ORDER BY r.rent_amount DESC";
            ResultSet rs = conn.createStatement().executeQuery(q);

            printHeader();

            while(rs.next()){
                printRow(rs);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void sortRentByDate() {
        try {
            Connection conn = DBConnection.getConnection();

            String q = RENT_JOIN + " ORDER BY r.rent_start_date DESC";
            ResultSet rs = conn.createStatement().executeQuery(q);

            printHeader();

            while(rs.next()){
                printRow(rs);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
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
}