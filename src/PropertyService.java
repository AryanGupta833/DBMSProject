import java.sql.Connection;

public class PropertyService {
    public static void addProperty(){
        try{
            Connection conn=DBConnection.getConnection();
            //queery
        }
        catch (Exception e){
            System.out.println("Error "+e.getMessage());
        }
    }
    public static void viewProperties(){
        try{
            Connection conn=DBConnection.getConnection();
            //quuuery
            //table util use karlena
        }
        catch (Exception e){
            System.out.println("Error "+e.getMessage());
        }
    }
    public static void updateAvailability(){
        try{
            Connection conn=DBConnection.getConnection();
            //queery
        }
        catch (Exception e){
            System.out.println("Error "+e.getMessage());
        }
    }
}
