package com.example.miskiparqueo.feature.auth.signup.data.datasource
import com.example.miskiparqueo.feature.auth.data.dto.UserFirebaseDto
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Email
import com.example.miskiparqueo.feature.auth.signup.domain.model.vo.Username
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume

class SignUpFirebaseDataSource {
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    suspend fun signUp(
        firstName: String,
        lastName: String,
        username: Username,
        email: Email,
        password: String
    ): Result<SignUpResponse> = suspendCancellableCoroutine { continuation ->

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Verificar si username existe
                val usernameExists = snapshot.children.any {
                    it.child("username").getValue(String::class.java)?.lowercase() ==
                            username.value.lowercase()
                }

                if (usernameExists) {
                    continuation.resume(
                        Result.failure(Exception("El username ya está en uso"))
                    )
                    return
                }

                // Verificar si email existe
                val emailExists = snapshot.children.any {
                    it.child("email").getValue(String::class.java)?.lowercase() ==
                            email.value.lowercase()
                }

                if (emailExists) {
                    continuation.resume(
                        Result.failure(Exception("El email ya está registrado"))
                    )
                    return
                }

                // Si no existe, crear nuevo usuario
                val userId = UUID.randomUUID().toString()
                val userDto = UserFirebaseDto(
                    userId = userId,
                    firstName = firstName,
                    lastName = lastName,
                    username = username.value,
                    email = email.value,
                    password = password
                )

                usersRef.child(userId).setValue(userDto)
                    .addOnSuccessListener {
                        continuation.resume(
                            Result.success(
                                SignUpResponse(
                                    userId = userId,
                                    firstName = firstName,
                                    lastName = lastName,
                                    username = username.value,
                                    email = email.value
                                )
                            )
                        )
                    }
                    .addOnFailureListener { error ->
                        continuation.resume(
                            Result.failure(
                                Exception("Error al registrar usuario: ${error.message}")
                            )
                        )
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resume(
                    Result.failure(
                        Exception("Error de conexión: ${error.message}")
                    )
                )
            }
        })
    }

    data class SignUpResponse(
        val userId: String,
        val firstName: String,
        val lastName: String,
        val username: String,
        val email: String
    )
}