package com.pennapps.labs.pennmobile.api.classes

class Account(
    firstName: String?,
    lastName: String?,
    pennkey: String?,
    pennid: Int?,
    email: String?,
    affiliation: Array<String>?,
) {
    val first = firstName
    val last = lastName
    val pennkey = pennkey
    val pennid = pennid
    val email = email
    val affiliation = affiliation
}
