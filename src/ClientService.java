import java.sql.*;
import java.util.*;

public class ClientService {

    public static void addClient() {
        try {
            Connection conn = DBConnection.getConnection();

            int id = InputUtil.getIntegerInput("Enter Client ID");
            String name = InputUtil.getStringInput("Enter Name");
            String phone = InputUtil.getStringInput("Enter Phone");
            String email = InputUtil.getStringInput("Enter Email");
            String address = InputUtil.getStringInput("Enter Address");

            String query = "INSERT INTO client VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setString(5, address);

            ps.executeUpdate();
            System.out.println("Client added successfully");

        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void viewClient() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT * FROM client";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

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

            TableUtil.printTable(headers, rows);

        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void assignRole() {
        try {
            Connection conn = DBConnection.getConnection();

            int clientId = InputUtil.getIntegerInput("Enter Client ID");
            String role = InputUtil.getStringInput("Enter Role");

            String query = "INSERT INTO client_role VALUES (?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, clientId);
            ps.setString(2, role);

            ps.executeUpdate();
            System.out.println("Role assigned successfully");

        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }
}