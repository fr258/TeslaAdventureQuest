package com.tesla.quest.TeslaAdventureQuest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.rockset.client.ApiException;
import com.rockset.client.RocksetClient;
import com.rockset.client.model.*;

@Service
public class TextingService {

    private static String GOOGLE_API_KEY = "AIzaSyC0FKxDMK_aR4hvX04ShLPDNddDjHOfWJo";

    @PostConstruct
    @Scheduled(cron = "0 15 * * *")
    public void generateUpdate() throws IOException, InterruptedException
    {

        RocksetClient rs = new RocksetClient("MkHyLoYViRW6njUZ2Kredc6uUg9Xy1PIyGu4bk6P5QUFXo8uKnFJhGNHJjhNzJuo", "api.rs2.usw2.rockset.com");

        QueryRequest request = new QueryRequest()
                            .sql(new QueryRequestSql()
            .query("SELECT latitude FROM questCollection where _event_time=(SELECT MAX(_event_time) from questCollection) LIMIT 1"));
            double latResponse = 0.0;
        try {
                QueryResponse response = rs.query(request);
            latResponse = fromJson(response.toString(), "latitude");
                System.out.println("YOUR ROCKSET RESULTS ARE: "+latResponse);
        } catch (Exception e) {
                e.printStackTrace();}

        QueryRequest request2 = new QueryRequest()
                            .sql(new QueryRequestSql()
            .query("SELECT longitude FROM questCollection where _event_time=(SELECT MAX(_event_time) from questCollection) LIMIT 1"));
            double longResponse = 0.0 ;
        try {
                QueryResponse response2 = rs.query(request2);
        longResponse = fromJson(response2.toString(), "longitude");
                System.out.println("YOUR ROCKSET RESULTS ARE: "+longResponse);
        } catch (Exception e) {
                e.printStackTrace();}
        

        String body = "Hello Farrah<3, please run this application on your machine and respond, this is intolerable \n";

        String latitude = "37.12165069580078";
        String longitude = "-106.62577056884766";
        String range = "100000";

        String GoogleMapsRequest = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
        GoogleMapsRequest+=latResponse + ",";
        GoogleMapsRequest+=longResponse;
        GoogleMapsRequest+="&radius=";
        GoogleMapsRequest+=range;
        GoogleMapsRequest+="&types=museum&key=";
        GoogleMapsRequest+=GOOGLE_API_KEY;

        //HttpRequest

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder().uri(URI.create(GoogleMapsRequest)).build();
        HttpResponse<String> httpResponse = client.send(request1, HttpResponse.BodyHandlers.ofString());
        //System.out.println("Body: " + GoogleMapsRequest);
        //System.out.println("Reponse: " + httpResponse.body());

        String[] results = httpResponse.body().split("business_status");
        int luckyNumber = (int)(Math.random()*results.length);
        int start = results[luckyNumber].indexOf("name");
        start+=9;
        String result = "";
        while(results[luckyNumber].charAt(start)!='"')
        {
            result+=results[luckyNumber].charAt(start);
            start++;
        }

        System.out.println("YOUR GOOGLE RESULT IS: " + result);
        
        body+="Check out this fun place: " + result + "\n";
        body+="Not interested? A new quest will spawn soon enough...";

        sendUpdate(body);
    }
    public void sendUpdate(String body)
    {
        System.out.println("You are here");
        Twilio.init("ACbdbb9baa6ef8dcc9c677e90b1b2f6c35", "a61c16d5d0d17cbe640afcef6b78842b");
        Message message = Message.creator(
        new PhoneNumber("+17329125411"),
        new PhoneNumber("+18509721153"),
        body)
            .create();
        System.out.println(message.getSid());
    }

    public static double fromJson(String response, String field) {
        String val1 = response.substring(response.indexOf(field) + field.length() + 1); 
        int end1 = val1.indexOf(",");
        int end2 = val1.indexOf("}");
        int endIndex = ((end1 < end2) && (end1>=0)) ? end1 : end2;
        


        return Float.parseFloat(val1.substring(0, endIndex)); 
    }
    //AIzaSyDVbrRP3sDpqcbK0Oqcei31aRKxLX6c-Tk
}
