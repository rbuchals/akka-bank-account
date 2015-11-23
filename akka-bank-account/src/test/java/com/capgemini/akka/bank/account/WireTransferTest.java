package com.capgemini.akka.bank.account;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import akka.testkit.TestProbe;
import com.capgemini.akka.bank.account.actor.WireTransfer;
import com.capgemini.akka.bank.account.messages.BankAccount.Deposit;
import com.capgemini.akka.bank.account.messages.BankAccount.Done;
import com.capgemini.akka.bank.account.messages.BankAccount.Withdraw;
import com.capgemini.akka.bank.account.messages.WireTransfer.Transfer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ldrygala on 2015-11-23.
 */
public class WireTransferTest {

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
    public void shouldTransferMoneyBetweenAccount() throws Exception {
        new JavaTestKit(system) {{
            BigInteger amount = BigInteger.TEN;
            ActorRef transaction = system.actorOf(Props.create(WireTransfer.class), "WireTransfer");
            TestProbe fromAccount = new TestProbe(system, "fromAccount");
            TestProbe toAccount = new TestProbe(system, "toAccount");

            transaction.tell(new Transfer(fromAccount.ref(), toAccount.ref(), amount), ActorRef.noSender());

            fromAccount.expectMsg(new Withdraw(amount));
            fromAccount.reply(new Done());
            toAccount.expectMsg(new Deposit(amount));
        }};
    }
}
