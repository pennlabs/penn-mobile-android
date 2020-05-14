package com.pennapps.labs.pennmobile.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.pennapps.labs.pennmobile.classes.Building;
import com.pennapps.labs.pennmobile.classes.CalendarEvent;
import com.pennapps.labs.pennmobile.classes.Course;
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.classes.FlingEvent;
import com.pennapps.labs.pennmobile.classes.GSRLocation;
import com.pennapps.labs.pennmobile.classes.GSRReservation;
import com.pennapps.labs.pennmobile.classes.Gym;
import com.pennapps.labs.pennmobile.classes.HomeCell;
import com.pennapps.labs.pennmobile.classes.HomeCellInfo;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple;
import com.pennapps.labs.pennmobile.classes.LaundryUsage;
import com.pennapps.labs.pennmobile.classes.Account;
import com.pennapps.labs.pennmobile.classes.Venue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    public static class CourseSerializer implements JsonDeserializer<List<Course>> {
        @Override
        public List<Course> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("courses");
            return new Gson().fromJson(content, new TypeToken<List<Course>>() {
            }.getType());
        }
    }

    public static class BuildingSerializer implements JsonDeserializer<List<Building>> {
        @Override
        public List<Building> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("result_data");
            return new Gson().fromJson(content, new TypeToken<List<Building>>() {
            }.getType());
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

    public static class VenueSerializer implements JsonDeserializer<List<Venue>> {
        @Override
        public List<Venue> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je
                    .getAsJsonObject().get("document")
                    .getAsJsonObject().get("venue");
            return new Gson().fromJson(content, new TypeToken<List<Venue>>() {
            }.getType());
        }
    }

    public static class MenuSerializer implements JsonDeserializer<DiningHall> {
        @Override
        public DiningHall deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("Document");
            content.getAsJsonObject().add("tblDayPart",
                    content.getAsJsonObject().get("tblMenu").getAsJsonObject().get("tblDayPart").getAsJsonArray());
            content.getAsJsonObject().remove("tblMenu");
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

    // gets laundry room list
    public static class LaundryRoomListSerializer implements JsonDeserializer<List<LaundryRoomSimple>> {
        @Override
        public List<LaundryRoomSimple> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("halls");
            return new Gson().fromJson(content, new TypeToken<List<LaundryRoomSimple>>() {
            }.getType());
        }
    }

    // gets gsr locations
    public static class GsrLocationSerializer implements JsonDeserializer<List<GSRLocation>> {
        @Override
        public List<GSRLocation> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("locations");
            return new Gson().fromJson(content, new TypeToken<List<GSRLocation>>() {
            }.getType());
        }
    }
  
    // gets laundry usage
    public static class LaundryUsageSerializer implements JsonDeserializer<LaundryUsage> {
        @Override
        public LaundryUsage deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject();
            return new Gson().fromJson(content, new TypeToken<LaundryUsage>() {
            }.getType());
        }
    }

    // gets laundry pref data from server
    public static class LaundryPrefSerializer implements JsonDeserializer<List<Integer>> {
        @Override
        public List<Integer> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
            throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("rooms");
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

    // home page custom deserializer, depends on type of cell
    public static class HomePageSerializer implements JsonDeserializer<List<HomeCell>> {
        @Override
        public List<HomeCell> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonArray cellJsonArr = je.getAsJsonObject().get("cells").getAsJsonArray();
            ArrayList<HomeCell> cells = new ArrayList<>();

            for (JsonElement cell : cellJsonArr) {
                JsonObject cellObj = cell.getAsJsonObject();
                String cellType = cellObj.get("type").getAsString();
                JsonElement info = cellObj.get("info");
                HomeCell newCell = new HomeCell();
                newCell.setType(cellType);
                if (cellType.equals("reservations")) {
                    ArrayList<GSRReservation> reservations = new Gson().fromJson(info, new TypeToken<List<GSRReservation>>() {
                    }.getType());
                    newCell.setReservations(reservations);
                } else if (cellType.equals("calendar")) {
                    ArrayList<CalendarEvent> events = new Gson().fromJson(info, new TypeToken<List<CalendarEvent>>() {
                    }.getType());
                    newCell.setEvents(events);
                } else if (cellType.equals("news") | cellType.equals("dining")
                        | cellType.equals("laundry") | cellType.equals("courses")) {
                    HomeCellInfo infoObj = new Gson().fromJson(info, new TypeToken<HomeCellInfo>() {
                    }.getType());
                    newCell.setInfo(infoObj);
                }
                cells.add(newCell);
            }

            return cells;
        }
    }

    // for FITNESS!
    public static class GymSerializer implements JsonDeserializer<List<Gym>> {

        @Override
        public List<Gym> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("schedule");
            return new Gson().fromJson(content, new TypeToken<List<Gym>>() {}.getType());
        }
    }

    // for GSR Reservations
    public static class GsrReservationSerializer implements JsonDeserializer<List<GSRReservation>> {

        @Override
        public List<GSRReservation> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("reservations");
            return new Gson().fromJson(content, new TypeToken<List<GSRReservation>>() {}.getType());
        }
    }
}
