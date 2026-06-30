//package api
//
//import com.google.gson.annotations.SerializedName
//
//data class WrappedResponse(
//    val semester: String,
//    val pages: List<WrappedUnit> = emptyList()
//)
//
//data class WrappedUnit(
//    val id: Int,
//    val name: String = "",
//    @SerializedName("template_path")
//    val lottieUrl: String,
//    @SerializedName("duration")
//    val durationString: String? = null,
//    @SerializedName("combined_stats")
//    val values: Map<String, String> = emptyMap()
//) {
//    val durationMillis: Int
//        get() {
//            if (durationString == null) return 5000
//            val parts = durationString.split(":")
//            if (parts.size != 3) return 5000
//            val hours = parts[0].toIntOrNull() ?: 0
//            val minutes = parts[1].toIntOrNull() ?: 0
//            val seconds = parts[2].toIntOrNull() ?: 0
//            return ((hours * 3600) + (minutes * 60) + seconds) * 1000
//        }
//}