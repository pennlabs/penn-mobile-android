package com.pennapps.labs.pennmobile.more

import com.pennapps.labs.pennmobile.more.repo.AboutRepositoryImpl
import com.pennapps.labs.pennmobile.more.repo.imgurMediumThumbnail
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AboutRepositoryImplTest {
    private val repository = AboutRepositoryImpl()

    @Test
    fun `members and alumni keep their display order`() {
        val members = repository.getMembers().map { it.name }
        val alumni = repository.getAlumni().map { it.name }

        assertEquals(9, members.size)
        assertEquals("Trini Feng", members.first())
        assertEquals("Yuetong Zheng", members.last())
        assertEquals(15, alumni.size)
        assertEquals("Kaushik Akula", alumni.first())
        assertEquals("Ali Krema", alumni.last())
    }

    @Test
    fun `photos are imgur medium thumbnails over https`() {
        val photos = (repository.getMembers() + repository.getAlumni()).mapNotNull { it.photoUrl }

        assertTrue(photos.isNotEmpty())
        assertTrue(photos.all { Regex("""^https://i\.imgur\.com/[A-Za-z0-9]+m\.[a-z]+$""").matches(it) })
    }

    @Test
    fun `people without a website photo have a null url`() {
        val withoutPhoto = (repository.getMembers() + repository.getAlumni()).filter { it.photoUrl == null }.map { it.name }

        assertEquals(listOf("Veer Kakar", "Yuetong Zheng", "Awad Irfan", "Liz Powell", "Davies Lumumba"), withoutPhoto)
    }

    @Test
    fun `imgur thumbnail inserts m before the extension`() {
        assertEquals("https://i.imgur.com/3jpnYWam.jpeg", imgurMediumThumbnail("https://i.imgur.com/3jpnYWa.jpeg"))
        assertEquals("https://i.imgur.com/1za1mAGm.png", imgurMediumThumbnail("https://i.imgur.com/1za1mAG.png"))
    }

    @Test
    fun `non imgur urls are left unchanged`() {
        val url = "https://example.com/photo.jpg"

        assertEquals(url, imgurMediumThumbnail(url))
        assertEquals("https://imgur.com/a/album", imgurMediumThumbnail("https://imgur.com/a/album"))
    }

    @Test
    fun `nobody is listed as both member and alumnus`() {
        val memberNames = repository.getMembers().map { it.name }.toSet()
        val alumniNames = repository.getAlumni().map { it.name }.toSet()

        assertTrue(memberNames.intersect(alumniNames).isEmpty())
    }
}
