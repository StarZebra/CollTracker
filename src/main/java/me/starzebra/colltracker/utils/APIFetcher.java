package me.starzebra.colltracker.utils;

import com.google.gson.*;
import me.starzebra.colltracker.CollTracker;
import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.HttpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APIFetcher {

    public static Map<Integer, String> fetchCollections(String url){

        try {
            HttpResponse response = Request.get(url).execute().returnResponse();
            Content content = Request.get(url).execute().returnContent();

            if(response.getCode() == 200){
                CollTracker.LOGGER.info("Response 200 OK");
                JsonElement json = new JsonParser().parse(content.asString());
                JsonObject jsonObject = json.getAsJsonObject();
                JsonObject miningItems = jsonObject.getAsJsonObject("collections").getAsJsonObject("MINING").getAsJsonObject("items");

                List<String> list = new ArrayList<>();

                for(Map.Entry<String, JsonElement> item : miningItems.entrySet()){
                    list.add(item.getValue().getAsJsonObject().get("name").getAsString());
                }

                //System.out.println(list);

                return parseData(list);
            }else{
                CollTracker.LOGGER.error("ERROR FETCHING COLLECTIONS");
                CollTracker.LOGGER.error("CODE: {}, REASON: {}", response.getCode(), response.getReasonPhrase());

                return new HashMap<>();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static Map<Integer, String> parseData(List<String> data){
        Map<Integer, String> result = new HashMap<>();

        if(data == null || data.isEmpty()) return result;

        for (int i = 0; i < data.size(); i++) {
            String item = data.get(i).trim();
            result.put(i, item);
        }

        return result;
    }

}
