package com.pennapps.labs.pennmobile.api;

import com.pennapps.labs.pennmobile.classes.Course;
import com.pennapps.labs.pennmobile.classes.DirectoryPerson;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Adel on 12/13/14.
 * Retrofit interface to the Penn Labs API
 */
public interface Labs {
    @GET("/registrar/search")
    List<Course> courses(
        @Query("q") String name);

    @GET("/directory/search")
    List<DirectoryPerson> people(
        @Query("name") String name);
}
