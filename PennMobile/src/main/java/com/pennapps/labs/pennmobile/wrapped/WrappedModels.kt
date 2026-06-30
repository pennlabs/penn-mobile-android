package com.pennapps.labs.pennmobile.wrapped

import com.google.gson.annotations.SerializedName

data class WrappedResponse(
    val semester: String,
    val pages: List<WrappedUnit> = emptyList()
)

data class WrappedUnit(
    val id: Int,
    @SerializedName("template_path")
    val lottieUrl: String,
    @SerializedName("duration")
    val durationString: String? = null,
    @SerializedName("combined_stats")
    val values: Map<String, String> = emptyMap()
) {
    val durationMillis: Int
        get() {
            if (durationString == null) return 5000
            val parts = durationString.split(":")
            if (parts.size != 3) return 5000
            val hours = parts[0].toIntOrNull() ?: 0
            val minutes = parts[1].toIntOrNull() ?: 0
            val seconds = parts[2].toIntOrNull() ?: 0
            return ((hours * 3600) + (minutes * 60) + seconds) * 1000
        }
}

object DummyData {
    val mockPages = listOf(
        WrappedUnit(
            id = 1,
            lottieUrl = "https://lottie.host/18f0d65f-8309-4a9b-9911-97f796f4dc06/EGkmEdSpOJ.json",
            durationString = "00:00:05",
            values = emptyMap()
        ),
        WrappedUnit(
            id = 2,
            lottieUrl = "https://lottie.host/09847f06-632a-45ec-b3b4-bb989e103723/taVQz65bKK.json",
            durationString = "00:00:05",
            values = emptyMap()
        ),
        WrappedUnit(
            id = 3,
            lottieUrl = "https://lottie.host/b0a2eab3-8da5-4667-8e50-899f0a9875dc/T5bz3WPKxW.json",
            durationString = "00:00:08",
            values = mapOf("total_hours" to "198,913", "total_reservations" to "151,101")
        ),
        WrappedUnit(
            id = 4,
            lottieUrl = "https://lottie.host/0b6cf2f1-56a4-47db-9974-08c50e29fe9e/fhZdynYmDu.json",
            durationString = "00:00:08",
            values = mapOf(
                "location1" to "Huntsman Hall", "hours1" to "100,863",
                "location2" to "Weigle", "hours2" to "23,635",
                "location3" to "Biotech Commons", "hours3" to "23,152"
            )
        ),
        WrappedUnit(
            id = 5,
            lottieUrl = "https://lottie.host/14a22c51-e630-42dd-991d-98fc9f75dd4d/wt9Ndka4jy.json",
            durationString = "00:00:08",
            values = mapOf("total_questions" to "31,043")
        ),
        WrappedUnit(
            id = 6,
            lottieUrl = "https://lottie.host/d89e9f85-9f11-48f2-b93a-5a0144298d83/5o9co8Lhtw.json",
            durationString = "00:00:12",
            values = mapOf(
                "questions1" to "7,321", "class1" to "Mathematical Foundations of Computer Science", "classcode1" to "CIS 1600",
                "questions2" to "5,260", "class2" to "Data Structures and Algorithms", "classcode2" to "CIS 1210",
                "questions3" to "3,877", "class3" to "Programming Languages and Techniques", "classcode3" to "CIS 1200"
            )
        ),
        WrappedUnit(
            id = 7,
            lottieUrl = "https://lottie.host/b0a3046c-17e0-40a8-833a-8a90893a47d9/Ojq23GNJSV.json",
            durationString = "00:00:10",
            values = mapOf(
                "classcode1" to "CBE 4590", "classname1" to "Product and Process Design", "difficulty1" to "4.0",
                "classcode2" to "BE 3090", "classname2" to "Bioengineering Lab I", "difficulty2" to "3.9",
                "classcode3" to "LSMP 4210B", "classname3" to "LSM Capstone", "difficulty3" to "3.9"
            )
        ),
        WrappedUnit(
            id = 8,
            lottieUrl = "https://lottie.host/2fdf2bb9-7217-423f-ba28-e263ef8f6866/TKrfu3li3e.json",
            durationString = "00:00:08",
            values = mapOf("total_schedules" to "32,455")
        )
    )
}
