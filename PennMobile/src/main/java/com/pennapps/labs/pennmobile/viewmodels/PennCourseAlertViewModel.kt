package com.pennapps.labs.pennmobile.viewmodels

import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pennapps.labs.pennmobile.api.PennCourseAlertApi
import com.pennapps.labs.pennmobile.classes.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PennCourseAlertViewModel: ViewModel() {
    //TODO: retrieve courses with API call
    val courses = mutableListOf<String>()
    var coursesList = mutableListOf<Course>()
    var sectionsList = mutableListOf<Section>()
    lateinit var selectedSection: Section
    private val _userRegistrations = MutableLiveData<List<PennCourseAlertRegistration>>()
    val userRegistrations : LiveData<List<PennCourseAlertRegistration>> get() = _userRegistrations
    var isSectionSelected: Boolean
    var deleteRegistrationErrorToast: Boolean = false


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
        sectionsList.add(Section(sectionId = "CIS-120-001"))
        isSectionSelected = false
    }

    fun createRegistration(section: String, autoResubscribe: Boolean = true, notifyWhenClosed: Boolean = false, id: String = "") {
        var response = ""
        Log.i("PCA-Create", "Section Number: $section")
        val registrationBody = PCARegistrationBody(section, autoResubscribe, notifyWhenClosed)
        PennCourseAlertApi.retrofitService.createRegistration(registrationBody)
            .enqueue(object: Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.i("PCA-Create", "Successfully created registration")
                    Log.i("PCA-Create", "Response: ${response.body()}")
                    updateRegistrationsList()
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    response = "Failure: " + t.message
                    Log.i("PCA", response)
                }
            })
    }

    fun getCourses(id: String, adapter: ArrayAdapter<Course>) {
        var response = ""
        //using search type course for query optimization
        PennCourseAlertApi.retrofitService.getCourses("current", id, "course")
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
    fun retrieveRegistrations() {
        var _response = ""
        PennCourseAlertApi.retrofitService.getAllRegistrations().enqueue( object: Callback<List<PennCourseAlertRegistration>> {
            override fun onFailure(call: Call<List<PennCourseAlertRegistration>>, t: Throwable) {
                _response = "Failure: " + t.message
                Log.i("PCA", "$_response")
            }

            override fun onResponse(call: Call<List<PennCourseAlertRegistration>>, response: Response<List<PennCourseAlertRegistration>>) {
                Log.i("PCA", "success")
                if (!response.body().isNullOrEmpty()) {
                    _userRegistrations.value = response.body()!!
                    _userRegistrations.value = _userRegistrations.value!!.sortedBy { it.section  }
                }
            }
        })
    }
    fun getSections(courseId: String, adapter: ArrayAdapter<Section>) {
        var response = ""

        //using search type course for query optimization
        PennCourseAlertApi.retrofitService.getSections("current", courseId)
            .enqueue(object: Callback<List<Section>> {
                override fun onResponse(
                    call: Call<List<Section>>,
                    response: Response<List<Section>>
                ) {
                    sectionsList = response.body() as MutableList<Section>? ?: mutableListOf()

                    if (sectionsList.isNotEmpty()) {
                        adapter.clear()
                        adapter.addAll(sectionsList.sortedBy{it.sectionId})
                    }
                }

                override fun onFailure(call: Call<List<Section>>, t: Throwable) {
                    response = "Failure: " + t.message
                    Log.i("PCA", response)
                }

            })
    }

    fun deleteRegistrations() {
        for (registration in _userRegistrations.value!!) {
            val id = registration.id.toString()
            deleteRegistration(id)
        }
        if (!deleteRegistrationErrorToast) {
            _userRegistrations.value = mutableListOf()
        }
    }

    private fun deleteRegistration(id: String) {
        var _response = ""
        Log.i("PCA_VM", "Id is: $id")
        val updateBody = PennCourseAlertUpdateBody(cancelled = true, deleted = true,
            autoResubscribe = false, closeNotifications = false, resubscribe = false
        )
        PennCourseAlertApi.retrofitService.updateRegistrationById(id, updateBody)
            .enqueue(object: Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    _response = response.code().toString() ?: ""
                    Log.i("PCA_VM", _response)
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    _response = "Failure: " + t.message
                    Log.i("PCA_VM", _response)
                    deleteRegistrationErrorToast = true
                }

            })
    }

    fun updateRegistrationsList() {
        retrieveRegistrations()
    }
}