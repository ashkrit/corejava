package tdd;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import tdd.model.BankAccount;
import tdd.model.Currency;
import tdd.model.Money;
import tdd.service.BankService;
import tdd.service.CurrencyConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FXServiceTest {

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();

    final Currency SGD = new Currency("SGD");
    final Currency INR = new Currency("INR");
    final CurrencyConverter currencyConverter = context.mock(CurrencyConverter.class);
    final BankService bankService = context.mock(BankService.class);


    @Test
    public void transfer_sgd_to_inr() {

        FXService fxService = new FXService(currencyConverter, bankService, 0.0d);
        BankAccount account = new BankAccount("1111-22222", "SuperStableBank");

        context.checking(new Expectations() {{
            oneOf(currencyConverter).convert(1, "SGD", "INR");
            will(returnValue(50d));

            oneOf(bankService).deposit(100d, account);
            will(returnValue("99999"));
        }});


        String id = fxService.transfer(new Money(SGD, 2d), account, INR);
        assertEquals("99999", id);

    }

    @Test
    public void transfer_sgd_to_inr_after_commission() {


        FXService fxService = new FXService(currencyConverter, bankService, 0.05d);
        BankAccount account = new BankAccount("1111-22222", "SuperStableBank");

        context.checking(new Expectations() {{
            oneOf(currencyConverter).convert(1, "SGD", "INR");
            will(returnValue(50d));

            oneOf(bankService).deposit(4750, account); // 95 * 50
            will(returnValue("99999"));
        }});

        String id = fxService.transfer(new Money(SGD, 100), account, INR);
        assertEquals("99999", id);

    }

    @AfterEach
    public void after() {
        context.assertIsSatisfied();
    }
}
