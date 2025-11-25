package com.miskidev.miskiparqueo.feature.auth.login.data.datasource

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlinx.coroutines.tasks.await

class LoginFirebaseDataSource {
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    suspend fun login(credential: String, password: String): Result<LoginResponse> =
        suspendCancellableCoroutine { continuation ->

            val normalizedCredential = credential.trim().lowercase()

            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var userFound: LoginResponse? = null

                    // Buscar usuario por email o username
                    for (userSnapshot in snapshot.children) {
                        val userEmail = userSnapshot.child("email").getValue(String::class.java)
                        val userUsername = userSnapshot.child("username").getValue(String::class.java)
                        val userPassword = userSnapshot.child("password").getValue(String::class.java)

                        if ((userEmail?.lowercase() == normalizedCredential ||
                                    userUsername?.lowercase() == normalizedCredential) &&
                            userPassword == password) {

                            userFound = LoginResponse(
                                userId = userSnapshot.child("userId").getValue(String::class.java) ?: "",
                                firstName = userSnapshot.child("firstName").getValue(String::class.java) ?: "",
                                lastName = userSnapshot.child("lastName").getValue(String::class.java) ?: "",
                                username = userSnapshot.child("username").getValue(String::class.java) ?: "",
                                email = userSnapshot.child("email").getValue(String::class.java) ?: ""
                            )
                            break
                        }
                    }

                    if (userFound != null) {
                        continuation.resume(Result.success(userFound))
                    } else {
                        // Verificar si el usuario existe pero la contraseña es incorrecta
                        val userExists = snapshot.children.any { userSnapshot ->
                            val userEmail = userSnapshot.child("email").getValue(String::class.java)
                            val userUsername = userSnapshot.child("username").getValue(String::class.java)
                            userEmail?.lowercase() == normalizedCredential ||
                                    userUsername?.lowercase() == normalizedCredential
                        }

                        val errorMessage = if (userExists) {
                            "Contraseña incorrecta."
                        } else {
                            "Usuario no encontrado."
                        }

                        continuation.resume(Result.failure(Exception(errorMessage)))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(
                        Result.failure(Exception("Error de conexión. Verifica tu internet."))
                    )
                }
            })
        }

    suspend fun getUserById(userId: String): Result<LoginResponse> {
        return try {
            val snapshot = usersRef.child(userId).get().await()
            val user = snapshot.getValue(LoginResponse::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Usuario no encontrado."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    data class LoginResponse(
        var userId: String = "",
        var firstName: String = "",
        var lastName: String = "",
        var username: String = "",
        var email: String = ""
    )
}