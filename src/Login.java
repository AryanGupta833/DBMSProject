/**
 * Login — application entry point.
 *
 * Improvements over original:
 *  • Masked password input (characters hidden in real terminal)
 *  • ANSI-colored splash screen
 *  • Failed-attempt counter with lock-out after 5 tries
 *  • Session.clear() on every new login attempt (no stale state)
 *  • Logout returns here instead of exiting the JVM
 */
public class Login {

    private static final int MAX_ATTEMPTS = 5;

    public static void main(String[] args) {
        printSplash();

        int attempts = 0;

        while (true) {
            if (attempts >= MAX_ATTEMPTS) {
                System.out.println(Color.BG_RED + Color.WHITE
                        + "  🔒  Too many failed attempts. Exiting for security.  "
                        + Color.RESET);
                System.exit(1);
            }

            System.out.println(Color.BOLD + Color.BRIGHT_CYAN
                    + "\n╔══════════════════════════════╗"
                    + "\n║          USER LOGIN          ║"
                    + "\n╚══════════════════════════════╝"
                    + Color.RESET);

            if (attempts > 0) {
                System.out.println(Color.YELLOW + "  Attempt " + (attempts + 1)
                        + " of " + MAX_ATTEMPTS + Color.RESET);
            }

            System.out.print(Color.CYAN + "  Username : " + Color.RESET);
            String username = InputUtil.sc.nextLine().trim();
            if("about".equals(username)){
                ContributionUtil.showMenu();
                continue;
            }

            String password = InputUtil.getMaskedInput("  Password : ");

            Session.clear();   // BUG FIX: reset stale session before each attempt
            String role = AuthService.login(username, password);

            if (role == null) {
                attempts++;
                System.out.println(Color.RED
                        + "  ❌ Invalid credentials. Please try again."
                        + Color.RESET);
                continue;
            }

            // Successful login
            attempts = 0;
            String badge = roleBadge(role);
            System.out.println(Color.GREEN + Color.BOLD
                    + "\n  ✅ Welcome, " + Session.username + "!  " + badge
                    + Color.RESET);
            InputUtil.pressEnterToContinue();

            switch (role) {
                case "ADMIN"  -> AdministratorCLI.start();
                case "AGENCY" -> OfficeMenu.start();
                case "AGENT"  -> AgentMenu.start();

            }

            // After start() returns the user logged out — loop back to login
            Session.clear();
            printSplash();
        }
    }

    // ----------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------

    private static String roleBadge(String role) {
        return switch (role) {
            case "ADMIN"  -> Color.BG_RED    + Color.WHITE + " ADMIN "  + Color.RESET;
            case "AGENCY" -> Color.BG_BLUE   + Color.WHITE + " AGENCY " + Color.RESET;
            case "AGENT"  -> Color.BG_GREEN  + Color.WHITE + " AGENT "  + Color.RESET;
            default       -> role;
        };
    }

    private static void printSplash() {
        AdministratorCLI.clearScreen();
        System.out.println(Color.BOLD + Color.BRIGHT_CYAN);
        System.out.println("  ██████╗ ███████╗ █████╗ ██╗      ███████╗███████╗████████╗ █████╗ ████████╗███████╗");
        System.out.println("  ██╔══██╗██╔════╝██╔══██╗██║      ██╔════╝██╔════╝╚══██╔══╝██╔══██╗╚══██╔══╝██╔════╝");
        System.out.println("  ██████╔╝█████╗  ███████║██║      █████╗  ███████╗   ██║   ███████║   ██║   █████╗  ");
        System.out.println("  ██╔══██╗██╔══╝  ██╔══██║██║      ██╔══╝  ╚════██║   ██║   ██╔══██║   ██║   ██╔══╝  ");
        System.out.println("  ██║  ██║███████╗██║  ██║███████╗ ███████╗███████║   ██║   ██║  ██║   ██║   ███████╗");
        System.out.println("  ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚══════╝ ╚══════╝╚══════╝   ╚═╝   ╚═╝  ╚═╝   ╚═╝   ╚══════╝");
        System.out.println(Color.RESET);
        System.out.println(Color.BRIGHT_YELLOW
                + "                 🏠  Real Estate Management System  🏠"
                + Color.RESET);
        System.out.println(Color.DIM
                + "  ─────────────────────────────────────────────────────────────────────────────────"
                + Color.RESET);
    }
}