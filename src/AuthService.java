import java.sql.*;
import java.security.MessageDigest;

/**
 * AuthService — handles login for ADMIN / AGENCY / AGENT.
 *
 * Passwords are stored as SHA-256 hex hashes in the DB.
 * The helper hashPassword() is also used by AgentService /
 * OfficeService when creating accounts.
 *
 * ADMIN credentials are still hardcoded (no DB row needed)
 * but the password is compared after hashing so you can
 * change ADMIN_PASS_HASH to any SHA-256 hash you like.
 */
public class AuthService {

    // SHA-256 of "admin123"  — change this constant to lock the admin account
    private static final String ADMIN_PASS_HASH =
            "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9";

    // ----------------------------------------------------------------
    // Public API
    // ----------------------------------------------------------------

    public static String login(String username, String password) {
        String hash = hashPassword(password);

        try {
            Connection conn = DBConnection.getConnection();

            // ── 1. ADMIN (hardcoded, hash-compared) ─────────────────
            if ("admin".equals(username) && ADMIN_PASS_HASH.equals(hash)) {
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
                            "WHERE agency_name = ? AND password_hash = ?"
            );
            ps2.setString(1, username);
            ps2.setString(2, hash);
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
            ps3.setString(2, hash);
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

    /** Returns the SHA-256 hex digest of the given plain-text password. */
    public static String hashPassword(String plain) {
        if (plain == null || plain.isEmpty()) return "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(plain.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /**
     * Interactive prompt: ask for a new password, confirm it,
     * and return the hash. Used when adding agents / agencies.
     */
    public static String promptAndHashNewPassword() {
        while (true) {
            String p1 = InputUtil.getMaskedInput("   Enter password       : ");
            String p2 = InputUtil.getMaskedInput("   Confirm password     : ");
            if (p1.equals(p2)) {
                return hashPassword(p1);
            }
            System.out.println(Color.RED + "   ❌ Passwords do not match. Try again." + Color.RESET);
        }
    }
}