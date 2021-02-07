# stock-data-bot
This program uses AutoIt to manually import daily stock data from Fidelity Active Trader Pro. It manually loads the app, signs in by typing password, and manually clicks to save all the CSV files for that day's stocks into a specified folder. Once it is done with that, it writes it all to Excel. Then, it takes in optional user input about each stock to also store in Excel as more information. This program looks for stocks that are up big on the day and tracks them until they have two consecutive red days. Once they have two consecutive red days, all the data for that stock in those couple of days is moved to the "Completed" sheet to signify that the stock has made its run up and has lost its momentum.

![image](./stockanalyzer.PNG)
