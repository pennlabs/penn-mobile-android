package com.pennapps.labs.pennmobile.more.classes

/**
 * Created by Adel on 12/16/14.
 * Class for a Person from Directory
 */
class Contact {
    var name: String
    var phone: String
    var phoneWords: String

    constructor(name: String, phone: String) {
        this.name = name
        this.phone = phone
        this.phoneWords = ""
    }

    constructor(name: String, phone: String, phone_words: String) {
        this.name = name
        this.phone = phone
        this.phoneWords = phone_words
    }

    val isURL: Boolean
        get() = phone.startsWith("http")
}
