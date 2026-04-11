import java.sql.Connection;

public class DealService {

    public static void viewAllDeals() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("\n[Query] All Deals (Sales + Rent)");
            System.out.println("Fetching combined transaction data...");

            // TODO: UNION of sales + rent

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void dealsByAgent() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId;

            if (Session.role.equals("AGENT")) {
                agentId = Session.userId;
            } else {
                agentId = InputUtil.getPositiveInt("Enter Agent ID");
            } // 🔥 AUTO

            System.out.println("\n[Query] My Deals");
            System.out.println("Fetching deals handled by Agent ID: " + agentId);

            // TODO: SQL

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void dealsByDateRange() {
        try {
            Connection conn = DBConnection.getConnection();

            String start = InputUtil.getStringInput("Enter start date (YYYY-MM-DD)");
            String end = InputUtil.getStringInput("Enter end date (YYYY-MM-DD)");

            System.out.println("\n[Query] Deals by Date Range");
            System.out.println("Fetching deals between " + start + " and " + end);

            // TODO: SQL

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void totalDealValue() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("\n[Query] Total Deal Value");
            System.out.println("Calculating total revenue from all deals...");

            // TODO: SQL

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }


}