package stockdata;

import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


/**
 * Creates a ChartCalculator that analyzes the data and writes
 * a new csv file with the data analysis
 * 
 * @author Trevor Miller
 * @version - 2/17/2020
 */
public class ChartWriter {
    private int preMarketVolume;  private int volume;
    private int highVolume; private double high; private String highTime;
    private String lowTime; private double low;
    private double preMarkHigh; private double preMarkLow;
    private double open;
    private String date;
    private String ticker;
    private ArrayList<Candlestick> candles;
    private ArrayList<High> highs; private ArrayList<Low> lows;
    private Workbook wb;
    private String[] userInfo;

    /**
     * Creates a new ChartWriter, calculates data from CSV file, and writes to file
     * @param candles - ArrayList of all candle data from chart
     * @param filename - filename in format "XXXX-05-12-20"
     * @param userInfo - answers to prompts the user is asked
     * @precondition candles isn't empty (most likely due to date not being correct)
     */
    public ChartWriter(ArrayList<Candlestick> candles, String filename, String[] userInfo, Workbook wb) {
        try {
            //initializes fields
            ticker = filename.substring(0, filename.indexOf("-")); //from filename
            this.candles = candles;
            highs = new ArrayList<High>(); lows = new ArrayList<Low>();
            volume = 0; preMarketVolume = 0; highVolume = 0;
            date = candles.get(0).getDate();
            high = Integer.MIN_VALUE; preMarkHigh = Integer.MIN_VALUE;
            low = Integer.MAX_VALUE; preMarkLow = Integer.MAX_VALUE;
            this.wb = wb;
            this.userInfo = userInfo;
            
            
            calculateData();
            createRowInFile();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        //Add Other Patterns Below
    
    }


    /**
     * Calculates all the data from the candlestick chart and updates the
     * highs, lows, and volumes
     */
    private void calculateData() {
        for (int i = 0; i < candles.size(); i++) {
            // collects data from pre market
            if (isPreMarket(candles.get(i).getTime())) {
                preMarkHigh = Math.max(preMarkHigh, candles.get(i).getHigh());
                preMarkLow = Math.min(preMarkLow, candles.get(i).getLow());
                preMarketVolume += candles.get(i).getVolume();
            }
            // collects highs, lows, and volume for market hours
            else if (isMarketHours(candles.get(i).getTime())) {
                if (checkNewHigh(candles.get(i).getHigh())) {
                    findNewHigh(i);
                }
                if (checkNewLow(candles.get(i).getLow())) {
                    findNewLow(i);
                }
                updateLowDataForHighs(i);
                updateHighDataForLows(i);
            }
            
            volume += candles.get(i).getVolume();
        }
        open = candles.get(0).getOpen();
    }
    
    /**
     * Writes a new row at end of "Current Runners" sheet with 
     * formulas and cell styles
     */
    private void createRowInFile() {
        Sheet currentRunners = wb.getSheet("Current Runners");
        Row row = currentRunners.createRow(currentRunners.getLastRowNum() + 1);    
        int rowNum = row.getRowNum() + 1;
        
        //Basic Info
        row.createCell(0).setCellFormula("_FV(B" + rowNum + ",\"Ticker symbol\",TRUE)");
        row.createCell(1).setCellValue(ticker);     
        row.createCell(2).setCellValue(date.replace("/", "_"));  
        row.createCell(3).setCellFormula("_FV(B" + rowNum + ",\"Exchange Abbreviation\",TRUE)");   
        row.createCell(4).setCellFormula("_FV(B" + rowNum + ",\"Market Cap\",TRUE) / 1000000");
        row.createCell(5).setCellValue(volume);
        if (userInfo[7].contentEquals("-")) {
            row.createCell(6).setCellValue(userInfo[7]);
        }
        else {
            row.createCell(6).setCellValue(Double.parseDouble(userInfo[7])); //float
        }
        row.createCell(7).setCellFormula("F" + rowNum +"/(G" + rowNum + "*1000000)");
        row.createCell(8).setCellFormula("_FV(B" + rowNum + ",\"Previous Close\",TRUE)");
        row.createCell(9).setCellFormula("(M" + rowNum + "-I" + rowNum + ")/I" + rowNum);
        row.createCell(10).setCellFormula("(N" + rowNum + "-I" + rowNum + ")/I" + rowNum);
        row.createCell(11).setCellFormula("(N" + rowNum + "-M" + rowNum + ")/M" + rowNum);
        row.createCell(12).setCellFormula("_FV(B" + rowNum + ",\"Open\",TRUE)");
        row.createCell(13).setCellFormula("_FV(B" + rowNum + ",\"Price\",TRUE)");        
        row.createCell(14).setCellValue(high);
        row.createCell(15).setCellValue(highTime);
        row.createCell(16).setCellValue(low);
        row.createCell(17).setCellValue(lowTime);
        row.createCell(18).setCellFormula("N" + rowNum + "*F" + rowNum + " / 1000000");
        row.createCell(19).setCellValue(preMarketVolume);
        row.createCell(20).setCellValue(preMarkHigh);
        row.createCell(21).setCellValue(preMarkLow);
        row.createCell(22).setCellValue(highs.size());
        row.createCell(23).setCellValue(lows.size());
        row.createCell(24).setCellFormula("_FV(B" + rowNum + ",\"Industry\",TRUE)");   
        
        //User Info Cells
        for (int x = 0; x < userInfo.length - 1; x++) {
            row.createCell(25 + x).setCellValue(userInfo[x]);
        }
        
        //HOD 10-12
        High brOutPoint = getHODBrOut();
        row.createCell(32).setCellValue(brOutPoint != null);
        if (brOutPoint != null) {            
            row.createCell(33).setCellValue(brOutPoint.getHighPrice());
            row.createCell(34).setCellValue(brOutPoint.getBrOutTime());
            row.createCell(35).setCellValue(brOutPoint.getBrOutVolume());
            row.createCell(36).setCellValue(high);
            row.createCell(37).setCellValue(highTime);
            row.createCell(38).setCellValue(highVolume);
            High highWithLowestLow = getLowAfterBrOut(brOutPoint);
            row.createCell(39).setCellValue(highWithLowestLow.getLowPrice());
            row.createCell(40).setCellValue(highWithLowestLow.getLowTime());
            row.createCell(41).setCellValue(highWithLowestLow.getLowVolume());
            row.createCell(42).setCellFormula("(AK" + rowNum + "-AH" + rowNum + ")/AH" + rowNum);
            row.createCell(43).setCellFormula("(AN" + rowNum + "-AH" + rowNum + ")/AH" + rowNum);
        }
        
        //Parabolic Short Off Open
        High parShort = getParShort();
        row.createCell(44).setCellValue(parShort != null 
            && parShort.getHighPrice() / open >= 1.15);
        if (parShort != null 
            && parShort.getHighPrice() / open >= 1.15) {
            row.createCell(45).setCellValue(parShort.getHighPrice());
            row.createCell(46).setCellValue(parShort.getHighTime());
            row.createCell(47).setCellValue(parShort.getHighVolume());
            row.createCell(48).setCellValue(parShort.getBrOutTime());
            row.createCell(49).setCellValue(parShort.getLowPrice());
            row.createCell(50).setCellValue(parShort.getLowTime());
            row.createCell(51).setCellValue(parShort.getLowVolume());
            row.createCell(52).setCellValue(low);
            row.createCell(53).setCellValue(lowTime);
            row.createCell(54).setCellFormula("(AT" + rowNum + "-M" + rowNum + ")/M" + rowNum);
            row.createCell(55).setCellFormula("(AK" + rowNum + "-AX" + rowNum + ")/AX" + rowNum);
            row.createCell(56).setCellFormula("(AK" + rowNum + "-O" + rowNum + ")/O" + rowNum);
        }

        
        //Afternoon HOD 12-4
        High aNoonBrOutPoint = getAfternoonBrOut();
        row.createCell(57).setCellValue(aNoonBrOutPoint != null);
        if (aNoonBrOutPoint != null) {
            row.createCell(58).setCellValue(aNoonBrOutPoint.getHighPrice());
            row.createCell(59).setCellValue(aNoonBrOutPoint.getBrOutTime());
            row.createCell(60).setCellValue(aNoonBrOutPoint.getBrOutVolume());
            row.createCell(61).setCellValue(high);
            row.createCell(62).setCellValue(highTime);
            row.createCell(63).setCellValue(highVolume);
            
            High aNoonHighWithLowestLow = getLowAfterBrOut(aNoonBrOutPoint);
            row.createCell(64).setCellValue(aNoonHighWithLowestLow.getLowPrice());
            row.createCell(65).setCellValue(aNoonHighWithLowestLow.getLowTime());
            row.createCell(66).setCellValue(aNoonHighWithLowestLow.getLowVolume());
            row.createCell(67).setCellFormula("(BJ" + rowNum + "-BG" + rowNum + ")/BG" + rowNum);
            row.createCell(68).setCellFormula("(BM" + rowNum + "-BG" + rowNum + ")/BG" + rowNum);
        }
        
        
        //Parabolic Short Before 10
        High parShortBefore10 = getParShortBefore10();
        row.createCell(69).setCellValue(parShortBefore10 != null 
            && parShortBefore10.getHighPrice() / open >= 1.15);
        if (parShortBefore10 != null && 
            (parShortBefore10.getHighPrice() / open >= 1.15)) {
            row.createCell(70).setCellValue(parShortBefore10.getHighPrice());
            row.createCell(71).setCellValue(parShortBefore10.getHighTime());
            row.createCell(72).setCellValue(parShortBefore10.getHighVolume());
            row.createCell(73).setCellValue(parShortBefore10.getBrOutTime());
            row.createCell(74).setCellValue(parShortBefore10.getLowPrice());
            row.createCell(75).setCellValue(parShortBefore10.getLowTime());
            row.createCell(76).setCellValue(parShortBefore10.getLowVolume());
            row.createCell(77).setCellValue(low);
            row.createCell(78).setCellValue(lowTime);
            row.createCell(79).setCellFormula("(BS" + rowNum + "-M" + rowNum + ")/M" + rowNum);
            row.createCell(80).setCellFormula("(BS" + rowNum + "-BW" + rowNum + ")/BW" + rowNum);
            row.createCell(81).setCellFormula("(BS" + rowNum + "-O" + rowNum + ")/O" + rowNum);
        }
        
        //Gap Up Short
        Low gapUpShort = getGapUpShort();
        row.createCell(82).setCellValue(gapUpShort != null);
        if (gapUpShort != null) {
            row.createCell(83).setCellValue(gapUpShort.getLowPrice());
            row.createCell(84).setCellValue(gapUpShort.getLowTime());
            row.createCell(85).setCellValue(gapUpShort.getLowVolume());
            row.createCell(86).setCellValue(gapUpShort.getBrDownTime());
            row.createCell(87).setCellValue(gapUpShort.getHighPrice());
            row.createCell(88).setCellValue(gapUpShort.getHighTime());
            row.createCell(89).setCellValue(gapUpShort.getHighVolume());
            row.createCell(90).setCellValue(low);
            row.createCell(91).setCellValue(lowTime);
            row.createCell(92).setCellValue(high);
            row.createCell(93).setCellValue(highTime);
            row.createCell(94).setCellFormula("(M" + rowNum + "-CF" + rowNum + ")/CF" + rowNum);
            row.createCell(95).setCellFormula("(CJ" + rowNum + "-CM" + rowNum + ")/CM" + rowNum);
            row.createCell(96).setCellFormula("(CJ" + rowNum + "-CO" + rowNum + ")/CO" + rowNum);
        }
        
        //LOD Breakdown 10-12
        Low brDownPoint = getLODBrDown();
        row.createCell(97).setCellValue(brDownPoint != null);
        if (brDownPoint != null) {
            row.createCell(98).setCellValue(brDownPoint.getLowPrice());
            row.createCell(99).setCellValue(brDownPoint.getBrDownTime());
            row.createCell(100).setCellValue(low);
            row.createCell(101).setCellValue(lowTime);
        
            Low lowWithHighestHigh = getHighAfterBrDown(brDownPoint);
            row.createCell(102).setCellValue(lowWithHighestHigh.getHighPrice());
            row.createCell(103).setCellValue(lowWithHighestHigh.getHighTime());
            row.createCell(104).setCellValue(lowWithHighestHigh.getHighVolume());
            row.createCell(105).setCellFormula("(CU" + rowNum + "-CW" + rowNum + ")/CW" + rowNum);
            row.createCell(106).setCellFormula("(CU" + rowNum + "-CY" + rowNum + ")/CY" + rowNum);
        }
        
        //LOD Breakdown 12-4
        Low aNoonBrDownPoint = getAfternoonBrDown();
        row.createCell(107).setCellValue(aNoonBrDownPoint != null);
        if (aNoonBrDownPoint != null) {
            row.createCell(108).setCellValue(aNoonBrDownPoint.getLowPrice());
            row.createCell(109).setCellValue(aNoonBrDownPoint.getBrDownTime());
            row.createCell(110).setCellValue(low);
            row.createCell(111).setCellValue(lowTime);
            
            Low aNoonLowWithHighestHigh = getHighAfterBrDown(aNoonBrDownPoint);
            row.createCell(112).setCellValue(aNoonLowWithHighestHigh.getHighPrice());
            row.createCell(113).setCellValue(aNoonLowWithHighestHigh.getHighTime());
            row.createCell(114).setCellValue(aNoonLowWithHighestHigh.getHighVolume());
            row.createCell(115).setCellFormula("(DE" + rowNum + "-DG" + rowNum + ")/DG" + rowNum);
            row.createCell(116).setCellFormula("(DE" + rowNum + "-DI" + rowNum + ")/DI" + rowNum);
        }
        for (Cell cell : row) {
            ExcelFileWriter.setCellStyle(cell);
        }
    }


    /**
     * Returns the high point in first 10 minutes
     * @return High object with high data; null if none
     */
    private High getParShort() {
        High newHigh = null;
        for (int i = 0; i < highs.size(); i++) {
            
            String time = highs.get(i).getHighTime();
            int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
            int minute = Integer.parseInt(time.substring(time.indexOf(":") + 1, time
                .indexOf(" ")));
            String suffix = time.substring(time.indexOf(" ") + 1);
            
            if (suffix.equals("AM") && hour == 9 && minute >=30 && minute <= 40) {
                newHigh = highs.get(i);
            }
            else {
                i = highs.size();
            }
        }
        return newHigh;
    }
    
    /**
     * Returns Low of first 10 minutes if it dropped 10%+
     * @return a Low object containing the low data
     */
    private Low getGapUpShort() {
        for (int i = 0; i < lows.size(); i++) {
            String time = lows.get(i).getLowTime();
            int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
            int minute = Integer.parseInt(time.substring(time.indexOf(":") + 1, time
                .indexOf(" ")));
            String suffix = time.substring(time.indexOf(" ") + 1);
            
            if (suffix.equals("AM") && hour == 9 && minute >=30 && minute <= 40) {
                if (open / lows.get(i).getLowPrice() >= 1.10) {
                    return lows.get(i);
                }
            }
            else {
                i = lows.size();
            }
            
        }
        return null;
    }
    
    /**
     * Returns the high point in first 30 minutes
     * @return High object with high data; null if none
     */
    private High getParShortBefore10() {
        High newHigh = null;
        for (int i = 0; i < highs.size(); i++) {
            
            String time = highs.get(i).getHighTime();
            int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
            int minute = Integer.parseInt(time.substring(time.indexOf(":") + 1, time
                .indexOf(" ")));
            String suffix = time.substring(time.indexOf(" ") + 1);
            
            if (suffix.equals("AM") && hour == 9 && minute >=30) {
                newHigh = highs.get(i);
            }
            else {
                i = highs.size();
            }
        }
        return newHigh;
    }
    
    /**
     * Returns HOD before breakout in 10-12 range
     * Example: Previous HOD was 5.50 at 9:45 and it breaks 5.50 at 10:30,
     * it will return the High at 9:45
     * @return High with the high data
     */
    private High getHODBrOut() {
        High newHigh = null;
        for (int i = 0; i < highs.size(); i++) {
            
            String time = highs.get(i).getBrOutTime();
            if (time != null) {
                int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
                String suffix = time.substring(time.indexOf(" ") + 1);
                if (suffix.equals("AM") && hour >= 10) {
                    newHigh = highs.get(i);
                }
                else if (suffix.equals("PM")) {
                    i = highs.size();
                }
            }
            
        }
        return newHigh;
    }
    
    /**
     * Returns LOD before breakdown in 10-12 range
     * Example: Previous LOD was 5.50 at 9:45 and it breaks 5.50 at 10:30,
     * it will return the Low at 9:45
     * @return Low with the low data
     */
    private Low getLODBrDown() {
        Low newLow = null;
        for (int i = 0; i < lows.size(); i++) {
            
            String time = lows.get(i).getBrDownTime();
            if (time != null) {
                int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
                String suffix = time.substring(time.indexOf(" ") + 1);
                if (suffix.equals("AM") && hour >= 10) {
                    newLow = lows.get(i);
                }
                else if (suffix.equals("PM")) {
                    i = lows.size();
                }
            }
            
        }
        return newLow;
    }
    
    /**
     * Returns LOD before breakdown in 12-4 range
     * Example: Previous LOD was 5.50 at 9:45 and it breaks 5.50 at 10:30,
     * it will return the Low at 9:45
     * @return Low with the low data
     */
    private Low getAfternoonBrDown() {
        for (Low low: lows) {
            String time = low.getBrDownTime();
            if (time != null) {
                String suffix = time.substring(time.indexOf(" ") + 1);
                
                if (suffix.equals("PM")) {
                    return low;
                }
            }
            
        }
        return null;
    }
    
    /**
     * Returns HOD before breakout in 12-4 range
     * Example: Previous HOD was 5.50 at 9:45 and it breaks 5.50 at 10:30,
     * it will return the High at 9:45
     * @return High with the high data
     */
    private High getAfternoonBrOut() {
        for (High high: highs) {
            String time = high.getBrOutTime();
            if (time != null) {
                String suffix = time.substring(time.indexOf(" ") + 1);
                
                if (suffix.equals("PM")) {
                    return high;
                }
            }
            
        }
        return null;
    }
    
    /**
     * Gets the lowest price after a specified HOD
     * @param high - specified HOD
     * @return the High with the lowest low for remainder of day
     */
    private High getLowAfterBrOut(High high) {
        if (!highs.isEmpty()) {
            int i = 0;
            while (highs.get(i) != high) {
                i++;
            }
            if (i + 1 == highs.size()) {
                return highs.get(i);
            }
            else {
                i++;
            }
            
            High highWithLowestLow = highs.get(i);
            for (int index = i; index < highs.size(); index++) {
                if (highWithLowestLow.getLowPrice() > highs.get(index).getLowPrice()) {
                    highWithLowestLow = highs.get(index);
                }
            }
            return highWithLowestLow;
        }
        return null;
    }
    
    /**
     * Gets the highest price after a specified LOD
     * @param low - specified LOD
     * @return the Low with the highest high for remainder of day
     */
    private Low getHighAfterBrDown(Low low) {
        if (!lows.isEmpty()) {
            int i = 0;
            while (lows.get(i) != low) {
                i++;
            }
            if (i + 1 == lows.size()) {
                return lows.get(i);
            }
            else {
                i++;
            }
            Low lowWithHighestHigh = lows.get(i);
            for (int index = i; index < lows.size(); index++) {
                if (lowWithHighestHigh.getHighPrice() < lows.get(index).getHighPrice()) {
                    lowWithHighestHigh = lows.get(index);
                }
            }
            return lowWithHighestHigh;
        }
        
        return null;
    }

    /**
     * Updates the high data for the current low
     * @param i - index in candles arraylist that it is currently on
     */
    private void updateHighDataForLows(int i) {
        if (!highs.isEmpty()) {
            High lastHigh = highs.get(highs.size() - 1);
            // makes sure it is in hours and not currently finding new high
            if (volume > lastHigh.getHighVolume() 
                && candles.get(i).getLow() < lastHigh.getLowPrice()) {
                lastHigh.setLowPrice(candles.get(i).getLow());
                lastHigh.setLowTime(candles.get(i).getTime());
                lastHigh.setLowVolume(volume);
            }
        }
    }

    /**
     * Updates the low data for the current high
     * @param i - index in candles ArrayList that it is currently on
     */
    private void updateLowDataForHighs(int i) {
        if (!lows.isEmpty()) {
            Low lastLow = lows.get(lows.size() - 1);
            // makes sure it is in hours and not currently finding new low
            if (volume > lastLow.getLowVolume() 
                && candles.get(i).getHigh() > lastLow.getHighPrice()) {
                lastLow.setHighPrice(candles.get(i).getHigh());
                lastLow.setHighTime(candles.get(i).getTime());
                lastLow.setHighVolume(volume);
            }
        }
    }


    /**
     * Finds and creates a new High when there is one
     * @param i - index of starting candle
     */
    private void findNewHigh(int i) {
        // set breakout point data
        if (highs.size() > 0) {
            High lastHigh = highs.get(highs.size() - 1);
            lastHigh.setBrOutTime(candles.get(i).getTime());
            lastHigh.setBrOutVolume(volume);
        }

        // find new high data
        int index = 1;
        int tempVolume = volume;
        while ((i + index) < candles.size() && 
            candles.get(i + index).getHigh() >= candles.get(i + index - 1)
            .getHigh() && isMarketHours(candles.get(i + index).getTime())) {
            tempVolume += candles.get(i + index).getVolume();
            index++;
        }
        index--;
        
        //set new high
        high = candles.get(i + index).getHigh();
        highTime = candles.get(i + index).getTime();
        highVolume = volume;
        High newHigh = new High(candles.get(i + index).getTime(), candles.get(i
            + index).getHigh(), tempVolume);
        highs.add(newHigh);
        newHigh.setLowPrice(candles.get(i + index).getLow());
        newHigh.setLowTime(candles.get(i + index).getTime());
        newHigh.setLowVolume(tempVolume);
        
    }


    /**
     * Finds and creates a new Low when there is one
     * @param i - index of starting candle
     */
    private void findNewLow(int i) {
        // set breakdown point data
        if (lows.size() > 0) {
            Low lastLow = lows.get(lows.size() - 1);
            lastLow.setBrDownTime(candles.get(i).getTime());
            lastLow.setBrDownVolume(volume);
        }

        // find new low data
        int index = 1;
        int tempVolume = volume;
        while ((i + index) < candles.size() &&
            isMarketHours(candles.get(i + index).getTime()) && 
            candles.get(i + index).getLow() <= candles.get(i + index - 1).getLow()) {
            tempVolume += candles.get(i + index).getVolume();
            index++;
        }
        index--;
        
        //set new low
        Low newLow = new Low(candles.get(i + index).getTime(), candles.get(i
            + index).getLow(), tempVolume);
        low = candles.get(i + index).getLow();
        lowTime = candles.get(i + index).getTime();
        lows.add(newLow);

        newLow.setHighPrice(candles.get(i + index).getLow());
        newLow.setHighTime(candles.get(i + index).getTime());
        newLow.setHighVolume(tempVolume);
    }

    /**
     * Checks if there is a new High
     * @param price - current price
     * @return true if new HOD; false if not
     */
    private boolean checkNewHigh(double price) {
        return price > high;
    }

    /**
     * Checks if there is a new low
     * @param price - current price
     * @return true if new LOD; false if not
     */
    private boolean checkNewLow(double price) {
        return price < low;
    }


    /**
     * Checks if time is in open hours
     * @param time - time in format "9:30 AM"
     * @return true if in market hours; false if not
     */
    private boolean isMarketHours(String time) {
        int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
        int minute = Integer.parseInt(time.substring(time.indexOf(":") + 1, time
            .indexOf(" ")));
        String suffix = time.substring(time.indexOf(" ") + 1);

        if (suffix.equals("AM")) {
            return hour >= 10 || (hour == 9 && minute > 30);
        }
        else {
            return (hour < 4 || hour == 12) || (hour == 4 && minute == 0);
        }
    }


    /**
     * Checks if time is in premarket
     * 
     * @param time - time in format "9:30 AM"
     * @return true if in premarket; false if not
     */
    private boolean isPreMarket(String time) {
        return !isMarketHours(time) && time.substring(time.indexOf(" ") + 1)
            .equals("AM");
    }
}
