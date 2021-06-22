package com.soumitra.auction;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.util.HashMap;
import java.util.Map;


public class AuctionHouseActor extends AbstractBehavior<UserInputMessage> {

    private final Map<String, ActorRef<AuctionHandler.Command>> itemIdAuctionHandlerMap;


    public static Behavior<UserInputMessage> create() {
        return Behaviors.setup(AuctionHouseActor::new);
    }

    private AuctionHouseActor(ActorContext<UserInputMessage> context) {
        super(context);

        //#create-actors map
        itemIdAuctionHandlerMap = new HashMap<>();

    }

    @Override
    public Receive<UserInputMessage> createReceive() {
        return newReceiveBuilder().onMessage(UserInputMessage.class, this::onUserInputMessage).build();
    }

    private Behavior<UserInputMessage> onUserInputMessage(UserInputMessage userInput) {

        getContext().getLog().info("onUserInputMessage " + userInput);

        if(userInput.getType().equalsIgnoreCase("newItem")) {
            ActorRef<AuctionHandler.Command> handler = getContext().spawn(AuctionHandler.create(userInput.getId(), userInput.getTimeOfAuction()), userInput.getId());

            itemIdAuctionHandlerMap.put(userInput.getId(), handler);
            handler.tell(new AuctionHandler.NewItem(userInput)); // I think this can be removed.
        } else {

            ActorRef<AuctionHandler.Command> handler = itemIdAuctionHandlerMap.get(userInput.getItem());
            handler.tell(new AuctionHandler.NewBidder(userInput));
        }

        return this;
    }
}
