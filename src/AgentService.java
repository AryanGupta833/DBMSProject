import java.sql.Connection;

public class AgentService {

    public static void addAgent(){
        try{
            Connection conn=DBConnection.getConnection();
            //querrrry
        }
        catch (Exception e){
            System.out.println("Error"+e.getMessage());
        }
    }
    public static void viewAgent(){
        try{
            Connection conn=DBConnection.getConnection();
            //qqqueyr
            //table util use karlena
        }
        catch (Exception e){
            System.out.println("Error"+e.getMessage());
        }
    }
}
