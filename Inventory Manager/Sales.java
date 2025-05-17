import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Sales extends Stock {

    private Users admin;

    public Sales(Users admin) {
        this.admin = admin;
    }

    public Sales() {

    }

    public void sellProduct() throws ParseException {
        Scanner scanner = new Scanner(System.in);

        viewInventory();
        System.out.print("Enter Stock ID: ");
        String stockID = scanner.nextLine();

        System.out.print("Enter Quantity to sell: ");
        int quantityToSell = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Customer ID: ");
        String customerID = scanner.nextLine();

        int soldQuantity = processSaleFromBatch(stockID, quantityToSell, customerID);
        if (soldQuantity > 0) {
            System.out.println("Successfully sold " + soldQuantity + " units of Stock ID: " + stockID);
        } else {
            System.out.println("Sale failed or insufficient stock.");
        }

        System.out.print("Do you want to sell another product? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            sellProduct(); // Recursive call to sell another product
        }
    }

    public int sellProductRecursive() {
        Scanner sc = new Scanner(System.in);
        viewInventory();
        System.out.print("Enter Stock ID: ");
        String stockID = sc.nextLine();

        System.out.print("Enter Quantity: ");
        int quantity = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Customer ID: ");
        String customerID = sc.nextLine();

        return processSaleFromBatch(stockID, quantity, customerID);
    }

    private int processSaleFromBatch(String stockID, int quantityToSell, String customerID) {
        List<String[]> batches = new ArrayList<>();
        String fileName = "Batch.csv";
        File batchFile = new File(fileName);
        double totalPrice = 0;

        if (!batchFile.exists()) {
            System.out.println("Batch file not found.");
            return -1;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(batchFile))) {
            String line;
            String[] nearestBatch = null;
            Date nearestExpiryDate = null;
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("d-M-yyyy");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("d/M/yyyy");

            while ((line = br.readLine()) != null) {
                String[] batchDetails = line.split(",");
                batches.add(batchDetails);
                if (batchDetails.length >= 6 && batchDetails[1].equalsIgnoreCase(stockID)) {
                    Date expiryDate = null;

                    try {
                        expiryDate = dateFormat1.parse(batchDetails[3]);
                    } catch (ParseException e1) {
                        try {
                            expiryDate = dateFormat2.parse(batchDetails[3]);
                        } catch (ParseException e2) {
                            System.out.println("Invalid date format for batch: " + batchDetails[0]);
                            continue;
                        }
                    }

                    int currentQuantity = Integer.parseInt(batchDetails[4]);

                    if (nearestExpiryDate == null || expiryDate.before(nearestExpiryDate)) {
                        nearestExpiryDate = expiryDate;
                        nearestBatch = batchDetails;
                    }
                }
            }

            if (nearestBatch != null) {
                int currentBatchQuantity = Integer.parseInt(nearestBatch[4]);

                if (currentBatchQuantity >= quantityToSell) {
                    currentBatchQuantity -= quantityToSell;
                    nearestBatch[4] = String.valueOf(currentBatchQuantity);

                    double costPerUnit = Double.parseDouble(nearestBatch[5]);
                    totalPrice = costPerUnit * quantityToSell;

                    updateBatchFile(batchFile, batches, nearestBatch);

                    recordTransaction(stockID, quantityToSell, customerID, costPerUnit);
                    Customers customers = new Customers();
                    customers.updateBalance(customerID, -totalPrice);

                    System.out.println("Sold " + quantityToSell + " units of stock ID: " + stockID);
                    return quantityToSell;
                } else {
                    System.out.println("Insufficient stock available in the nearest expiring batch.");
                    return -1;
                }
            } else {
                System.out.println("Product ID " + stockID + " not found.");
                return -1;
            }

        } catch (IOException e) {
            System.out.println("Error processing the batches file: " + e.getMessage());
            return -1;
        }
    }

    private void updateBatchFile(File batchFile, List<String[]> batches, String[] updatedBatch) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(batchFile))) {
            for (String[] batch : batches) {
                if (batch[0].equals(updatedBatch[0])) {
                    bw.write(String.join(",", updatedBatch));
                } else {
                    bw.write(String.join(",", batch));
                }
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to the batch file: " + e.getMessage());
        }
    }

    private void recordTransaction(String stockID, double quantity, String customerID, double pricePerUnit) {

        String customerName = getCustomerNameByID(customerID);

        Transaction.createTransaction(customerName, customerID, stockID, (int) quantity, pricePerUnit);

        System.out.println("Transaction recorded successfully for stock ID: " + stockID);
    }

    private String getCustomerNameByID(String customerID) {
        String customersFileName = "Customers.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(customersFileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] customerDetails = line.split(",");

                if (customerDetails.length >= 2 && customerDetails[1].equalsIgnoreCase(customerID)) {
                    return customerDetails[0];
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading customer file: " + e.getMessage());
        }

        return "Unknown Customer";
    }

}
