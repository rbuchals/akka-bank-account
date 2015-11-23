package com.capgemini.akka.bank.account.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import com.capgemini.akka.bank.account.messages.WireTransfer.Transfer;

public class TransferMaster extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private ActorRef client = ActorRef.noSender();

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Transfer) {
            log.info("TransferMaster received: {}", message);
            client = getSender();
            ActorRef transaction = getContext().actorOf(Props.create(WireTransfer.class), "transfer");
            transaction.tell(Transfer.class.cast(message), getSelf());
            getContext().become(new Procedure<Object>() {

                @Override
                public void apply(Object message) {
                    client.forward(message, getContext());
                    getContext().stop(getSelf());
                }
            });
        } else {
            log.error("Received unhandled message: {}", message);
            unhandled(message);
        }
    }

}
