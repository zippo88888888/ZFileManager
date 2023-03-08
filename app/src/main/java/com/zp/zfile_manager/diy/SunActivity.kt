package com.zp.zfile_manager.diy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zp.z_file.ui.ZFileVideoPlayer
import com.zp.zfile_manager.R
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

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.zfile_out_bottom)
    }

    companion object {

        fun jump(activity: Activity) {
            activity.startActivity(Intent(activity, SunActivity::class.java))
            activity.overridePendingTransition(R.anim.zfile_in_bottom, 0)
        }

    }

}