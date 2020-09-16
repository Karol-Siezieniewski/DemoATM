import java.util.Date;

public class Transaction {

    // Amount of cash used for this transaction
    private double amount;
    // Date with time of transaction taking place
    private Date timestamp;
    // Notes provided for this transaction
    private String notes;
    // Indicator of which account made transaction
    private Account whichAccount;

    /**
     * Creates a new transaction containing 2 variables
     * @param amount
     * @param whichAccount
     */
    public Transaction(double amount, Account whichAccount){

        this.amount = amount;
        this.whichAccount = whichAccount;
        this.timestamp = new Date();
        this.notes = "";

    }

    /**
     * Creates a new transaction containing 3 variables
     * @param amount
     * @param whichAccount
     * @param notes
     */
    public Transaction(double amount, Account whichAccount, String notes){

        this(amount, whichAccount);

        this.notes = notes;
    }

    /**
     * Get the amount of the transaction
     * @return  the amount
     */
    public double getAmount() {
        return this.amount;
    }

    /**
     * Get a string summarizing the transaction
     * @return  the summary string
     */
    public String getSummaryLine(){

        if(this.amount >= 0){
            return String.format("%s : +\u20AC%.02f : %s", this.timestamp.toString(), this.amount, this.notes);
        } else {
            return String.format("%s : -\u20AC%.02f : %s", this.timestamp.toString(), -this.amount, this.notes);
        }

    }
}
