package com.example.iptvplayer.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface PlaylistService {
    @GET("animation.m3u")
    fun fetchPlaylist(): Call<String>
}
