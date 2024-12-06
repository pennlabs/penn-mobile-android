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
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.classes.FlingEvent;
import com.pennapps.labs.pennmobile.classes.GSRLocation;
import com.pennapps.labs.pennmobile.classes.GSRReservation;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
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

    private static Venue parseVenue(JsonObject jsonVenue) {
        Venue venue = new Venue();
        venue.setId(jsonVenue.get("id").getAsInt());
        venue.setName(jsonVenue.get("name").getAsString());
        // venue.setVenueType(jsonVenue.get("venueType").getAsString());
        List<VenueInterval> venueIntervals = new ArrayList<>();
        JsonArray jsonVenueIntervals = jsonVenue.get("days").getAsJsonArray();
        for (int i = 0; i < jsonVenueIntervals.size(); i++) {
            VenueInterval venueInterval = new VenueInterval();
            List<VenueInterval.MealInterval> mealIntervals = new ArrayList<VenueInterval.MealInterval>();
            JsonObject jsonVenueInterval = jsonVenueIntervals.get(i).getAsJsonObject();
            JsonElement jsonMealIntervalsElement = jsonVenueInterval.get("dayparts");
            venueInterval.setDate(jsonVenueInterval.get("date").getAsString());
            JsonArray jsonMealIntervals = null;
            try {
                jsonMealIntervals = jsonMealIntervalsElement.getAsJsonArray();
            } catch (Exception e) {
                jsonMealIntervals = new JsonArray();
                jsonMealIntervals.add(jsonMealIntervalsElement);
            }
            for (int j = 0; j < jsonMealIntervals.size(); j++) {
                VenueInterval.MealInterval mealInterval = new VenueInterval.MealInterval();
                JsonObject jsonMeal = jsonMealIntervals.get(j).getAsJsonObject();
                mealInterval.setClose(jsonMeal.get("endtime").getAsString().substring(11));
                mealInterval.setType(jsonMeal.get("label").getAsString());
                mealInterval.setOpen(jsonMeal.get("starttime").getAsString().substring(11));
                mealIntervals.add(mealInterval);
            }
            venueInterval.setMeals(mealIntervals);
            venueIntervals.add(venueInterval);
        }
        venue.setHours(venueIntervals);
        return venue;
    }
    public static class VenueSerializer implements JsonDeserializer<List<Venue>> {
        @Override
        public List<Venue> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            List<Venue> res = new LinkedList<>();
            JsonArray venueArray = je.getAsJsonArray();
            for (int i = 0; i < venueArray.size(); i++) {
                JsonObject venue = venueArray.get(i).getAsJsonObject();
                res.add(parseVenue(venue));
            }
            return res;
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

    // gets laundry room
    public static class LaundryRoomSerializer implements JsonDeserializer<LaundryRoom> {
        @Override
        public LaundryRoom deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject();
            return new Gson().fromJson(content, new TypeToken<LaundryRoom>() {
            }.getType());
        }
    }

    // gets gsr locations
    public static class GsrLocationSerializer implements JsonDeserializer<List<GSRLocation>> {
        @Override
        public List<GSRLocation> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonArray jsonLocations = je.getAsJsonArray();
            List<GSRLocation> locations = new ArrayList<GSRLocation>();
            for (int i = 0; i < jsonLocations.size(); i++) {
                GSRLocation location = new GSRLocation();
                JsonObject jsonLocation = jsonLocations.get(i).getAsJsonObject();

                location.id = jsonLocation.get("lid").getAsString();
                location.gid = jsonLocation.get("gid").getAsInt();
                location.name = jsonLocation.get("name").getAsString();
                location.kind = jsonLocation.get("kind").getAsString();
                locations.add(location);
            }
            return locations;
        }
    }

    // gets laundry pref data from server
    public static class LaundryPrefSerializer implements JsonDeserializer<List<Integer>> {
        @Override
        public List<Integer> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
            throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("rooms");
            if (content == null) {
                content = je.getAsJsonObject().get("preferences");
            }
            return new Gson().fromJson(content, new TypeToken<List<Integer>>() {
            }.getType());
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

    // for GSR Reservations
    public static class GsrReservationSerializer implements JsonDeserializer<List<GSRReservation>> {

        @Override
        public List<GSRReservation> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonArray content = je.getAsJsonArray();
            List<GSRReservation> reservationList = new ArrayList<>();
            for (JsonElement jsonElement: content) {
                GSRReservation reservation = new GSRReservation();
                JsonObject jsonReservation = jsonElement.getAsJsonObject();
                reservation.bookingId = jsonReservation.get("booking_id").getAsString();
                reservation.name = jsonReservation.get("room_name").getAsString();
                reservation.fromDate = jsonReservation.get("start").getAsString();
                reservation.toDate = jsonReservation.get("end").getAsString();
                JsonObject jsonInfo = jsonReservation.get("gsr").getAsJsonObject();
                reservation.gid = jsonInfo.get("gid").getAsString();
                Map<String, String> info = new HashMap<>();
                String thumbnail = jsonInfo.get("image_url").getAsString();
                info.put("thumbnail", thumbnail);
                reservation.info = info;
                reservationList.add(reservation);
            }
            return reservationList;
        }
    }

    // for custom posts
    public static class PostsSerializer implements JsonDeserializer<List<Post>> {

        @Override
        public List<Post> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonArray();
            return new Gson().fromJson(content, new TypeToken<List<Post>>() {}.getType());
        }
    }
}
