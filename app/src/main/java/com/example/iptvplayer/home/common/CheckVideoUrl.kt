package com.example.iptvplayer.home.common

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class CheckVideoUrl {

    companion object {
        fun checkURL(videoUrl: String, onResult: (Boolean) -> Unit) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(videoUrl)
                .head() // Chỉ cần kiểm tra header, không cần tải nội dung
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onResult(false) // Thất bại khi gửi yêu cầu
                }

                override fun onResponse(call: Call, response: Response) {
                    onResult(response.isSuccessful) // Trả về true nếu mã phản hồi từ 200 đến 299
                }
            })
        }
    }


}