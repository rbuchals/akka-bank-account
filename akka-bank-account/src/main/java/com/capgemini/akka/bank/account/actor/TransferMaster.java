package com.capgemini.akka.bank.account.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import akka.japi.Procedure;
import com.capgemini.akka.bank.account.messages.BankAccount.Deposit;
import com.capgemini.akka.bank.account.messages.BankAccount.Done;
import com.capgemini.akka.bank.account.messages.WireTransfer.Transfer;

import java.math.BigInteger;

public class TransferMaster extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final ActorRef accountA;
    private final ActorRef accountB;

    private final BigInteger transferAmount;
    private final BigInteger initialDeposit;

    public TransferMaster(BigInteger transferAmount, BigInteger initialDeposit) {
        this.transferAmount = transferAmount;
        this.initialDeposit = initialDeposit;
        accountA = getContext().actorOf(Props.create(BankAccount.class), "accountA");
        accountB = getContext().actorOf(Props.create(BankAccount.class), "accountB");
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String && "start".equals((String) message)) {
            log.info("Received message: {}", message);
            accountA.tell(new Deposit(initialDeposit), getSelf());
        } else if (message instanceof Done) {
            log.info("Received message: {}", message);
            ActorRef transaction = getContext().actorOf(Props.create(WireTransfer.class), "transfer");
            transaction.tell(new Transfer(accountA, accountB, transferAmount), getSelf());
            getContext().become(new Procedure<Object>() {

                @Override
                public void apply(Object arg0) {
                    log.info("Success: {}", arg0);
                    getContext().stop(getSelf());
                    getContext().system().shutdown();
                }
            });
        } else {
            log.error("Received unhandled message: {}", message);
            unhandled(message);
        }
    }

    public static Props props(BigInteger transferAmount, BigInteger initialDeposit) {
        return Props.create(new Creator<TransferMaster>() {

            @Override
            public TransferMaster create() throws Exception {
                return new TransferMaster(transferAmount, initialDeposit);
            }
        });
    }
}
