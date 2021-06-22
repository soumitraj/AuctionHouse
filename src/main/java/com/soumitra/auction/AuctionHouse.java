package com.soumitra.auction;

import akka.actor.typed.ActorSystem;

public class AuctionHouse {

    //#actor-system
    final ActorSystem<UserInputMessage> auctionHouseMainActor = ActorSystem.create(AuctionHouseActor.create(), "AuctionHouseMainActor");
    //#actor-system


    public AuctionHouse() {
    }

    public void processUserInputMessage(UserInputMessage userInputMessage) {
        //#main-send-messages
        auctionHouseMainActor.tell(userInputMessage);
        //#main-send-messages
    }

    public void terminate() {
        auctionHouseMainActor.terminate();
    }
}
