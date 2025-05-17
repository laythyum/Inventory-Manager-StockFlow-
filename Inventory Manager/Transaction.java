import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Transaction {
    private String transactionID;
    private String customerName;
    private String customerID;
    private String stockID;
    private int quantity;
    private double totalAmount;
    private Date transactionDate;
    private static final String TRANSACTION_FILE = "Transactions.csv";

    public Transaction() {

    }

    public Transaction(String transactionID, String customerName, String customerID, String stockID, int quantity,
            double totalAmount) {
        this.transactionID = transactionID;
        this.customerName = customerName;
        this.customerID = customerID;
        this.stockID = stockID;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.transactionDate = new Date();
    }

    public void saveTransaction() {
        File transactionFile = new File(TRANSACTION_FILE);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(transactionFile, true))) {

            if (transactionFile.length() == 0) {
                String headers = "Transaction ID,Customer Name,Customer ID,Stock ID,Quantity,Total Amount,Transaction Date";
                bw.write(headers);
                bw.newLine();
            }

            String transactionData = String.join(",",
                    transactionID,
                    customerName,
                    customerID,
                    stockID,
                    String.valueOf(quantity),
                    String.valueOf(totalAmount),
                    transactionDate.toString());

            bw.write(transactionData);
            bw.newLine();
            System.out.println("Transaction recorded successfully.");

        } catch (IOException e) {
            System.out.println("An error occurred while saving the transaction: " + e.getMessage());
        }
    }

    public String getTransactionID() {
        return transactionID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getStockID() {
        return stockID;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public static Transaction createTransaction(String customerName, String customerID, String stockID, int quantity,
            double pricePerUnit) {
        String transactionID = generateTransactionID();
        double totalAmount = quantity * pricePerUnit;
        Transaction transaction = new Transaction(transactionID, customerName, customerID, stockID, quantity,
                totalAmount);
        transaction.saveTransaction();
        return transaction;
    }

    private static String generateTransactionID() {
        return "TRANS" + System.currentTimeMillis();
    }

}
