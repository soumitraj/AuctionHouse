package com.soumitra.auction;

import akka.actor.testkit.typed.CapturedLogEvent;
import akka.actor.testkit.typed.Effect;
import akka.actor.testkit.typed.javadsl.BehaviorTestKit;
import org.junit.Test;
import org.slf4j.event.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

//#definition
public class AuctionHouseActorTest {

    //#test
    @Test
    public void testAuctionHandlerCreatedWithAuctionId() {
        UserInputMessage userInput = new UserInputMessage("a8cfcb76-7f24-4420-a5ba-d46dd77bdffd","newItem",
                "Bicycle","Hot Wheels Child's Bicycle", (long) 300);

        BehaviorTestKit<UserInputMessage> test = BehaviorTestKit.create(AuctionHouseActor.create());
        test.run(userInput);
        assertEquals(userInput.getId(), test.expectEffectClass(Effect.Spawned.class).childName());

        List<CapturedLogEvent> allLogEntries = test.getAllLogEntries();
        assertEquals(1, allLogEntries.size());

        CapturedLogEvent expectedLogEvent =
                new CapturedLogEvent(
                        Level.INFO,
                        "onUserInputMessage " + userInput,
                        Optional.empty(),
                        Optional.empty(),
                        new HashMap<>());
        assertEquals(expectedLogEvent, allLogEntries.get(0));

    }

}
