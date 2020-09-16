import java.util.ArrayList;

public class Account {

    // Name of the specific bank account
    private String name;
    // Unique ID number of account
    private String id;
    // User that is owner of the account
    private User owner;
    // History of transactions of this account
    private ArrayList<Transaction> transactions;

    /**
     * Creates a new user account
     *
     * @param name
     * @param owner
     * @param bankOfUser
     */
    public Account(String name, User owner, Bank bankOfUser) {
        this.name = name;
        this.id = id;
        this.owner = owner;

        this.id = bankOfUser.getNewAccountID();

        this.transactions = new ArrayList<Transaction>();

    }

    public String getID() {
        return id;
    }

    /**
     * Get summary line for the account
     *
     * @return the string summary
     */
    public String getSummaryLine() {

        // get the account's balance
        double balance = this.getBalance();

        //format summary line, depending on the wether the balance is negative
        if (balance >= 0) {
            return String.format("%s : \u20AC%.02f : %s", this.id, balance, this.name);
        } else {
            return String.format("%s : \u20AC(%.02f) : %s", this.id, balance, this.name);
        }
    }

    public double getBalance() {
        double balance = 0;
        for (Transaction t : this.transactions) {
            balance += t.getAmount();
        }
        return balance;
    }

    /**
     * Prints transaction history of the account
     */
    public void printTransHistory() {

        System.out.printf("\nTransaction history for account %s\n", this.id);
        for (int t = this.transactions.size() - 1; t >= 0; t--) {
            System.out.println(this.transactions.get(t).getSummaryLine());
        }
        System.out.println();
    }

    /**
     * Add a new transaction in this account
     *
     * @param amount the amount transacted
     * @param note   the transaction note
     */
    public void addTransaction(double amount, String note) {
        // create new transaction object and add it to our list
        Transaction newTrans = new Transaction(amount, this, note);
        this.transactions.add(newTrans);
    }
}
