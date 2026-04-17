
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    static final String url="jdbc:mysql://localhost:3306/project3";
    static final String user="root";
    static final String password="imbatman";

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }
}