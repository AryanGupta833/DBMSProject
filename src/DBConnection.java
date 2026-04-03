import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    static final String url="jdbc:mysql://localhost:3306/randombullshitgo";
    static final String user="root";
    static final String password="aapkibaar400paar";
    public static Connection getConnection() throws Exception{
        return DriverManager.getConnection(url,user,password);

    }
}
