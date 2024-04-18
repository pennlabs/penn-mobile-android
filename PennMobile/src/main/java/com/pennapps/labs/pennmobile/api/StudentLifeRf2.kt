import com.pennapps.labs.pennmobile.classes.LaundryPreferences
import com.pennapps.labs.pennmobile.classes.LaundryRequest
import com.pennapps.labs.pennmobile.classes.LaundryRoom
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple
import com.pennapps.labs.pennmobile.classes.LaundryUsage
import okhttp3.ResponseBody
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface StudentLifeRf2 {
    @GET("laundry/halls/ids")
    suspend fun laundryRooms(): Response<List<LaundryRoomSimple>>

    @GET("laundry/hall/{id}")
    fun room(@Path("id") id: Int): Observable<LaundryRoom>

    @GET("laundry/hall/{id}")
    suspend fun room2(@Path("id") id: Int): Response<LaundryRoom>

    @GET("laundry/usage/{id}")
    suspend fun usage(@Path("id") id: Int): Response<LaundryUsage>
    @GET("laundry/preferences")
    fun getLaundryPref(@Header("Authorization") bearerToken: String): Observable<LaundryPreferences>

    @GET("laundry/preferences")
    suspend fun getLaundryPref2(@Header("Authorization") bearerToken: String): Response<LaundryPreferences>

    @POST("laundry/preferences/")
    suspend fun sendLaundryPref(
        @Header("Authorization") bearerToken: String,
        @Body rooms: LaundryRequest
    ): Response<ResponseBody>
}