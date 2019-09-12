package FlightScheduler;

/**
 *
 * @author David Lu
 */
public class FlightRecords
{
    private String customer;
    private String flightNumber;
    private String date;
    private String timestamp;
        
    public FlightRecords(String name, String number, String date)
    {
        this.customer = name;
        this.flightNumber = number;
        this.date = date;
        this.timestamp = "";
    }
        
    public FlightRecords(String customer, String flight, String date, String timestamp)
    {
        this.customer = customer;
        this.flightNumber = flight;
        this.date = date;
        this.timestamp = timestamp;
    }

    public String getCustomer()
    {
        return customer;
    }

    public String getFlightNumber()
    {
        return flightNumber;
    }

    public String getDate()
    {
        return date;
    }

    public void setCustomer(String customer)
    {
        this.customer = customer;
    }

    public void setFlight(String flight)
    {
        this.flightNumber = flight;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString()
    {
        return getCustomer() + "\t" + getFlightNumber() + "\t" + getDate() + "\t" + getTimestamp() + "\n";
    }
    
}


