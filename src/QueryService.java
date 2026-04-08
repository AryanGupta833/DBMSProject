import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QueryService {
    public static void query1() {
        try {
            Connection conn = DBConnection.getConnection();
            String q = "SELECT p.property_id, p.address, p.city, p.year_built FROM property p JOIN rent r ON p.property_id = r.property_id WHERE p.city = 'Guwahati' AND p.year_built > 2023 AND p.availability_status = true";
            PreparedStatement ps = conn.prepareStatement(q);
            ResultSet rs = ps.executeQuery();

            System.out.printf("%-10s %-25s %-15s %-10s%n","ID","Address","City","Year");
            System.out.println("----------------------------------------------------------");

            while(rs.next()){
                System.out.printf("%-10d %-25s %-15s %-10d%n",
                        rs.getInt("property_id"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getInt("year_built"));
            }
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }

    public static void query2() {
        try {
            Connection conn = DBConnection.getConnection();
            String q = "SELECT p.address FROM property p JOIN property_type pt ON p.property_id = pt.property_id WHERE pt.price BETWEEN 2000000 AND 6000000";
            PreparedStatement ps = conn.prepareStatement(q);
            ResultSet rs = ps.executeQuery();

            System.out.printf("%-30s%n","Address");
            System.out.println("----------------------------------------");

            while(rs.next()){
                System.out.printf("%-30s%n", rs.getString("address"));
            }
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }

    public static void query3() {
        try {
            Connection conn = DBConnection.getConnection();
            String q = "SELECT p.address FROM property p JOIN rent r ON p.property_id = r.property_id WHERE p.locality = 'G.S.Road' AND p.bedrooms >= 2 AND r.rent_amount < 15000";
            PreparedStatement ps = conn.prepareStatement(q);
            ResultSet rs = ps.executeQuery();

            System.out.printf("%-30s%n","Address");
            System.out.println("----------------------------------------");

            while(rs.next()){
                System.out.printf("%-30s%n", rs.getString("address"));
            }
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }

    public static void query4() {
        try {
            Connection conn = DBConnection.getConnection();
            String q = "SELECT a.name, SUM(s.sales_price) AS total_sales FROM agent a JOIN sales s ON a.agent_id = s.agent_id WHERE YEAR(s.sales_date) = 2023 GROUP BY a.name ORDER BY total_sales DESC LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(q);
            ResultSet rs = ps.executeQuery();

            System.out.printf("%-25s %-20s%n","Agent","Total Sales");
            System.out.println("---------------------------------------------");

            while(rs.next()){
                System.out.printf("%-25s %-20d%n",
                        rs.getString("name"),
                        rs.getInt("total_sales"));
            }
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }

    public static void query5() {
        try {
            Connection conn = DBConnection.getConnection();
            String q = "SELECT a.name, AVG(s.sales_price) AS avg_price, AVG(DATEDIFF(s.sales_date, p.listing_date)) AS avg_days FROM agent a JOIN sales s ON a.agent_id = s.agent_id JOIN property p ON s.property_id = p.property_id WHERE YEAR(s.sales_date) = 2018 GROUP BY a.name";
            PreparedStatement ps = conn.prepareStatement(q);
            ResultSet rs = ps.executeQuery();

            System.out.printf("%-25s %-20s %-20s%n","Agent","Avg Price","Avg Days");
            System.out.println("-------------------------------------------------------------");

            while(rs.next()){
                System.out.printf("%-25s %-20.2f %-20.2f%n",
                        rs.getString("name"),
                        rs.getDouble("avg_price"),
                        rs.getDouble("avg_days"));
            }
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }

    public static void query6() {
        try {
            Connection conn = DBConnection.getConnection();

            String q1 = "SELECT p.address, pt.price FROM property p JOIN property_type pt ON p.property_id = pt.property_id ORDER BY pt.price DESC LIMIT 1";
            PreparedStatement ps1 = conn.prepareStatement(q1);
            ResultSet rs1 = ps1.executeQuery();

            System.out.println("Most Expensive Property:");
            System.out.printf("%-30s %-15s%n","Address","Price");
            System.out.println("---------------------------------------------");

            while(rs1.next()){
                System.out.printf("%-30s %-15d%n",
                        rs1.getString("address"),
                        rs1.getInt("price"));
            }

            String q2 = "SELECT p.address, r.rent_amount FROM property p JOIN rent r ON p.property_id = r.property_id ORDER BY r.rent_amount DESC LIMIT 1";
            PreparedStatement ps2 = conn.prepareStatement(q2);
            ResultSet rs2 = ps2.executeQuery();

            System.out.println("\nHighest Rent Property:");
            System.out.printf("%-30s %-15s%n","Address","Rent");
            System.out.println("---------------------------------------------");

            while(rs2.next()){
                System.out.printf("%-30s %-15d%n",
                        rs2.getString("address"),
                        rs2.getInt("rent_amount"));
            }
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
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
}