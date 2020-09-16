import java.sql.*;
import java.util.Scanner;

public class ATM {

    public static void main(String[] args) {


        Scanner sc = new Scanner(System.in);

        // Creates an example bank
        Bank bankNBB = new Bank("Normal National Bank (NNB)");

        // Creates an example user
        User exampleUser = bankNBB.addUser("John", "Doe", "1234");

        Account newAccount = new Account("Checking", exampleUser, bankNBB);
        exampleUser.addAccount(newAccount);
        bankNBB.addAccount(newAccount);


        User curUser;
        while (true) {
            // stay in login prompt until successful login
            curUser = ATM.mainMenuPrompt(bankNBB, sc);

            // stay in main menu until user quits
            ATM.printUserMenu(curUser, sc);
        }
    }

    /**
     * Prints the ATM's login menu
     *
     * @param theBank the Bank object whose accounts to use
     * @param sc      the Scanner object to use for user input
     * @return authenticated user object
     */
    public static User mainMenuPrompt(Bank theBank, Scanner sc) {
        String userID;
        String pin;
        User authUser;
        int register;

        // prompt the user for user ID/pin combo until a correct one is reached
        do {
            authUser = null;
            System.out.printf("\n\nWelcome to %s \n", theBank.getName());
            System.out.println("please select one of the options provided bellow: ");
            System.out.println("    1) Log in for existing users");
            System.out.println("    2) Register new account");
            System.out.println("    3) to import existing users from banks database");
            System.out.println("Enter option: ");
            register = sc.nextInt();
            if (register == 1) {
                sc.nextLine();
                System.out.println("Enter user ID: ");
                userID = sc.nextLine();
                System.out.println("Enter pin: ");
                pin = sc.nextLine();

                // try to get object corresponding to ID and pin combo
                authUser = theBank.userLogin(userID, pin);
                if (authUser == null) {
                    System.out.println("Incorrect user ID or pin number. Please try again.");
                }
            } else if (register == 2) {
                sc.nextLine();
                System.out.println("Please provide your first name: ");
                String firstNameOfNewUser = sc.nextLine();
                System.out.println("Please provide your last name: ");
                String lastNameOfNewUser = sc.nextLine();
                System.out.println("Please provide your desired pin for this account: ");
                String pinOfNewUser = sc.nextLine();

                String url = "jdbc:mysql://localhost:3306/bank_users?useTimezone=true&serverTimezone=UTC";

                try {
                    // Establish Connection Object
                    Connection conn = DriverManager.getConnection(url, "root", "test");

                    // create a statement object to send to the database
                    Statement statement = conn.createStatement();

                    // execute prepared statement object
                    PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users_tbl (FirstName, LastName, Pin) VALUE (?,?,?)");
                    pstmt.setString(1, firstNameOfNewUser);
                    pstmt.setString(2, lastNameOfNewUser);
                    pstmt.setString(3, pinOfNewUser);
                    pstmt.executeUpdate();

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                theBank.addUser(firstNameOfNewUser, lastNameOfNewUser, pinOfNewUser);
            } else if (register == 3) {
                String url = "jdbc:mysql://localhost:3306/bank_users?useTimezone=true&serverTimezone=UTC";

                try {
                    // Establish Connection Object
                    Connection conn = DriverManager.getConnection(url, "root", "test");

                    // create a statement object to send to the database
                    Statement statement = conn.createStatement();


                    ResultSet resultSet = statement.executeQuery("select * from users_tbl");

                    // process the result
                    while (resultSet.next()) {
                        String firstNameOfNewUser = resultSet.getString("FirstName");
                        String lastNameOfNewUser = resultSet.getString("LastName");
                        String pinOfNewUser = resultSet.getString("Pin");
                        if (theBank.checkBankUsers(firstNameOfNewUser, lastNameOfNewUser) == false) {
                            theBank.addUser(firstNameOfNewUser, lastNameOfNewUser, pinOfNewUser);
                        }
                    }

                    System.out.println("Imported data from remote database");

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            } else if (register != 1 && register != 2 && register != 3) {
                System.out.println("Please select a valid option");
            }
        }
        while (authUser == null); // continue looping until successful login

        return authUser;

    }

    public static void printUserMenu(User theUser, Scanner sc) {

        // print summary of users account
        theUser.printAccountsSummary();

        int choice;

        // user menu
        do {
            System.out.printf("Welcome %s, what would you like to do?\n", theUser.getFirstName());
            System.out.println("    1) Show account transaction history");
            System.out.println("    2) Withdraw");
            System.out.println("    3) Deposit");
            System.out.println("    4) Transfer");
            System.out.println("    5) Exit");
            System.out.println();
            System.out.println("Enter choice: ");
            choice = sc.nextInt();

            if (choice < 1 || choice > 5) {
                System.out.println("Invalid choice, please select options 1-5");
            }
        } while (choice < 1 || choice > 5);

        // process the choice
        switch (choice) {

            case 1:
                ATM.showTransactionHistory(theUser, sc);
                break;
            case 2:
                ATM.withdrawFunds(theUser, sc);
                break;
            case 3:
                ATM.depositFunds(theUser, sc);
                break;
            case 4:
                ATM.trasnferFunds(theUser, sc);
                break;
            case 5:
                sc.nextLine();
                break;
        }

        // redisplay this menu unless the user wants to quit
        if (choice != 5) {
            ATM.printUserMenu(theUser, sc);
        }
    }

    /**
     * Shows transaction hisotry for an account
     *
     * @param theUser the logged-in User object
     * @param sc      the Scanner object used for user input
     */
    public static void showTransactionHistory(User theUser, Scanner sc) {

        int theAcct;

        // get account whose transaction history to look at
        do {
            System.out.printf("Enter the number (1-%d) of the account whose transactions you want to see: ", theUser.numAccounts());
            theAcct = sc.nextInt() - 1;
            if (theAcct < 0 || theAcct >= theUser.numAccounts()) {
                System.out.println("Invalid account. Please try again.");
            }
        } while (theAcct < 0 || theAcct >= theUser.numAccounts());

        // print transaction history
        theUser.printAcctTransHistory(theAcct);
    }

    /**
     * Process transferring funds from one account to another
     *
     * @param theUser the logged-in User object
     * @param sc      the Scanner object used for user input
     */
    public static void trasnferFunds(User theUser, Scanner sc) {
        int fromAcct;
        int toAcct;
        double amount;
        double acctBal;

        // get the account to transfer from
        do {
            System.out.printf("Enter the number (1-%d) of the account\n to transfer from: ", theUser.numAccounts());
            fromAcct = sc.nextInt() - 1;
            if (fromAcct < 0 || fromAcct >= theUser.numAccounts()) {
                System.out.println("Invalid account. Please try again.");
            }
        } while (fromAcct < 0 || fromAcct >= theUser.numAccounts());
        acctBal = theUser.getAcctBalance(fromAcct);

        // get the account to transfer to
        do {
            System.out.printf("Enter the number (1-%d) of the account\n to transfer to: ", theUser.numAccounts());
            toAcct = sc.nextInt() - 1;
            if (toAcct < 0 || toAcct >= theUser.numAccounts()) {
                System.out.println("Invalid account. Please try again.");
            }
        } while (toAcct < 0 || toAcct >= theUser.numAccounts());

        // get the amount to transfer
        do {
            System.out.printf("Enter the amount to transfer (max \u20AC%.02f): \u20AC", acctBal);
            amount = sc.nextDouble();
            if (amount < 0) {
                System.out.println("Amount must be greater than zero.");
            } else if (amount > acctBal) {
                System.out.printf("Insufficient funds. Provided amount is bigger than current account balance:" +
                        " \u20AC%.02f.\n", acctBal);
            }
        } while (amount < 0 || amount > acctBal);

        // do the transfer
        theUser.addAccountTransaction(fromAcct, -1 * amount, String.format("Transfer to account %s", theUser.getAcctID(toAcct)));
        theUser.addAccountTransaction(toAcct, amount, String.format("Transfer to account %s", theUser.getAcctID(fromAcct)));
    }

    /**
     * Process a fund withdraw from an account
     *
     * @param theUser the logged-in User object
     * @param sc      the Scanner object user for user input
     */
    public static void withdrawFunds(User theUser, Scanner sc) {

        int fromAcct;
        double amount;
        double acctBal;
        String note;

        // get the account to transfer from
        do {
            System.out.printf("Enter the number (1-%d) of the account\n to withdraw from: ", theUser.numAccounts());
            fromAcct = sc.nextInt() - 1;
            if (fromAcct < 0 || fromAcct >= theUser.numAccounts()) {
                System.out.println("Invalid account. Please try again.");
            }
        } while (fromAcct < 0 || fromAcct >= theUser.numAccounts());
        acctBal = theUser.getAcctBalance(fromAcct);

        // get the amount to transfer
        do {
            System.out.printf("Enter the amount to withdraw (max \u20AC%.02f): \u20AC", acctBal);
            amount = sc.nextDouble();
            if (amount < 0) {
                System.out.println("Amount must be greater than zero.");
            } else if (amount > acctBal) {
                System.out.printf("Insufficient funds. Provided amount is bigger than current account balance:" +
                        " \u20AC%.02f.\n", acctBal);
            }
        } while (amount < 0 || amount > acctBal);

        sc.nextLine();

        // get notes
        System.out.println("Enter a note: ");
        note = sc.nextLine();

        // do the withdraw
        theUser.addAccountTransaction(fromAcct, -1 * amount, note);

    }

    /**
     * Process a fund deposit to an account
     *
     * @param theUser the logged-in User Object
     * @param sc      the Scanner object used for user input
     */
    public static void depositFunds(User theUser, Scanner sc) {

        int toAcct;
        double amount;
        double acctBal;
        String note;

        // get the account to transfer from
        do {
            System.out.printf("Enter the number (1-%d) of the account\n to deposit in: ", theUser.numAccounts());
            toAcct = sc.nextInt() - 1;
            if (toAcct < 0 || toAcct >= theUser.numAccounts()) {
                System.out.println("Invalid account. Please try again.");
            }
        } while (toAcct < 0 || toAcct >= theUser.numAccounts());
        acctBal = theUser.getAcctBalance(toAcct);

        // get the amount to transfer
        do {
            System.out.print("Enter the amount to deposit(min \u20AC0.01): \u20AC");
            amount = sc.nextDouble();
            if (amount < 0) {
                System.out.println("Amount must be greater than zero.");
            }
        } while (amount < 0);

        sc.nextLine();

        // get notes
        System.out.print("Enter a note: ");
        note = sc.nextLine();

        // do the deposit
        theUser.addAccountTransaction(toAcct, amount, note);
    }
}
