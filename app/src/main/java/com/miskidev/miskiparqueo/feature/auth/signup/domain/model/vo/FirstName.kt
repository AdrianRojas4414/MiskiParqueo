package com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo

@JvmInline
value class FirstName private constructor(val value: String) {
    init {
        require(value.isNotBlank()) {
            "El nombre no puede estar vacío"
        }
        require(value.length <= 50) {
            "El nombre no puede exceder 50 caracteres"
        }
    }

    companion object {
        fun create(raw: String): FirstName {
            return FirstName(raw.trim())
        }
    }
}