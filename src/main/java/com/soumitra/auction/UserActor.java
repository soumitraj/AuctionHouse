package com.soumitra.auction;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class UserActor extends AbstractBehavior<UserActor.CurrentStatus> {

    private  final String userId;
    private  final String item;
    private  final Long startingBid;
    private  final Long maxBid;
    private  final Long bidIncrement;


    public static Behavior<UserActor.CurrentStatus> create(String userId, String item, Long startingBid,
                                                            Long maxBid, Long bidIncrement) {

        return Behaviors.setup(context -> new UserActor(context, userId, item, startingBid, maxBid, bidIncrement ));
    }


    public static final class CurrentStatus {

        public final String auctionItemId;
        public final Long currentMaxBid;
        public final ActorRef<AuctionHandler.Command> replyToAuctionHandler;
        public CurrentStatus(String auctionItemId, Long currentMaxBid, ActorRef<AuctionHandler.Command> replyTo) {
            this.auctionItemId = auctionItemId;
            this.currentMaxBid = currentMaxBid;
            this.replyToAuctionHandler = replyTo;
        }

        @Override
        public String toString() {
            return "CurrentStatus{" +
                    "auctionItemId='" + auctionItemId + '\'' +
                    ", currentMaxBid=" + currentMaxBid +
                    ", replyToAuctionHandler=" + replyToAuctionHandler +
                    '}';
        }
    }

    private UserActor(ActorContext<UserActor.CurrentStatus> context,String userId, String item, Long startingBid,
                      Long maxBid, Long bidIncrement) {
        super(context);
        this.userId = userId;
        this.item = item;
        this.startingBid = startingBid;
        this.maxBid = maxBid;
        this.bidIncrement = bidIncrement;
    }



    @Override
    public Receive<UserActor.CurrentStatus> createReceive() {
        return newReceiveBuilder()
                .onMessage(UserActor.CurrentStatus.class, this::onCurrentStatus)
                .build();
    }

    private Behavior<CurrentStatus> onCurrentStatus(UserActor.CurrentStatus currentAuctionStatus) {
        getContext().getLog().info("onCurrentStatus " + currentAuctionStatus);

        if(currentAuctionStatus.currentMaxBid > this.startingBid
                && currentAuctionStatus.currentMaxBid < (this.maxBid -  this.bidIncrement)) {
            getContext().getLog().info( "user " + userId +  " submitting new bid : " + currentAuctionStatus.currentMaxBid + this.bidIncrement);
            currentAuctionStatus.replyToAuctionHandler.tell(new AuctionHandler.NewBid(currentAuctionStatus.currentMaxBid + this.bidIncrement, userId));
        }

        return this;
    }


}
