import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Customers {
    private String CustomerName;
    private String CustomerID;
    private String PhoneNumber;
    private String Email;
    private double balance = 0.0;
    private static final String CUSTOMER_FILE = "Customers.csv";
    private static Random random = new Random();

    public Customers() {
    }

    public Customers(String customerName, String customerID, String phoneNumber, String email, double balance) {
        this.CustomerName = customerName;
        this.CustomerID = customerID;
        this.PhoneNumber = phoneNumber;
        this.Email = email;
        this.balance = balance;
    }

    // Getters and Setters
    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
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

    public void addCustomer() throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Customer Name: ");
        this.CustomerName = scanner.nextLine();

        System.out.println("Generating user ID...");
        CustomerID = generateId();

        System.out.print("Enter Phone Number: ");
        this.PhoneNumber = scanner.nextLine();

        System.out.print("Enter Email: ");
        this.Email = scanner.nextLine();

        this.balance = 0.0;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CUSTOMER_FILE, true))) {

            if (new BufferedReader(new FileReader(CUSTOMER_FILE)).readLine() == null) {
                bw.write("Customer Name,Customer ID,Phone Number,Email,Balance");
                bw.newLine();
            }

            bw.write(this.CustomerName + "," + this.CustomerID + "," + this.PhoneNumber + "," + this.Email + ","
                    + this.balance + "\n");
            System.out.println("Customer added successfully.");
            bw.flush();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    public void updateBalance(String customerID, double amount) {
        List<String[]> customersList = new ArrayList<>();
        String customersFileName = "Customers.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(customersFileName))) {
            String line;
            boolean found = false;

            while ((line = br.readLine()) != null) {
                String[] customerDetails = line.split(",");
                if (customerDetails[1].equalsIgnoreCase(customerID)) {
                    double currentBalance = Double.parseDouble(customerDetails[4]);
                    currentBalance += amount;
                    customerDetails[4] = String.valueOf(currentBalance);
                    found = true;
                }
                customersList.add(customerDetails);
            }

            if (found) {

                try (BufferedWriter bw = new BufferedWriter(new FileWriter(customersFileName))) {
                    for (String[] customer : customersList) {
                        bw.write(String.join(",", customer));
                        bw.newLine();
                    }
                }
            } else {
                System.out.println("Customer ID " + customerID + " not found.");
            }
        } catch (IOException e) {
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }

    public void processPayment() throws IOException {
        List<String> customers = new ArrayList<>();
        String line;
        Scanner scanner = new Scanner(System.in);

        try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMER_FILE))) {
            System.out.println("List of Customers and Their Balance:");
            int index = 1;
            while ((line = br.readLine()) != null) {
                String[] customerDetails = line.split(",");
                if (!line.startsWith("Customer Name")) {
                    System.out.println(index + ". " + customerDetails[0] + " (ID: " + customerDetails[1]
                            + ") - Balance: " + customerDetails[4]);
                    customers.add(line);
                    index++;
                }
            }
        }

        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        System.out.print("Enter the number corresponding to the customer who wants to make a payment: ");
        int customerIndex = Integer.parseInt(scanner.nextLine()) - 1;

        if (customerIndex >= 0 && customerIndex < customers.size()) {
            String[] selectedCustomerDetails = customers.get(customerIndex).split(",");

            String customerID = selectedCustomerDetails[1];
            double currentBalance = Double.parseDouble(selectedCustomerDetails[4]);

            System.out.println("Current balance for " + selectedCustomerDetails[0] + " (ID: " + customerID + "): "
                    + currentBalance);
            System.out.print("Enter the payment amount: ");
            double paymentAmount = Double.parseDouble(scanner.nextLine());

            if (paymentAmount > 0) {
                currentBalance -= paymentAmount;
                selectedCustomerDetails[4] = String.valueOf(currentBalance);
                System.out.println(
                        "Payment of " + paymentAmount + " processed successfully. New balance: " + currentBalance);
            } else {
                System.out.println("Invalid payment amount. Payment must be positive.");
                return;
            }

            customers.set(customerIndex, String.join(",", selectedCustomerDetails));

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(CUSTOMER_FILE))) {
                bw.write("Customer Name,Customer ID,Phone Number,Email,Balance");
                bw.newLine();
                for (String customer : customers) {
                    bw.write(customer);
                    bw.newLine();
                }
            }
        } else {
            System.out.println("Invalid selection.");
        }
    }

    public static List<Customers> loadCustomersFromCSV(String filename) {
        List<Customers> customers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {

                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = line.split(",");
                if (fields.length < 5) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }

                String customerName = fields[0];
                String customerID = fields[1];
                String phoneNumber = fields[2];
                String email = fields[3];
                double balance;

                try {
                    balance = Double.parseDouble(fields[4]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid balance format for customer: " + customerName);
                    balance = 0.0;
                }

                Customers customer = new Customers(customerName, customerID, phoneNumber, email, balance);
                customers.add(customer);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return customers;
    }
}
