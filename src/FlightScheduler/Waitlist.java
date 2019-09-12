package FlightScheduler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import java.sql.Timestamp;
/**
 *
 * @author David Lu
 * 
 */
public class Waitlist {
    
    private static final Connection connection = Database.getConnection();
    private static PreparedStatement waitlistQuery;
    private static ResultSet waitlistResult;
    
    public static void createWaitList(String customer, String flight, Date date, Timestamp timestamp) {
        
        try {
            waitlistQuery = connection.prepareStatement("INSERT INTO WAITLIST (CUSTOMER, FLIGHT, DATE, TIMESTAMP) VALUES (?,?,?,?)");
            waitlistQuery.setString(1, customer);
            waitlistQuery.setString(2, flight);
            waitlistQuery.setDate(3, date);
            waitlistQuery.setTimestamp(4, timestamp);
            waitlistQuery.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
   
    public static ArrayList<FlightRecords> getWaitEntries(String flight, Date date) {
        
        ArrayList<FlightRecords> waitlisted = new ArrayList<FlightRecords>();
        try{
            waitlistQuery = connection.prepareStatement("SELECT * FROM WAITLIST WHERE FLIGHT = ? and DATE = ?");
            waitlistQuery.setString(1, flight);
            waitlistQuery.setDate(2, date);
            waitlistResult = waitlistQuery.executeQuery();
            while (waitlistResult.next()) {
                waitlisted.add(new FlightRecords(waitlistResult.getString("CUSTOMER"), waitlistResult.getString("FLIGHT"), waitlistResult.getDate("DATE").toString(), waitlistResult.getTimestamp("TIMESTAMP").toString()));
            }
        } catch(SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return waitlisted;
    }
    
     public static ArrayList<FlightRecords> getWaitEntries(String customer) {
        
        ArrayList<FlightRecords> waitlisted = new ArrayList<FlightRecords>();
        try{
            waitlistQuery = connection.prepareStatement("SELECT * FROM WAITLIST WHERE CUSTOMER = ?");
            waitlistQuery.setString(1, customer);
            waitlistResult = waitlistQuery.executeQuery();
            while (waitlistResult.next()) {
                waitlisted.add(new FlightRecords(waitlistResult.getString("CUSTOMER"), waitlistResult.getString("FLIGHT"), waitlistResult.getDate("DATE").toString()));
            }
        } catch(SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return waitlisted;
    }
}