package com.example.miskiparqueo.feature.auth.login.data.datasource

import android.util.Log // <-- AÑADE ESTE IMPORT
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class LoginFirebaseDataSource {
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    suspend fun login(credential: String, password: String): Result<LoginResponse> =
        suspendCancellableCoroutine { continuation ->

            val normalizedCredential = credential.trim().lowercase()

            Log.d("LoginDebug", "Iniciando login para: '$normalizedCredential' con contraseña de '${password.length}' caracteres.")

            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var userFound: LoginResponse? = null

                    Log.d("LoginDebug", "Datos recibidos de Firebase. Se encontraron ${snapshot.childrenCount} usuarios.")

                    // Buscar usuario por email o username
                    for (userSnapshot in snapshot.children) {
                        val userId = userSnapshot.child("userId").getValue(String::class.java)
                        val userEmail = userSnapshot.child("email").getValue(String::class.java)
                        val userUsername = userSnapshot.child("username").getValue(String::class.java)
                        val userPassword = userSnapshot.child("password").getValue(String::class.java)

                        Log.d("LoginDebug", "--- Chequeando Usuario: $userId ---")
                        Log.d("LoginDebug", "Email en DB: '$userEmail' | Username en DB: '$userUsername'")
                        Log.d("LoginDebug", "Contraseña en DB: '$userPassword' (longitud: ${userPassword?.length})")

                        val credentialMatches = userEmail?.lowercase() == normalizedCredential || userUsername?.lowercase() == normalizedCredential
                        val passwordMatches = userPassword == password

                        Log.d("LoginDebug", "Coincide credencial? $credentialMatches | Coincide contraseña? $passwordMatches")


                        if (credentialMatches && passwordMatches) {
                            Log.d("LoginDebug", "¡USUARIO Y CONTRASEÑA ENCONTRADOS! ID: $userId")
                            userFound = userSnapshot.getValue(LoginResponse::class.java)
                            break
                        }
                    }

                    if (userFound != null) {
                        Log.d("LoginDebug", "Login exitoso. Reanudando corrutina con éxito.")
                        continuation.resume(Result.success(userFound!!))
                    } else {
                        Log.d("LoginDebug", "Usuario no encontrado o contraseña incorrecta. Reanudando corrutina con fallo.")
                        val userExists = snapshot.children.any { userSnapshot ->
                            val userEmail = userSnapshot.child("email").getValue(String::class.java)
                            val userUsername = userSnapshot.child("username").getValue(String::class.java)
                            userEmail?.lowercase() == normalizedCredential || userUsername?.lowercase() == normalizedCredential
                        }

                        val errorMessage = if (userExists) "Contraseña incorrecta." else "Usuario no encontrado."
                        Log.e("LoginDebug", "Error de login: $errorMessage")
                        continuation.resume(Result.failure(Exception(errorMessage)))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("LoginDebug", "Firebase canceló la operación: ${error.message}")
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