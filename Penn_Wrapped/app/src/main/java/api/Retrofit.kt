//package api
//
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//object RetrofitClient {
//
//    // Use 10.0.2.2 to reach host machine's localhost from Android emulator.
//    // Change this to your actual server URL when deploying.
//    private const val BASE_URL = "http://10.0.2.2:8000/"
//
//    private val retrofit: Retrofit by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    val wrappedApi: WrappedApiService by lazy {
//        retrofit.create(WrappedApiService::class.java)
//    }
//}