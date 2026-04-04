import java.sql.*;
import java.util.*;

public class AgentService {

    public static void addAgent() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getIntegerInput("Enter Agent ID");
            String name = InputUtil.getStringInput("Enter Name");
            String phone = InputUtil.getStringInput("Enter Phone");
            String email = InputUtil.getStringInput("Enter Email");
            int exp = InputUtil.getIntegerInput("Enter Experience (years)");
            int agencyId = InputUtil.getIntegerInput("Enter Agency ID");

            String query = "INSERT INTO agent VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setInt(5, exp);
            ps.setInt(6, agencyId);

            ps.executeUpdate();
            System.out.println("Agent added successfully");

        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void viewAgent() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT * FROM agent";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            List<String> headers = Arrays.asList(
                    "ID", "Name", "Phone", "Email", "Experience", "Agency ID"
            );

            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        String.valueOf(rs.getInt("agent_id")),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        String.valueOf(rs.getInt("experience_year")),
                        String.valueOf(rs.getInt("agency_id"))
                ));
            }

            TableUtil.printTable(headers, rows);

        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }
}