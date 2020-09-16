import java.util.ArrayList;
import java.util.Random;

public class Bank {

    private String name;

    private ArrayList<User> users;

    private ArrayList<Account> listOfAccounts;

    /**
     * Creates a new Bank object with empty lists of users and accounts
     * @param name  name of the bank
     */
    public Bank(String name) {
        this.name = name;
        this.users = new ArrayList<User>();
        this.listOfAccounts = new ArrayList<Account>();
    }

    /**
     * Generate unique ID for the new user
     *
     * @return ID
     */
    public String getNewUserID() {
        String id;
        Random random = new Random();
        int lenght = 6;
        boolean notUnique;

        do {
            id = "";
            for (int counter = 0; counter < lenght; counter++) {
                id += ((Integer) random.nextInt(10)).toString();
            }
            notUnique = false;
            for (User user : this.users) {
                if (id.compareTo(user.getID()) == 0) {
                    notUnique = true;
                    break;
                }
            }
        } while (notUnique);
        return id;
    }

    public String getNewAccountID() {
        String id;
        Random random = new Random();
        int lenght = 10;
        boolean notUnique;

        do {
            id = "";
            for (int counter = 0; counter < lenght; counter++) {
                id += ((Integer) random.nextInt(10)).toString();
            }
            notUnique = false;
            for (Account acc : this.listOfAccounts) {
                if (id.compareTo(acc.getID()) == 0) {
                    notUnique = true;
                    break;
                }
            }
        } while (notUnique);
        return id;
    }

    public void addAccount(Account account){
        this.listOfAccounts.add(account);
    }

    /**
     * Creates a new user of the bank
     * @param firstName user's first name
     * @param lastName  user's last name
     * @param pin       user's pin number
     * @return          the new User object
     */
    public User addUser(String firstName, String lastName, String pin){
        // create new User object and add it to our list
        User newUser = new User(firstName, lastName, pin, this);
        this.users.add(newUser);

        // create a savings account for the user and add to User and Bank accounts lists
        Account newAccount = new Account("Savings", newUser, this);
        newUser.addAccount(newAccount);
        this.addAccount(newAccount);

        return newUser;
    }

    public User userLogin(String userID, String pin){

        // search list of users
        for (User user : this.users){

            // check if provided user ID is valid
            if(user.getID().compareTo(userID) == 0 && user.validatePin(pin)) {
                return user;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
}
