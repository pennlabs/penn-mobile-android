package com.pennapps.labs.pennmobile.classes

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.pennapps.labs.pennmobile.api.Serializer
import org.joda.time.Interval
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Unit tests
class HomePageSerializerTest {
    /*
    var serializer = Serializer.HomePageSerializer()
    var jsonStr = """{"cells":[{"info":{"image_url":"https://s3.amazonaws.com/penn.mobile.portal/images/Penn%20Labs/1619465670.144596-cropped.png","post_id":53,"post_url":"https://docs.google.com/forms/u/1/d/12TQpqnattdWnL4tpczR6HPGu8a6-FuujU5dWS7k2k8I/edit","source":"Penn Labs","subtitle":"Having issues with Penn Mobile? Or maybe an idea for a new feature? Chat with us to share your thoughts and help improve the Penn Mobile experience!","test":false,"time_label":"Tap the Image!","title":"Penn Mobile User Feedback"},"type":"post"},{"info":{"venues":[593,636]},"type":"dining"},{"info":{"article_url":"https://www.thedp.com/article/2021/04/penn-museum-move-protest-philadelphia","image_url":"https://s3.amazonaws.com/snwceomedia/dpn/b1fb9439-7490-4f86-980a-55152825dc69.sized-1000x1000.gif?w=1000","source":"The Daily Pennsylvanian","subtitle":"The protest was organized by Black liberation advocacy group MOVE, in collaboration with Black Lives Matter Philadelphia, following the discovery that the Penn Museum stored the remains of at least one child killed in the bombing.","timestamp":"13 hours ago\n","title":"West Philadelphians protest on campus for Penn to return MOVE victim remains to family"},"type":"news"},{"info":[{"end":"2021-05-04","name":"Reading Days","start":"2021-04-30"},{"end":"2021-05-12","name":"Final Examinations","start":"2021-05-04"},{"end":"2021-05-11","name":"Spring Term ends","start":"2021-05-11"}],"type":"calendar"},{"info":[1086,2587],"type":"gsr-locations"},{"info":{"room_id":29},"type":"laundry"}]}"""
    var gson = Gson()
    var jsonObj = gson.fromJson(jsonStr, JsonObject::class.java)
    lateinit var homeData: List<HomeCell>

    @Before
    fun parse() {
        this.homeData = this.serializer.deserialize(this.jsonObj, null, null)
    }

    @Test
    fun testCellCount() {
        assertEquals(6, this.homeData.size)
    }

    @Test
    fun testCellContentZero() {
        var imgUrl = "https://s3.amazonaws.com/penn.mobile.portal/images/Penn%20Labs/1619465670.144596-cropped.png"
        var postId = 53
        var postUrl = "https://docs.google.com/forms/u/1/d/12TQpqnattdWnL4tpczR6HPGu8a6-FuujU5dWS7k2k8I/edit"
        var source = "Penn Labs"
        var subtitle = "Having issues with Penn Mobile? Or maybe an idea for a new feature? Chat with us to share your thoughts and help improve the Penn Mobile experience!"
        var test = false
        var timeLabel = "Tap the Image!"
        var title = "Penn Mobile User Feedback"

        var type = "post"

        var cell = homeData[0]
        var info = cell.info!!
        assertEquals(imgUrl, info.imageUrl)
        assertEquals(postId, info.postId)
        assertEquals(postUrl, info.postUrl)
        assertEquals(source, info.source)
        assertEquals(subtitle, info.subtitle)
        assertEquals(test, info.isTest)
        assertEquals(timeLabel, info.timeLabel)
        assertEquals(title, info.title)

        assertEquals(type, cell.type)
    }

    @Test
    fun testCellContentOne() {

        var type = "dining"

        var cell = homeData[1]
        var info = cell.info!!
        assertEquals(2, info.venues!!.size)
        assertEquals(593, info.venues!![0])
        assertEquals(636, info.venues!![1])

        assertEquals(type, cell.type)
    }

    @Test
    fun testCellContentTwo() {
        var articleUrl = "https://www.thedp.com/article/2021/04/penn-museum-move-protest-philadelphia"
        var imageUrl = "https://s3.amazonaws.com/snwceomedia/dpn/b1fb9439-7490-4f86-980a-55152825dc69.sized-1000x1000.gif?w=1000"
        var source = "The Daily Pennsylvanian"
        var subtitle = "The protest was organized by Black liberation advocacy group MOVE, in collaboration with Black Lives Matter Philadelphia, following the discovery that the Penn Museum stored the remains of at least one child killed in the bombing."
        var timestamp = "13 hours ago\n"
        var title = "West Philadelphians protest on campus for Penn to return MOVE victim remains to family"

        var type = "news"

        var cell = homeData[2]
        var info = cell.info!!
        assertEquals(articleUrl, info.articleUrl)
        assertEquals(imageUrl, info.imageUrl)
        assertEquals(source, info.source)
        assertEquals(subtitle, info.subtitle)
        assertEquals(timestamp, info.timestamp)
        assertEquals(title, info.title)

        assertEquals(type, cell.type)
    }

    @Test
    fun testCellContentThree() {
        var end0 = "2021-05-04"
        var name0 = "Reading Days"
        var start0 = "2021-04-30"

        var end1 = "2021-05-12"
        var name1 = "Final Examinations"
        var start1 = "2021-05-04"

        var end2 = "2021-05-11"
        var name2 = "Spring Term ends"
        var start2 = "2021-05-11"

        var type = "calendar"

        assertEquals(3, homeData[3].events!!.size)
        assertEquals(name0, homeData[3].events!![0].name)
        assertEquals(name1, homeData[3].events!![1].name)
        assertEquals(name2, homeData[3].events!![2].name)
        assertEquals(end0, homeData[3].events!![0].end)
        assertEquals(end1, homeData[3].events!![1].end)
        assertEquals(end2, homeData[3].events!![2].end)
        assertEquals(start0, homeData[3].events!![0].start)
        assertEquals(start1, homeData[3].events!![1].start)
        assertEquals(start2, homeData[3].events!![2].start)
        assertEquals(type, homeData[3].type)
    }

    @Test
    fun testCellContentFour() {

        var type = "gsr-locations"

        assertEquals(type, homeData[4].type)
    }

    @Test
    fun testCellContentFive() {
        var roomId = 29
        var type = "laundry"

        assertEquals(roomId, homeData[5].info!!.roomId)
        assertEquals(type, homeData[5].type)
    } */
}