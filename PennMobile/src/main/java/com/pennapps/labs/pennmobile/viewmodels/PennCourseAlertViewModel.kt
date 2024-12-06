package com.pennapps.labs.pennmobile.viewmodels

import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pennapps.labs.pennmobile.api.PennCourseAlertApi
import com.pennapps.labs.pennmobile.classes.Course
import com.pennapps.labs.pennmobile.classes.PCARegistrationBody
import com.pennapps.labs.pennmobile.classes.PennCourseAlertRegistration
import com.pennapps.labs.pennmobile.classes.PennCourseAlertUpdateBody
import com.pennapps.labs.pennmobile.classes.Profile
import com.pennapps.labs.pennmobile.classes.Section
import com.pennapps.labs.pennmobile.classes.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val TAG = "PCA_VM"

class PennCourseAlertViewModel : ViewModel() {
    var coursesList = mutableListOf<Course>()
    var sectionsList = mutableListOf<Section>()
    lateinit var selectedSection: Section
    private val _userRegistrations = MutableLiveData<List<PennCourseAlertRegistration>>()
    val userRegistrations: LiveData<List<PennCourseAlertRegistration>> get() = _userRegistrations
    var isSectionSelected: Boolean
    private val _registrationCreatedSuccessfullyToast = MutableLiveData<Boolean>()
    val registrationCreatedSuccessfullyToast: LiveData<Boolean>
        get() =
            _registrationCreatedSuccessfullyToast
    var bearerToken: String = ""
    var currentRegistration = PennCourseAlertRegistration(id = -1)
    private val _userInfo = MutableLiveData<UserInfo>()
    val userInfo: LiveData<UserInfo> get() = _userInfo

    init {
        isSectionSelected = false
    }

    fun createRegistration(
        section: String,
        notifyWhenClosed: Boolean = false,
        id: String = "",
    ) {
        var response = ""
        Log.i(TAG, "Created Section Number: $section")
        val registrationBody =
            PCARegistrationBody(
                section,
                autoResubscribe = true,
                closeNotification = notifyWhenClosed,
            )
        PennCourseAlertApi.retrofitService
            .createRegistration(registrationBody, bearerToken)
            .enqueue(
                object : Callback<String> {
                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>,
                    ) {
                        Log.i(TAG, "Successfully created registration")
                        Log.i(TAG, "Response: ${response.body()}")
                        updateRegistrationsList()
                        _registrationCreatedSuccessfullyToast.value = true
                    }

                    override fun onFailure(
                        call: Call<String>,
                        t: Throwable,
                    ) {
                        response = "Failure: " + t.message
                        Log.i(TAG, response)
                    }
                },
            )
    }

    fun getCourses(
        id: String,
        adapter: ArrayAdapter<Course>,
    ) {
        var response = ""
        // using search type course for query optimization
        PennCourseAlertApi.retrofitService
            .getCourses("current", id, "course")
            .enqueue(
                object : Callback<List<Course>> {
                    override fun onResponse(
                        call: Call<List<Course>>,
                        response: Response<List<Course>>,
                    ) {
                        coursesList = response.body() as MutableList<Course>? ?: mutableListOf()
                        Log.i(TAG, "Size: ${coursesList.size}")
                        if (coursesList.isNotEmpty()) {
                            adapter.clear()
                            adapter.addAll(coursesList.sortedBy { it.id })
                        }
                    }

                    override fun onFailure(
                        call: Call<List<Course>>,
                        t: Throwable,
                    ) {
                        response = "Failure: " + t.message
                        Log.i(TAG, response)
                    }
                },
            )
    }

    fun retrieveRegistrations() {
        var internalResponse = ""
        PennCourseAlertApi.retrofitService
            .getAllRegistrations(bearerToken)
            .enqueue(
                object : Callback<List<PennCourseAlertRegistration>> {
                    override fun onFailure(
                        call: Call<List<PennCourseAlertRegistration>>,
                        t: Throwable,
                    ) {
                        internalResponse = "Failure: " + t.message
                        Log.i(TAG, "$internalResponse")
                    }

                    override fun onResponse(
                        call: Call<List<PennCourseAlertRegistration>>,
                        response: Response<List<PennCourseAlertRegistration>>,
                    ) {
                        Log.i(TAG, "success")
                        if (!response.body().isNullOrEmpty()) {
                            _userRegistrations.value = response.body()!!
                            _userRegistrations.value =
                                _userRegistrations.value!!.sortedBy { it.section }
                            Log.i(
                                TAG,
                                "notify ${_userRegistrations.value!![0].closeNotification}",
                            )
                            Log.i(
                                TAG,
                                "subscribed ${_userRegistrations.value!![0].cancelled}",
                            )
                        }
                    }
                },
            )
    }

    fun getSections(
        courseId: String,
        adapter: ArrayAdapter<Section>,
    ) {
        var response = ""

        // using search type course for query optimization
        PennCourseAlertApi.retrofitService
            .getSections("current", courseId)
            .enqueue(
                object : Callback<List<Section>> {
                    override fun onResponse(
                        call: Call<List<Section>>,
                        response: Response<List<Section>>,
                    ) {
                        sectionsList = response.body() as MutableList<Section>? ?: mutableListOf()

                        if (sectionsList.isNotEmpty()) {
                            adapter.clear()
                            adapter.addAll(sectionsList.sortedBy { it.sectionId })
                        }
                    }

                    override fun onFailure(
                        call: Call<List<Section>>,
                        t: Throwable,
                    ) {
                        response = "Failure: " + t.message
                        Log.i(TAG, response)
                    }
                },
            )
    }

    fun deleteRegistrations() {
        if (_userRegistrations.value.isNullOrEmpty()) {
            return
        }
        for (registration in _userRegistrations.value!!) {
            val id = registration.id.toString()
            deleteRegistration(id)
        }
        _userRegistrations.value = mutableListOf()
    }

    fun cancelRegistration(id: String) {
        var internalResponse = ""
        Log.i(TAG, "Id is: $id")
        val updateBody =
            PennCourseAlertUpdateBody(
                cancelled = true,
                deleted = false,
                autoResubscribe = false,
                closeNotifications = false,
                resubscribe = false,
            )
        PennCourseAlertApi.retrofitService
            .updateRegistrationById(id, updateBody, bearerToken)
            .enqueue(
                object : Callback<String> {
                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>,
                    ) {
                        internalResponse = response.code().toString() + "canceled successfully"
                        Log.i(TAG, internalResponse)
                    }

                    override fun onFailure(
                        call: Call<String>,
                        t: Throwable,
                    ) {
                        internalResponse = "Failure: " + t.message
                        Log.i(TAG, internalResponse)
                    }
                },
            )
    }

    fun resubscribeToRegistration(id: String) {
        var internalResponse = ""
        Log.i(TAG, "Id is: $id")
        val updateBody =
            PennCourseAlertUpdateBody(
                cancelled = false,
                closeNotifications = false,
                resubscribe = true,
                autoResubscribe = true,
            )
        PennCourseAlertApi.retrofitService
            .updateRegistrationById(id, updateBody, bearerToken)
            .enqueue(
                object : Callback<String> {
                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>,
                    ) {
                        internalResponse = response.code().toString() + "resubscribed successfully"
                        Log.i(TAG, internalResponse)
                    }

                    override fun onFailure(
                        call: Call<String>,
                        t: Throwable,
                    ) {
                        internalResponse = "Failure: " + t.message
                        Log.i(TAG, internalResponse)
                    }
                },
            )
    }

    fun switchOnClosedNotifications(
        id: String,
        notifyWhenClosed: Boolean,
    ) {
        var internalResponse = ""
        Log.i(TAG, "Id is: $id")

        val updateBody = PennCourseAlertUpdateBody(closeNotifications = notifyWhenClosed)

        PennCourseAlertApi.retrofitService
            .updateRegistrationById(id, updateBody, bearerToken)
            .enqueue(
                object : Callback<String> {
                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>,
                    ) {
                        internalResponse = response.code().toString() + " - " + response.body().toString()
                        Log.i(TAG, internalResponse)
                    }

                    override fun onFailure(
                        call: Call<String>,
                        t: Throwable,
                    ) {
                        internalResponse = "Failure: " + t.message
                        Log.i(TAG, internalResponse)
                    }
                },
            )
    }

    private fun deleteRegistration(id: String) {
        var internalResponse = ""
        Log.i(TAG, "Id is: $id")
        val updateBody =
            PennCourseAlertUpdateBody(
                cancelled = true,
                deleted = true,
                autoResubscribe = false,
                closeNotifications = false,
                resubscribe = false,
            )
        PennCourseAlertApi.retrofitService
            .updateRegistrationById(id, updateBody, bearerToken)
            .enqueue(
                object : Callback<String> {
                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>,
                    ) {
                        internalResponse = response.code().toString()
                        Log.i(TAG, internalResponse)
                    }

                    override fun onFailure(
                        call: Call<String>,
                        t: Throwable,
                    ) {
                        internalResponse = "Failure: " + t.message
                        Log.i(TAG, internalResponse)
                    }
                },
            )
    }

    fun getRegistrationById(id: String) {
        var internalResponse = ""
        PennCourseAlertApi.retrofitService
            .getRegistrationById(id, bearerToken)
            .enqueue(
                object : Callback<PennCourseAlertRegistration> {
                    override fun onResponse(
                        call: Call<PennCourseAlertRegistration>,
                        response: Response<PennCourseAlertRegistration>,
                    ) {
                        currentRegistration = response.body()!!
                    }

                    override fun onFailure(
                        call: Call<PennCourseAlertRegistration>,
                        t: Throwable,
                    ) {
                        internalResponse = "Failure: " + t.message
                        Log.i(TAG, internalResponse)
                    }
                },
            )
    }

    fun updateRegistrationsList() {
        retrieveRegistrations()
    }

    fun setBearerTokenValue(token: String) {
        bearerToken = token
    }

    fun updateUserInfo(
        email: String,
        phone: String,
    ) {
        var internalResponse = ""
        val profile = Profile(true, phone = phone, email = email)
        PennCourseAlertApi.retrofitService
            .updateInfo(profile, bearerToken)
            .enqueue(
                object : Callback<String> {
                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>,
                    ) {
                        internalResponse = response.code().toString() ?: ""
                        Log.i(TAG, "User info updated successfully: $internalResponse")
                    }

                    override fun onFailure(
                        call: Call<String>,
                        t: Throwable,
                    ) {
                        internalResponse = "Failure: " + t.message
                        Log.i(TAG, internalResponse)
                    }
                },
            )
    }

    fun getUserInfo() {
        var internalResponse = ""
        PennCourseAlertApi.retrofitService
            .retrieveUser(bearerToken)
            .enqueue(
                object : Callback<UserInfo> {
                    override fun onResponse(
                        call: Call<UserInfo>,
                        response: Response<UserInfo>,
                    ) {
                        internalResponse = response.code().toString() ?: ""
                        Log.i(TAG, internalResponse)
                        _userInfo.value = response.body()
                    }

                    override fun onFailure(
                        call: Call<UserInfo>,
                        t: Throwable,
                    ) {
                        internalResponse = "Failure: " + t.message
                        Log.i(TAG, internalResponse)
                    }
                },
            )
    }

    fun clearSelectedSection() {
        isSectionSelected = false
    }

    fun onSuccessToastDone() {
        _registrationCreatedSuccessfullyToast.value = false
    }
}
