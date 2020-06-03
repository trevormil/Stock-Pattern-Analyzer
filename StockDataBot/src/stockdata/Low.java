package stockdata;
/**
 * Creates a class that stores data for a new LOD on the chart.
 * @author Trevor Miller
 * @version 05-14-2020
 */
public class Low {
    private String highTime;
    private double highPrice;
    private int highVolume;
    private String lowTime;
    private double lowPrice;
    private int lowVolume;
    private String brDownTime;
    private int brDownVolume;
    
    /**
     * Creates a new Low object
     * @param lowTime - time the low occurred at
     * @param lowPrice - price the low occurred at
     * @param lowVolume - volume the low occurred at
     */
    public Low(String lowTime, double lowPrice, int lowVolume) {
        this.lowTime = lowTime;
        this.lowPrice = lowPrice;
        this.lowVolume = lowVolume;
        brDownVolume = 0;
        brDownTime = null;
        highVolume = 0;
        highTime = null;
        highPrice = Integer.MIN_VALUE;
    }
    
    /**
     * Getter method for the low time
     * @return the low time in format "2:30 PM"
     */
    public String getLowTime() {
        return lowTime;
    }
    
    /**
     * Getter method for the high time after low 
     * and before breakdown (if there is one)
     * @return the high time after low in format "2:30 PM"
     */
    public String getHighTime() {
        return highTime;
    }
    
    /**
     * Getter method for the high price after low 
     * and before breakdown (if there is one)
     * @return the double the high after low occurred at
     */
    public double getHighPrice() {
        return highPrice;
    }
    
    /**
     * Getter method for the high volume after low 
     * and before breakdown (if there is one)
     * @return the volume as an int
     */
    public int getHighVolume() {
        return highVolume;
    }
    
    /**
     * Getter method for the low volume
     * @return the volume as an int
     */
    public int getLowVolume() {
        return lowVolume;
    }
    
    /**
     * Getter method for the low price
     * @return the double the low occurred at
     */
    public double getLowPrice() {
        return lowPrice;
    }
    
    /**
     * Getter method for the time of breakdown
     * @return time in format "11:30 AM"
     */
    public String getBrDownTime() {
        return brDownTime;
    }
    
    /**
     * Sets the breakdown time
     * @param time - time in format "11:30 AM"
     * @precondition time is already in format "11:30 AM"
     */
    public void setBrDownTime(String time) {
        brDownTime = time;
    }
    
    /**
     * Sets the breakdown volume
     * @param volume - volume to be set to
     */
    public void setBrDownVolume(int volume) {
        this.brDownVolume = volume;
    }
    
    /**
     * Sets the price of high after the low
     * @param price - price to be set to as a double
     */
    public void setHighPrice(double price) {
        this.highPrice = price;
    }
    
    /**
     * Sets the time of high after low
     * @param time - time in format "11:30 AM"
     * @precondition already formatted like "12:30 AM"
     */
    public void setHighTime(String time) {
        this.highTime = time;
    }
    
    /**
     * Sets the volume of high after low
     * @param volume - low volume as an int
     */
    public void setHighVolume(int volume) {
        this.highVolume = volume;
    }
    
    /**
     * toString method that displays all data
     */
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("High: " + highPrice + ";");
        str.append("Vol of High: " + highVolume + ";");
        str.append("Time of High: " + highTime + ";");
        str.append("Low: " + lowPrice + ";");
        str.append("Vol of Low: " + lowVolume + ";");
        str.append("Time of Low: " + lowTime + ";");
        str.append("BrDown Vol: " + brDownVolume + ";");
        str.append("BrDown Time: " + brDownVolume + ";");
        return str.toString();
    }
}
