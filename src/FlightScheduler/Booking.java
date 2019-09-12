package FlightScheduler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
/**
 *
 * @author David Lu
 */
public class Booking {
    
    private static final Connection connection = Database.getConnection();
    private static PreparedStatement bookQuery;
    private static ResultSet bookResult;
    private static int bookedSeats;

    
    public static void createBooking(String customer, String flight, Date date, String timestamp) {
        
        try {
            bookQuery = connection.prepareStatement("INSERT INTO BOOKING (CUSTOMER, FLIGHT, DATE, TIMESTAMP) VALUES (?,?,?,?)");
            bookQuery.setString(1, customer);
            bookQuery.setString(2, flight);
            bookQuery.setDate(3, date);
            bookQuery.setString(4, timestamp);
            bookQuery.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static int getNumBookedSeats(String flight, Date date) {
        bookedSeats = 0;
        try {
            bookQuery = connection.prepareStatement("SELECT * FROM Booking WHERE FLIGHT = ? AND DATE = ?");
            bookQuery.setString(1, flight);
            bookQuery.setDate(2, date);
            bookResult = bookQuery.executeQuery();
            while(bookResult.next()) {
                bookedSeats = bookedSeats + 1;
            }
        } catch(SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return bookedSeats;
    }
    
    public static ArrayList<FlightRecords> getBookedFlightEntries(String customer) {
        
        ArrayList<FlightRecords> bookings = new ArrayList<FlightRecords>();
        try{
            bookQuery = connection.prepareStatement("SELECT * FROM BOOKING WHERE CUSTOMER = ?");
            bookQuery.setString(1, customer);
            bookResult = bookQuery.executeQuery();
            while (bookResult.next()) {
                bookings.add(new FlightRecords(bookResult.getString("CUSTOMER"), bookResult.getString("FLIGHT"), bookResult.getDate("DATE").toString()));
            }
        } catch(SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return bookings;
    }
    
    public static ArrayList<FlightRecords> getBookedEntries(String flight, Date date) {
        
        ArrayList<FlightRecords> bookings = new ArrayList<FlightRecords>();
        try{
            bookQuery = connection.prepareStatement("SELECT * FROM BOOKING WHERE FLIGHT = ? and DATE = ?");
            bookQuery.setString(1, flight);
            bookQuery.setDate(2, date);
            bookResult = bookQuery.executeQuery();
            while (bookResult.next()) {
                bookings.add(new FlightRecords(bookResult.getString("CUSTOMER"), bookResult.getString("FLIGHT"), bookResult.getDate("DATE").toString()));
            }
        } catch(SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return bookings;
    }
    
    public static String cancelBooking(String name, String date)
    {
        Connection connection;  
        ResultSet resultSet;
        boolean insideWait=false;
        boolean insideBook=false;
        try
        {
           connection=Database.getConnection();
           PreparedStatement checkWaitList = connection.prepareStatement("SELECT CUSTOMER, FLIGHT, DATE FROM WAITLIST where CUSTOMER = ? and DATE = ?");
           checkWaitList.setString(1, name);
           checkWaitList.setDate(2, java.sql.Date.valueOf(date));           
           resultSet = checkWaitList.executeQuery();
           
           if(resultSet.next())
           {
               insideWait=true;
           }
           
           if(insideWait)
           {
                PreparedStatement removeFlight = connection.prepareStatement("DELETE FROM WAITLIST where CUSTOMER = ? and DATE = ?");
                removeFlight.setString(1, name);
                removeFlight.setDate(2, java.sql.Date.valueOf(date));
                removeFlight.executeUpdate();
                return "waitlist";
           }
           PreparedStatement checkBookings = connection.prepareStatement("SELECT CUSTOMER, DATE FROM BOOKING where CUSTOMER = ? and DATE = ?");
           checkBookings.setString(1, name);
           checkBookings.setDate(2, java.sql.Date.valueOf(date));
           
           resultSet = checkBookings.executeQuery();
           
           if(resultSet.next())
           {
               insideBook=true;
           }
           
           //not true
           if(insideBook)
           {
            PreparedStatement removeFlight = connection.prepareStatement("DELETE FROM BOOKING where CUSTOMER = ? and DATE = ?");
            removeFlight.setString(1, name);
            removeFlight.setDate(2, Date.valueOf(date));        
            
            removeFlight.executeUpdate();
            
            //NOW MOVE FROM WAITLIST TO BOOKING
            
            Statement statement = connection.createStatement();
            resultSet=statement.executeQuery("SELECT CUSTOMER, FLIGHT, DATE FROM WAITLIST order by TIMESTAMP asc");
            ResultSetMetaData metaData = resultSet.getMetaData();
            int cols = metaData.getColumnCount();
            String customerName="ERROR", flightName="ERROR";
            Date dayDate = Date.valueOf(date);
            
            if(resultSet.next())
            {
                for(int i = 1; i<=cols;i++)
                {
                    if(metaData.getColumnName(i).equalsIgnoreCase("CUSTOMER"))
                        customerName=resultSet.getObject(i).toString();
                    
                    if(metaData.getColumnName(i).equalsIgnoreCase("FLIGHT"))
                        flightName=resultSet.getObject(i).toString();
                    
                    if(metaData.getColumnName(i).equalsIgnoreCase("DATE"))
                        dayDate=Date.valueOf(resultSet.getDate(i).toString());
                }
                //delete customer from waitlist
                PreparedStatement removeWait = connection.prepareStatement("DELETE FROM WAITLIST where CUSTOMER = ? and FLIGHT = ? and DATE = ?");
                removeWait.setString(1, customerName);
                removeWait.setString(2, flightName);
                removeWait.setDate(3, java.sql.Date.valueOf(dayDate.toString()));          
                removeWait.executeUpdate();
                //move customer to bookings
                PreparedStatement insertBooking= connection.prepareStatement("INSERT INTO BOOKING (CUSTOMER, FLIGHT, DATE, TIMESTAMP) values (?,?,?,?)");
                insertBooking.setString(1, customerName);
                insertBooking.setString(2, flightName);
                insertBooking.setDate(3, dayDate);
                insertBooking.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                insertBooking.executeUpdate();   
         
            }    
             //return updated bookings!!!!
            return "bookings";
           }
        }catch(SQLIntegrityConstraintViolationException e){
                     
        }catch(SQLException e){
            e.printStackTrace();
        }
        
        return "";
    }
    public static String dropFlight(String flight)
    {
        Connection connection = Database.getConnection();
        ResultSet resultSet;
        ArrayList<String> unbooked = new ArrayList();
        int amountOriginallyBooked = 0;
        int amountBooked = 0;
        String output = "";
        int unbookcount = 0;
        
        try{
            
        //see if valid flight
            PreparedStatement checkValid = connection.prepareStatement("Select FLIGHT from FLIGHT where FLIGHT = ?");
            checkValid.setString(1, flight);
            resultSet = checkValid.executeQuery();
            
            if(!resultSet.next())
                return null;
           
            //cancel those in waitlist
            PreparedStatement emptyWaitList = connection.prepareStatement("DELETE FROM WAITLIST where FLIGHT=?");
            emptyWaitList.setString(1, flight);      
            emptyWaitList.executeUpdate();
            
            //remove the flight itself from the flights table
            PreparedStatement clearFlights = connection.prepareStatement("DELETE From FLIGHT where FLIGHT = ?");
            clearFlights.setString(1, flight);
            clearFlights.executeUpdate();
           
           //get all passengers in booking
           PreparedStatement getPassengers = connection.prepareStatement("SELECT CUSTOMER, DATE, TIMESTAMP FROM BOOKING where FLIGHT = ? order by TIMESTAMP asc");
           getPassengers.setString(1, flight);           
           resultSet = getPassengers.executeQuery();
           
           ResultSetMetaData metaData = resultSet.getMetaData();
           int cols = metaData.getColumnCount();
            
            PreparedStatement clearBookings = connection.prepareStatement("DELETE From BOOKING where FLIGHT = ?");
            clearBookings.setString(1, flight);
            clearBookings.executeUpdate();
  
            String customerName="";
            Date dayDate = Date.valueOf("1111-11-11");
            Timestamp ts = new Timestamp(System.currentTimeMillis());
                        
            ArrayList<String> customers = new ArrayList();
            ArrayList<Date> days = new ArrayList();
            
            
            while(resultSet.next()){
            //collect values into lists for priority              
                for(int i = 1; i <=cols; i++){
                    if(metaData.getColumnName(i).equalsIgnoreCase("CUSTOMER")){
                        customerName=resultSet.getObject(i).toString();
                    }
                    if(metaData.getColumnName(i).equalsIgnoreCase("DATE"))
                        dayDate=resultSet.getDate(i);           
                    }
                
                days.add(dayDate);
                customers.add(customerName);

            }
            System.out.println("customers: " + customers);
            amountOriginallyBooked = customers.size();
            
            //getting all the flights for attempted booking
            Statement statement = connection.createStatement();
            resultSet=statement.executeQuery("SELECT FLIGHT FROM FLIGHT");  
            metaData=resultSet.getMetaData();
            cols= metaData.getColumnCount();
            String flightName="";
            ArrayList<String> flightNames = new ArrayList();
            while(resultSet.next()){ 
            //collect values into lists for priority              
                for(int i = 1; i <=cols; i++){
                    if(metaData.getColumnName(i).equalsIgnoreCase("FLIGHT")){
                        flightName=resultSet.getObject(i).toString();
                    }
                }
                flightNames.add(flightName);
              }
            System.out.println("flightNames: " + flightNames);
              
         
         PreparedStatement insertBookings = connection.prepareStatement("INSERT INTO BOOKING (CUSTOMER, FLIGHT, DATE, TIMESTAMP) values (?,?,?,?)");

         for(int i=0; i<days.size();i++){
             dayDate=days.get(i);
             insertBookings.setDate(3, dayDate);
             
             //for this day, check all flights and check their seatings
             for(int j = 0; j <flightNames.size(); j++){
                 flightName=flightNames.get(j);
                 insertBookings.setString(2, flightName);
                 
                 //check occupancy
                PreparedStatement getFlightSeats = connection.prepareStatement("select count(flight) from BOOKING where FLIGHT = ? and DATE = ?"); 
                getFlightSeats.setString(1, flightName); 
                getFlightSeats.setDate(2, dayDate);            
                resultSet = getFlightSeats.executeQuery(); 
                resultSet.next(); 
                int seatsBooked = resultSet.getInt(1);
                System.out.println("seatsBooked: " + seatsBooked);
            
                PreparedStatement getMaxOccupancy = connection.prepareStatement("select SEATS from FLIGHT where FLIGHT = ?");
                getMaxOccupancy.setString(1, flightName);
                resultSet = getMaxOccupancy.executeQuery();
            
                resultSet.next();
                int maxSeats = resultSet.getInt(1);
                System.out.println("maxSeats: " + maxSeats);
                 
                if(seatsBooked<maxSeats)
                {
                    System.out.println("seatsBooked<maxSeats");
                    customerName=customers.get(i);
                    insertBookings.setString(1, customerName);
                    insertBookings.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                    insertBookings.executeUpdate();
                    //System.out.println("booking "+customerName);
                    
                    j=days.size();
                    amountBooked++;
                }    
                else          
                
                            
                if(unbookcount==flightNames.size()){
                    System.out.println(customers.get(i)+" cannot be booked");
                        unbooked.add(customers.get(i));
                }
                
             }
         }    
        }catch(SQLIntegrityConstraintViolationException e){
            
        }catch(SQLException e){
            e.printStackTrace();
        }
        unbookcount = amountOriginallyBooked - amountBooked;
        System.out.println("Unbooked arraylist:");
        System.out.println(unbooked);
        output = "Bookings and waitlist for this flight has been successfully cancelled. \n" + amountOriginallyBooked + " customer(s) booked for that flight. \n"+ amountBooked + " customer(s) booked for cancelled flight moved. \n" + unbookcount + " customer(s) unable to be booked for a flight.";
        return output;
    } 
}

