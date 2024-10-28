package com.example.iptvplayer.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    fun getInstance(): Retrofit {


        val gson = GsonBuilder()
            .setLenient()
            .create()

        return   Retrofit.Builder()
            .baseUrl("https://example.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    }
}