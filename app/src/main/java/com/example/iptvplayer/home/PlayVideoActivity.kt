package com.example.iptvplayer.home

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Rect
import android.media.AudioManager
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iptvplayer.R
import com.example.iptvplayer.databinding.ActivityPlayVideoBinding
import com.example.iptvplayer.home.adapter.ItemChannelAdapter
import com.example.iptvplayer.home.common.CheckVideoUrl
import com.example.iptvplayer.home.common.DataHolder
import com.example.iptvplayer.home.common.ResizeVideo
import com.example.iptvplayer.home.common.ToggleAnimationHelper
import com.example.iptvplayer.model.ItemChannel
import kotlin.math.abs


class PlayVideoActivity : AppCompatActivity(), GestureDetector.OnGestureListener,
    AudioManager.OnAudioFocusChangeListener {

    private lateinit var binding: ActivityPlayVideoBinding
    private lateinit var player: ExoPlayer
    private lateinit var resizeVideo: ResizeVideo
    private lateinit var toggleAnimationHelper: ToggleAnimationHelper
    private lateinit var listChannel: ArrayList<ItemChannel?>
    private lateinit var adapterItemChannel: ItemChannelAdapter
    private var itemChannel: ItemChannel? = null
    private var idCurrent: Int = 0
    private lateinit var gestureDetectorCompat: GestureDetectorCompat
    private var brightness: Int = 0
    private val modes = listOf("4:3", "16:9", "Fit Screen", "Original", "Streak")
    private var currentModeIndex = 0
    private var minSwipeY: Float = 0f
    private var volume: Int = 0
    private var audioManager: AudioManager? = null


    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toggleAnimationHelper = ToggleAnimationHelper(this@PlayVideoActivity)
        listChannel = ArrayList()
        resizeVideo = ResizeVideo(this@PlayVideoActivity)
        gestureDetectorCompat = GestureDetectorCompat(this, this)
        player = ExoPlayer.Builder(this@PlayVideoActivity).build()

        binding.playerView.player = player

        setUpView()
        onClick()
        giveVideoPlay()


        binding.playerView.setOnTouchListener { _, motionEvent ->
//            binding.playerView.isDoubleTapEnabled = false
//            if (!isLocked) {
//                binding.playerView.isDoubleTapEnabled = true
            gestureDetectorCompat.onTouchEvent(motionEvent)
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                binding.brightnessIcon.visibility = View.GONE
                binding.volumeIcon.visibility = View.GONE
                //for immersive mode
                WindowCompat.setDecorFitsSystemWindows(window, false)
                WindowInsetsControllerCompat(window, binding.root).let { controller ->
                    controller.hide(WindowInsetsCompat.Type.systemBars())
                    controller.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
//            }
            return@setOnTouchListener false
        }

    }

    private fun setUpView() {
        adapterItemChannel =
            ItemChannelAdapter(listChannel, this@PlayVideoActivity) { item, index ->
                switchVideoPlay(item)
                idCurrent = item.id
                updateSelected(index)
                index?.let { adapterItemChannel.itemSelected(item.id) }
            }

        binding.rcvItemChannel.layoutManager = LinearLayoutManager(this@PlayVideoActivity)
        binding.rcvItemChannel.adapter = adapterItemChannel
    }

    private fun updateSelected(index: Int) {
        val nextBtn = binding.playerView.findViewById<ImageButton>(R.id.nextBtn)
        val prevBtn = binding.playerView.findViewById<ImageButton>(R.id.prevBtn)
        nextBtn.isEnabled = index < listChannel.size - 1
        nextBtn.alpha = if (nextBtn.isEnabled) 1f else 0.5f
        prevBtn.isEnabled = index > 0
        prevBtn.alpha = if (prevBtn.isEnabled) 1f else 0.5f
    }

    private fun switchVideoPlay(it: ItemChannel) {
        binding.layoutAllChannels.visibility = View.GONE
        binding.tvName.text = it.channelName
        val videoUrl = it.streamUrl
        player.run {
            stop() // Dừng video đang phát hiện tại
            clearMediaItems() // Xóa tất cả các Media Items hiện tại
            playWhenReady = false // Đảm bảo rằng nó sẽ không tự động phát khi chưa sẵn sàng
        }
        // Confirm the new video URL before playing
        CheckVideoUrl.checkURL(videoUrl) { isValid ->
            runOnUiThread {
                if (isValid) {
                    // Chuẩn bị video mới
                    val newMediaItem = MediaItem.fromUri(videoUrl)
                    player.setMediaItem(newMediaItem)
                    player.prepare()
                    player.playWhenReady = true // Bắt đầu phát nếu bạn muốn tự động phát
                } else {
                    Toast.makeText(
                        this,
                        "Không thể phát video: URL không hợp lệ hoặc không có quyền truy cập",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun giveVideoPlay() {

        itemChannel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("object", ItemChannel::class.java)
        } else {
            intent.getParcelableExtra<ItemChannel>("object")
        }

        DataHolder.itemList?.let {
            listChannel.addAll(it)
            itemChannel?.let {
                idCurrent = itemChannel!!.id
                adapterItemChannel.itemSelected(itemChannel!!.id)
                adapterItemChannel.notifyDataSetChanged()

            }

        }

        itemChannel?.let {
            if (DataHolder.itemList!!.isNotEmpty())
                checkEnableButton()
            binding.tvName.text = itemChannel!!.channelName
            val videoUrl = itemChannel!!.streamUrl
            Log.d("5452352352", videoUrl.toString())
            // confirm url video
            CheckVideoUrl.checkURL(videoUrl) { isValid ->
                runOnUiThread {
                    if (isValid) {
                        val mediaItem = MediaItem.fromUri(videoUrl)
                        player.setMediaItem(mediaItem)
                        player.prepare()
                        player.playWhenReady = true
                    } else {
                        Toast.makeText(
                            this,
                            "Không thể phát video: URL không hợp lệ hoặc không có quyền truy cập",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

    }

    private fun checkEnableButton() {
        DataHolder.itemList.let {
            if (DataHolder.itemList!!.isNotEmpty()) {
                val prevBtn = binding.playerView.findViewById<ImageButton>(R.id.prevBtn)
                val nextBtn = binding.playerView.findViewById<ImageButton>(R.id.nextBtn)
                val isFirst = DataHolder.itemList?.first()?.id == itemChannel?.id
                prevBtn.isEnabled = !isFirst
                prevBtn.alpha = if (isFirst) 0.5f else 1f
                val isLast = DataHolder.itemList?.last()?.id == itemChannel?.id
                nextBtn.isEnabled = !isLast
                nextBtn.alpha = if (isLast) 0.5f else 1f
            }
        }
    }


    private fun onClick() {
        binding.apply {
            icBack.setOnClickListener {
                player.stop()
                player.clearMediaItems()
                onBackPressed()
                finish()
            }

            binding.playerView.findViewById<ImageButton>(R.id.btn_orientation).setOnClickListener {
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                else requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            }

            binding.playerView.findViewById<ImageButton>(R.id.pauseBtn).setOnClickListener {
                if (player.isPlaying) player.pause() else player.play()
            }
            binding.playerView.findViewById<ImageButton>(R.id.btn_ratio).setOnClickListener {
                changePlayerViewMode()
            }
            icListPlay.setOnClickListener {
                if (binding.layoutAllChannels.visibility == View.VISIBLE)
                    toggleAnimationHelper.closeView(binding.layoutAllChannels)
                else
                    toggleAnimationHelper.openView(binding.layoutAllChannels)
            }

            binding.playerView.findViewById<ImageButton>(R.id.nextBtn).apply {
                setOnClickListener {
                    val indexCurrent = listChannel.indexOfFirst { it?.id == idCurrent }

                    if (indexCurrent != -1 && indexCurrent < listChannel.size - 1) {
                        val indexNew = indexCurrent + 1
                        // Kiểm tra nếu indexNew là chỉ mục cuối cùng
                        if (indexNew == listChannel.size - 1) {
                            isEnabled = false
                            binding.playerView.findViewById<ImageButton>(R.id.nextBtn).alpha = 0.5f
                        } else {
                            isEnabled = true
                            binding.playerView.findViewById<ImageButton>(R.id.nextBtn).alpha = 1f
                        }
                        binding.playerView.findViewById<ImageButton>(R.id.prevBtn).alpha = 1f
                        binding.playerView.findViewById<ImageButton>(R.id.prevBtn).isEnabled = true


                        // Kiểm tra chỉ mục hợp lệ và phần tử không null
                        if (indexNew in listChannel.indices) {
                            listChannel[indexNew]?.let { it1 ->
                                switchVideoPlay(it1)
                                idCurrent = it1.id
                                Log.d("ioioifioasdf", idCurrent.toString())
                                adapterItemChannel.itemSelected(it1.id)
                                adapterItemChannel.notifyDataSetChanged()
                            }
                        }
                        if (indexNew == listChannel.size - 1) {
                            binding.playerView.findViewById<ImageButton>(R.id.nextBtn).alpha = 0.5f
                            binding.playerView.findViewById<ImageButton>(R.id.prevBtn).alpha = 1f
                            binding.playerView.findViewById<ImageButton>(R.id.prevBtn).isEnabled =
                                true
                        }
                    } else {
                        // end list
                        isEnabled = false
                        binding.playerView.findViewById<ImageButton>(R.id.nextBtn).alpha = 0.5f
                        binding.playerView.findViewById<ImageButton>(R.id.prevBtn).alpha = 1f
                        binding.playerView.findViewById<ImageButton>(R.id.prevBtn).isEnabled = true

                    }
                }
            }


            binding.playerView.findViewById<ImageButton>(R.id.prevBtn).apply {
                setOnClickListener {
                    val indexCurrent = listChannel.indexOfFirst { it?.id == idCurrent }
                    // Kiểm tra xem chỉ mục hiện tại có hợp lệ hay không
                    if (indexCurrent > 0) {
                        isEnabled = true
                        binding.playerView.findViewById<ImageButton>(R.id.prevBtn).alpha = 1f

                        binding.playerView.findViewById<ImageButton>(R.id.nextBtn).alpha = 1f
                        binding.playerView.findViewById<ImageButton>(R.id.nextBtn).isEnabled = true

                        val indexNew = indexCurrent - 1

                        // Kiểm tra chỉ mục hợp lệ và phần tử không null
                        if (indexNew in listChannel.indices) {
                            listChannel[indexNew]?.let { previousChannel ->
                                switchVideoPlay(previousChannel)
                                idCurrent = previousChannel.id
                                Log.d("PreviousChannel", idCurrent.toString())
                                adapterItemChannel.itemSelected(previousChannel.id)
                                adapterItemChannel.notifyDataSetChanged()
                            }
                        }
                        if (indexNew == 0) {
                            isEnabled = false
                            binding.playerView.findViewById<ImageButton>(R.id.prevBtn).alpha = 0.5f
                            binding.playerView.findViewById<ImageButton>(R.id.nextBtn).alpha = 1f
                            binding.playerView.findViewById<ImageButton>(R.id.nextBtn).isEnabled =
                                true
                        }

                    } else {
                        // first list
                        isEnabled = false
                        binding.playerView.findViewById<ImageButton>(R.id.prevBtn).alpha = 0.5f
                        binding.playerView.findViewById<ImageButton>(R.id.nextBtn).alpha = 1f
                        binding.playerView.findViewById<ImageButton>(R.id.nextBtn).isEnabled = true
                    }
                }
            }


        }
    }

    @OptIn(UnstableApi::class)
    private fun changePlayerViewMode() {
        val mode = modes[currentModeIndex]
        binding.playerView.resizeMode = resizeVideo.getResizeModeForMode(mode)
        currentModeIndex = (currentModeIndex + 1) % modes.size
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            if (ev.action == MotionEvent.ACTION_DOWN) {
                if (binding.layoutAllChannels != null && binding.layoutAllChannels.isShown) {
                    val outRect = Rect()
                    val outRect2 = Rect()
                    binding.layoutAllChannels.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt()) && !outRect2.contains(
                            ev.rawX.toInt(),
                            ev.rawY.toInt()
                        )
                    ) {
                        toggleAnimationHelper.closeView(binding.layoutAllChannels)
                    }
                }
            }

        }
        return super.dispatchTouchEvent(ev)
    }


    override fun onBackPressed() {
        player.stop()
        player.clearMediaItems()
        super.onBackPressed()

    }

    override fun onDown(p0: MotionEvent): Boolean = false

    override fun onShowPress(p0: MotionEvent) = Unit

    override fun onSingleTapUp(p0: MotionEvent): Boolean = false
    override fun onLongPress(p0: MotionEvent) {}

    override fun onFling(p0: MotionEvent?, p1: MotionEvent, p2: Float, p3: Float): Boolean = false

    override fun onScroll(
        e1: MotionEvent?,
        event: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        minSwipeY += distanceY

        val sWidth = Resources.getSystem().displayMetrics.widthPixels
        val border = 100 * Resources.getSystem().displayMetrics.density.toInt()
        if (event.x < border || event.y < border || event.x > sWidth - border) return false

        // Tính toán hệ số thay đổi dựa trên tốc độ vuốt
        val swipeSensitivity = if (abs(distanceY) > 100) 5 else 1 // Thay đổi giá trị này để điều chỉnh độ nhạy

        if (abs(distanceX) < abs(distanceY) && abs(minSwipeY) > 20) {
            if (event.x < sWidth / 2) {
                // Điều chỉnh độ sáng
                binding.brightnessIcon.visibility = View.VISIBLE
                binding.volumeIcon.visibility = View.GONE
                val increase = distanceY > 0
                val newValue = if (increase) brightness + swipeSensitivity else brightness - swipeSensitivity
                if (newValue in 0..30) brightness = newValue
                binding.brightnessIcon.text = brightness.toString()
                setScreenBrightness(brightness)
            } else {
                // Điều chỉnh âm lượng
                binding.brightnessIcon.visibility = View.GONE
                binding.volumeIcon.visibility = View.VISIBLE
                val maxVolume = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val increase = distanceY > 0
                val newValue = if (increase) volume + swipeSensitivity else volume - swipeSensitivity
                if (newValue in 0..maxVolume) volume = newValue
                binding.seekbarVolume.max = maxVolume
                binding.seekbarVolume.progress = volume.toInt()
                audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
            }
            minSwipeY = 0f
        }
        return true
    }


    private fun setScreenBrightness(value: Int) {
        val d = 1.0f / 30
        val lp = this.window.attributes
        lp.screenBrightness = d * value
        this.window.attributes = lp
    }

    override fun onResume() {
        super.onResume()
        if (audioManager == null) audioManager =
            getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager!!.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        if (brightness != 0) setScreenBrightness(brightness)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.pause()
        audioManager?.abandonAudioFocus(this)
    }

    override fun onAudioFocusChange(focusChange: Int) {
//        if (focusChange <= 0) pauseVideo()
    }


}



