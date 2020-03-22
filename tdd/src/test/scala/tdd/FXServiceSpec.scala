package tdd

import org.jmock.AbstractExpectations._
import org.jmock._
import org.scalatest._
import org.scalatest.matchers.should.Matchers
import tdd.model.{BankAccount, Currency, Money}
import tdd.service.{BankService, CurrencyConverter}

class FXServiceSpec extends FlatSpec with BeforeAndAfter with Matchers {

  val SGD = new Currency("SGD")
  val INR = new Currency("INR")


  "A FXService" should "convert SGD to INR and transfer to bank account" in {

    val context = new Mockery
    val currencyConverter = context.mock(classOf[CurrencyConverter])
    val bankService = context.mock(classOf[BankService])

    val fxService = new FXService(currencyConverter, bankService, 0.0d)
    val account = new BankAccount("1111-22222", "SuperStableBank")

    context.checking(
      new Expectations() {
        oneOf(currencyConverter).convert(1, "SGD", "INR")
        will(returnValue(50d))

        oneOf(bankService).deposit(100d, account)
        will(returnValue("99999"))
      }
    )

    val id = fxService.transfer(new Money(SGD, 2), account, INR)
    id should be("99999")

  }

  it should ("convert INR to SGD and transfer to bank account") in {

    val context = new Mockery
    val currencyConverter = context.mock(classOf[CurrencyConverter])
    val bankService = context.mock(classOf[BankService])

    val fxService = new FXService(currencyConverter, bankService, 0.0d)
    val account = new BankAccount("1111-22222", "SuperStableBank")

    context.checking(
      new Expectations() {
        oneOf(currencyConverter).convert(1, "INR", "SGD")
        will(returnValue(0.020))

        oneOf(bankService).deposit(2d, account)
        will(returnValue("99999"))
      }
    )

    val id = fxService.transfer(new Money(INR, 100d), account, SGD)
    id should be("99999")

    context.assertIsSatisfied()
  }


}
