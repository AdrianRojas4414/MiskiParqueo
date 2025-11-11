package com.example.miskiparqueo.feature.profile.data.datasource

import com.example.miskiparqueo.feature.auth.domain.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ProfileFirebaseDataSource {
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    suspend fun updateUser(user: UserModel): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            val userUpdates = mapOf(
                "firstName" to user.firstName.value,
                "lastName" to user.lastName.value,
                "username" to user.username.value,
                "email" to user.email.value
            )

            usersRef.child(user.userId).updateChildren(userUpdates)
                .addOnSuccessListener {
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception))
                }
        }

    suspend fun changePassword(userId: String, currentPassword: String, newPassword: String): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val storedPassword = snapshot.child("password").getValue(String::class.java)

                    if (storedPassword == null) {
                        continuation.resume(Result.failure(Exception("No se pudo encontrar al usuario.")))
                        return
                    }

                    if (storedPassword == currentPassword) {
                        // La contraseña actual es correcta, proceder a actualizar
                        snapshot.ref.child("password").setValue(newPassword)
                            .addOnSuccessListener {
                                continuation.resume(Result.success(Unit))
                            }
                            .addOnFailureListener { exception ->
                                continuation.resume(Result.failure(exception))
                            }
                    } else {
                        // La contraseña actual es incorrecta
                        continuation.resume(Result.failure(Exception("La contraseña actual es incorrecta.")))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(Result.failure(error.toException()))
                }
            })
        }
}