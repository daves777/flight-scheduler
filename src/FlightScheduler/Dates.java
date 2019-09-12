package FlightScheduler;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 *
 * @author David Lu
 */
public class Dates {
    
    private static final Connection connection = Database.getConnection();
    private static PreparedStatement dateQuery;
    private static ResultSet dateResult;
    
    public static ArrayList<String> getDates() {
        
        ArrayList<String> dates = new ArrayList<String>();
        try{
            dateQuery = connection.prepareStatement("SELECT DATE FROM Date");
            dateResult = dateQuery.executeQuery();
            while (dateResult.next()) {
                dates.add(dateResult.getDate("Date").toString());
            }
        } catch(SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        return dates;
    }
    
    public static void addDay(Date d)
    {
         try{
            Connection connection = Database.getConnection();
            PreparedStatement insertFlight = connection.prepareStatement("INSERT INTO DATE values ?"); 
            insertFlight.setDate(1, d);
            insertFlight.executeUpdate();
            
        }catch(SQLException e){ //check for invalid conversion
            e.printStackTrace();
        }
    }
}
