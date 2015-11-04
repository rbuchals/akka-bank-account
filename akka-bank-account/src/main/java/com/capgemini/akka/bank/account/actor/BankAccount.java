package com.capgemini.akka.bank.account.actor;

import java.math.BigInteger;

import com.capgemini.akka.bank.account.messages.BankAccount.Deposit;
import com.capgemini.akka.bank.account.messages.BankAccount.Done;
import com.capgemini.akka.bank.account.messages.BankAccount.Failed;
import com.capgemini.akka.bank.account.messages.BankAccount.Withdraw;

import akka.actor.UntypedActor;

public class BankAccount extends UntypedActor {

	private BigInteger balance = BigInteger.ZERO;

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Deposit) {
			Deposit deposit = (Deposit) message;
			balance = balance.add(deposit.getAmount());
			getSender().tell(new Done(), getSelf());
		} else if (message instanceof Withdraw) {
			Withdraw withdraw = (Withdraw) message;
			if (balance.compareTo(withdraw.getAmount()) >= 0) {
				balance = balance.subtract(withdraw.getAmount());
				getSender().tell(new Done(), getSelf());
			} else {
				getSender().tell(new Failed(), getSelf());
			}
		} else {
			unhandled(message);
		}
	}

}
