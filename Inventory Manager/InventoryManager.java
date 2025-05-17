import java.io.IOException;

public class InventoryManager {
    public static void main(String[] args) throws IOException {

        // User creation
        Users admin = new Users();
        // admin.SignUp();
        // admin.SignInCheck();

        Stock stock = new Stock();
        stock.addStockToFile();
        stock.viewInventory();
        // stock.setRestockAlert();

        // Customer methods
        Customers customer = new Customers();
        customer.addCustomer();

        // Selling methods
        Sales sales = new Sales(admin);
        sales.sellProductRecursive();

        // Reports
        Reports report = new Reports();
        report.generateReport(10, 2024);

    }
}