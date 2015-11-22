package com.capgemini.akka.bank.account;

import akka.actor.*;
import com.capgemini.akka.bank.account.actor.TransferMaster;

import java.math.BigInteger;

public class AkkaBankAccountRunner {

	private static final BigInteger INITIAL_DEPOSIT_AMOUNT = new BigInteger("100");
	private static final BigInteger TRANSFER_AMOUNT = new BigInteger("50");

	public static void main(String[] args) {
		// Create an Akka system
		ActorSystem system = ActorSystem.create("AkkaBankAccount");

		// create the master
		ActorRef master = system.actorOf( TransferMaster.props(TRANSFER_AMOUNT,INITIAL_DEPOSIT_AMOUNT),"master");

		// start the calculation
		master.tell("start", ActorRef.noSender());
	}

}
