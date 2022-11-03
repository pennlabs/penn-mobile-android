package com.pennapps.labs.pennmobile.viewmodels

import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModel
import com.pennapps.labs.pennmobile.api.PennCourseAlertApi
import com.pennapps.labs.pennmobile.classes.Course
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PennCourseAlertViewModel: ViewModel() {
    //TODO: retrieve courses with API call
    val courses = mutableListOf<String>()
    var coursesList = mutableListOf<Course>()
    init {
        courses.add("CIS-192-001")
        courses.add("MKTG-642-001")
        courses.add("MATH-3400-002")
        courses.add("CIS-545-001")
        courses.add("CIS-110-004")
        courses.add("EAS-512-004")
        courses.add("CIS-189-001")
        courses.add("CIS-2400-001")

        coursesList.add(Course(id = "CIS-100-001"))
//        getCourses()
    }

    //TODO: call with coroutines
    private fun getCourses() {
        var response = ""
        PennCourseAlertApi.retrofitService.getCourses("current").enqueue( object:
            Callback<List<Course>> {
            override fun onFailure(call: Call<List<Course>>, t: Throwable) {
                response = "Failure: " + t.message
                Log.i("PCA", response)
            }

            override fun onResponse(call: Call<List<Course>>, response: Response<List<Course>>) {
                Log.i("PCA", "${response.code()}")
                coursesList = response.body() as MutableList<Course>? ?: mutableListOf()
                Log.i("PCA", "Size: ${coursesList.size}")
                Log.i("PCA", coursesList.first().title)
            }
        })
    }

    fun searchForCourse(query: String, adapter: ArrayAdapter<Course>) {
        var response = ""
        //using search type course for query optimization
        PennCourseAlertApi.retrofitService.searchForCourse("current", query, "course")
            .enqueue(object: Callback<List<Course>> {
                override fun onResponse(
                    call: Call<List<Course>>,
                    response: Response<List<Course>>
                ) {
                    coursesList = response.body() as MutableList<Course>? ?: mutableListOf()
                    Log.i("PCA", "Size: ${coursesList.size}")
                    if (coursesList.isNotEmpty()) {
                        adapter.clear()
                        adapter.addAll(coursesList.sortedBy{it.id})
                    }
                }

                override fun onFailure(call: Call<List<Course>>, t: Throwable) {
                    response = "Failure: " + t.message
                    Log.i("PCA", response)
                }

            })

    }
}