import com.pennapps.labs.pennmobile.classes.AccessTokenResponse
import com.pennapps.labs.pennmobile.classes.Account
import com.pennapps.labs.pennmobile.classes.Article
import com.pennapps.labs.pennmobile.classes.CalendarEvent
import com.pennapps.labs.pennmobile.classes.DiningHall
import com.pennapps.labs.pennmobile.classes.DiningPreferences
import com.pennapps.labs.pennmobile.classes.DiningRequest
import com.pennapps.labs.pennmobile.classes.FitnessRequest
import com.pennapps.labs.pennmobile.classes.FitnessRoom
import com.pennapps.labs.pennmobile.classes.FitnessRoomUsage
import com.pennapps.labs.pennmobile.classes.FlingEvent
import com.pennapps.labs.pennmobile.classes.GSR
import com.pennapps.labs.pennmobile.classes.GSRBookingResult
import com.pennapps.labs.pennmobile.classes.GSRLocation
import com.pennapps.labs.pennmobile.classes.GSRReservation
import com.pennapps.labs.pennmobile.classes.LaundryPreferences
import com.pennapps.labs.pennmobile.classes.LaundryRequest
import com.pennapps.labs.pennmobile.classes.LaundryRoom
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple
import com.pennapps.labs.pennmobile.classes.LaundryUsage
import com.pennapps.labs.pennmobile.classes.Poll
import com.pennapps.labs.pennmobile.classes.Post
import com.pennapps.labs.pennmobile.classes.SaveAccountResponse
import com.pennapps.labs.pennmobile.classes.Venue
import com.pennapps.labs.pennmobile.classes.WhartonStatus
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import io.reactivex.Observable

interface StudentLife {
    @FormUrlEncoded
    @POST("accounts/token/")
    fun getAccessToken(
        @Field("code") authCode: String,
        @Field("grant_type") grantType: String,
        @Field("client_id") clientID: String,
        @Field("redirect_uri") redirectURI: String,
        @Field("code_verifier") codeVerifier: String
    ): Call<AccessTokenResponse>

    @FormUrlEncoded
    @POST("accounts/token/")
    fun refreshAccessToken(
        @Field("refresh_token") refreshToken: String?,
        @Field("grant_type") grantType: String,
        @Field("client_id") clientID: String
    ): Call<AccessTokenResponse>

    @GET("dining/venues")
    fun venues(): Observable<List<Venue>>

    @GET("dining/menus/{day}")
    fun getMenus(@Path("day") day: String): Observable<List<DiningHall.Menu>>

    @GET("dining/weekly_menu/{id}")
    fun dailyMenu(@Path("id") id: Int): Observable<DiningHall>

    @GET("dining/preferences")
    fun getDiningPreferences(@Header("Authorization") bearerToken: String): Observable<DiningPreferences>

    @GET("laundry/halls/ids")
    fun laundryRooms(): Observable<List<LaundryRoomSimple>>

    @GET("laundry/hall/{id}")
    fun room(@Path("id") id: Int): Observable<LaundryRoom>

    @GET("gsr/locations")
    fun location(): Observable<List<GSRLocation>>

    @GET("gsr/availability/{id}/{gid}")
    fun gsrRoom(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: String,
        @Path("gid") gid: Int,
        @Query("start") date: String
    ): Observable<GSR>

    @GET("gsr/wharton")
    fun isWharton(@Header("Authorization") bearerToken: String): Observable<WhartonStatus>

    @FormUrlEncoded
    @POST("gsr/book/")
    fun bookGSR(
        @Header("Authorization") bearerToken: String,
        @Field("start_time") start: String?,
        @Field("end_time") end: String?,
        @Field("gid") gid: Int,
        @Field("id") id: Int,
        @Field("room_name") roomName: String
    ): Call<GSRBookingResult>

    @GET("laundry/usage/{id}")
    fun usage(@Path("id") id: Int): Observable<LaundryUsage>

    @GET("events/fling")
    fun getFlingEvents(): Observable<List<FlingEvent>>

    @GET("penndata/news")
    fun getNews(): Observable<Article>

    @GET("penndata/calendar")
    fun getCalendar(): Observable<List<CalendarEvent>>

    @GET("gsr/reservations")
    fun getGsrReservations(@Header("Authorization") bearerToken: String): Observable<List<GSRReservation>>

    @FormUrlEncoded
    @POST("gsr/cancel/")
    fun cancelReservation(
        @Header("Authorization") bearerToken: String,
        @Header("X-Device-ID") deviceID: String?,
        @Field("booking_id") bookingID: String?,
        @Field("sessionid") sessionID: String?
    ): Call<ResponseBody>

    @POST("users/{pennkey}/activate/")
    fun saveAccount(
        @Header("Authorization") bearerToken: String,
        @Path("pennkey") pennkey: String,
        @Body account: Account
    ): Call<SaveAccountResponse>

    @GET("laundry/preferences")
    fun getLaundryPref(@Header("Authorization") bearerToken: String): Observable<LaundryPreferences>

    @POST("laundry/preferences/")
    fun sendLaundryPref(
        @Header("Authorization") bearerToken: String,
        @Body rooms: LaundryRequest
    ): Call<ResponseBody>

    @POST("dining/preferences/")
    fun sendDiningPref(
        @Header("Authorization") bearerToken: String,
        @Body body: DiningRequest
    ): Call<ResponseBody>

    @GET("portal/posts/browse/")
    fun validPostsList(@Header("Authorization") bearerToken: String): Observable<List<Post>>

    @FormUrlEncoded
    @POST("portal/polls/browse/")
    fun browsePolls(
        @Header("Authorization") bearerToken: String,
        @Field("id_hash") idHash: String
    ): Observable<List<Poll>>

    @FormUrlEncoded
    @POST("portal/votes/")
    fun createPollVote(
        @Header("Authorization") bearerToken: String,
        @Field("id_hash") idHash: String,
        @Field("poll_options") pollOptions: ArrayList<Int>
    ): Call<ResponseBody>

    @GET("penndata/fitness/rooms/")
    fun getFitnessRooms(): Observable<List<FitnessRoom>>

    @GET("penndata/fitness/usage/{id}")
    fun getFitnessRoomUsage(
        @Path("id") id: Int,
        @Query("num_samples") samples: Int,
        @Query("group_by") groupBy: String
    ): Observable<FitnessRoomUsage>

    @GET("penndata/fitness/preferences")
    fun getFitnessPreferences(@Header("Authorization") bearerToken: String): Observable<List<Int>>

    @POST("penndata/fitness/preferences/")
    fun sendFitnessPref(
        @Header("Authorization") bearerToken: String,
        @Body rooms: FitnessRequest
    ): Call<ResponseBody>
}