package com.pennapps.labs.pennmobile.more.repo

import com.pennapps.labs.pennmobile.more.classes.TeamMember
import javax.inject.Inject

/**
 * Static roster of current Penn Labs members and alumni; update these lists when the team changes.
 *
 * Photo URLs are copied verbatim from the `photo` field of each person's JSON in
 * pennlabs/website (`src/json/members/` and `src/json/alumni/`), so they stay easy to diff
 * against the website. Null means the person has no JSON there.
 */
class AboutRepositoryImpl
    @Inject
    constructor() : AboutRepository {
        override fun getMembers(): List<TeamMember> = members

        override fun getAlumni(): List<TeamMember> = alumni

        private companion object {
            val members =
                listOf(
                    member("Trini Feng", "https://i.imgur.com/LyTB20B.jpg"),
                    member("Joe MacDougall", "https://i.imgur.com/OU2TGRa.jpg"),
                    member("Baron Ping-Yeh Hsieh", "https://i.imgur.com/wDs56DN.jpg"),
                    member("David Fu", "https://i.imgur.com/1za1mAG.png"),
                    member("Andrew Chelimo", "https://i.imgur.com/3jpnYWa.jpeg"),
                    member("Veer Kakar", null),
                    member("Cassie Mai", "https://i.imgur.com/EGnoVQu.jpeg"),
                    member("Ronnie Wang", "https://i.imgur.com/OpKBcn6.jpeg"),
                    member("Yuetong Zheng", null),
                )

            val alumni =
                listOf(
                    member("Kaushik Akula", "https://i.imgur.com/KNH9bsW.jpeg"),
                    member("Rohan Chhaya", "https://i.imgur.com/foDwf0O.jpeg"),
                    member("Julius Snipes", "https://i.imgur.com/63CaFxr.jpg"),
                    member("Aaron Mei", "https://i.imgur.com/9SWyLAQ.jpg"),
                    member("Vedha Avali", "https://i.imgur.com/bVCbRVf.jpg"),
                    member("Marta García Ferreiro", "https://i.imgur.com/HdLTlAU.jpg"),
                    member("Varun Ramakrishnan", "https://i.imgur.com/vhx3Rqa.jpg"),
                    member("Sahit Penmatcha", "https://i.imgur.com/SF8C4e8.jpg"),
                    member("Anna Wang", "https://i.imgur.com/QAXyuBW.jpg"),
                    member("Sophia Ye", "https://i.imgur.com/rIe9M74.jpg"),
                    member("Awad Irfan", null),
                    member("Liz Powell", null),
                    member("Davies Lumumba", null),
                    member("Anna Jiang", "https://i.imgur.com/acetToI.jpg"),
                    member("Ali Krema", "https://i.imgur.com/lT1Xivj.jpeg"),
                )

            fun member(
                name: String,
                websitePhotoUrl: String?,
            ) = TeamMember(name, websitePhotoUrl?.let(::imgurMediumThumbnail))
        }
    }

private val IMGUR_DIRECT_IMAGE = Regex("""^(https://i\.imgur\.com/[A-Za-z0-9]+)(\.[A-Za-z]+)$""")

/**
 * Rewrites a direct Imgur link to its 320px "medium" thumbnail (`abc.jpg` -> `abcm.jpg`).
 *
 * Website uploads are full-resolution (up to several MB); the About screen draws them at 80dp,
 * so the thumbnail cuts download size ~10x. Non-Imgur URLs are returned unchanged.
 */
internal fun imgurMediumThumbnail(url: String): String = IMGUR_DIRECT_IMAGE.replace(url, "$1m$2")
