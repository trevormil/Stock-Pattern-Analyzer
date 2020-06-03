package stockdata;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Reads the candle data from a csv file and puts all the data into
 * an ArrayList of candles
 * @author Trevor Miller
 * @version 5/7/2020
 */
public class ChartReader {
    private ArrayList<Candlestick> candles;
    /**
     * Constructor for the chart reader that reads the file
     * @param filename - name of file (ex. "SDC-4-29-2020.csv")
     * @throws IOException - if file can not be found
     */
    public ChartReader(String filename, String date, String[] userInfo, Workbook wb) {
        candles = new ArrayList<Candlestick>();
        try {
            readChart(filename, date);
            new ChartWriter(candles, filename, userInfo, wb);
        }
        catch (IOException e) {
            System.out.print("404 ERROR!!!! File not found.");
        }
    }
    
    /**
     * Scans the file line by line and inputs all data into a
     * ArrayList full of candles
     * @param filename - filename where data is located
     * @throws IOException - if file can not be found
     */
    private void readChart(String filename, String date) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        
        // read file line by line
        String line = null;
        Scanner scanner = null;
        
        while ((line = reader.readLine()) != null) {
            scanner = new Scanner(line);
            scanner.useDelimiter(",");
            String currDate = "";
            if (scanner.hasNext()) {
                currDate = scanner.next(); 
            }
            
            //checks if the row starts with a date and adds candle to ArrayList
            if (currDate.equals(date)) {
                candles.add(new Candlestick(currDate, 
                    scanner.next(), Double.parseDouble(scanner.next()), 
                    Double.parseDouble(scanner.next()), 
                    Double.parseDouble(scanner.next()), 
                    Double.parseDouble(scanner.next()), 
                    Integer.parseInt(scanner.next())));
            }
        }
        
        //close reader
        reader.close();
    }
}
