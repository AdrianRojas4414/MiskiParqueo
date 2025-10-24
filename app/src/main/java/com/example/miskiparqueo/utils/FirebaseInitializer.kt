package com.example.miskiparqueo.utils

import com.example.miskiparqueo.feature.auth.data.dto.UserFirebaseDto
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

object FirebaseInitializer {
    fun initializeTestUsers() {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        val testUsers = listOf(
            UserFirebaseDto(
                userId = UUID.randomUUID().toString(),
                firstName = "Admin",
                lastName = "User",
                username = "admin",
                email = "admin@test.com",
                password = "password123"
            ),
            UserFirebaseDto(
                userId = UUID.randomUUID().toString(),
                firstName = "Juan",
                lastName = "Perez",
                username = "juan123",
                email = "juan@mail.com",
                password = "password123"
            ),
            UserFirebaseDto(
                userId = UUID.randomUUID().toString(),
                firstName = "Test",
                lastName = "Account",
                username = "test_user",
                email = "test@example.com",
                password = "securepass"
            )
        )

        testUsers.forEach { user ->
            usersRef.child(user.userId).setValue(user)
                .addOnSuccessListener {
                    println("✅ Usuario ${user.username} agregado correctamente")
                }
                .addOnFailureListener { error ->
                    println("❌ Error al agregar usuario ${user.username}: ${error.message}")
                }
        }
    }
}