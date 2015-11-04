package com.capgemini.akka.bank.account.actor;

import com.capgemini.akka.bank.account.messages.BankAccount.Deposit;
import com.capgemini.akka.bank.account.messages.BankAccount.Done;
import com.capgemini.akka.bank.account.messages.BankAccount.Failed;
import com.capgemini.akka.bank.account.messages.BankAccount.Withdraw;
import com.capgemini.akka.bank.account.messages.WireTransfer.Transfer;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.japi.Procedure;

public class WireTransfer extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Transfer) {
			Transfer transfer = (Transfer) message;
			transfer.getFrom().tell(new Withdraw(transfer.getAmount()), getSelf());
			getContext().become(new AwaitWithdraw(transfer, getSender()));
		} else {
			unhandled(message);
		}

	}

	private class AwaitWithdraw implements Procedure<Object> {

		private final Transfer transfer;

		private final ActorRef transferSender;

		public AwaitWithdraw(Transfer transfer, ActorRef transferSender) {
			this.transfer = transfer;
			this.transferSender = transferSender;
		}

		@Override
		public void apply(Object message) {
			if (message instanceof Done) {
				ActorRef to = transfer.getTo();
				to.tell(new Deposit(transfer.getAmount()), getSelf());
				getContext().become(new AwaitDeposit(transfer, transferSender));
			} else if (message instanceof Failed) {
				getContext().stop(getSelf());
			}
		}
	}

	private class AwaitDeposit implements Procedure<Object> {

		private final Transfer transfer;

		private final ActorRef transferSender;

		public AwaitDeposit(Transfer transfer, ActorRef transferSender) {
			this.transfer = transfer;
			this.transferSender = transferSender;
		}

		@Override
		public void apply(Object message) {
			if (message instanceof Done) {
				transferSender.tell(new Done(), getSelf());
				getContext().stop(getSelf());
			} else {
				unhandled(message);
			}
		}
	}
}
