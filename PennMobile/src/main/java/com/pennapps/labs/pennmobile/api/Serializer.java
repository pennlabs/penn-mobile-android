package com.pennapps.labs.pennmobile.api;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.pennapps.labs.pennmobile.classes.Building;
import com.pennapps.labs.pennmobile.classes.BusRoute;
import com.pennapps.labs.pennmobile.classes.BusStop;
import com.pennapps.labs.pennmobile.classes.Course;
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
import com.pennapps.labs.pennmobile.classes.LaundryMachine;
import com.pennapps.labs.pennmobile.classes.Venue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adel on 12/15/14.
 * Wrapper class for Gson Serializers
 */
public class Serializer {
    public static class CourseSerializer implements JsonDeserializer<List<Course>> {
        @Override
        public List<Course> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("courses");
            return new Gson().fromJson(content, new TypeToken<List<Course>>(){}.getType());
        }
    }

    public static class BuildingSerializer implements JsonDeserializer<List<Building>> {
        @Override
        public List<Building> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("result_data");
            return new Gson().fromJson(content, new TypeToken<List<Building>>(){}.getType());
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
            return new Gson().fromJson(content, new TypeToken<List<Venue>>(){}.getType());
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

    public static class BusStopSerializer implements JsonDeserializer<List<BusStop>> {
        @Override
        public List<BusStop> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("result_data");

            return new Gson().fromJson(content, new TypeToken<List<BusStop>>(){}.getType());
        }
    }

    public static class BusRouteSerializer implements JsonDeserializer<BusRoute> {
        @Override
        public BusRoute deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("result_data");
            JsonObject jsonObject = content.getAsJsonObject();

            BusRoute busRoute = new Gson().fromJson(content, BusRoute.class);

            if (jsonObject.get("path") != null) {
                JsonElement stopList = jsonObject.get("path");
                ArrayList<BusStop> stops = new Gson().fromJson(stopList, new TypeToken<List<BusStop>>() {
                }.getType());
                busRoute.setStops(stops);
            }

            return busRoute;
        }
    }

    public static class BusRouteListSerializer implements JsonDeserializer<List<BusRoute>> {
        @Override
        public List<BusRoute> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("result_data");
            return new Gson().fromJson(content, new TypeToken<List<BusRoute>>(){}.getType());
        }
    }

    public static class LaundryListSerializer implements JsonDeserializer<List<LaundryRoom>> {
        @Override
        public List<LaundryRoom> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("halls");
            return new Gson().fromJson(content, new TypeToken<List<LaundryRoom>>(){}.getType());
        }
    }

    public static class LaundryMachineSerializer implements JsonDeserializer<List<LaundryMachine>> {
        @Override
        public List<LaundryMachine> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("machines");
            return new Gson().fromJson(content, new TypeToken<List<LaundryMachine>>(){}.getType());
        }
    }
}
