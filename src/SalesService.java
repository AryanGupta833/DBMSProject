import java.sql.Connection;

public class SalesService {
    public static void RecordSale(){
        try{
            Connection conn=DBConnection.getConnection();
            //query
        }
        catch (Exception e){
            System.out.println("Error "+e.getMessage());
        }
    }
    public static void viewSales(){
        try {
            Connection conn=DBConnection.getConnection();
            //query
            //table util use karlena
        }
        catch (Exception e){
            System.out.println("Error "+e.getMessage());
        }
    }
}
