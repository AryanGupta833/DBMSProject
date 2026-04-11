import java.sql.*;

public class AuthService {

    public static String login(String username, String password) {

        try {
            Connection conn = DBConnection.getConnection();

            // 🔥 1. ADMIN (HARDCODED)
            if ("admin".equals(username) && "admin123".equals(password)) {
                Session.userId = 1;        // dummy ID
                Session.role = "ADMIN";
                Session.agencyId = 0;      // not needed for admin
                return "ADMIN";
            }

            // 🔥 2. OFFICE (AGENCY LOGIN)
            PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT agency_id FROM enterprise WHERE agency_name=? AND agency_email=?"
            );

            ps2.setString(1, username);
            ps2.setString(2, password);

            ResultSet rs2 = ps2.executeQuery();

            if (rs2.next()) {
                Session.userId = rs2.getInt("agency_id"); // agency acts as office
                Session.agencyId = rs2.getInt("agency_id"); // 🔥 same
                Session.role = "AGENCY";
                return "AGENCY";
            }

            // 🔥 3. AGENT (LOGIN USING NAME + EMAIL)
            PreparedStatement ps3 = conn.prepareStatement(
                    "SELECT agent_id, agency_id FROM agent WHERE name=? AND email=?"
            );

            ps3.setString(1, username);
            ps3.setString(2, password);

            ResultSet rs3 = ps3.executeQuery();

            if (rs3.next()) {
                Session.userId = rs3.getInt("agent_id");
                Session.agencyId = rs3.getInt("agency_id"); // 🔥 important for filtering
                Session.role = "AGENT";
                return "AGENT";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // ❌ login failed
    }
}