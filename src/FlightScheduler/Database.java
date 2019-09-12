package FlightScheduler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author David Lu
 */
public class Database {
    
    private static Connection connection;
    
    public static Connection getConnection() {
        try{
            final String URL = "jdbc:derby://localhost:1527/FlightSchedulerDBDavidLudkl7";
            final String username = "java";
            final String password = "java";                
            connection=DriverManager.getConnection(URL, username, password);
            return connection;
        }
        catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
        return connection;
    }
    
}
