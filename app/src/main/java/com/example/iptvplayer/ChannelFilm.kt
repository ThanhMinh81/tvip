package com.example.iptvplayer

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.iptvplayer.databinding.ActivityChannelFilmBinding
import com.example.iptvplayer.home.common.ApiHolder
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class ChannelFilm : AppCompatActivity() {

    private lateinit var binding: ActivityChannelFilmBinding

    private lateinit var exoPlayer: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChannelFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

//     val url  =  "http://vp-txpro.com/player_api.php?username=ECR4k54a6wdd&password=UHPdmwE6TELm&action=get_vod_streams"
//        val url2 = "http://vp-txpro.com/live/ECR4k54a6wdd/UHPdmwE6TELm/63020.ts"
        val url3 = "http://vp-txpro.com/player_api.php?username=ECR4k54a6wdd&password=UHPdmwE6TELm&action=get_vod_info&vod_id=63020"



        // Khởi tạo ExoPlayer
//        exoPlayer = ExoPlayer.Builder(this).build()
//        binding.playerView.player = exoPlayer
//
//        // Thêm URL stream
//        val mediaItem = MediaItem.fromUri("http://vp-txpro.com/live/ECR4k54a6wdd/UHPdmwE6TELm/63020.ts")
//        exoPlayer.setMediaItem(mediaItem)
//
//        // Chuẩn bị và play
//        exoPlayer.prepare()
//        exoPlayer.playWhenReady = true


        val client = ApiHolder.HttpClient.instance
        val request = Request.Builder()
            .url(url3)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.use { responseBody -> // Sử dụng use để tự động đóng responseBody
                    val bodyString = responseBody.string() // Đọc body string một lần
                     Log.d("53455253",bodyString.toString())

//                    63020

                }
            }
        })


//        loginXtream("ECR4k54a6wdd", "UHPdmwE6TELm", "http://vp-txpro.com")

    }

    // Hàm mã hóa thông tin đăng nhập
    fun encodeCredentials(username: String, password: String): String {
        val credentials = "$username:$password"
        return android.util.Base64.encodeToString(credentials.toByteArray(), android.util.Base64.NO_WRAP)
    }

    fun loginXtream(username: String, password: String, host: String) {
        // Tạo HttpClient
        val client = OkHttpClient()

        // Tạo URL
        val url = "$host" // Thay đổi theo endpoint thực tế

        // Tạo request body
        val requestBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        // Tạo request
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        // Thực hiện yêu cầu
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Xử lý lỗi kết nối
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        val responseString = responseBody.string()
                        // Xử lý phản hồi JSON
                        println(responseString)
                        // Bạn có thể chuyển đổi JSON thành đối tượng Kotlin nếu cần
                    }
                } else {
                    // Xử lý lỗi
                    println("Error: ${response.code}")
                }
            }
        })
    }


}

//https://github.com/AndreyPavlenko/Fermata/discussions/434