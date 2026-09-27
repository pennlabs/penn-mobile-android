package com.pennapps.labs.pennmobile.more

import com.pennapps.labs.pennmobile.more.classes.TeamMember
import com.pennapps.labs.pennmobile.more.repo.AboutRepository
import com.pennapps.labs.pennmobile.more.viewmodels.AboutViewModel
import com.pennapps.labs.pennmobile.more.viewmodels.PENN_LABS_URL
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AboutViewModelTest {
    private class FakeAboutRepository(
        private val members: List<TeamMember>,
        private val alumni: List<TeamMember>,
    ) : AboutRepository {
        override fun getMembers() = members

        override fun getAlumni() = alumni
    }

    @Test
    fun `ui state mirrors the repository`() {
        val members = listOf(TeamMember("A", "https://example.com/a.jpg"), TeamMember("B", null))
        val alumni = listOf(TeamMember("C", "https://example.com/c.jpg"))

        val state = AboutViewModel(FakeAboutRepository(members, alumni)).uiState.value

        assertEquals(members, state.members)
        assertEquals(alumni, state.alumni)
        assertEquals(PENN_LABS_URL, state.pennLabsUrl)
    }

    @Test
    fun `empty repository yields empty lists`() {
        val state = AboutViewModel(FakeAboutRepository(emptyList(), emptyList())).uiState.value

        assertTrue(state.members.isEmpty())
        assertTrue(state.alumni.isEmpty())
    }
}
