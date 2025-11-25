package com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo

@JvmInline
value class Email private constructor(val value: String) {
    init {
        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        require(regex.matches(value)) {
            "El formato del email no es válido"
        }
    }

    companion object {
        fun create(raw: String): Email {
            return Email(raw.trim().lowercase())
        }
    }
}