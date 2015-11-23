package com.capgemini.akka.bank.account;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import com.capgemini.akka.bank.account.actor.BankAccount;
import com.capgemini.akka.bank.account.actor.TransferMaster;
import com.capgemini.akka.bank.account.messages.BankAccount.Failed;
import com.capgemini.akka.bank.account.messages.WireTransfer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static com.capgemini.akka.bank.account.messages.BankAccount.Done;

/**
 * Created by ldrygala on 2015-11-23.
 */
public class TransferMasterTest {

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
    public void shouldTransferMoneyBetweenAccountsWhenTransferAmountIsLowerThenDeposit() throws Exception {
        new JavaTestKit(system) {{
            ActorRef master = system.actorOf(Props.create(TransferMaster.class), "master");
            ActorRef accountA = system.actorOf(BankAccount.props(BigInteger.TEN), "accountA");
            ActorRef accountB = system.actorOf(BankAccount.props(BigInteger.ZERO), "accountB");

            master.tell(new WireTransfer.Transfer(accountA, accountB, BigInteger.ONE), getTestActor());

            expectMsgClass(Done.class);
        }};
    }

    @Test
    public void shouldNotTransferMoneyWhenTransferAmountIsGreaterThenDeposit() throws Exception {
        new JavaTestKit(system) {{
            ActorRef master = system.actorOf(Props.create(TransferMaster.class), "master");
            ActorRef accountA = system.actorOf(BankAccount.props(BigInteger.ONE), "accountA");
            ActorRef accountB = system.actorOf(BankAccount.props(BigInteger.ZERO), "accountB");

            master.tell(new WireTransfer.Transfer(accountA, accountB, BigInteger.TEN), getTestActor());

            expectMsgClass(Failed.class);
        }};
    }
}
