import java.sql.*;

/**
 * AuthService — handles login for ADMIN / AGENCY / AGENT.
 * Passwords are stored as plain text in the DB.
 * ADMIN credentials remain hardcoded.
 */
public class AuthService {

    private static final String ADMIN_PASSWORD = "admin123";

    // ----------------------------------------------------------------
    // Public API
    // ----------------------------------------------------------------

    public static String login(String username, String password) {
        try {
            Connection conn = DBConnection.getConnection();

            // ── 1. ADMIN (hardcoded) ─────────────────────────────────
            if ("admin".equals(username) && ADMIN_PASSWORD.equals(password)) {
                Session.userId   = 1;
                Session.role     = "ADMIN";
                Session.agencyId = 0;
                Session.username = "Administrator";
                return "ADMIN";
            }

            // ── 2. AGENCY (enterprise table) ────────────────────────
            PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT agency_id, agency_name " +
                            "FROM enterprise " +
                            "WHERE agency_name = ? AND password_hash= ?"
            );
            ps2.setString(1, username);
            ps2.setString(2, password);
            ResultSet rs2 = ps2.executeQuery();

            if (rs2.next()) {
                Session.userId   = rs2.getInt("agency_id");
                Session.agencyId = rs2.getInt("agency_id");
                Session.role     = "AGENCY";
                Session.username = rs2.getString("agency_name");
                ps2.close();
                return "AGENCY";
            }
            ps2.close();

            // ── 3. AGENT ─────────────────────────────────────────────
            PreparedStatement ps3 = conn.prepareStatement(
                    "SELECT agent_id, agency_id, name " +
                            "FROM agent " +
                            "WHERE name = ? AND password_hash = ?"
            );
            ps3.setString(1, username);
            ps3.setString(2, password);
            ResultSet rs3 = ps3.executeQuery();

            if (rs3.next()) {
                Session.userId   = rs3.getInt("agent_id");
                Session.agencyId = rs3.getInt("agency_id");
                Session.role     = "AGENT";
                Session.username = rs3.getString("name");
                ps3.close();
                return "AGENT";
            }
            ps3.close();

        } catch (Exception e) {
            System.out.println(Color.RED + "❌ Login error: " + e.getMessage() + Color.RESET);
            e.printStackTrace();
        }

        return null; // login failed
    }

    // ----------------------------------------------------------------
    // Password utilities (public so services can call them)
    // ----------------------------------------------------------------

    /**
     * Interactive prompt: ask for a new password, confirm it, and return it.
     * Used when adding agents / agencies.
     */
    public static String promptAndHashNewPassword() {
        while (true) {
            String p1 = InputUtil.getMaskedInput("   Enter password       : ");
            String p2 = InputUtil.getMaskedInput("   Confirm password     : ");
            if (p1.equals(p2)) {
                return p1;
            }
            System.out.println(Color.RED + "   ❌ Passwords do not match. Try again." + Color.RESET);
        }
    }
}