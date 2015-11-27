package com.capgemini.akka.bank.account.actor;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import com.capgemini.akka.bank.account.messages.BankAccount.Deposit;
import com.capgemini.akka.bank.account.messages.BankAccount.Done;
import com.capgemini.akka.bank.account.messages.BankAccount.Failed;
import com.capgemini.akka.bank.account.messages.BankAccount.Withdraw;

import java.math.BigInteger;

public class BankAccount extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private BigInteger balance = BigInteger.ZERO;

    public BankAccount(BigInteger initBalance) {
        balance = initBalance;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Deposit) {
            Deposit deposit = (Deposit) message;
            log.info("BankAccount before deposit {}", balance);
            balance = balance.add(deposit.getAmount());
            log.info("BankAccount after deposit {}", balance);
            getSender().tell(new Done(), getSelf());
        } else if (message instanceof Withdraw) {
            Withdraw withdraw = (Withdraw) message;
            if (balance.compareTo(withdraw.getAmount()) >= 0) {
                log.info("BankAccount before withdraw {}", balance);
                balance = balance.subtract(withdraw.getAmount());
                log.info("BankAccount after withdraw {}", balance);
                getSender().tell(new Done(), getSelf());
            } else {
                getSender().tell(new Failed(), getSelf());
            }
        } else {
            unhandled(message);
        }
    }

    public static Props props(BigInteger initBalance) {
        return Props.create(new Creator<BankAccount>() {

            @Override
            public BankAccount create() throws Exception {
                return new BankAccount(initBalance);
            }
        });
    }

    public BigInteger getBalance() {
        return balance;
    }
}
