package com.example.iptvplayer.home.common

import okhttp3.OkHttpClient

class ApiHolder {

    object HttpClient {
        val instance: OkHttpClient by lazy {
            OkHttpClient.Builder()

                .build()
        }
    }

}