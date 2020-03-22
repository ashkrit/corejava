package tdd;

import tdd.model.BankAccount;
import tdd.model.Currency;
import tdd.model.Money;
import tdd.service.BankService;
import tdd.service.CurrencyConverter;

public class FXService {
    private final CurrencyConverter currencyConverter;
    private final BankService bankService;
    private final double commissionPer;

    public FXService(CurrencyConverter currencyConverter, BankService bankService, double commissionPer) {
        this.currencyConverter = currencyConverter;
        this.bankService = bankService;
        this.commissionPer = commissionPer;
    }

    public String transfer(Money money, BankAccount destinationAccount, Currency target) {

        String sourceCurrency = money.currency().name();
        String targetCurrency = target.name();

        double commissionAmount = calculateCommission(money.amount());
        double fxRate = currencyConverter.convert(1, sourceCurrency, targetCurrency);

        double transferAmount = calculateTransferAmount(money, commissionAmount);
        double totalAmount = applyFxRate(transferAmount, fxRate);

        String transactionId = bankService.deposit(totalAmount, destinationAccount);

        return transactionId;
    }

    private double calculateTransferAmount(Money money, double commissionAmount) {
        return money.amount() - commissionAmount;
    }

    private double calculateCommission(double amount) {
        return amount * commissionPer;
    }

    private double applyFxRate(double amount, double fxRate) {
        return amount * fxRate;
    }
}
