package com.zp.zfile_manager.diy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zp.z_file.ui.ZFileVideoPlayer
import com.zp.zfile_manager.R
import kotlinx.android.synthetic.main.activity_sun.*

class SunActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sun)
        sun_back.setOnClickListener {
            onBackPressed()
        }
        sun_phoneBg1.background.alpha = 100
        sun_loginBtn2.background.alpha = 100
        sun_videoPlayer.videoName = "sun.mp4"
        sun_videoPlayer.sizeType = ZFileVideoPlayer.CENTER_CROP_MODE
        sun_videoPlayer.post {
            sun_videoPlayer.play()
        }
    }

    override fun onResume() {
        super.onResume()
        sun_videoPlayer.play()
    }

    override fun onPause() {
        super.onPause()
        sun_videoPlayer.pause()
    }

}