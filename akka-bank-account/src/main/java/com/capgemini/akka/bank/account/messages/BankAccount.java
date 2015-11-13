package com.capgemini.akka.bank.account.messages;

import lombok.Getter;

import java.math.BigInteger;

public class BankAccount {
	public static class Deposit {
        @Getter
		private final BigInteger amount;

		public Deposit(BigInteger amount) {
			if (amount.compareTo(BigInteger.ZERO) <= 0) {
				throw new IllegalArgumentException("Amount must be greater then 0!");
			}
			this.amount = amount;
		}

	}

	public static class Withdraw {
		@Getter
        private final BigInteger amount;

		public Withdraw(BigInteger amount) {
			if (amount.compareTo(BigInteger.ZERO) <= 0) {
				throw new IllegalArgumentException("Amount must be greater then 0!");
			}
			this.amount = amount;
		}
	}

	public static class Done {
	}

	public static class Failed {
	}
}
