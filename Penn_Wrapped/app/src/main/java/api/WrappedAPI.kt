//package api
//
//import com.example.penn_wrapped.WrappedResponse
//import retrofit2.http.GET
//import retrofit2.http.Path
//
//interface WrappedApiService {
//
//    @GET("api/wrapped/semester/{semesterId}/")
//    suspend fun getWrapped(
//        @Path("semesterId") semesterId: String
//    ): WrappedResponse
//
//}