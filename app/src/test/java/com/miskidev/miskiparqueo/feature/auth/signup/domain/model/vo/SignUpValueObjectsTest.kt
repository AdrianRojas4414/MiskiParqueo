package com.miskidev.miskiparqueo.feature.auth.signup.domain.model.vo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SignUpValueObjectsTest {

    @Test
    fun `email se normaliza`() {
        val email = Email.create("  Example@DOMAIN.Com ")

        assertEquals("example@domain.com", email.value)
    }

    @Test
    fun `email formato invalido lanza error`() {
        assertThrows(IllegalArgumentException::class.java) {
            Email.create("not-an-email")
        }
    }

    @Test
    fun `password reglas minimas`() {
        assertThrows(IllegalArgumentException::class.java) {
            Password.create("short1")
        }

        assertThrows(IllegalArgumentException::class.java) {
            Password.create("onlyletters")
        }
    }

    @Test
    fun `username valido`() {
        val username = Username.create("valid_user123")

        assertEquals("valid_user123", username.value)
    }

    @Test
    fun `username rechaza caracteres invalidos`() {
        assertThrows(IllegalArgumentException::class.java) {
            Username.create("invalid-user")
        }
    }

    @Test
    fun `nombre apellido longitud maxima`() {
        val longName = "a".repeat(51)
        assertThrows(IllegalArgumentException::class.java) {
            FirstName.create(longName)
        }
        assertThrows(IllegalArgumentException::class.java) {
            LastName.create(longName)
        }
    }

    @Test
    fun `nombre apellido no vacios`() {
        assertThrows(IllegalArgumentException::class.java) {
            FirstName.create("   ")
        }

        assertThrows(IllegalArgumentException::class.java) {
            LastName.create("")
        }
    }
}
