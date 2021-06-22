# AuctionHouse

To run the project please execute :  `mvn compile exec:exec`

### Files :


| File Name     | Description   |
| ------------- |---------------|
| ApplicationMain.java       | Contains the main() method, reads the JSON file in memory then process input according to the configured rate|
| AuctionHouse.java     | Entry point for the AuctionAPI, exposes  `processUserInputMessage(UserInputMessage userInputMessage)` method  to process each inout message one by one   |
| AuctionHouseActor.java | Receives the UserInputMessage and based on the message type creates or updates the AuctionHandler      |
| AuctionHandler.java | Handles auction of one item, supports : NewItem, NewBidder, NewBid and EndAuction commands       |
| UserActor.java | Bidding Actor, auto increments the bid until user Max Bid is reached, supports `onCurrentStatus(UserActor.CurrentStatus currentAuctionStatus)` command |
| UserInputMessage.java | POJO representation of the the input JSON|
| application.conf | Holds the configrations items : ingestionRate and inputFilePath |
| logback.xml | logging config|


System Design :
This auction system is based on Akka actor framework. There are three main actors in the system currently :
* AuctionHouseActor
* AuctionHandler
* UserActor

On creation time the AuctionHandler actor schedules a timer which send "EndAuction" command to itself to end the auction and announce the winner.
