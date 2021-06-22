package com.soumitra.auction;

import java.io.FileReader;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.common.util.concurrent.RateLimiter;



public class ApplicationMain {

  public static void main(String[] args) {

    Config conf = ConfigFactory.load();

    AuctionHouse auctionHouse = new AuctionHouse();


    // ingestion rate configs

    double ingestionRate=conf.getDouble("auction-java.ingestionRate");  // input read rate
    String inputFilePath = conf.getString("auction-java.inputFilePath"); //input file
    final RateLimiter rateLimiter = RateLimiter.create(ingestionRate);

    //JSON parser object to parse read file
    JSONParser jsonParser = new JSONParser();

    try (FileReader reader = new FileReader(inputFilePath))
    {
      //Read JSON file
      Object obj = jsonParser.parse(reader);
      JSONArray auctionMessageList = (JSONArray) obj;
      //System.out.println(auctionMessageList);


      auctionMessageList.forEach( inout -> {
        rateLimiter.acquire(); // may wait
        auctionHouse.processUserInputMessage( new UserInputMessage((JSONObject) inout ) );

      });

      System.out.println(">>> Press ENTER to exit <<<");
      System.in.read();
    } catch (Exception ignored) {
      ignored.printStackTrace();
    } finally {
      auctionHouse.terminate();
    }
  }
}
