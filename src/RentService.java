import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class RentService {
    public static void recordRent(){
        try{
            Connection conn = DBConnection.getConnection();
            Scanner sc = new Scanner(System.in);

            System.out.print("Enter Rent ID: ");
            int rentId = sc.nextInt();

            System.out.print("Enter Rent Amount: ");
            int rentAmount = sc.nextInt();

            System.out.print("Enter Rent Start Date (YYYY-MM-DD): ");
            String startDate = sc.next();

            System.out.print("Enter Rent End Date (YYYY-MM-DD): ");
            String endDate = sc.next();

            System.out.print("Enter Tenant ID: ");
            int tenantId = sc.nextInt();

            System.out.print("Enter Property ID: ");
            int propertyId = sc.nextInt();

            System.out.print("Enter Agent ID: ");
            int agentId = sc.nextInt();

            String query = "INSERT INTO rent (rent_id, rent_amount, rent_start_date, rent_end_date, tenant_id, property_id, agent_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, rentId);
            ps.setInt(2, rentAmount);
            ps.setString(3, startDate);
            ps.setString(4, endDate);
            ps.setInt(5, tenantId);
            ps.setInt(6, propertyId);
            ps.setInt(7, agentId);
            ps.executeUpdate();

            String updateQuery = "UPDATE property SET availability_status = false WHERE property_id = ?";
            PreparedStatement ps2 = conn.prepareStatement(updateQuery);
            ps2.setInt(1, propertyId);
            ps2.executeUpdate();
        }
        catch (Exception e){
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void viewRent(){
        try{
            Connection conn = DBConnection.getConnection();

            String query = "SELECT r.rent_id, r.rent_amount, r.rent_start_date, r.rent_end_date, c.client_name AS tenant, a.name AS agent, p.address FROM rent r JOIN client c ON r.tenant_id = c.client_id JOIN agent a ON r.agent_id = a.agent_id JOIN property p ON r.property_id = p.property_id";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.printf("%-10s %-12s %-12s %-12s %-20s %-20s %-25s%n",
                    "ID","Amount","Start","End","Tenant","Agent","Property");

            System.out.println("-------------------------------------------------------------------------------------------");

            while(rs.next()){
                System.out.printf("%-10d %-12d %-12s %-12s %-20s %-20s %-25s%n",
                        rs.getInt("rent_id"),
                        rs.getInt("rent_amount"),
                        rs.getString("rent_start_date"),
                        rs.getString("rent_end_date"),
                        rs.getString("tenant"),
                        rs.getString("agent"),
                        rs.getString("address")
                );
            }
        }
        catch (Exception e){
            System.out.println("Error " + e.getMessage());
        }
    }
}