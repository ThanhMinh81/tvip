package com.example.iptvplayer.home

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration

import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.iptvplayer.R
import com.example.iptvplayer.databinding.ActivityPlayVideoBinding
import com.example.iptvplayer.home.adapter.ItemChannelAdapter
import com.example.iptvplayer.home.common.CheckVideoUrl
import com.example.iptvplayer.home.common.ToggleAnimationHelper
import com.example.iptvplayer.model.ItemChannel

class PlayerControls(
    private val player: ExoPlayer, // Thay đổi kiểu theo lớp player của bạn
    private val listChannel: ArrayList<ItemChannel?>, // Thay đổi kiểu theo lớp Channel của bạn
    private var idCurrent: Int,
    private val adapterItemChannel: ItemChannelAdapter, // Thay đổi kiểu theo adapter của bạn
    private val toggleAnimationHelper: ToggleAnimationHelper, // Thay đổi kiểu theo helper của bạn
    private val binding: ActivityPlayVideoBinding,
    private val runOnUiThread: (Runnable) -> Unit
) {
    init {
        setupListeners()
    }

    private fun setupListeners() {
        binding.playerView.findViewById<ImageButton>(R.id.pauseBtn).setOnClickListener {
            togglePlayPause()
        }

        binding.playerView.findViewById<ImageButton>(R.id.btn_ratio).setOnClickListener {
            changePlayerViewMode()
        }

        binding.icListPlay.setOnClickListener {
            toggleChannelList()
        }

        binding.playerView.findViewById<ImageButton>(R.id.nextBtn).apply {
            setOnClickListener {
                onNextButtonClicked()
            }
        }
    }

    private fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    private fun changePlayerViewMode() {
        // Thêm logic thay đổi chế độ hiển thị ở đây
    }

    private fun toggleChannelList() {
        if (binding.layoutAllChannels.visibility == View.VISIBLE) {
            toggleAnimationHelper.closeView(binding.layoutAllChannels)
        } else {
            toggleAnimationHelper.openView(binding.layoutAllChannels)
        }
    }

    private fun onNextButtonClicked() {
        val indexCurrent = listChannel.indexOfFirst { it?.id == idCurrent }

        if (indexCurrent != -1 && indexCurrent < listChannel.size - 1) {
            val indexNew = indexCurrent + 1
            updateNextButtonState(indexNew)
            playNextChannel(indexNew)
        } else {
            disableNextButton()
        }
    }

    private fun updateNextButtonState(indexNew: Int) {
        val nextBtn = binding.playerView.findViewById<ImageButton>(R.id.nextBtn)

        if (indexNew == listChannel.size - 1) {
            disableNextButton()
        } else {
            nextBtn.isEnabled = true
            nextBtn.alpha = 1f
        }
    }

    private fun playNextChannel(indexNew: Int) {
        if (indexNew in listChannel.indices) {
            listChannel[indexNew]?.let { nextChannel ->
                switchVideoPlay(nextChannel)
                idCurrent = nextChannel.id
                adapterItemChannel.itemSelected(nextChannel.id)
                adapterItemChannel.notifyDataSetChanged()
            }
        }
    }

    private fun disableNextButton() {
        val nextBtn = binding.playerView.findViewById<ImageButton>(R.id.nextBtn)
        nextBtn.isEnabled = false
        nextBtn.alpha = 0.5f
    }

    private fun switchVideoPlay(it: ItemChannel) {
        binding.layoutAllChannels.visibility = View.GONE
        binding.tvName.text = it.channelName
        val videoUrl = it.streamUrl
        player.run {
            stop()
            clearMediaItems()
            playWhenReady = false
        }

        CheckVideoUrl.checkURL(videoUrl) { isValid ->
            runOnUiThread {
                if (isValid) {
                    val newMediaItem = MediaItem.fromUri(videoUrl)
                    player.setMediaItem(newMediaItem)
                    player.prepare()
                    player.playWhenReady = true
                } else {
                    Toast.makeText(
                        binding.root.context, // Hoặc context của Activity
                        "Không thể phát video: URL không hợp lệ hoặc không có quyền truy cập",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

}
