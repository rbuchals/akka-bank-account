package com.capgemini.akka.bank.account.messages;

import akka.actor.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigInteger;

public class WireTransfer {
    @AllArgsConstructor
	public static class Transfer {
        @NonNull
        @Getter
		private ActorRef from;

        @NonNull
        @Getter
		private ActorRef to;

        @NonNull
        @Getter
		private BigInteger amount;

	}

	public static class Done {
	}

	public static class Failed {
	}

}
