import java.sql.Connection;

public class RentService {
    public static void recordRent(){
        try{
            Connection conn=DBConnection.getConnection();
            //queey
        }
        catch (Exception e){
            System.out.println("Error "+e.getMessage());
        }
    }
    public static void viewRent(){
        try{
            Connection conn=DBConnection.getConnection();
            //quuuuery
            //you know kya use karna h

        }
        catch (Exception e){
            System.out.println("Error "+e.getMessage());

        }
    }
}
