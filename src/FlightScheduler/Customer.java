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
public class Customer {
    
    private static final Connection connection = Database.getConnection();
    private static PreparedStatement customerQuery;
    private static ResultSet customerResult;
    
    public static void insertCustomer(String customer) {
        try{
            customerQuery = connection.prepareStatement("INSERT INTO Customer (Customer) VALUES (?)");
            customerQuery.setString(1,customer);
            customerQuery.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static ArrayList<String> getCustomers() {
        
        ArrayList<String> customers = new ArrayList<String>();
        try{
            customerQuery = connection.prepareStatement("SELECT CUSTOMER FROM Customer");
            customerResult = customerQuery.executeQuery();
            while (customerResult.next()) {
                customers.add(customerResult.getString("CUSTOMER"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        return customers;
    }
    
}
