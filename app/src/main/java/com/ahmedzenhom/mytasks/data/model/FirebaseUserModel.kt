package com.ahmedzenhom.mytasks.data.model

import com.google.firebase.auth.FirebaseUser
import java.io.Serializable

data class FirebaseUserModel(
    val uid: String? = null,
    val phone: String? = null,
) : Serializable {
    companion object {
        fun fromFirebaseUser(user: FirebaseUser): FirebaseUserModel {
            return FirebaseUserModel(user.uid, user.phoneNumber)
        }
    }
}
