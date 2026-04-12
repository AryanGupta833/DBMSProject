import java.sql.*;
import java.util.*;

public class QueryService {

    static Scanner sc = new Scanner(System.in);

    // ---------------- QUERY 1 ----------------
    public static void query1() {
        try {
            Connection conn = DBConnection.getConnection();


            System.out.print("Enter City: ");
            String city = sc.nextLine();

            System.out.print("Enter Year Built After: ");
            int year = sc.nextInt();

            String q = """
                SELECT property_id, address, city, year_built
                FROM property
                WHERE city = ? 
                AND year_built > ?
                AND availability_status = true
            """;

            PreparedStatement ps = conn.prepareStatement(q);
            ps.setString(1, city);
            ps.setInt(2, year);

            ResultSet rs = ps.executeQuery();

            System.out.printf("%-10s %-30s %-15s %-10s%n","ID","Address","City","Year");
            System.out.println("---------------------------------------------------------------------");

            boolean found = false;

            while(rs.next()){
                found = true;
                System.out.printf("%-10d %-30s %-15s %-10d%n",
                        rs.getInt("property_id"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getInt("year_built"));
            }

            if(!found) System.out.println("No properties found.");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ---------------- QUERY 2 ----------------
    public static void query2() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Min Price: ");
            int min = sc.nextInt();

            System.out.print("Enter Max Price: ");
            int max = sc.nextInt();

            String q = """
                SELECT p.address, pt.price
                FROM property p
                JOIN property_type pt ON p.property_id = pt.property_id
                WHERE pt.price BETWEEN ? AND ?
                ORDER BY pt.price
            """;

            PreparedStatement ps = conn.prepareStatement(q);
            ps.setInt(1, min);
            ps.setInt(2, max);

            ResultSet rs = ps.executeQuery();

            System.out.printf("%-30s %-10s%n","Address","Price");
            System.out.println("----------------------------------------------");

            boolean found = false;

            while(rs.next()){
                found = true;
                System.out.printf("%-30s %-10d%n",
                        rs.getString("address"),
                        rs.getInt("price"));
            }

            if(!found) System.out.println("No properties found.");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ---------------- QUERY 3 ----------------
    public static void query3() {
        try {
            Connection conn = DBConnection.getConnection();

            sc.nextLine();
            System.out.print("Enter Locality: ");
            String locality = sc.nextLine();

            System.out.print("Min Bedrooms: ");
            int beds = sc.nextInt();

            System.out.print("Max Rent: ");
            int rent = sc.nextInt();

            String q = """
                SELECT DISTINCT p.address, p.bedrooms, r.rent_amount
                FROM property p
                JOIN rent r ON p.property_id = r.property_id
                WHERE p.locality = ?
                AND p.bedrooms >= ?
                AND r.rent_amount < ?
            """;

            PreparedStatement ps = conn.prepareStatement(q);
            ps.setString(1, locality);
            ps.setInt(2, beds);
            ps.setInt(3, rent);

            ResultSet rs = ps.executeQuery();

            System.out.printf("%-30s %-10s %-10s%n","Address","Beds","Rent");
            System.out.println("------------------------------------------------");

            boolean found = false;

            while(rs.next()){
                found = true;
                System.out.printf("%-30s %-10d %-10d%n",
                        rs.getString("address"),
                        rs.getInt("bedrooms"),
                        rs.getInt("rent_amount"));
            }

            if(!found) System.out.println("No matching rental properties.");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ---------------- QUERY 4 ----------------
    public static void query4() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Year: ");
            int year = sc.nextInt();

            String q = """
                SELECT a.name, SUM(s.sales_price) AS total_sales
                FROM agent a
                JOIN sales s ON a.agent_id = s.agent_id
                WHERE YEAR(s.sales_date) = ?
                GROUP BY a.name
                ORDER BY total_sales DESC
                LIMIT 1
            """;

            PreparedStatement ps = conn.prepareStatement(q);
            ps.setInt(1, year);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                System.out.println("Top Agent:");
                System.out.println(rs.getString("name") + " → ₹" + rs.getInt("total_sales"));
            } else {
                System.out.println("No data found.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ---------------- QUERY 5 ----------------
    public static void query5() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Year: ");
            int year = sc.nextInt();

            String q = """
                SELECT a.name, 
                       AVG(s.sales_price) AS avg_price, 
                       AVG(DATEDIFF(s.sales_date, p.listing_date)) AS avg_days
                FROM agent a
                JOIN sales s ON a.agent_id = s.agent_id
                JOIN property p ON s.property_id = p.property_id
                WHERE YEAR(s.sales_date) = ?
                GROUP BY a.name
            """;

            PreparedStatement ps = conn.prepareStatement(q);
            ps.setInt(1, year);

            ResultSet rs = ps.executeQuery();

            System.out.printf("%-25s %-15s %-15s%n","Agent","Avg Price","Avg Days");
            System.out.println("-----------------------------------------------------");

            while(rs.next()){
                System.out.printf("%-25s %-15.2f %-15.2f%n",
                        rs.getString("name"),
                        rs.getDouble("avg_price"),
                        rs.getDouble("avg_days"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ---------------- QUERY 6 ----------------
    public static void query6() {
        try {
            Connection conn = DBConnection.getConnection();

            String q1 = """
                SELECT p.address, pt.price
                FROM property p
                JOIN property_type pt ON p.property_id = pt.property_id
                ORDER BY pt.price DESC
                LIMIT 1
            """;

            ResultSet rs1 = conn.createStatement().executeQuery(q1);

            if(rs1.next()){
                System.out.println("Most Expensive Property:");
                System.out.println(rs1.getString("address") + " → ₹" + rs1.getInt("price"));
            }

            String q2 = """
                SELECT p.address, r.rent_amount
                FROM property p
                JOIN rent r ON p.property_id = r.property_id
                ORDER BY r.rent_amount DESC
                LIMIT 1
            """;

            ResultSet rs2 = conn.createStatement().executeQuery(q2);

            if(rs2.next()){
                System.out.println("\nHighest Rent Property:");
                System.out.println(rs2.getString("address") + " → ₹" + rs2.getInt("rent_amount"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void runCustomQuery() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("Enter SQL query (end with ';' on new line):");

            StringBuilder queryBuilder = new StringBuilder();

            while (true) {
                String line = InputUtil.sc.nextLine();

                if (line.trim().endsWith(";")) {
                    queryBuilder.append(line, 0, line.lastIndexOf(";"));
                    break;
                }

                queryBuilder.append(line).append("\n ");
            }

            String query = queryBuilder.toString().trim();

            Statement stmt = conn.createStatement();

            boolean hasResultSet = stmt.execute(query);

            if (hasResultSet) {
                ResultSet rs = stmt.getResultSet();

                int colCount = rs.getMetaData().getColumnCount();

                List<String> headers = new ArrayList<>();
                for (int i = 1; i <= colCount; i++) {
                    headers.add(rs.getMetaData().getColumnName(i));
                }

                List<List<String>> rows = new ArrayList<>();

                while (rs.next()) {
                    List<String> row = new ArrayList<>();
                    for (int i = 1; i <= colCount; i++) {
                        row.add(rs.getString(i));
                    }
                    rows.add(row);
                }

                if (rows.isEmpty()) {
                    System.out.println("✅ Query executed. No rows returned.");
                } else {
                    TableUtil.printTable(headers, rows);
                }

            } else {
                int affected = stmt.getUpdateCount();
                System.out.println("✅ Query executed. Rows affected: " + affected);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }


    //filter transaction by a property
    public static void transactionsByProperty() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Property ID: ");
            int id = sc.nextInt();

            System.out.println("\n--- SALES ---");

            String salesQ =
                    "SELECT s.sales_id, s.sales_price, s.sales_date, c.client_name AS buyer " +
                            "FROM sales s " +
                            "JOIN client c ON s.buyer_id = c.client_id " +
                            "WHERE s.property_id = ?";

            PreparedStatement ps1 = conn.prepareStatement(salesQ);
            ps1.setInt(1, id);

            ResultSet rs1 = ps1.executeQuery();

            while(rs1.next()){
                System.out.printf("SaleID: %d | ₹%d | %s | Buyer: %s%n",
                        rs1.getInt("sales_id"),
                        rs1.getInt("sales_price"),
                        rs1.getString("sales_date"),
                        rs1.getString("buyer"));
            }

            System.out.println("\n--- RENT ---");

            String rentQ =
                    "SELECT r.rent_id, r.rent_amount, r.rent_start_date, c.client_name AS tenant " +
                            "FROM rent r " +
                            "JOIN client c ON r.tenant_id = c.client_id " +
                            "WHERE r.property_id = ?";

            PreparedStatement ps2 = conn.prepareStatement(rentQ);
            ps2.setInt(1, id);

            ResultSet rs2 = ps2.executeQuery();

            while(rs2.next()){
                System.out.printf("RentID: %d | ₹%d | %s | Tenant: %s%n",
                        rs2.getInt("rent_id"),
                        rs2.getInt("rent_amount"),
                        rs2.getString("rent_start_date"),
                        rs2.getString("tenant"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    //ease out so we can find client fast
    public static void searchClient() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.print("Enter Client Name (partial allowed): ");
            String name = sc.next();

            String q =
                    "SELECT * FROM client WHERE client_name LIKE ?";

            PreparedStatement ps = conn.prepareStatement(q);
            ps.setString(1, "%" + name + "%");

            ResultSet rs = ps.executeQuery();

            System.out.printf("%-5s %-20s %-15s %-25s%n",
                    "ID","Name","Phone","Email");
            System.out.println("-----------------------------------------------------------");

            boolean found = false;

            while(rs.next()){
                found = true;
                System.out.printf("%-5d %-20s %-15s %-25s%n",
                        rs.getInt("client_id"),
                        rs.getString("client_name"),
                        rs.getString("client_phone"),
                        rs.getString("client_email"));
            }

            if(!found){
                System.out.println("No client found.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    //ease out on listing rule
    public static void listAvailableProperties() {
        try {
            Connection conn = DBConnection.getConnection();

            String q =
                    "SELECT property_id, address, city, bedrooms, size_sqft " +
                            "FROM property " +
                            "WHERE availability_status = true";

            ResultSet rs = conn.prepareStatement(q).executeQuery();

            System.out.printf("%-5s %-25s %-15s %-10s %-10s%n",
                    "ID","Address","City","Beds","Size");
            System.out.println("-------------------------------------------------------------");

            while(rs.next()){
                System.out.printf("%-5d %-25s %-15s %-10d %-10d%n",
                        rs.getInt("property_id"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getInt("bedrooms"),
                        rs.getInt("size_sqft"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}