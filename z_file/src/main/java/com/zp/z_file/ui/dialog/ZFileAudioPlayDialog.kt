package com.zp.z_file.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.view.Gravity
import android.view.View
import android.widget.SeekBar
import com.zp.z_file.R
import com.zp.z_file.common.ZFileManageDialog
import com.zp.z_file.content.setNeedWH
import com.zp.z_file.util.ZFileOtherUtil
import kotlinx.android.synthetic.main.dialog_zfile_audio_play.*
import java.lang.ref.WeakReference

internal class ZFileAudioPlayDialog : ZFileManageDialog(), SeekBar.OnSeekBarChangeListener, Runnable {

    companion object {
        fun getInstance(filePath: String) = ZFileAudioPlayDialog().apply {
            arguments = Bundle().apply { putString("filePath", filePath) }
        }
    }

    private val UNIT = -1
    private val PLAY = 0
    private val PAUSE = 1

    private var playerState = UNIT

    private var audioHandler: AudioHandler? = null
    private var mediaPlayer: MediaPlayer? = null

    private var beginTime: Long = 0
    private var falgTime: Long = 0
    private var pauseTime: Long = 0

    override fun getContentView() = R.layout.dialog_zfile_audio_play

    override fun createDialog(savedInstanceState: Bundle?) = Dialog(context!!, R.style.ZFile_Common_Dialog).apply {
        window?.setGravity(Gravity.CENTER)
    }

    override fun init(savedInstanceState: Bundle?) {
        audioHandler = AudioHandler(this)
        initPlayer()
        dialog_zfile_audio_play.setOnClickListener {
            when (playerState) {
                PAUSE -> {
                    startPlay()
                    falgTime = SystemClock.elapsedRealtime()
                    beginTime = falgTime - dialog_zfile_audio_bar.progress
                    dialog_zfile_audio_nowTime.base = beginTime
                    dialog_zfile_audio_nowTime.start()
                }
                PLAY -> {
                    if (mediaPlayer?.isPlaying == true) {
                        mediaPlayer?.pause()
                        playerState = PAUSE
                        dialog_zfile_audio_nowTime.stop()
                        pauseTime = SystemClock.elapsedRealtime()
                        dialog_zfile_audio_play.setImageResource(R.drawable.zfile_play)
                    }
                }
                else -> {
                    initPlayer()
                }
            }
        }
        dialog_zfile_audio_bar.setOnSeekBarChangeListener(this)
        dialog_zfile_audio_name.text = arguments?.getString("filePath")?.let {
            it.substring(it.lastIndexOf("/") + 1, it.length)
        }
    }

    override fun onStart() {
        super.onStart()
        setNeedWH()
    }

    private fun initPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(arguments?.getString("filePath"))
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener { play ->
            dialog_zfile_audio_bar.max = play.duration
            audioHandler?.post(this)
            dialog_zfile_audio_countTime.text = ZFileOtherUtil.secToTime(play.duration / 1000)

            // 设置运动时间
            falgTime = SystemClock.elapsedRealtime()
            pauseTime = 0
            dialog_zfile_audio_nowTime.base = falgTime
            dialog_zfile_audio_nowTime.start()

            startPlay()
        }
        mediaPlayer?.setOnCompletionListener {
            stopPlay()
            dialog_zfile_audio_bar.isEnabled = false
            dialog_zfile_audio_bar.progress = 0
            dialog_zfile_audio_nowTime.base = SystemClock.elapsedRealtime()
            dialog_zfile_audio_nowTime.start()
            dialog_zfile_audio_nowTime.stop()
        }
    }

    // 开始播放
    private fun startPlay() {
        mediaPlayer?.start()
        playerState = PLAY
        dialog_zfile_audio_play.setImageResource(R.drawable.zfile_pause)
        dialog_zfile_audio_bar.isEnabled = true
    }

    // 停止播放
    private fun stopPlay() {
        dialog_zfile_audio_play.setImageResource(R.drawable.zfile_play)
        mediaPlayer?.release()
        mediaPlayer = null
        playerState = UNIT
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser && mediaPlayer != null) {
            mediaPlayer?.seekTo(progress)
            falgTime = SystemClock.elapsedRealtime()
            beginTime = falgTime - seekBar.progress
            dialog_zfile_audio_nowTime.base = beginTime
            dialog_zfile_audio_nowTime.start()
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
    override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (mediaPlayer?.isPlaying == true) mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        playerState = UNIT
        audioHandler?.removeMessages(0)
        audioHandler?.removeCallbacks(this)
        audioHandler?.removeCallbacksAndMessages(null)
        audioHandler = null
    }

    override fun run() {
        // 获得歌曲现在播放位置并设置成播放进度条的值
        if (mediaPlayer != null) {
            audioHandler?.sendEmptyMessage(0)
            audioHandler?.postDelayed(this, 100)
        }
    }

    class AudioHandler(dialog: ZFileAudioPlayDialog) : Handler() {
        private val week: WeakReference<ZFileAudioPlayDialog> by lazy {
            WeakReference<ZFileAudioPlayDialog>(dialog)
        }

        override fun handleMessage(msg: Message) {
            week.get()?.dialog_zfile_audio_bar?.progress = week.get()?.mediaPlayer?.currentPosition ?: 0
        }
    }

}