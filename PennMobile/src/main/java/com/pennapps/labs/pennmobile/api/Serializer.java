package com.pennapps.labs.pennmobile.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.pennapps.labs.pennmobile.classes.Account;
import com.pennapps.labs.pennmobile.classes.CalendarEvent;
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.classes.FlingEvent;
import com.pennapps.labs.pennmobile.classes.GSRLocation;
import com.pennapps.labs.pennmobile.classes.GSRReservation;
import com.pennapps.labs.pennmobile.classes.Gym;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple;
import com.pennapps.labs.pennmobile.classes.LaundryUsage;
import com.pennapps.labs.pennmobile.classes.Post;
import com.pennapps.labs.pennmobile.classes.Venue;
import com.pennapps.labs.pennmobile.classes.VenueInterval;

import java.lang.reflect.Type;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Adel on 12/15/14.
 * Wrapper class for Gson Serializers
 */
public class Serializer {

    public static class UserSerializer implements JsonDeserializer<Account> {
        @Override
        public Account deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject();
            return new Gson().fromJson(content, Account.class);
        }
    }

    public static class DataSerializer<T> implements JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("result_data");
            return new Gson().fromJson(content, type);
        }
    }

    public static class MenuSerializer implements JsonDeserializer<DiningHall> {
        @Override
        public DiningHall deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("Document");
            JsonArray menus = content.getAsJsonObject().get("tblMenu").getAsJsonArray();
            Format f = new SimpleDateFormat("MM/dd/yyyy");
            String today = f.format(new Date());
            if (today.startsWith("0")) {
                today = today.substring(1);
            }
            try {
                for (int i = 0; i < menus.size(); i++) {
                    JsonObject menu = menus.get(i).getAsJsonObject();
                    String date = menu.get("menudate").getAsString();
                    if (date.equals(today)) {
                        content.getAsJsonObject().add("tblDayPart", menu.get("tblDayPart").getAsJsonArray());
                        break;
                    }
                }
            } catch (Exception e) {

            }
            return new Gson().fromJson(content, DiningHall.class);
        }
    }

    // fling events
    public static class FlingEventSerializer implements  JsonDeserializer<List<FlingEvent>> {

        @Override
        public List<FlingEvent> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonElement content = json.getAsJsonObject().get("events");
            return new Gson().fromJson(content, new TypeToken<List<FlingEvent>>() {
            }.getType());
        }
    }

}
