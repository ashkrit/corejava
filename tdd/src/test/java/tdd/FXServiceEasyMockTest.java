package tdd;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;
import tdd.model.BankAccount;
import tdd.model.Currency;
import tdd.model.Money;
import tdd.service.BankService;
import tdd.service.CurrencyConverter;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FXServiceEasyMockTest {

    final Currency SGD = new Currency("SGD");
    final Currency INR = new Currency("INR");
    final CurrencyConverter currencyConverter = EasyMock.createMock(CurrencyConverter.class);
    final BankService bankService = EasyMock.createMock(BankService.class);


    @Test
    public void transfer_sgd_to_inr() {

        FXService fxService = new FXService(currencyConverter, bankService, 0.0d);
        BankAccount account = new BankAccount("1111-22222", "SuperStableBank");

        expect(currencyConverter.convert(1, "SGD", "INR")).andReturn(50d);
        expect(bankService.deposit(100d, account)).andReturn("99999");
        replay(currencyConverter, bankService);

        String id = fxService.transfer(new Money(SGD, 2d), account, INR);
        assertEquals("99999", id);

        verify(currencyConverter, bankService);
    }

}
