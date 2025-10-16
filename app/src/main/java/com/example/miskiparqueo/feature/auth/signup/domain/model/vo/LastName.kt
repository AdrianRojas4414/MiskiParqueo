package com.example.miskiparqueo.feature.auth.signup.domain.model.vo

@JvmInline
value class LastName private constructor(val value: String) {
    init {
        require(value.isNotBlank()) {
            "El apellido no puede estar vacío"
        }
        require(value.length <= 50) {
            "El apellido no puede exceder 50 caracteres"
        }
    }

    companion object {
        fun create(raw: String): LastName {
            return LastName(raw.trim())
        }
    }
}