package tdd.model;

public class BankAccount {
    private final String accountNumber;
    private final String bankCode;

    public BankAccount(String accountNumber, String bankCode) {
        this.accountNumber = accountNumber;
        this.bankCode = bankCode;
    }

    public String accountNumber() {
        return accountNumber;
    }

    public String bankCode() {
        return bankCode;
    }
}
