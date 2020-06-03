package stockdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.jacob.com.LibraryLoader;
import autoitx4java.AutoItX;

/**
 * Driver class for the project that prompts the user for info
 * and writes the file
 * 
 * @author Trevor Miller
 * @version 05-14-2020
 */
public class ExcelFileWriter {
    private static final String filePath =
        "C:\\Users\\trevo\\OneDrive\\Documents\\Financial\\Trading\\Trading Tracker.xlsx";
    private static CellStyle twoDecStyle;
    private static CellStyle percStyle;
    private static CellStyle volStyle;
    private Sheet currentRunners;
    private Sheet completedRunners;
    private Workbook wb;
    private FormulaEvaluator evaluator;
    private String date;
    private FileInputStream file;
    private HashMap<String, String> map;
    private String marketUp;
    
    /**
     * Creates a new File Writer and calls all methods to update the file
     */
    public ExcelFileWriter() {
        try {
            createFileInput();
            date = "";
            
            Scanner scan = new Scanner(System.in);
            writeCurrentRunners(scan);
            writeCompletedRunners();
            analyze();
            
            file.close();
            scan.close();
            createFileOutput();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Writes the new data from charts into "Current Runners" sheet
     * @param scan - System.in scanner
     * @throws IOException
     * @throws InterruptedException
     */
    private void writeCurrentRunners(Scanner scan)
        throws IOException, InterruptedException {
        //sets up AutoItX
        File jacobFile = new File("C:\\Users\\trevo\\eclipse-workspace"
            + "\\StockDataBot\\src\\jacob-1.19-x64.dll");
        System.setProperty(LibraryLoader.JACOB_DLL_PATH, jacobFile.getAbsolutePath());
        AutoItX x = new AutoItX();
        
        //gets all info about each chart and exports it
        getGeneralInfo(scan);
        autoFidelity(x, map);
        x.sleep(5000);
        autoImport(x);
        
        getTickerInfo(scan);
        
        //needed for the AutoIt Excel Script
        wb.setForceFormulaRecalculation(true);
        currentRunners.setSelected(true);
        completedRunners.setSelected(false);
        wb.getSheetAt(0).setSelected(false);
        currentRunners.setActiveCell(new CellAddress("B2"));
        
        //writes everything so far to the file
        file.close();
        createFileOutput();
        
        //opens excel and uses its stock functions to calculate data
        autoOpenCloseExcel(x);
        x.sleep(5000);
        
        //gets the updated data
        createFileInput();
        
        //changes all formulas to String, int, double, etc for Apache POI purposes
        removeFormulas(currentRunners); 
    }

    /**
     * Sorts the current runners and writes the completed ones to
     * "Completed Runners" page
     * @throws IOException
     */
    private void writeCompletedRunners() throws IOException {
        //sorts sheet alphabetically then by date
        sortSheet(currentRunners, 3);
        
        //scans current runners
        scanForRuns();
        
        currentRunners.createRow(1); //removeEmptyRows uses first row as temporary storage
        removeEmptyRows(currentRunners);
    }
    
    /**
     * Analyzes the completed runners and writes data to home page
     */
    private void analyze() {

    }
    
    /**
     * AutoIt script that opens excel and converts column B to Stock data type,
     * then saves the file
     * @param x - AutoItX object
     * @throws IOException
     * @throws InterruptedException
     */
    private void autoOpenCloseExcel(AutoItX x) throws IOException, InterruptedException {
        //opens excel document
        String[] commandAndArguments = {"cmd","/C", filePath, "c:\\"};
        Runtime runtime = Runtime.getRuntime();
        runtime.exec(commandAndArguments);
        x.sleep(12000);
        
        //runs AutoIt Script that sets all of column B to stock data type
        //and saves it
        runtime.exec("C:\\Users\\trevo\\OneDrive\\Documents\\Financial\\"
            + "Trading\\AutoExportCSV.exe");
        x.sleep(14000);
        
        //close the excel file
        String exitCommandAndArguments = "taskkill /F /IM" + " EXCEL.exe";
        runtime.exec(exitCommandAndArguments);
    }
    
    
    /**
     * Opens up Fidelity Active Trader Pro and exports all chart data to CSV
     * @param x - AutoItX object
     * @param map - HashMap containing the tickers and floats
     */
    private void autoFidelity(AutoItX x, HashMap<String, String> map) {
        
        String activeTraderPro = "Fidelity Active Trader Pro";
        x.run("C:\\WINDOWS\\system32\\cmd.exe /c \"C:\\Users\\trevo\\AppData\\"
            + "Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Fidelity "
            + "Investments\\Fidelity Active Trader Pro®.appref-ms\"");
        
        x.winWaitActive(activeTraderPro);
        x.sleep(9000);
        
        //login details
        x.mouseClick("", 750, 450);
        x.send("Wrestlingman23?");
        x.mouseClick("", 700, 530);
        x.sleep(20000);
        
        //open new chart and add extended hours
        x.mouseClick("", 730, 5);
        x.sleep(1000);
        x.mouseClick("", 730, 35);
        x.sleep(1000);
        x.mouseClick("", 730, 55);
        x.sleep(1000);
        x.mouseClick("right", 350, 85);
        x.sleep(1000);
        x.mouseClick("", 320, 115);
        x.mouseMove(360,  115);
        x.mouseClick("",  360, 220);
        
        //exports chart data to CSV file for all tickers
        boolean first = true;
        for (String ticker : map.keySet()) {
            x.mouseClick("", 95, 55);
            x.send(ticker + "\r");
            
            if (first) {
                x.sleep(2000);
                first = false;
            }
            x.sleep(3700);
            x.mouseClick("", 500, 105);
            x.mouseClick("", 500, 135);
            x.sleep(1500);
            x.mouseClick("", 300, 323);
            x.send(ticker + "-" + date + "\r");
            x.sleep(900);
        }
        
        //closes the app
        x.mouseClick("", 1480, 10);
        x.sleep(2000);
        x.mouseClick("", 800, 500);
        x.winWaitClose(activeTraderPro);
    }
    
    /**
     * AutoItX script that imports all CSV files in Chart Data to Eclipse
     * @param x - AutoItX object
     */
    private void autoImport(AutoItX x) {
        x.mouseClick("", 5, 30); //File
        x.mouseClick("", 5, 360); //Import
        x.mouseClick("", 550, 330); //File System
        x.mouseClick("", 750, 670); //Next
        x.sleep(800);
        x.mouseClick("", 700, 270); //Directory
        x.send("C:\\Users\\trevo\\OneDrive\\Documents\\Financial\\Trading\\Chart Data \r");
        x.mouseClick("", 490, 300); //Select Folder
        x.mouseClick("", 650, 480); //Select All
        x.mouseClick("", 650, 525); //Folder
        x.send("StockDataBot\r");
        x.sleep(500);
        x.mouseClick("", 900, 470); //Overwrite
    }
    
    /**
     * Sorts the sheet using a insertion sort algorithm; uses row 1 as a 
     * temporary storage row; sorts by ticker then date
     * @param sheet - sheet to sort
     * @param rowStart - row to start at
     */
    private void sortSheet(Sheet sheet, int rowStart) {
        boolean sorting = true;
        int lastRow = sheet.getLastRowNum();
        while (sorting) {
            sorting = false;
            Iterator<Row> rowIterator = sheet.rowIterator();
            boolean swapped = false;
            while (rowIterator.hasNext() && !swapped) {
                Row row = rowIterator.next();
                if (row.getRowNum() < rowStart)
                    continue;
                if (lastRow == row.getRowNum())
                    break;
                Row nextRow = sheet.getRow(row.getRowNum() + 1);
                if (nextRow == null)
                    continue;
                String firstValue = row.getCell(0).getStringCellValue();
                String secondValue = nextRow.getCell(0).getStringCellValue();
                String firstDate = row.getCell(2).getStringCellValue();
                String secondDate = nextRow.getCell(2).getStringCellValue(); 
                
                if (secondValue.compareTo(firstValue) < 0
                    || (secondValue.compareTo(firstValue) == 0
                    && secondDate.compareTo(firstDate) < 0)) {
                    int originalRowNum = nextRow.getRowNum();
                    writeRowTo(nextRow, 1, sheet);
                    for (int i = 3; i < originalRowNum; i++) {
                        String currRowValue = sheet.getRow(i).getCell(0).getStringCellValue();
                        String currRowDate = sheet.getRow(i).getCell(2).getStringCellValue();
                        if (secondValue.compareTo(currRowValue) < 0
                            || (secondValue.compareTo(currRowValue) == 0
                            && secondDate.compareTo(currRowDate) < 0)) {
                            sheet.shiftRows(i, originalRowNum - 1, 1);
                            writeRowTo(sheet.getRow(1), i, sheet);
                            sheet.createRow(1);
                            i = sheet.getLastRowNum() + 1;
                            swapped = true;
                            sorting = true;
                        }
                    }
                }
            }
        }
        sheet.createRow(1);
    }
    
    /**
     * Writes one row to another with formulas and cell styles
     * @param row - row to copy
     * @param newRowNum - index of destination row
     * @param sheet - sheet to be written on
     * @return new written row
     */
    private Row writeRowTo(Row row, int newRowNum, Sheet sheet) {
        Row newRow = sheet.createRow(newRowNum);
        
        //writes and styles each cell in row to the new row
        for (Cell cell : row) {
            CellType cellType = cell.getCellType();
            if (cellType == CellType.FORMULA) {
                cellType = evaluator.evaluateInCell(cell).getCellType();
            }
            Cell createdCell = newRow.createCell(cell.getColumnIndex());
            switch (cellType) {
                case BOOLEAN:
                    createdCell.setCellValue(cell.getBooleanCellValue());
                    break;
                case NUMERIC:
                    createdCell.setCellValue(cell.getNumericCellValue());
                    break;
                case STRING:
                    createdCell.setCellValue(cell.getStringCellValue());
                    break;
                case BLANK:
                case ERROR:
                case _NONE:
                case FORMULA:
                    break;
            }
            setCellStyle(createdCell);
        }
        return newRow;
    }
    
    /**
     * Removes all formulas from sheet and converts them to their
     * equivalent string, int, double, boolean, etc.
     * @param sheet - sheet to be removed from
     */
    private void removeFormulas(Sheet sheet) {
        //removes formulas from all sheets and converts them to their equivalent
        //string, int, double, boolean, etc
        for (Row r : sheet) {
            for (Cell cell : r) {
                if (cell.getRowIndex() >= 3
                    && cell.getColumnIndex() != 1
                    && cell.getCellType() == CellType.FORMULA) {
                    CellType cellType = cell.getCachedFormulaResultType();
                    
                    String formStr;
                    switch (cellType) {
                        case BOOLEAN:
                            formStr = cell.getCellFormula();
                            boolean temp = cell.getBooleanCellValue();
                            cell.setCellFormula(formStr);
                            cell.removeFormula();
                            cell.setCellValue(temp);
                            break;
                        case NUMERIC:
                            formStr = cell.getCellFormula();
                            double num = cell.getNumericCellValue();
                            cell.setCellFormula(formStr);
                            cell.removeFormula();
                            cell.setCellValue(num);
                            break;
                        case STRING:
                            formStr = cell.getCellFormula();
                            String tempStr = cell.getStringCellValue();
                            cell.setCellFormula(formStr);
                            cell.removeFormula();
                            cell.setCellValue(tempStr);
                            break;
                        case BLANK:
                            formStr = cell.getCellFormula();
                            cell.setCellFormula(formStr);
                            cell.removeFormula();
                            break;
                        case ERROR:
                            cell.setBlank();
                            break;
                        case _NONE:
                            formStr = cell.getCellFormula();
                            cell.setCellFormula(formStr);
                            cell.removeFormula();
                            break;
                        case FORMULA:
                            formStr = cell.getCellFormula();
                            cell.setCellFormula(formStr);
                            cell.removeFormula();
                            break;
                    }
                }
                else if (cell.getRowIndex() >= 3
                    && cell.getColumnIndex() == 1) {
                    cell.setBlank();
                }
            }
        }
    }
    
    /**
     * Writes the data from current to completed runners
     * @param startOfRun - starting row index of run
     * @param endOfRun - ending row index of run
     */
    private void writeToCompleted(int startOfRun, int endOfRun, boolean first) {
        //if completed runner, write it to completedRunners
        if (endOfRun >= 0) {
            for (int j = startOfRun; j <= endOfRun; j++) {
                Row row = writeRowTo(currentRunners.getRow(j), 
                    completedRunners.getPhysicalNumberOfRows(), completedRunners);
                currentRunners.removeRow(currentRunners.getRow(j));
                if (first) {
                    row.setHeightInPoints(5);
                    first = false;
                }
                else {
                    row.setZeroHeight(true);
                }
            }
        }

    }
    
    /**
     * Sets the cell style according to what column its in
     * @param cell - cell to be styled
     */
    public static void setCellStyle(Cell cell) {
        
        if (cell.getRowIndex() >= 3) {
            int index = cell.getColumnIndex();
            switch (index) {
                //sets cells to 5.55 format
                case 4:
                case 6:
                case 7:
                case 18:
                    cell.setCellStyle(twoDecStyle);
                    break;
                //sets cells to 2,333,444 style
                case 5:
                case 35:
                case 38:
                case 41:
                case 47:
                case 51:
                case 60:
                case 63:
                case 66:
                case 72:
                case 76:
                case 85:
                case 89:
                case 104:
                case 114:
                    cell.setCellStyle(volStyle);
                    break;
                //sets cell style to "50%" format   
                case 9:
                case 10:
                case 11:
                case 42:
                case 43:
                case 54:
                case 55:
                case 56:
                case 67:
                case 68:
                case 80:
                case 81:
                case 79:
                case 95:
                case 96:             
                case 94:
                case 105:              
                case 106:
                case 115:
                case 116:
                    cell.setCellStyle(percStyle);
                    break;
                //sets cells to 9:34 AM format
                case 15:
                case 17:
                case 34:
                case 37:
                case 40:
                case 46:
                case 48:
                case 50:
                case 53:
                case 59:
                case 62:
                case 65:
                case 71:
                case 73:
                case 75:
                case 78:
                case 84:
                case 88:
                case 91:
                case 93:
                case 99:
                case 101:
                case 103:
                case 109:
                case 111:
                case 113:
                    if (cell.getCellType() == CellType.NUMERIC) {
                        int seconds = (int) (cell.getNumericCellValue() * 86400);
                        int hours = seconds / 3600;
                        int minutes = (seconds % 3600) / 60;
                        String suffix;
                        if (hours <= 12) {
                            suffix = "AM";
                        }
                        else {
                            suffix = "PM";
                            hours -= 12;
                        }
                        String min;
                        if (minutes < 10) {
                            min = "0" + minutes;
                        }
                        else {
                            min = minutes + "";
                        }
                        cell.setCellValue(hours + ":" + min + " " + suffix);
                    }
                    break;
            }
        }
    }
    
    /**
     * Removes all empty rows from sheet
     * @param sheet - specified sheet to remove rows from
     */
    private void removeEmptyRows(Sheet sheet) {
        Boolean isRowEmpty = Boolean.FALSE;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            //shift if row is empty
            if (sheet.getRow(i) == null) {
                isRowEmpty = true;
                sheet.shiftRows(i + 1, sheet.getLastRowNum() + 1, -1);
                i--;
                continue;
            }
            //checks if all cells in row have no value
            for (int j = 0; j < sheet.getRow(i).getLastCellNum(); j++) {
                if (sheet.getRow(i).getCell(j) == null || sheet.getRow(i)
                    .getCell(j).toString().trim().equals("")) {
                    isRowEmpty = true;
                }
                else {
                    isRowEmpty = false;
                    break;
                }
            }
            //moves i down 1 because row was shifted down
            if (isRowEmpty == true) {
                sheet.shiftRows(i + 1, sheet.getLastRowNum() + 1, -1);
                i--;
            }
        }
    }


    /**
     * Checks to see if it meets the criteria for the first day of running
     * @return - true if it does; false if not
     */
    private boolean firstDayOfRun(Row row) {
        if (row.getCell(9).getNumericCellValue() >= 0.15 
            || row.getCell(10).getNumericCellValue() >= 0.3) {
            return true;
        }
        return false;
    }
    
    /**
     * Creates a new file input stream and respective variables
     * @throws IOException
     */
    public void createFileInput() throws IOException {
        //creates new file and workbook
        file = new FileInputStream(filePath);
        wb = new XSSFWorkbook(file);
        currentRunners = wb.getSheet("Current Runners");
        completedRunners = wb.getSheet("Completed Runners");
        evaluator = wb.getCreationHelper()
            .createFormulaEvaluator();
        
        //assigns styles to that workbook
        DataFormat format = wb.createDataFormat();
        twoDecStyle = wb.createCellStyle();
        twoDecStyle.setDataFormat(format.getFormat("0.00"));        
        percStyle = wb.createCellStyle();
        percStyle.setDataFormat(format.getFormat("0%"));
        volStyle = wb.createCellStyle();
        volStyle.setDataFormat(format.getFormat("#,###,###"));
    }
    
    /**
     * Writes the file output to the file
     * @throws IOException
     */
    private void createFileOutput() throws IOException {
        FileOutputStream out = new FileOutputStream(new File(filePath));
        wb.write(out);
        out.close();
    }
    
    /**
     * Gets the general info (date, tickers and floats, DOW performance)
     * @param scan - System.in scanner
     */
    private void getGeneralInfo(Scanner scan) {
        //gets date, DOW performance, and floats from screener
        System.out.print("Date (MM-DD-YYYY): ");
        date = scan.nextLine();
        System.out.print("DOW Performance: ");
        marketUp = scan.nextLine();
        System.out.println("Floats from Screener (Type * When Done): ");
        map = new HashMap<String, String>();
        scan.nextLine(); // for the "Ticker Float" line
        //puts tickers and floats into HashMap
        while (scan.hasNextLine()) {
            String str = scan.next();
            if (str.contentEquals("*")) {
                scan.nextLine();
                break;
            }
            else {
                map.put(str, scan.next());
            }
        }
    }
    
    /**
     * Prompts the user for info about each ticker
     * @param scan - System.in scanner
     */
    private void getTickerInfo(Scanner scan) {
        
        //asks user for info about each chart
        for (String str : map.keySet()) {

            String[] userInputInfo = new String[8];
            
            System.out.println(str); // filename
            System.out.print("Resistance (no commas): ");
            userInputInfo[0] = scan.nextLine();
            System.out.print("News (no commas): ");
            userInputInfo[1] = scan.nextLine();
            System.out.print("Bagholder: ");
            userInputInfo[2] = scan.nextLine();
            System.out.print("Former Runner: ");
            userInputInfo[3] = scan.nextLine();
            System.out.print("SSR: ");
            userInputInfo[4] = scan.nextLine();
            System.out.print("Warrants: ");
            userInputInfo[5] = scan.nextLine();
            userInputInfo[6] = marketUp;
            userInputInfo[7] = map.get(str);

            @SuppressWarnings("unused")
            ChartReader chart = new ChartReader(str + "-" + date + ".csv", 
                date.replace("-", "/"), userInputInfo, wb);
        }
    }
    
    /**
     * Scans a sorted "Current Runners" for runners and if completed, writes
     * to completed
     */
    private void scanForRuns() {
        int startOfRun = -1, endOfRun = -1;
        String runnerTicker = "";
        boolean first = true;
        //iterates through all data rows
        for (int i = 3; i < currentRunners.getLastRowNum(); i++) {
            Row currRow = currentRunners.getRow(i);
            if (currRow != null) {
                // looks for runner and iterates until end of runner
                if (firstDayOfRun(currRow)) {
                    startOfRun = i;
                    runnerTicker = currRow.getCell(0).getStringCellValue();
                    boolean end = false;
                    
                    while (i + 1 <= currentRunners.getLastRowNum()
                        && currentRunners.getRow(i + 1) != null
                        && currentRunners.getRow(i + 1).getCell(0) != null
                        && currentRunners.getRow(i + 1).getCell(0)
                            .getStringCellValue().equals(runnerTicker)
                        && !end) {

                        if (currentRunners.getRow(i).getCell(10).getNumericCellValue() <= 0 
                            && !firstDayOfRun(currentRunners.getRow(i + 1))) {
                            endOfRun = i + 1;
                            end = true;
                        }
                        i++;
                    }
                }
                // writes to completed runners
                writeToCompleted(startOfRun, endOfRun, first);
                first = false;
                startOfRun = -1; endOfRun = -1;
            }
        }
    }
}
