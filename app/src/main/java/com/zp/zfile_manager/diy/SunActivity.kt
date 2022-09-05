package com.zp.zfile_manager.diy

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zp.z_file.ui.ZFileVideoPlayer
import com.zp.zfile_manager.databinding.ActivitySunBinding

class SunActivity : AppCompatActivity() {

    private lateinit var vb: ActivitySunBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivitySunBinding.inflate(layoutInflater)
        setContentView(vb.root)
        vb.sunBack.setOnClickListener {
            onBackPressed()
        }
        vb.sunVideoPlayer.assetsVideoName = "sun.mp4"
        vb.sunPhoneBg1.background.alpha = 100
        vb.sunLoginBtn2.background.alpha = 100
        vb.sunVideoPlayer.sizeType = ZFileVideoPlayer.CENTER_CROP_MODE
        vb.sunVideoPlayer.videoPlayError = {
            Toast.makeText(this@SunActivity.applicationContext, "播放失败", Toast.LENGTH_SHORT).show()
        }
        vb.sunVideoPlayer.play()

    }

    override fun onResume() {
        super.onResume()
        if (vb.sunVideoPlayer.isPause()) vb.sunVideoPlayer.play()
    }

    override fun onPause() {
        super.onPause()
        if (vb.sunVideoPlayer.isPlaying()) vb.sunVideoPlayer.pause()
    }

}