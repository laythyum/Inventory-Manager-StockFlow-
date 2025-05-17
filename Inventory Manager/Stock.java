import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Random;

public class Stock {
    private String stockName;
    private String stockID;
    private double price;
    private double cost;
    private int stockSize;
    private File picture;
    private String type; // Attribute for stock type
    private String expiryDate;
    private File stockFile; // File object for Stock.txt
    private File BatchFile;
    private BufferedWriter bw; // BufferedWriter for writing to the file
    private BufferedReader br; // BufferedReader for reading from the file
    private static Set<Integer> generatedID;
    private static Random random = new Random();

    public Stock() {
        new HashSet<>();
        this.stockFile = new File("Inventory.csv");
        this.BatchFile = new File("Batch.csv");
        try {
            this.bw = new BufferedWriter(new FileWriter(stockFile, true));
            this.br = new BufferedReader(new FileReader(stockFile));
        } catch (IOException e) {
            System.out.println("Error initializing file writers: " + e.getMessage());
        }
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockID() {
        return stockID;
    }

    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    public int getStockSize() {
        return stockSize;
    }

    public void setStockSize(int stockSize) {
        this.stockSize = stockSize;
    }

    public File getPicture() {
        return picture;
    }

    public void setPicture(File picture) {
        this.picture = picture;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public File getStockFile() {
        return stockFile;
    }

    public void setStockFile(File stockFile) {
        this.stockFile = stockFile;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String generateId() {
        StringBuilder idBuilder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            int digit = random.nextInt(10);
            idBuilder.append(digit);
        }
        for (int i = 0; i < 3; i++) {
            char letter = (char) ('A' + random.nextInt(26));
            idBuilder.append(letter);
        }
        return idBuilder.toString();

    }

    private String getExistingStockId(String stockName) {
        String fileName = "Inventory.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2 && data[0].equalsIgnoreCase(stockName)) {
                    return data[1];
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return null;
    }

    public void addStockToFile() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Stock Name: ");
        this.stockName = scanner.nextLine();

        this.stockID = getExistingStockId(this.stockName);
        if (this.stockID == null) {
            System.out.print("Generating ID...: ");
            this.stockID = generateId();
            System.out.println(this.stockID);
        } else {
            System.out.println("Stock already exists. Using existing ID: " + this.stockID);
        }

        System.out.print("Enter Stock Type: ");
        this.type = scanner.nextLine().toLowerCase();

        System.out.print("Enter Price: ");
        this.price = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter Cost: ");
        this.cost = scanner.nextDouble();
        scanner.nextLine();

        String fileName = "Inventory.csv";
        File stockFile = new File(fileName);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(stockFile, true))) {
            if (stockFile.length() == 0) {
                bw.write("Stock Name,Stock ID,Type,Price,Cost\n");
            }

            String formattedPrice = String.format("%.2f", this.price);
            String formattedCost = String.format("%.2f", this.cost);
            bw.write(this.stockName + "," + this.stockID + "," + this.type + "," + formattedPrice + "," + formattedCost
                    + "\n");
            System.out.println("Stock item saved successfully!");

        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }

        Batch batch = new Batch();

        System.out.print("Enter Quantity for this batch: ");
        int quantity = scanner.nextInt();

        System.out.print("Enter Batch Price for this stock item: ");
        double batchPrice = scanner.nextDouble();

        System.out.print("Enter Cost per Unit for this batch: ");
        double costPerUnit = scanner.nextDouble();

        System.out.print("Enter Expiry Date for this batch (DD-MM-YYYY): ");
        scanner.nextLine();
        String expiryDate = scanner.nextLine();

        // Add batch to file
        batch.addBatchToFile(this.stockID, batchPrice, expiryDate, quantity, costPerUnit);

        System.out.print("Do you want to add another stock item? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            addStockToFile();
        }
    }

    public void restockProduct() {
        Scanner scanner = new Scanner(System.in);

        viewInventory();

        System.out.print("Enter Product ID to restock: ");
        String productID = scanner.nextLine();

        System.out.print("Enter Quantity to restock: ");
        int quantity = scanner.nextInt();

        Batch batch = new Batch();

        System.out.print("Enter Batch Price for Product ID " + productID + ": ");
        double batchPrice = scanner.nextDouble();

        System.out.print("Enter Cost per Unit for Product ID " + productID + ": ");
        double costPerUnit = scanner.nextDouble();

        System.out.print("Enter Expiry Date for this batch (DD-MM-YYYY): ");
        scanner.nextLine();
        String expiryDate = scanner.nextLine();

        batch.addBatchToFile(productID, batchPrice, expiryDate, quantity, costPerUnit);

        System.out.print("Do you want to add another batch? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            restockProduct();
        }
    }

    public void viewInventoryByType() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter Stock Type to View: ");
            String typeToView = scanner.nextLine().toLowerCase().trim();

            String fileName = "Inventory.csv";
            File stockFile = new File(fileName);

            if (!stockFile.exists()) {
                System.out.println("Inventory file not found.");
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(stockFile))) {
                String line;
                boolean found = false;

                while ((line = br.readLine()) != null) {

                    String[] stockDetails = line.split(",");

                    if (stockDetails.length == 4) {
                        String stockType = stockDetails[2].toLowerCase();

                        if (stockType.equals(typeToView)) {
                            found = true; // Type found
                            System.out.println("Viewing stock of type: " + stockType);

                            System.out.println("Stock Name: " + stockDetails[0]);
                            System.out.println("Stock ID: " + stockDetails[1]);
                            System.out.println("Type: " + stockDetails[2]);
                            System.out.println("Price: " + stockDetails[3]);
                            System.out.println("-------------------------------");
                        }
                    } else {
                        System.out.println("Incorrect data format for line: " + line);
                    }
                }

                if (!found) {
                    System.out.println("No stock items of type \"" + typeToView + "\" found.");
                }
            } catch (IOException e) {
                System.out.println("Error reading from file: " + e.getMessage());
            }
        }
    }

    public void viewInventory() {
        String fileName = "Inventory.csv";
        File stockFile = new File(fileName);

        if (!stockFile.exists()) {
            System.out.println("Inventory file not found at: " + stockFile.getAbsolutePath());
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(stockFile))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] stockDetails = line.split(",");

                if (stockDetails.length == 5) {

                    System.out.println("Viewing stock...");
                    System.out.println("Stock Name: " + stockDetails[0]);
                    System.out.println("Stock ID: " + stockDetails[1]);
                    System.out.println("Type: " + stockDetails[2]);
                    System.out.println("Price: " + stockDetails[3]);
                    System.out.println("Cost: " + stockDetails[4]);
                    System.out.println("-------------------------------");
                } else {
                    System.out.println("Incorrect data format for line: " + line);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SearchByID() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter the ID of the product: ");
            String idToView = scanner.nextLine().trim();

            String fileName = "Inventory.csv";
            File stockFile = new File(fileName);

            if (!stockFile.exists()) {
                System.out.println("Inventory file not found.");
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(stockFile))) {
                String line;
                boolean found = false;

                while ((line = br.readLine()) != null) {
                    String[] stockDetails = line.split(",");

                    if (stockDetails.length == 5 && stockDetails[1].equalsIgnoreCase(idToView)) { // Updated to 5
                        found = true;

                        System.out.println("Viewing stock...");
                        System.out.println("Stock Name: " + stockDetails[0]);
                        System.out.println("Stock ID: " + stockDetails[1]);
                        System.out.println("Type: " + stockDetails[2]);
                        System.out.println("Price: " + stockDetails[3]);
                        System.out.println("Cost: " + stockDetails[4]);
                        System.out.println("-------------------------------");
                        break;
                    }
                }

                if (!found) {
                    System.out.println("Product with ID " + idToView + " not found.");
                }
            } catch (IOException e) {
                System.out.println("Error reading from the inventory file: " + e.getMessage());
            }
        }
    }

    public void closeResources() {
        try {
            if (bw != null) {
                bw.close();
                System.out.println("BufferedWriter closed.");
            }
            if (br != null) {
                br.close();
                System.out.println("BufferedReader closed.");
            }
        } catch (IOException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }

    public void restockProduct(int productId, int quantity) {

    }

    public void viewLowStockItems() {
        String fileName = "Batch.csv";
        File batchFile = new File(fileName);

        if (!batchFile.exists()) {
            System.out.println("Batch file not found.");
            return;
        }

        List<String[]> lowStockList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(batchFile))) {
            String line;

            if ((line = br.readLine()) != null) {

            }

            while ((line = br.readLine()) != null) {
                String[] batchDetails = line.split(",");
                if (batchDetails.length >= 6) {
                    int amount = Integer.parseInt(batchDetails[4]);

                    if (amount < 5) {
                        lowStockList.add(batchDetails);
                    }
                }
            }

            System.out.println("Low Stock Items (Amount < 5):");
            for (String[] batch : lowStockList) {
                String batchID = batch[0];
                String productID = batch[1];
                String price = batch[2];
                String expiryDate = batch[3];
                String amount = batch[4];
                String costPerUnit = batch[5];

                System.out.println("Batch ID: " + batchID);
                System.out.println("Product ID: " + productID);
                System.out.println("Price: " + price);
                System.out.println("Expiry Date: " + expiryDate);
                System.out.println("Amount Left: " + amount);
                System.out.println("Cost Per Unit: " + costPerUnit);
                System.out.println("-----------------------------");
            }

        } catch (IOException e) {
            System.out.println("Error reading the batch file: " + e.getMessage());
        }
    }

    public void setRestockAlert() {
        checkStockAlert(1);
    }

    private void checkStockAlert(int lineNum) {
        String fileName = "Batch.csv";
        File batchFile = new File(fileName);

        if (!batchFile.exists()) {
            System.out.println("Batch file not found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(batchFile))) {

            for (int i = 0; i < lineNum; i++) {
                br.readLine();
            }

            String line = br.readLine();
            if (line == null) {
                return;
            }

            String[] batchDetails = line.split(",");
            if (batchDetails.length >= 6) {

                int amount = Integer.parseInt(batchDetails[4]);

                if (amount < 5) {
                    System.out.println("Low stock alert for Batch ID: " + batchDetails[0] + " (Product ID: "
                            + batchDetails[1] + ").");
                    System.out.println("Amount left: " + amount);
                }
            }

            checkStockAlert(lineNum + 1);

        } catch (IOException e) {
            System.out.println("Error reading from batch file: " + e.getMessage());
        }
    }

}