import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
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

public class Batch {
    private String batchID;
    private String productID;
    private double price;
    private String expiryDate;
    private int amount;
    private static final Random random = new Random();

    public Batch() {
    }

    public Batch(String productID, double price, String expiryDate, int amount) {
        this.batchID = generateBatchId();
        this.productID = productID;
        this.price = price;
        this.expiryDate = expiryDate;
        this.amount = amount;
    }

    public String generateBatchId() {
        StringBuilder idBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int digit = random.nextInt(10);
            idBuilder.append(digit);
        }
        for (int i = 0; i < 4; i++) {
            char letter = (char) ('A' + random.nextInt(26));
            idBuilder.append(letter);
        }
        return idBuilder.toString();
    }

    public void addBatchToFile(String productID, double price, String expiryDate, int amount, double costPerUnit) {
        String batchID = generateBatchId();
        String filename = "Batch.csv";
        File batchFile = new File(filename);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {

            if (batchFile.length() == 0) {
                bw.write("BatchID,ProductID,Price,ExpiryDate,Amount,CostPerUnit\n");
            }

            bw.write(batchID + "," + productID + "," + price + "," + expiryDate + "," + amount + "," + costPerUnit
                    + "\n");
            System.out.println("Batch saved successfully!");

        } catch (IOException e) {
            System.out.println("ERROR WRITING TO FILE: " + e.getMessage());
        }
    }

    public void viewBatches() {
        String fileName = "Batch.csv";
        File batchFile = new File(fileName);

        if (!batchFile.exists()) {
            System.out.println("Batch file not found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(batchFile))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] batchDetails = line.split(",");

                if (batchDetails.length == 5) {

                    System.out.println("Viewing batch...");
                    System.out.println("Batch ID: " + batchDetails[0]);
                    System.out.println("Product ID: " + batchDetails[1]);
                    System.out.println("Price: " + batchDetails[2]);
                    System.out.println("Expiry Date: " + batchDetails[3]);
                    System.out.println("Amount: " + batchDetails[4]);
                    System.out.println("-------------------------------");
                } else {
                    System.out.println("Incorrect data format for line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from batch file: " + e.getMessage());
        }
    }

}
