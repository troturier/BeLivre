package com.openclassrooms.belivre.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.openclassrooms.belivre.chat.FirestoreUtil

/**
 * Custom FirebaseMessagingService
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(s: String?) {
        if (s == null) throw NullPointerException("FCM token is null.")
        //addTokenToFirestore(s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            //TODO: Show notification if we're not online
            Log.d("FCM", remoteMessage.data.toString())
        }
    }

    companion object{
        fun addTokenToFirestore(newRegistrationToken: String){
            if (FirebaseAuth.getInstance().currentUser != null) {
                FirestoreUtil.getFCMRegistrationTokens { tokens ->
                    if (tokens.contains(newRegistrationToken))
                        return@getFCMRegistrationTokens

                    tokens.add(newRegistrationToken)
                    FirestoreUtil.setFCMRegistrationTokens(tokens)
                }
            }
        }
    }
}