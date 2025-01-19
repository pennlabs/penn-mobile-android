package com.pennapps.labs.pennmobile.Subletting

class SublettingModel {

    var listingImage: Int?
    var listingTitle: String?
    var listingPrice: Int?
    var isNegotiable: Boolean?
    var numberBeds: Int?
    var numberBath: Int?
    var startDate: Int?
    var endDate: Int?

    constructor(listingImage: Int, listingTitle: String, listingPrice: Int, isNegotiable: Boolean,
                numberBeds: Int, numberBath: Int,  startDate: Int, endDate: Int) {
        this.listingImage = listingImage
        this.listingTitle = listingTitle
        this.listingPrice = listingPrice
        this.isNegotiable = isNegotiable
        this.numberBeds = numberBeds
        this.numberBath = numberBath
        this.startDate = startDate
        this.endDate = endDate
    }
}