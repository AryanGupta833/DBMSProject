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
    public static void findRentById() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void deleteRent() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void filterRentByDate() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void filterRentByClient() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void filterRentByProperty() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void totalRentAmount() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void countRents() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void sortRentByAmount() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void sortRentByDate() {
        try {
            Connection conn = DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }
}
