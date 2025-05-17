import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Monetary {
    private static final String PAYMENT_FILE = "Payments.csv";
    private List<Customers> customers;

    public Monetary() {
        this.customers = Customers.loadCustomersFromCSV("Customers.csv");
    }

    public void acceptPayment() {
        Scanner scanner = new Scanner(System.in);

        if (customers == null || customers.isEmpty()) {
            System.out.println("No customers available for payment.");
            return;
        }

        System.out.println("Select a customer to process payment:");
        for (int i = 0; i < customers.size(); i++) {
            Customers customer = customers.get(i);
            System.out.printf("%d. %s (ID: %s, Balance: %.2f)\n", i + 1, customer.getCustomerName(),
                    customer.getCustomerID(), customer.getBalance());
        }

        System.out.print("Enter the number of the customer: ");
        int choice = scanner.nextInt();

        if (choice < 1 || choice > customers.size()) {
            System.out.println("Invalid choice. Please try again.");
            return;
        }

        Customers selectedCustomer = customers.get(choice - 1);

        System.out.print("Enter the payment amount: ");
        double amount = scanner.nextDouble();

        if (amount <= 0) {
            System.out.println("Invalid payment amount. Payment must be greater than zero.");
            return;
        }

        try {

            double newBalance = selectedCustomer.getBalance() + amount;
            selectedCustomer.setBalance(newBalance);

            logPayment(selectedCustomer.getCustomerID(), amount);
            System.out.printf("Payment of %.2f received from %s.\n", amount, selectedCustomer.getCustomerName());

            updateCustomerFile();
        } catch (Exception e) {
            System.out.println("Error processing payment: " + e.getMessage());
        }
    }

    private void updateCustomerFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Customers.csv"))) {
            for (Customers customer : customers) {
                String customerData = String.join(",", customer.getCustomerName(), customer.getCustomerID(),
                        customer.getPhoneNumber(), customer.getEmail(), String.valueOf(customer.getBalance()));
                bw.write(customerData);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating customer file: " + e.getMessage());
        }
    }

    // Helper method to log payment in the CSV file
    private void logPayment(String customerID, double amount) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PAYMENT_FILE, true))) {
            String paymentData = customerID + "," + amount + "," + System.currentTimeMillis();
            bw.write(paymentData);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error logging payment: " + e.getMessage());
        }
    }

}