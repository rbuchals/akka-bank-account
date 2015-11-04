package com.capgemini.akka.bank.account.actor;

import java.math.BigInteger;

import com.capgemini.akka.bank.account.messages.BankAccount.Deposit;
import com.capgemini.akka.bank.account.messages.BankAccount.Done;
import com.capgemini.akka.bank.account.messages.WireTransfer.Transfer;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;

public class TransferMaster extends UntypedActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private final ActorRef accountA;
	private final ActorRef accountB;

	private final BigInteger transferAmount;
	private final BigInteger initialDeposit;

	public TransferMaster(BigInteger transferAmount, BigInteger initialDeposit) {
		this.transferAmount = transferAmount;
		this.initialDeposit = initialDeposit;
		accountA = getContext().actorOf(new Props(BankAccount.class), "accountA");
		accountB = getContext().actorOf(new Props(BankAccount.class), "accountB");
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof String && "start".equals((String) message)) {
			log.info("Received start message: {}", message);
			accountA.tell(new Deposit(initialDeposit), getSelf());
		} else if (message instanceof Done) {
			log.info("Received String message: {}", message);
			ActorRef transaction = getContext().actorOf(new Props(WireTransfer.class), "transfer");
			transaction.tell(new Transfer(accountA, accountB, transferAmount), getSelf());
			getContext().become(new Procedure<Object>() {

				@Override
				public void apply(Object arg0) {
					log.info("Success");
					getContext().stop(getSelf());
				}
			});
		} else {
			log.error("Received unhandled message: {}", message);
			unhandled(message);
		}
	}
}
