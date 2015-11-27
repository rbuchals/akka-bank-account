package com.capgemini.akka.bank.account;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import com.capgemini.akka.bank.account.actor.BankAccount;
import com.capgemini.akka.bank.account.actor.TransferMaster;
import com.capgemini.akka.bank.account.messages.WireTransfer;
import scala.concurrent.duration.Duration;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AkkaBankAccountRunner {

	private static final BigInteger INITIAL_DEPOSIT_AMOUNT = new BigInteger("100");
	private static final BigInteger TRANSFER_AMOUNT = new BigInteger("50");

	public static void main(String[] args) throws TimeoutException {
		// Create an Akka system
		ActorSystem system = ActorSystem.create("AkkaBankAccount");

        ActorRef accountA = system.actorOf(BankAccount.props(INITIAL_DEPOSIT_AMOUNT),"accountA");
        ActorRef accountB = system.actorOf(BankAccount.props(BigInteger.ZERO),"accountb");

		// create the master
		ActorRef master = system.actorOf(Props.create(TransferMaster.class),"master");

        Inbox inbox = Inbox.create(system);

		// start the calculation
		master.tell(new WireTransfer.Transfer(accountA,accountB, TRANSFER_AMOUNT), inbox.getRef());

        Object result = inbox.receive(Duration.create(10, TimeUnit.SECONDS));
        System.out.println("Result: " +result);
        system.shutdown();
    }

}
