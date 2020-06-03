package stockdata;
/**
 * Creates a Candlestick that holds data for that candle 
 * represented on the chart.
 * @author Trevor Miller
 * @version - 5/7/2020
 */
public class Candlestick {
    private String date; private String time;
    private double open; private double high;
    private double low; private double close;
    private int volume;
    
    /**
     * Constructor that initializes all of its data fields
     * @param date - date in format "mm/dd/yyyy"
     * @param time - time in format "1:30 PM" and "11:00 AM"
     * @param open - double that represents price at start of candle
     * @param high - double that represents price at peak of candle
     * @param low - double that represents lowest price of candle
     * @param close - double that represents price at end of candle
     * @param volume - int that represents how many shares were traded 
     *                 during candle
     * @param ticker - ticker as a string
     */
    public Candlestick(String date, String time, 
        double open, double high, double low, 
        double close, int volume) {
        
        this.date = date;
        this.time = time;
        this.low = low;
        this.open = open;
        this.close = close;
        this.volume = volume;
        this.high = high;
    }
    
    /**
     * Gets the date in format "mm/dd/yyyy"
     * @return the date string
     */
    public String getDate() {
        return date;
    }
    /**
     * Gets the time
     * @return the time in format "1:30 PM"
     */
    public String getTime() {
        return time;
    }
    
    /**
     * Gets the open
     * @return the open price as a double
     */
    public double getOpen() {
        return open;
    }
    
    /**
     * Gets the close
     * @return the close price as a double
     */
    public double getClose() {
        return close;
    }
    
    /**
     * Gets the high
     * @return the high price as a double
     */
    public double getHigh() {
        return high;
    }
    
    /**
     * Gets the low
     * @return the low price as a double
     */
    public double getLow() {
        return low;
    }
    
    /**
     * Gets the volume
     * @return the volume as an int
     */
    public int getVolume() {
        return volume;
    }
}
