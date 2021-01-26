package com.zp.z_file.ui

import android.content.Context
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import com.zp.z_file.util.ZFileLog

/**
 * 该处的实现请查看 JZVideoPlayer ，VideoView
 * 只是单纯的实现一个视频播放功能，如需手势功能，进度条拖拽可直接使用 JZVideoPlayer
 */
internal class ZFileVideoPlayer : TextureView, TextureView.SurfaceTextureListener {

    private var player: MediaPlayer? = null

    var videoPath = ""

    private var videoWidth = 0
    private var videoHeight = 0

    private var leftVolume = 1f
    private var rightVolume = 1f

    private val IS_INIT = 0
    private val IS_PLAYING = 1
    private val IS_PAUSE = 2

    private var playState = IS_INIT

    companion object {
        /** 中心裁剪模式 */
        const val CENTER_CROP_MODE = 0x10001
        /** 中心填充模式 */
        const val CENTER_MODE = 0x10002

        /** 父容器多大，视频多大，强制填充显示 */
        const val VIDEO_IMAGE_DISPLAY_TYPE_FILL_PARENT = 0x1001
        /** 原视频大小显示 */
        const val VIDEO_IMAGE_DISPLAY_TYPE_ORIGINAL = 0x1002
        /** 剪切部分视频大小显示 */
        const val VIDEO_IMAGE_DISPLAY_TYPE_FILL_SCROP = 0x1003
    }

    var size_type = CENTER_MODE

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0)
    constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int) : super(context, attributes, defStyleAttr) {
        surfaceTextureListener = this
        ZFileLog.i("初始化....")
    }

    private var videoPlayListener: ((MediaPlayer?) -> Unit)? = null
    fun setVideoPlayListener(videoPlayListener: ((MediaPlayer?) -> Unit)?) {
        this.videoPlayListener = videoPlayListener
    }

    /*override fun onMeasure(wMeasureSpec: Int, hMeasureSpec: Int) {
        var widthMeasureSpec = wMeasureSpec
        var heightMeasureSpec = hMeasureSpec
        val viewRotation = rotation.toInt()
        val videoWidth = videoWidth
        var videoHeight = videoHeight
        var parentHeight = (parent as View).measuredHeight
        var parentWidth = (parent as View).measuredWidth
        if (parentWidth != 0 && parentHeight != 0 && videoWidth != 0 && videoHeight != 0) {

            // 父容器多大，视频多大，强制填充
            if (size_type == VIDEO_IMAGE_DISPLAY_TYPE_FILL_PARENT) {
                if (viewRotation == 90 || viewRotation == 270) {
                    val tempSize = parentWidth
                    parentWidth = parentHeight
                    parentHeight = tempSize
                }
                // 强制充满
                videoHeight = videoWidth * parentHeight / parentWidth
            }
        }

        // 如果判断成立，则说明显示的TextureView和本身的位置是有90度的旋转的，所以需要交换宽高参数。
        if (viewRotation == 90 || viewRotation == 270) {
            widthMeasureSpec = heightMeasureSpec
            heightMeasureSpec = widthMeasureSpec
        }

        var width = View.getDefaultSize(videoWidth, widthMeasureSpec)
        var height = View.getDefaultSize(videoHeight, heightMeasureSpec)
        if (videoWidth > 0 && videoHeight > 0) {

            val widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec)
            val widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec)
            val heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec)
            val heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec)

            if (widthSpecMode == View.MeasureSpec.EXACTLY && heightSpecMode == View.MeasureSpec.EXACTLY) {
                // the size is fixed
                width = widthSpecSize
                height = heightSpecSize
                // for compatibility, we adjust size based on aspect ratio
                if (videoWidth * height < width * videoHeight) {
                    width = height * videoWidth / videoHeight
                } else if (videoWidth * height > width * videoHeight) {
                    height = width * videoHeight / videoWidth
                }
            } else if (widthSpecMode == View.MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize
                height = width * videoHeight / videoWidth
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize
                    width = height * videoWidth / videoHeight
                }
            } else if (heightSpecMode == View.MeasureSpec.EXACTLY) {
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize
                width = height * videoWidth / videoHeight
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize
                    height = width * videoHeight / videoWidth
                }
            } else {
                // neither the width nor the height are fixed, try to use actual video size
                width = videoWidth
                height = videoHeight
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize
                    width = height * videoWidth / videoHeight
                }
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize
                    height = width * videoHeight / videoWidth
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }
        if (parentWidth != 0 && parentHeight != 0 && videoWidth != 0 && videoHeight != 0) {
            if (size_type == VIDEO_IMAGE_DISPLAY_TYPE_ORIGINAL) {
                // 原图
                height = videoHeight
                width = videoWidth
            } else if (size_type == VIDEO_IMAGE_DISPLAY_TYPE_FILL_SCROP) {
                if (viewRotation == 90 || viewRotation == 270) {
                    val tempSize = parentWidth
                    parentWidth = parentHeight
                    parentHeight = tempSize
                }
                // 充满剪切
                if (videoHeight.toDouble() / videoWidth > parentHeight.toDouble() / parentWidth) {
                    height = (parentWidth.toDouble() / width.toDouble() * height.toDouble()).toInt()
                    width = parentWidth
                } else if (videoHeight.toDouble() / videoWidth < parentHeight.toDouble() / parentWidth) {
                    width = (parentHeight.toDouble() / height.toDouble() * width.toDouble()).toInt()
                    height = parentHeight
                }
            }
        }
        setMeasuredDimension(width, height)
    }*/

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        if (player == null) {
            player = MediaPlayer()

            /** 当装载流媒体完毕的时候回调 */
            player?.setOnPreparedListener {
                ZFileLog.i("媒体装载完成")
                setVolume()
                videoPlayListener?.invoke(player)
            }
            player?.setOnBufferingUpdateListener { _, percent ->
                ZFileLog.i("缓存中：$percent")
            }
            player?.setOnCompletionListener {
                ZFileLog.i("播放完成")
                play()
            }
            player?.setOnVideoSizeChangedListener { _, videoWidth, videoHeight ->
                this.videoWidth = videoWidth
                this.videoHeight = videoHeight
                updateTextureViewSize()
//                setVideoSize(videoWidth, videoHeight)
            }
            player?.setOnErrorListener { _, _, _ ->
                ZFileLog.e("播放失败")
                false
            }
        }
        val s = Surface(surface)
        // 将surface 与播放器进行绑定
        player?.setSurface(s)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
//        updateTextureViewSize()
//        setVideoSize(videoWidth, videoHeight)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        player?.pause()
        player?.stop()
        player?.release()
        ZFileLog.i("播放器被销毁...")
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) = Unit

    fun setVolume(leftVolume: Float = 1f, rightVolume: Float = 1f) {
        this.leftVolume = leftVolume
        this.rightVolume = rightVolume
        player?.setVolume(leftVolume, rightVolume)
    }

    /**
     * 播放或暂停后播放
     */
    fun play() {
        if (player == null) return
        if (videoPath.isEmpty()) {
            ZFileLog.i("视频播放地址不能为空")
            return
        }
        if (isPlaying() == true) {
            ZFileLog.i("视频正在播放...")
            return
        }
        try {
            if (isPause()) {
                player?.start()
            } else {
                player?.reset()
                player?.setDataSource(videoPath)
                player?.prepare()
                player?.start()
            }
            playState = IS_PLAYING
        } catch (e: Exception) {
            e.printStackTrace()
            ZFileLog.e("播放失败！视频路径为：$videoPath")
        }
    }

    /**
     * 暂停
     */
    fun pause() {
        if (player == null) return
        if (isPlaying() == true) {
            player?.pause()
            playState = IS_PAUSE
        }
    }

    fun isPlaying() = player?.isPlaying
    fun isPause() = playState == IS_PAUSE

    fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        this.videoWidth = videoWidth
        this.videoHeight = videoHeight
        requestLayout()
    }

    private fun updateTextureViewSize() {
        when (size_type) {
            CENTER_MODE -> updateTextureViewSizeCenter()
            CENTER_CROP_MODE -> updateTextureViewSizeCenterCrop()
            else -> throw IllegalArgumentException("参数错误")
        }
    }

    // 剪裁部分视频内容并全屏显示
    private fun updateTextureViewSizeCenterCrop() {
        val sx = width.toFloat() / videoWidth.toFloat()
        val sy = height.toFloat() / videoHeight.toFloat()
        val matrix = Matrix()
        val maxScale = sx.coerceAtLeast(sy)
        // 视频区移动到View区,使两者中心点重合.
        matrix.preTranslate(((width - videoWidth) / 2).toFloat(), ((height - videoHeight) / 2).toFloat())
        // 默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
        matrix.preScale(videoWidth / width.toFloat(), videoHeight / height.toFloat())
        // 等比例放大或缩小,直到视频区的一边超过View一边, 另一边与View的另一边相等.
        matrix.postScale(maxScale, maxScale, (width / 2).toFloat(), (height / 2).toFloat())
        setTransform(matrix)
        postInvalidate()
    }

    // 居中显示
    private fun updateTextureViewSizeCenter(rotation: Float = 0f) {
        if (rotation == 90f || rotation == 270f) { // 宽高与之前相反

        }
        val sx = width.toFloat() / videoWidth.toFloat()
        val sy = height.toFloat() / videoHeight.toFloat()
        val matrix = Matrix()
        // 把视频区移动到View区,使两者中心点重合.
        matrix.preTranslate(((width - videoWidth) / 2).toFloat(), ((height - videoHeight) / 2).toFloat())
        // 默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
        matrix.preScale(videoWidth / width.toFloat(), videoHeight / height.toFloat())
        // 等比例放大或缩小,直到视频区的一边和View一边相等.如果另一边和view的一边不相等，则留下空隙
        if (sx >= sy) {
            matrix.postScale(sy, sy, (width / 2).toFloat(), (height / 2).toFloat())
        } else {
            matrix.postScale(sx, sx, (width / 2).toFloat(), (height / 2).toFloat())
        }
        setTransform(matrix)
        postInvalidate()
    }
}