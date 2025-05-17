import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Reports extends Transaction {

    public void generateReport(int month, int year) {
        String transactionFileName = "Transactions.csv";
        double totalRevenue = 0;
        List<String> transactionsDetails = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

        try (BufferedReader br = new BufferedReader(new FileReader(transactionFileName))) {
            String line;

            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] transactionDetails = line.split(",");

                if (transactionDetails.length >= 7) {
                    String transactionID = transactionDetails[0];
                    String customerName = transactionDetails[1];
                    String customerID = transactionDetails[2];
                    String stockID = transactionDetails[3];
                    int quantity = Integer.parseInt(transactionDetails[4]);
                    double totalAmount = Double.parseDouble(transactionDetails[5]);
                    String transactionDateString = transactionDetails[6];

                    Date transactionDate = dateFormat.parse(transactionDateString);

                    if (isSameMonth(transactionDate, month, year)) {

                        transactionsDetails.add("Transaction ID: " + transactionID +
                                ", Date: " + transactionDateString +
                                ", Customer: " + customerName +
                                ", Stock ID: " + stockID +
                                ", Quantity: " + quantity +
                                ", Total: $" + totalAmount);

                        totalRevenue += totalAmount;
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading transaction file: " + e.getMessage());
            return;
        } catch (ParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
            return;
        }

        System.out.println("===== Sales Report =====");
        for (String transactionDetail : transactionsDetails) {
            System.out.println(transactionDetail);
        }

        System.out.println("\nTotal Revenue Earned: $" + totalRevenue);
        System.out.println("=========================");
    }

    private boolean isSameMonth(Date date, int month, int year) {

        SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
        String formattedDate = sdf.format(date);
        return formattedDate.equals(String.format("%02d-%d", month, year));
    }

    private boolean isWithinDateRange(Date date, String startDateStr, String endDateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

        try {

            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);

            return !date.before(startDate) && !date.after(endDate);
        } catch (ParseException e) {
            System.out.println("Error parsing date range: " + e.getMessage());
            return false;
        }
    }

    public void generateInventoryReport(String dateRange) {
        String[] dates = dateRange.split(" to ");
        String startDateStr = dates[0];
        String endDateStr = dates[1];

        String inventoryFileName = "Batch.csv";
        List<String> inventoryDetails = new ArrayList<>();
        double totalStockValue = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

        try (BufferedReader br = new BufferedReader(new FileReader(inventoryFileName))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] stockDetails = line.split(",");
                if (stockDetails.length >= 5) {
                    String stockID = stockDetails[0];
                    String stockName = stockDetails[1];

                    double quantity = Double.parseDouble(stockDetails[2]);
                    String expiryDateString = stockDetails[3];
                    double unitPrice = Double.parseDouble(stockDetails[4]);
                    Date expiryDate = dateFormat.parse(expiryDateString);

                    if (isWithinDateRange(expiryDate, startDateStr, endDateStr)) {
                        inventoryDetails.add("Stock ID: " + stockID + ", Name: " + stockName + ", Quantity: " + quantity
                                + ", Expiry Date: " + expiryDateString);
                        totalStockValue += quantity * unitPrice;
                    }
                }
            }
        } catch (IOException | ParseException e) {
            System.out.println("Error reading inventory file: " + e.getMessage());
            return;
        }

        // Print the report
        System.out.println("===== Inventory Report =====");
        for (String stockDetail : inventoryDetails) {
            System.out.println(stockDetail);
        }

        System.out.println("Total Stock Value: ₪ " + totalStockValue + " ILS");
        System.out.println("============================");
    }
}
