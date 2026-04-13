import java.sql.*;
import java.util.*;

public class DealService {

    public static void viewAllDeals() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("\n[Query] All Deals (Sales + Rent)");
            System.out.println("Fetching combined transaction data...\n");

            String query = """
                SELECT 'Sale' as deal_type, sales_id as deal_id, property_id, 
                       sales_date as deal_date, sales_price as amount, agent_id
                FROM sales
                UNION ALL
                SELECT 'Rent' as deal_type, rent_id as deal_id, property_id, 
                       rent_start_date as deal_date, rent_amount as amount, agent_id
                FROM rent
                ORDER BY deal_date DESC
            """;

            ResultSet rs = conn.createStatement().executeQuery(query);

            List<String> headers = Arrays.asList("Type", "Deal ID", "Property ID", "Date", "Amount", "Agent ID");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        rs.getString("deal_type"),
                        String.valueOf(rs.getInt("deal_id")),
                        String.valueOf(rs.getInt("property_id")),
                        rs.getString("deal_date"),
                        "₹" + String.format("%,d", rs.getLong("amount")),
                        String.valueOf(rs.getInt("agent_id"))
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No deals found.");
            } else {
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void dealsByAgent() {
        try {
            Connection conn = DBConnection.getConnection();

            int agentId;

            if ("AGENT".equals(Session.role)) {
                agentId = Session.userId;
            } else {
                agentId = InputUtil.getPositiveInt("Enter Agent ID");
            } // 🔥 AUTO

            System.out.println("\n[Query] My Deals");
            System.out.println("Fetching deals handled by Agent ID: " + agentId + "\n");

            String query = """
                SELECT 'Sale' as deal_type, sales_id as deal_id, property_id, 
                       sales_date as deal_date, sales_price as amount
                FROM sales WHERE agent_id = ?
                UNION ALL
                SELECT 'Rent' as deal_type, rent_id as deal_id, property_id, 
                       rent_start_date as deal_date, rent_amount as amount
                FROM rent WHERE agent_id = ?
                ORDER BY deal_date DESC
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, agentId);
            ps.setInt(2, agentId);

            ResultSet rs = ps.executeQuery();

            List<String> headers = Arrays.asList("Type", "Deal ID", "Property ID", "Date", "Amount");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        rs.getString("deal_type"),
                        String.valueOf(rs.getInt("deal_id")),
                        String.valueOf(rs.getInt("property_id")),
                        rs.getString("deal_date"),
                        "₹" + String.format("%,d", rs.getLong("amount"))
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No deals found for Agent ID: " + agentId);
            } else {
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }

    public static void dealsByDateRange() {
        try {
            Connection conn = DBConnection.getConnection();

            String start = InputUtil.getStringInput("Enter start date (YYYY-MM-DD)");
            String end = InputUtil.getStringInput("Enter end date (YYYY-MM-DD)");

            System.out.println("\n[Query] Deals by Date Range");

            String roleFilter = "";
            if ("AGENT".equals(Session.role)) {
                System.out.println("Fetching your specific deals between " + start + " and " + end + "\n");
                roleFilter = " AND agent_id = " + Session.userId;
            } else if ("AGENCY".equals(Session.role)) {
                System.out.println("Fetching deals for your agency between " + start + " and " + end + "\n");
                // Join with agent table to filter by agency_id
                roleFilter = " AND agent_id IN (SELECT agent_id FROM agent WHERE agency_id = " + Session.agencyId + ")";
            } else {
                System.out.println("Fetching all deals between " + start + " and " + end + "\n");
            }

            String query = "SELECT 'Sale' as deal_type, sales_id as deal_id, property_id, " +
                    "sales_date as deal_date, sales_price as amount, agent_id " +
                    "FROM sales WHERE (sales_date BETWEEN ? AND ?)" + roleFilter +
                    " UNION ALL " +
                    "SELECT 'Rent' as deal_type, rent_id as deal_id, property_id, " +
                    "rent_start_date as deal_date, rent_amount as amount, agent_id " +
                    "FROM rent WHERE (rent_start_date BETWEEN ? AND ?)" + roleFilter +
                    " ORDER BY deal_date DESC";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, start);
            ps.setString(2, end);
            ps.setString(3, start);
            ps.setString(4, end);

            ResultSet rs = ps.executeQuery();

            List<String> headers = Arrays.asList("Type", "Deal ID", "Property ID", "Date", "Amount", "Agent ID");
            List<List<String>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(Arrays.asList(
                        rs.getString("deal_type"),
                        String.valueOf(rs.getInt("deal_id")),
                        String.valueOf(rs.getInt("property_id")),
                        rs.getString("deal_date"),
                        "₹" + String.format("%,d", rs.getLong("amount")),
                        String.valueOf(rs.getInt("agent_id"))
                ));
            }

            if (rows.isEmpty()) {
                System.out.println("❌ No deals found in the specified date range for your access level.");
            } else {
                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }
    public static void totalDealValue() {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println("\n[Query] Total Deal Value");
            System.out.println("Calculating total revenue from all deals...\n");

            String query = """
                SELECT 
                    (SELECT COALESCE(SUM(sales_price), 0) FROM sales) as total_sales_revenue,
                    (SELECT COALESCE(SUM(rent_amount), 0) FROM rent) as total_rent_revenue
            """;

            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next()) {
                long salesRevenue = rs.getLong("total_sales_revenue");
                long rentRevenue = rs.getLong("total_rent_revenue");
                long totalRevenue = salesRevenue + rentRevenue;

                List<String> headers = Arrays.asList("Category", "Total Revenue");
                List<List<String>> rows = new ArrayList<>();

                rows.add(Arrays.asList("Total Sales", "₹" + String.format("%,d", salesRevenue)));
                rows.add(Arrays.asList("Total Rentals", "₹" + String.format("%,d", rentRevenue)));
                rows.add(Arrays.asList("GRAND TOTAL", "₹" + String.format("%,d", totalRevenue)));

                TableUtil.printTable(headers, rows);
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
    }
}