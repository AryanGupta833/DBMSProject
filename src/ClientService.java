import java.sql.Connection;

public class ClientService {

    public static void addClient(){
        try{
            Connection conn=DBConnection.getConnection();
            //query likh dena
        }
        catch (Exception e){
            System.out.println("Error"+e.getMessage());
        }
    }
    public static void viewClient(){
        try{
            Connection conn=DBConnection.getConnection();
            //querylikh dena
            //tableutil use karlena print ka liye
        }
        catch (Exception e){
            System.out.println("Error"+e.getMessage());
        }
    }
    public static void assignRole(){
        try{
            Connection conn=DBConnection.getConnection();
            //queery
        }
        catch (Exception e){
            System.out.println("Error"+e.getMessage());
        }
    }
}
