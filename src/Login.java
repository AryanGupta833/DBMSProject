import java.util.Scanner;

public class Login{

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== LOGIN =====");

            System.out.print("Username: ");
            String username = sc.nextLine();

            System.out.print("Password: ");//Password ma e-mail daldo
            String password = sc.nextLine();

            String role = AuthService.login(username, password);

            if (role == null) {
                System.out.println("❌ Invalid credentials");
                continue;
            }

            System.out.println("✅ Login successful as " + role);

            // 🔥 THIS IS YOUR REDIRECTION
            if (role.equals("ADMIN")) {
                AdministratorCLI.start();
            } else if (role.equals("OFFICE")) {
                OfficeMenu.start();
            } else if (role.equals("AGENT")) {
                AgentMenu.start();
            }
        }
    }
}