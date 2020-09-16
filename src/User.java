import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class User {

    // First name of the user
    private String firstName;
    // Last name of the user
    private String lastName;
    // Unique ID of the user
    private String id;
    // MD5 hash of user's pin
    private byte hashValueOfPin[];
    // List of accounts available for this user
    private ArrayList<Account> accounts;

    /**
     * Creates a new user
     *
     * @param firstName
     * @param lastName
     * @param pin
     * @param bankOfUser
     */
    public User(String firstName, String lastName, String pin, Bank bankOfUser) {
        this.firstName = firstName;
        this.lastName = lastName;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            this.hashValueOfPin = messageDigest.digest(pin.getBytes());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error: caught NoSuchAlgorithmException");
            e.printStackTrace();
            System.exit(1);
        }

        this.id = bankOfUser.getNewUserID();

        this.accounts = new ArrayList<Account>();


        System.out.printf("New user %s, %s, with ID %s created. \n", lastName, firstName, this.id);

    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    /**
     * Adds an account for the user
     *
     * @param acct
     */
    public void addAccount(Account acct) {
        this.accounts.add(acct);

    }

    public String getID() {
        return id;
    }

    /**
     * Check whether a given pin matches the true USER pin
     *
     * @param pin pin to check
     * @return whether the pin is valid or not
     */
    public boolean validatePin(String pin) {

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            return MessageDigest.isEqual(messageDigest.digest(pin.getBytes()), this.hashValueOfPin);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error: caught NoSuchAlgorithmException");
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public String getFirstName() {
        return firstName;
    }

    /**
     * Prints summaries for the accounts of this user
     */
    public void printAccountsSummary() {
        System.out.printf("\n\n%s's accounts summary\n", this.firstName);
        for (int a = 0; a < this.accounts.size(); a++) {
            System.out.printf("  %d) %s", a + 1, this.accounts.get(a).getSummaryLine());
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Get the number of accounts of the user
     *
     * @return the number of accounts
     */
    public int numAccounts() {
        return this.accounts.size();
    }

    /**
     * Print transaction history for a particular account.
     *
     * @param acctIdx the index of the account to use
     */
    public void printAcctTransHistory(int acctIdx) {
        this.accounts.get(acctIdx).printTransHistory();
    }

    /**
     * Get the balance of a aprticular account
     *
     * @param acctIdx the index of the account to use
     * @return the balance of the account
     */
    public double getAcctBalance(int acctIdx) {
        return this.accounts.get(acctIdx).getBalance();
    }

    /**
     * Get the ID of a particular account
     *
     * @param acctIdx the index of the account to use
     * @return the ID of the account
     */
    public String getAcctID(int acctIdx) {
        return this.accounts.get(acctIdx).getID();
    }

    public void addAccountTransaction(int acctIdx, double amount, String note) {
        this.accounts.get(acctIdx).addTransaction(amount, note);
    }
}
