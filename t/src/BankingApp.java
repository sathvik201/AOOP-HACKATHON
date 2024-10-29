import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

class BankDetails {
    private String accno;
    private String name;
    private String acc_type;
    private long balance;
    private int loginCode;
    private String username;
    private String password;
    private final ArrayList<String> transferHistory = new ArrayList<>();

    public void openAccount() {
        accno = JOptionPane.showInputDialog("Enter Account No:");
        acc_type = JOptionPane.showInputDialog("Enter Account Type:");
        name = JOptionPane.showInputDialog("Enter Name:");
        balance = Long.parseLong(JOptionPane.showInputDialog("Enter Balance:"));
        username = JOptionPane.showInputDialog("Enter Username:");
        password = JOptionPane.showInputDialog("Enter Password:");

        Random rand = new Random();
        loginCode = rand.nextInt(90000) + 10000;  // Generate a random 5-digit number
        JOptionPane.showMessageDialog(null, "Your 5-digit login code is: " + loginCode);
    }

    public boolean login(String user, String pass) {
        if (user.equals(username) && pass.equals(password)) {
            int code = Integer.parseInt(JOptionPane.showInputDialog("Enter your 5-digit login code:"));
            if (code == loginCode) {
                Random rand = new Random();
                loginCode = rand.nextInt(90000) + 10000;  // Generate a new 5-digit number
                JOptionPane.showMessageDialog(null, "Login successful. Your new 5-digit login code is: " + loginCode);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect login code.");
                return false;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Incorrect username or password.");
            return false;
        }
    }

    public void showAccount() {
        String details = "Name: " + name + "\nAccount No.: " + accno + "\nAccount Type: " + acc_type;
        JOptionPane.showMessageDialog(null, details);
    }

    public void deposit() {
        long amt = Long.parseLong(JOptionPane.showInputDialog("Enter the amount you want to deposit:"));
        balance += amt;
        JOptionPane.showMessageDialog(null, "Deposit successful. New balance: " + balance);
    }

    public void withdrawal() {
        long amt = Long.parseLong(JOptionPane.showInputDialog("Enter the amount you want to withdraw:"));
        if (balance >= amt) {
            balance -= amt;
            JOptionPane.showMessageDialog(null, "Withdrawal successful. New balance: " + balance);
        } else {
            JOptionPane.showMessageDialog(null, "Insufficient balance. Transaction failed.");
        }
    }

    public boolean search(String ac_no) {
        if (accno.equals(ac_no)) {
            showAccount();
            return true;
        }
        return false;
    }

    public void transfer(BankDetails receiver, long amount) {
        if (balance >= amount) {
            balance -= amount;
            receiver.balance += amount;
            transferHistory.add("Transferred " + amount + " to " + receiver.getAccno());
            JOptionPane.showMessageDialog(null, "Transfer successful. New balance: " + balance);
        } else {
            JOptionPane.showMessageDialog(null, "Insufficient balance. Transfer failed.");
        }
    }

    public void showTransferHistory() {
        StringBuilder history = new StringBuilder("Transfer History:\n");
        for (String record : transferHistory) {
            history.append(record).append("\n");
        }
        JOptionPane.showMessageDialog(null, history.toString());
    }

    public String getAccno() {
        return accno;
    }
}

public class BankingApp extends JFrame {
    private final ArrayList<BankDetails> customers = new ArrayList<>();
    private BankDetails currentUser;

    public BankingApp() {
        setTitle("Banking Application");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        JButton createAccountBtn = new JButton("Create New Account");
        JButton loginBtn = new JButton("Login");

        createAccountBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BankDetails customer = new BankDetails();
                customer.openAccount();
                customers.add(customer);
            }
        });

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog("Enter Username:");
                String password = JOptionPane.showInputDialog("Enter Password:");
                for (BankDetails cust : customers) {
                    if (cust.login(username, password)) {
                        currentUser = cust;
                        showBankingOptions();
                        break;
                    }
                }
            }
        });

        panel.add(createAccountBtn);
        panel.add(loginBtn);
        add(panel);
    }

    private void showBankingOptions() {
        while (true) {
            String[] options = {
                    "Display all account details",
                    "Search by Account number",
                    "Deposit the amount",
                    "Withdraw the amount",
                    "Transfer money",
                    "Show transfer history",
                    "Logout",
                    "Exit"
            };
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Select an option:",
                    "Banking Options",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                case 0:
                    for (BankDetails cust : customers) {
                        cust.showAccount();
                    }
                    break;
                case 1:
                    String accountNumberSearch = JOptionPane.showInputDialog("Enter account no. you want to search:");
                    boolean found = false;
                    for (BankDetails cust : customers) {
                        found = cust.search(accountNumberSearch);
                        if (found) break;
                    }
                    if (!found) {
                        JOptionPane.showMessageDialog(null, "Account not found.");
                    }
                    break;
                case 2:
                    currentUser.deposit();
                    break;
                case 3:
                    currentUser.withdrawal();
                    break;
                case 4:
                    String receiverAccountNumber = JOptionPane.showInputDialog("Enter receiver's account no.:");
                    long amount = Long.parseLong(JOptionPane.showInputDialog("Enter amount to transfer:"));
                    BankDetails receiver = null;
                    for (BankDetails cust : customers) {
                        if (cust.getAccno().equals(receiverAccountNumber)) {
                            receiver = cust;
                            break;
                        }
                    }
                    if (receiver != null) {
                        currentUser.transfer(receiver, amount);
                    } else {
                        JOptionPane.showMessageDialog(null, "Receiver's account number is incorrect.");
                    }
                    break;
                case 5:
                    currentUser.showTransferHistory();
                    break;
                case 6:
                    JOptionPane.showMessageDialog(null, "Logging out...");
                    currentUser = null;
                    return;
                case 7:
                    JOptionPane.showMessageDialog(null, "Exiting the application. Thank you!");
                    System.exit(0);
                    break;
                default:
                    break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankingApp app = new BankingApp();
            app.setVisible(true);
        });
    }
}
