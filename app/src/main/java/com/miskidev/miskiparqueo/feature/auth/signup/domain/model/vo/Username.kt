package com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo

@JvmInline
value class Username private constructor(val value: String) {
    init {
        require(value.length in 3..36) {
            "El username debe tener entre 3 y 36 caracteres"
        }
        val regex = Regex("^[a-zA-Z0-9_]+$")
        require(regex.matches(value)) {
            "El username solo puede contener letras, números y guiones bajos"
        }
    }

    companion object {
        fun create(raw: String): Username {
            return Username(raw.trim())
        }
    }
}