package com.soumitra.auction;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class AuctionHandler extends AbstractBehavior<AuctionHandler.Command> {

  String auctionItemId;
  Long currentMaxBid = Long.MIN_VALUE;
  Long timeOfAuction;
  String currentWinnerId;
  Boolean isAuctionActive;
  Integer totalBidders;
  Integer totalBids;

//  private final TimerScheduler<Command> timer;

  private final Map<String, ActorRef<UserActor.CurrentStatus>> userNameActorRefMap = new HashMap<>();

  public interface Command {}

  public static final class NewItem implements Command {
    public final UserInputMessage input;

    public NewItem(UserInputMessage input) {
      this.input = input;
    }

    @Override
    public String toString() {
      return "NewItem{" +
              "input=" + input +
              '}';
    }
  }

  public static final class NewBidder implements Command {
    public final UserInputMessage input;


    public NewBidder(UserInputMessage input) {
      this.input = input;

    }

    @Override
    public String toString() {
      return "NewBidder{" +
              "input=" + input +
              '}';
    }
  }

  public static final class NewBid implements Command {

    public final Long newBidValue;
    public final String bidderId;

    public NewBid(Long newBidValue, String bidderId ) {

      this.newBidValue = newBidValue;
      this.bidderId = bidderId;
    }

    @Override
    public String toString() {
      return "NewBid{" +
              "newBidValue=" + newBidValue +
              ", bidderId='" + bidderId + '\'' +
              '}';
    }
  }

  public static final class EndAuction implements Command {

    @Override
    public String toString() {
      return "EndAuction{}";
    }
  }


  public static Behavior<Command> create(String auctionItemId, Long timeOfAuction) {
    return Behaviors.setup(context ->  Behaviors.withTimers(
            timer -> new AuctionHandler(context, timer, auctionItemId, timeOfAuction)
    ));
  }

  private AuctionHandler(ActorContext<Command> context, TimerScheduler<Command> timer, String auctionItemId, Long timeOfAuction) {
    super(context);
    this.auctionItemId = auctionItemId;
    this.timeOfAuction = timeOfAuction;
    this.isAuctionActive = true;
    this.totalBidders = 0;
    this.totalBids = 0;
//    this.timer = timer;
    timer.startSingleTimer(new EndAuction(),Duration.ofSeconds(this.timeOfAuction));
  }

  @Override
  public Receive<Command> createReceive() {
    return newReceiveBuilder()
            .onMessage(NewItem.class, this::onNewItem)
            .onMessage(NewBidder.class, this::onNewBidder)
            .onMessage(NewBid.class, this::onNewBid)
            .onMessage(EndAuction.class, this::onEndAuction)
            .build();
  }



  private Behavior<Command> onEndAuction(EndAuction command) {
    getContext().getLog().info("Ending Auction " + this.auctionItemId +
            "! Winner id: " + currentWinnerId + " winning bid:" +currentMaxBid);
    this.isAuctionActive = false;
    return this;
  }

  private Behavior<Command> onNewItem(NewItem command) {
    this.auctionItemId = command.input.getId();
    getContext().getLog().info("onNewItem! " + this.auctionItemId);
    return this;
  }

  private Behavior<Command> onNewBidder(NewBidder command) {
    getContext().getLog().info("onNewBidder! " + command  +
            " Current Winner: " + currentWinnerId + " with Current Max bid:" +currentMaxBid);

    if(!this.isAuctionActive) {
      getContext().getLog().info("onNewBidder! " + command  +
              "This Auction Has Already ended" +
              "Winner: " + currentWinnerId + " with Winner bid:" +currentMaxBid);
      return this;
    }

    totalBidders++;

    // this is a new bid
    String userId = command.input.getId();
    Long currentStartingBid = command.input.getStartingBid();
    Long currentBidderMax = command.input.getMaxBid();
    Long bidIncrement = command.input.getBidIncrement();

    ActorRef<UserActor.CurrentStatus> bidderActor
            = userNameActorRefMap.getOrDefault(userId, getContext().spawn(UserActor.create(userId,this.auctionItemId,
            currentStartingBid, currentBidderMax, bidIncrement), userId+"-"+auctionItemId));
    userNameActorRefMap.put(userId,bidderActor) ;

    getContext().getSelf().tell(new NewBid(currentStartingBid, userId));

    return this;
  }

  private Behavior<Command> onNewBid(NewBid command) {
    getContext().getLog().info("onNewBid! " + command  +
            " Current Winner: " + currentWinnerId + " Current Max bid:" +currentMaxBid);

    if(!this.isAuctionActive) {
      getContext().getLog().info("onNewBid: " + command  +
              "This Auction Has Already ended" +
              "Winner: " + currentWinnerId + " with Winner bid:" +currentMaxBid);
      return this; 
    }

    this.totalBids++;
    processNewBid(command.newBidValue, command.bidderId);
    getContext().getLog().info("onNewBid Exit: Current Winner: " + currentWinnerId + "with Current Max bid:" +currentMaxBid);

    return this;
  }

  private void processNewBid(Long currentBid, String userId){

    if(this.currentMaxBid < currentBid) {
      this.currentMaxBid = currentBid;
      this.currentWinnerId = userId;
      tellOtherBidders(userId);
    } else {
      getContext().getLog().info( "user " + userId +  " currentBid : " + currentBid + " rejected, currentMaxBid : " + currentMaxBid);
    }
  }


  private void tellOtherBidders(String currentBidder) {
    for(Map.Entry<String, ActorRef<UserActor.CurrentStatus>> entry : userNameActorRefMap.entrySet()){
      if(!entry.getKey().equalsIgnoreCase(currentBidder)){
        entry.getValue().tell(new UserActor.CurrentStatus(this.auctionItemId, this.currentMaxBid,  getContext().getSelf()));
      }

    }
  }
}


