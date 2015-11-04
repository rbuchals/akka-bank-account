package com.capgemini.akka.bank.account.messages;

import java.math.BigInteger;

import akka.actor.ActorRef;

public class WireTransfer {

	public static class Transfer {
		private final ActorRef from;

		private final ActorRef to;

		private final BigInteger amount;

		public Transfer(ActorRef from, ActorRef to, BigInteger amount) {
			super();
			if (from == null) {
				throw new IllegalArgumentException("Reference on ActorRef from can't be null!");
			}
			if (to == null) {
				throw new IllegalArgumentException("Reference on ActorRef to can't be null!");
			}
			if (amount.compareTo(BigInteger.ZERO) <= 0) {
				throw new IllegalArgumentException("Amount must be greater then 0!");
			}
			this.from = from;
			this.to = to;
			this.amount = amount;
		}

		public ActorRef getFrom() {
			return from;
		}

		public ActorRef getTo() {
			return to;
		}

		public BigInteger getAmount() {
			return amount;
		}
	}

	public static class Done {
	}

	public static class Failed {
	}

}
