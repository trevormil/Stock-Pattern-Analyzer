package stockdata;

/**
 * Creates a class that stores data for a new HOD on the chart.
 * @author Trevor Miller
 * @version 05-14-2020
 */
public class High {
    private String highTime; private double highPrice; private int highVolume;
    private String lowTime; private double lowPrice;
    private int lowVolume; private String brOutTime; private int brOutVolume;
    
    /**
     * Creates a new High object
     * @param highTime - time the high occurred at
     * @param highPrice - price the high occurred at
     * @param highVolume - volume the high occurred at
     */
    public High(String highTime, double highPrice, int highVolume) {
        this.highTime = highTime;
        this.highPrice = highPrice;
        this.highVolume = highVolume;
        brOutVolume = -1;
        brOutTime = null;
        lowVolume = -1;
        lowTime = null;
        lowPrice = Integer.MAX_VALUE;
    }
    
    /**
     * Getter method for the low time after high 
     * before breakout (if there is one)
     * @return the low time in format "2:30 PM"
     */
    public String getLowTime() {
        return lowTime;
    }
    
    /**
     * Getter method for the high time
     * @return the high time in format "2:30 PM"
     */
    public String getHighTime() {
        return highTime;
    }
    
    /**
     * Getter method for the high volume
     * @return the volume as an int
     */
    public int getHighVolume() {
        return highVolume;
    }
    
    /**
     * Getter method for the high price
     * @return the double the high occurred at
     */
    public double getHighPrice() {
        return highPrice;
    }
    
    /**
     * Getter method for the low price after high 
     * and before breakout (if there is one)
     * @return price as a double
     */
    public double getLowPrice() {
        return lowPrice;
    }
    
    /**
     * Getter method for the volume at time of breakout
     * @return the volume as an int
     */
    public int getBrOutVolume() {
        return brOutVolume;
    }
    
    /**
     * Getter method for the volume at low after high 
     * and before breakout (if there is one)
     * @return the volume as an int
     */
    public int getLowVolume() {
        return lowVolume;
    }
    
    /**
     * Getter method for the time of breakout
     * @return time in format "11:30 AM"
     */
    public String getBrOutTime() {
        return brOutTime;
    }
    
    /**
     * Sets the break out time
     * @param time - time in format "11:30 AM"
     * @precondition time is already in format "11:30 AM"
     */
    public void setBrOutTime(String time) {
        brOutTime = time;
    }
    
    /**
     * Sets the break out volume
     * @param volume - volume to be set to
     */
    public void setBrOutVolume(int volume) {
        this.brOutVolume = volume;
    }
    
    /**
     * Sets the price of low after the high
     * @param price - price to be set to as a double
     */
    public void setLowPrice(double price) {
        this.lowPrice = price;
    }
    
    /**
     * Sets the time of low after high
     * @param time - time in format "11:30 AM"
     * @precondition already formatted like "12:30 AM"
     */
    public void setLowTime(String time) {
        this.lowTime = time;
    }
    
    /**
     * Sets the volume of low after high
     * @param volume - low volume as an int
     */
    public void setLowVolume(int volume) {
        this.lowVolume = volume;
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
        str.append("BrOut Vol: " + brOutVolume + ";");
        str.append("BrOut Time: " + brOutVolume + ";");
        return str.toString();
    }
}
