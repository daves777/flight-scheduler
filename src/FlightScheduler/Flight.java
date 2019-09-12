package FlightScheduler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 *
 * @author David Lu
 */
public class Flight {
    
    private static final Connection connection = Database.getConnection();
    private static PreparedStatement flightQuery;
    private static ResultSet flightResult;
    
    public static void createFlight(String flight, int seats ) {
        
        try {
            flightQuery = connection.prepareStatement("INSERT INTO Flight (FLIGHT, SEATS) VALUES (?,?)");
            flightQuery.setString(1, flight);
            flightQuery.setInt(2, seats);
            flightQuery.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static ArrayList<String> getFlights() {
        
        ArrayList<String> flights = new ArrayList<String>();
        try{
            flightQuery = connection.prepareStatement("SELECT FLIGHT FROM Flight");
            flightResult = flightQuery.executeQuery();
            while (flightResult.next()) {
                flights.add(flightResult.getString("FLIGHT"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        return flights;
    }
    
    public static int getSeats(String flight) {
        int flightSeats = 0;
        try{
            flightQuery = connection.prepareStatement("SELECT SEATS FROM Flight WHERE FLIGHT = ?");
            flightQuery.setString(1, flight);
            flightResult = flightQuery.executeQuery();
            flightResult.next();
            flightSeats = flightResult.getInt("SEATS");
        } catch(SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        return flightSeats;
    }   
}
