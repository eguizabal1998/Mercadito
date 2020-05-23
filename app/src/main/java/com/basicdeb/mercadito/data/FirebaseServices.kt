package com.basicdeb.mercadito.data

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService


class FirebaseServices: FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.i("token","nuevo token"+p0)
    }
}