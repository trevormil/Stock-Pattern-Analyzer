package stockdata;

import java.io.IOException;

/**
 * Driver class for the project that prompts the user for info
 * and writes the file
 * 
 * @author Trevor Miller
 * @version 05-14-2020
 */
public class DataRunner {
    
    /**
     * Main method that prompts the user and runs all methods
     * 
     * @param args
     *            - array of filenames to be analyzed
     * @throws IOException
     * @throws InterruptedException 
     * @precondition - each filename is in format "XXXX-05-12-20"
     *               with XXXX being the ticker
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        new ExcelFileWriter();
    }


}