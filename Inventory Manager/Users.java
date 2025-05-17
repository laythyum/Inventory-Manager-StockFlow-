import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Users {
    protected String name;
    protected String email;
    protected String password;
    protected String phoneNumber;
    private File UserFile;
    private BufferedWriter bw;
    private BufferedReader br;
    Scanner in = new Scanner(System.in);

    public Users() throws IOException {
        this.UserFile = new File("Users.csv");
        if (!UserFile.exists() || UserFile.length() == 0) {
            this.bw = new BufferedWriter(new FileWriter(UserFile));
            // Write header here if necessary
            bw.write("Name,Email,Password,AdminID,PhoneNumber\n");
            bw.flush();
        } else {
            this.bw = new BufferedWriter(new FileWriter(UserFile, true));
        }
        this.br = new BufferedReader(new FileReader(UserFile));
    }

    // Getter and setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void addUser() throws IOException {
        System.out.println("Please enter your name:");
        this.name = in.nextLine();

        System.out.println("Enter your Email:");
        this.email = in.nextLine();

        System.out.println("Enter your phone number:");
        this.phoneNumber = in.nextLine();

        // Generate Admin ID
        String adminID = generateAdminID();

        // Write the user data to the file in CSV format
        bw.write(this.name + "," + this.email + "," + this.password + "," + adminID + "," + this.phoneNumber + "\n");
        bw.flush();

        System.out.println("User added successfully!");
    }

    public void displayInfo() {
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Phone Number: " + phoneNumber);
    }

    public void enterPassword() {
        System.out.println("Enter password:");
        this.password = in.nextLine();
        passwordComplexity(this.password);
    }

    public void checkPassword() {
        String currentPass = this.password;
        System.out.println("Re-enter password:");
        String newSamePassword = in.nextLine();
        if (currentPass.equals(newSamePassword)) {
            System.out.println("Passwords match.");
        } else {
            System.out.println("Password is incorrect.");
            enterPassword();
        }
    }

    public void passwordComplexity(String password) {
        if (password.length() < 8) {
            System.out.println("Password must be at least 8 characters long.");
            return;
        }
        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*[0-9].*")) {
            System.out.println("Password must contain both letters and numbers.");
            return;
        }
        System.out.println("Password is valid.");
    }

    private String generateAdminID() {
        Random random = new Random();
        StringBuilder adminID = new StringBuilder("ADMIN");
        for (int i = 0; i < 5; i++) {
            adminID.append(random.nextInt(10));
        }
        return adminID.toString();
    }

    public void SignUp() throws IOException {
        System.out.println("Enter your name:");
        this.name = in.nextLine();

        System.out.println("Enter your Email:");
        this.email = in.nextLine();

        System.out.println("Create password:");
        enterPassword();
        checkPassword();

        System.out.println("Enter your phone number:");
        this.phoneNumber = in.nextLine();

        String adminID = generateAdminID();
        System.out.println("Your Admin ID is: " + adminID);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(UserFile, true))) {
            bw.write(
                    this.name + "," + this.email + "," + this.password + "," + adminID + "," + this.phoneNumber + "\n");
            System.out.println("User signed up successfully!");
        } catch (IOException e) {
            System.out.println("Error writing to user file: " + e.getMessage());
        }
    }

    public void SignInCheck() throws IOException {
        System.out.println("Enter username:");
        String inputUsername = in.nextLine();

        System.out.println("Enter Password:");
        String inputPassword = in.nextLine();

        try (BufferedReader br = new BufferedReader(new FileReader(UserFile))) {
            String line;
            boolean authenticated = false;

            while ((line = br.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails.length >= 5) {
                    if (userDetails[0].equals(inputUsername) && userDetails[2].equals(inputPassword)) {
                        authenticated = true;
                        this.name = userDetails[0];
                        this.phoneNumber = userDetails[4];
                        break;
                    }
                } else {
                    System.out.println("Warning: User data line is malformed: " + line);
                }
            }

            if (authenticated) {
                System.out.println("Sign-in successful!");
                System.out.println("Welcome, " + name + ".");
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
    }
}
