import java.net.ConnectException;
import java.sql.Connection;

public class QueryService {
    public static void query1() {
        try {
            Connection conn = DBConnection.getConnection();
            //query1

        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }
    public static void query2() {
        try {
            Connection conn = DBConnection.getConnection();
            //query2
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }
    public static void query3() {
        try {
            Connection conn = DBConnection.getConnection();
            //query3
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }
    public static void query4() {
        try {
            Connection conn = DBConnection.getConnection();
            //query4
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }
    public static void query5() {
        try {
            Connection conn = DBConnection.getConnection();
            //query5
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }
    public static void query6() {
        try {
            Connection conn = DBConnection.getConnection();
            //query6
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }

    }

