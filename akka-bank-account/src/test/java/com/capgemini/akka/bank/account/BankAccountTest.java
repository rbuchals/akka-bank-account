package com.capgemini.akka.bank.account;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import com.capgemini.akka.bank.account.actor.BankAccount;
import com.capgemini.akka.bank.account.messages.BankAccount.Deposit;
import com.capgemini.akka.bank.account.messages.BankAccount.Failed;
import com.capgemini.akka.bank.account.messages.BankAccount.Withdraw;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by ldrygala on 2015-11-23.
 */
public class BankAccountTest {

    private static ActorSystem system;

    @Before
    public void setUp() throws Exception {
        system = ActorSystem.create("akka-bank-account-test");
    }

    @After
    public void tearDown() throws Exception {
        JavaTestKit.shutdownActorSystem(system);
    }

    @Test
    public void shouldDepositMoneyOnBankAccount() throws Exception {
        TestActorRef<BankAccount> bankAccount = TestActorRef.create(system, BankAccount.props(BigInteger.ZERO));

        bankAccount.tell(new Deposit(BigInteger.TEN), ActorRef.noSender());

        assertThat(bankAccount.underlyingActor().getBalance()).isEqualTo(BigInteger.TEN);
    }

    @Test
    public void shouldWithdrawMoneyOnBankAccount() throws Exception {
        TestActorRef<BankAccount> bankAccount = TestActorRef.create(system, BankAccount.props(BigInteger.TEN));

        bankAccount.tell(new Withdraw(BigInteger.TEN), ActorRef.noSender());

        assertThat(bankAccount.underlyingActor().getBalance()).isEqualTo(BigInteger.ZERO);
    }

    @Test
    public void shouldNotWithdrawMoneyWhenAmountIsGreaterThenBankAccount() throws Exception {
        new JavaTestKit(system) {
            {
                BigInteger initBalance = BigInteger.ONE;
                TestActorRef<BankAccount> bankAccount = TestActorRef.create(system, BankAccount.props(initBalance));

                bankAccount.tell(new Withdraw(BigInteger.TEN), getTestActor());

                expectMsgClass(Failed.class);
                assertThat(bankAccount.underlyingActor().getBalance()).isEqualTo(initBalance);
            }
        };
    }
}
