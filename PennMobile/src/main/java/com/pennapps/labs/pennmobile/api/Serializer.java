package com.pennapps.labs.pennmobile.api;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.pennapps.labs.pennmobile.classes.Building;
import com.pennapps.labs.pennmobile.classes.Course;
import com.pennapps.labs.pennmobile.classes.NewDiningHall;
import com.pennapps.labs.pennmobile.classes.Venue;

import java.lang.reflect.Type;
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

    public static class MenuSerializer implements JsonDeserializer<NewDiningHall> {
        @Override
        public NewDiningHall deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get("Document");
            content.getAsJsonObject().add("tblDayPart",
                    content.getAsJsonObject().get("tblMenu").getAsJsonObject().get("tblDayPart").getAsJsonArray());
            content.getAsJsonObject().remove("tblMenu");
            return new Gson().fromJson(content, NewDiningHall.class);
        }
    }
}
