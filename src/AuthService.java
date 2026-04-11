import java.sql.*;

public class AuthService {

    public static String login(String username, String password) {

        try {
            Connection conn = DBConnection.getConnection();

            // ADMIN
            PreparedStatement ps1 = conn.prepareStatement(
                    "SELECT admin_id FROM admin WHERE username=? AND password=?"//yaha par password ko email sa replace kardena
            );
            ps1.setString(1, username);
            ps1.setString(2, password);

            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                Session.userId = rs1.getInt("admin_id");
                Session.role = "ADMIN";
                return "ADMIN";
            }

            // OFFICE
            PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT office_id FROM office WHERE username=? AND password=?"//yaha par password ko email sa replace kardena
                                                                                    //Office ko agency sa replace kardena
            );
            ps2.setString(1, username);
            ps2.setString(2, password);

            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                Session.userId = rs2.getInt("office_id");
                Session.role = "OFFICE";
                return "OFFICE";
            }

            // AGENT
            PreparedStatement ps3 = conn.prepareStatement(
                    "SELECT agent_id FROM agent WHERE username=? AND password=?"//yaha par password ko email sa replace kardena
            );
            ps3.setString(1, username);
            ps3.setString(2, password);

            ResultSet rs3 = ps3.executeQuery();
            if (rs3.next()) {
                Session.userId = rs3.getInt("agent_id");
                Session.role = "AGENT";
                return "AGENT";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}