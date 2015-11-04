package com.capgemini.akka.bank.account;

import java.math.BigInteger;

import com.capgemini.akka.bank.account.actor.TransferMaster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;

public class AkkaBankAccountRunner {

	private static final BigInteger INITIAL_DEPOSIT_AMOUNT = new BigInteger("100");
	private static final BigInteger TRANSFER_AMOUNT = new BigInteger("50");

	public static void main(String[] args) {
		AkkaBankAccountRunner runner = new AkkaBankAccountRunner();
		runner.run();
	}

	private void run() {
		// Create an Akka system
		ActorSystem system = ActorSystem.create("AkkaBankAccount");

		// create the master
		ActorRef master = system.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new TransferMaster(TRANSFER_AMOUNT, INITIAL_DEPOSIT_AMOUNT);
			}
		}), "master");

		// start the calculation
		master.tell("start");
	}
}
