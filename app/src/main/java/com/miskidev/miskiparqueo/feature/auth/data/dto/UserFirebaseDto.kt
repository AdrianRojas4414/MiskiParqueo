package com.miskidev.miskiparqueo.feature.auth.data.dto

data class UserFirebaseDto(
    var userId: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var username: String = "",
    var email: String = "",
    var password: String = ""
) {
    constructor() : this("", "", "", "", "", "")
}