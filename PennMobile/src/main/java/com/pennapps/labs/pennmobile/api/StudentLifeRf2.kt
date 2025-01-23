import com.pennapps.labs.pennmobile.api.classes.AccessTokenResponse
import com.pennapps.labs.pennmobile.fitness.classes.FitnessPreferences
import com.pennapps.labs.pennmobile.fitness.classes.FitnessRequest
import com.pennapps.labs.pennmobile.fitness.classes.FitnessRoom
import com.pennapps.labs.pennmobile.fitness.classes.FitnessRoomUsage
import com.pennapps.labs.pennmobile.laundry.classes.LaundryPreferences
import com.pennapps.labs.pennmobile.laundry.classes.LaundryRequest
import com.pennapps.labs.pennmobile.laundry.classes.LaundryRoom
import com.pennapps.labs.pennmobile.laundry.classes.LaundryRoomSimple
import com.pennapps.labs.pennmobile.laundry.classes.LaundryUsage
import okhttp3.ResponseBody
import retrofit.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

interface StudentLifeRf2 {
    @FormUrlEncoded
    @POST("accounts/token/")
    suspend fun getAccessToken(
        @Field("code") authCode: String,
        @Field("grant_type") grantType: String,
        @Field("client_id") clientID: String,
        @Field("redirect_uri") redirectURI: String,
        @Field("code_verifier") codeVerifier: String,
    ): Response<AccessTokenResponse>

    @FormUrlEncoded
    @POST("accounts/token/")
    suspend fun refreshAccessToken(
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String,
        @Field("client_id") clientID: String,
    ): Response<AccessTokenResponse>

    @GET("laundry/halls/ids")
    suspend fun laundryRooms(): Response<List<LaundryRoomSimple>>

    @GET("laundry/hall/{id}")
    suspend fun room(
        @Path("id") id: Int,
    ): Response<LaundryRoom>

    @GET("laundry/usage/{id}")
    suspend fun usage(
        @Path("id") id: Int,
    ): Response<LaundryUsage>

    @GET("laundry/preferences")
    suspend fun getLaundryPref(
        @Header("Authorization") bearerToken: String,
    ): Response<LaundryPreferences>

    @POST("laundry/preferences/")
    suspend fun sendLaundryPref(
        @Header("Authorization") bearerToken: String,
        @Body rooms: LaundryRequest,
    ): Response<ResponseBody>

    @GET("penndata/fitness/rooms/")
    fun getFitnessRooms(): Observable<List<FitnessRoom?>?>?

    @GET("penndata/fitness/usage/{id}")
    fun getFitnessRoomUsage(
        @Path("id") id: Int,
        @Query("num_samples") samples: Int,
        @Query("group_by") groupBy: String?
    ): Observable<FitnessRoomUsage?>?

    @GET("penndata/fitness/preferences")
    fun getFitnessPreferences(
        @Header("Authorization") bearerToken: String?
    ): Observable<FitnessPreferences?>?

    @POST("penndata/fitness/preferences/")
    suspend fun sendFitnessPref(
        @Header("Authorization") bearerToken: String,
        @Body rooms: FitnessRequest
    ) : Response<ResponseBody>
}

