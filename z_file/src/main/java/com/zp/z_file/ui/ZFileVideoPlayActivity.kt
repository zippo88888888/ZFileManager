package com.zp.z_file.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.zp.z_file.R
import com.zp.z_file.common.ZFileActivity
import com.zp.z_file.content.getZFileHelp
import com.zp.z_file.content.inflate
import com.zp.z_file.content.setStatusBarTransparent
import com.zp.z_file.content.toFile
import com.zp.z_file.databinding.ActivityZfileVideoPlayBinding

internal class ZFileVideoPlayActivity : ZFileActivity() {

    private val vb by inflate<ActivityZfileVideoPlayBinding>()

    override fun getContentView() = R.layout.activity_zfile_video_play

    override fun create() = Unit

    override fun init(savedInstanceState: Bundle?) {
        setStatusBarTransparent()
        val videoPath = intent.getStringExtra("videoFilePath") ?: ""
        getZFileHelp().getImageLoadListener().loadImage(vb.videoImg, videoPath.toFile())
        vb.videoPlayerButton.setOnClickListener { v ->
            vb.videoPlayer.videoPath = videoPath
            vb.videoPlayer.play()
            v.visibility = View.GONE
            vb.videoImg.visibility = View.GONE
        }
        vb.videoPlayer.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onPause() {
        if (vb.videoPlayer.isPlaying()) {
            vb.videoPlayer.pause()
            vb.videoPlayerButton.visibility = View.VISIBLE
        }
        super.onPause()
    }

    override fun onBackPressed() {
        vb.videoImg.visibility = View.VISIBLE
        super.onBackPressed()
    }


    companion object {

        fun show(context: Context, videoFilePath: String) {
            context.startActivity(Intent(context, ZFileVideoPlayActivity::class.java).apply {
                putExtra("videoFilePath", videoFilePath)
            })
        }

    }
}
