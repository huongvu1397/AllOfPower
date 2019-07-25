package com.example.allofpower.floatingview_case1.myextend

import android.content.Context
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.example.allofpower.services.FloatingService

class FloatingBubbleView : FrameLayout, View.OnTouchListener,View.OnClickListener {
    private var windowManager: WindowManager? = null
    private var floatingButton: RelativeLayout? = null
    private var floatingService: FloatingService? = null
    private var params: WindowManager.LayoutParams? = null

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }

    override fun onClick(v: View?) {

    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setParams(
        mParams: WindowManager.LayoutParams,
        mWm: WindowManager,
        mPushServiceScreenRecorder: FloatingService
    ) {
        this.floatingService = mPushServiceScreenRecorder
        this.params = mParams
        this.windowManager = mWm

        val params =
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            )
        params.gravity = Gravity.CENTER or Gravity.START
    }

}