public class Session {
    public static int    userId;
    public static String role;
    public static int    agencyId;
    public static String username;   // display name shown in header

    /** Call on logout so a fresh login starts clean. */
    public static void clear() {
        userId   = 0;
        role     = null;
        agencyId = 0;
        username = null;
    }
}