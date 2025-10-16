package com.example.miskiparqueo.feature.auth.signup.domain.model.vo

@JvmInline
value class Password private constructor(val value: String) {
    init {
        require(value.length >= 8) {
            "La contraseña debe tener al menos 8 caracteres"
        }
        require(value.any { it.isDigit() } && value.any { it.isLetter() }) {
            "La contraseña debe contener letras y números"
        }
    }

    companion object {
        fun create(raw: String): Password {
            return Password(raw)
        }
    }
}