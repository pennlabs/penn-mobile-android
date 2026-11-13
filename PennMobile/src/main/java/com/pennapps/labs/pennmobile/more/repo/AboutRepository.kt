package com.pennapps.labs.pennmobile.more.repo

import com.pennapps.labs.pennmobile.more.classes.TeamMember

interface AboutRepository {
    fun getMembers(): List<TeamMember>

    fun getAlumni(): List<TeamMember>
}
