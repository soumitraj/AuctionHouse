package com.soumitra.auction;

import org.json.simple.JSONObject;

public class UserInputMessage {

    private String id;
    private String type;
    private String name;
    private String description;
    private Long timeOfAuction;

    private String itemName;
    private String item;
    private Long startingBid;
    private Long maxBid;
    private Long bidIncrement;

    public UserInputMessage(String id, String type, String name, String description, Long timeOfAuction) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.timeOfAuction = timeOfAuction;
    }

    public UserInputMessage(String id, String type, String name, String itemName,
                            String item, Long startingBid, Long maxBid, Long bidIncrement) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.itemName = itemName;
        this.item = item;
        this.startingBid = startingBid;
        this.maxBid = maxBid;
        this.bidIncrement = bidIncrement;
    }

    public UserInputMessage(JSONObject auctionMessage) {
        this.id = (String) auctionMessage.get("id");
        this.type = (String) auctionMessage.get("type");
        this.name = (String) auctionMessage.get("name");
        this.description = (String) auctionMessage.get("description");
        this.timeOfAuction = (Long) auctionMessage.get("timeOfAuction");
        this.itemName = (String) auctionMessage.get("itemName");
        this.item = (String) auctionMessage.get("item");
        this.startingBid = (Long) auctionMessage.get("startingBid");
        this.maxBid = (Long) auctionMessage.get("maxBid");
        this.bidIncrement = (Long) auctionMessage.get("bidIncrement");
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getTimeOfAuction() {
        return timeOfAuction;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItem() {
        return item;
    }

    public Long getStartingBid() {
        return startingBid;
    }

    public Long getMaxBid() {
        return maxBid;
    }

    public Long getBidIncrement() {
        return bidIncrement;
    }

    @Override
    public String toString() {
        return "UserInputMessage{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", timeOfAuction=" + timeOfAuction +
                ", itemName='" + itemName + '\'' +
                ", item='" + item + '\'' +
                ", startingBid=" + startingBid +
                ", maxBid=" + maxBid +
                ", bidIncrement=" + bidIncrement +
                '}';
    }
}
