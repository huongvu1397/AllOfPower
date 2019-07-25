package com.example.allofpower.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.example.allofpower.R
import com.example.allofpower.floatingview_case1.FloatRootView
import com.example.allofpower.floatingview_case1.FloatRootView.MARGIN_EDGE
import com.example.allofpower.floatingview_case1.FloatView
import com.example.allofpower.floatingview_case1.listener.FloatMoveListener
import com.example.allofpower.floatingview_case2.FloatingView
import com.example.allofpower.floatingview_case2.MyPreferenceHelper


class FloatingService : Service(), FloatMoveListener {

    override fun onMove(magnetView: FloatRootView?) {
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initOverlayView()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if(touchLayout!=null) {
            windowManager?.removeView(touchLayout!!)
        }
    }

    private var windowManager: WindowManager? = null
    private var isFirst = false
    private var touchLayout: FloatingView? = null


    private fun initOverlayView(){
        if (this.windowManager == null) {
            this.windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }
        try{
            if (this.windowManager == null) {
                this.windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            }
            val inflater = LayoutInflater.from(ContextThemeWrapper(this, R.style.AppTheme))
            if (this.touchLayout == null) {
                this.touchLayout = inflater.inflate(R.layout.widget_floating, null) as FloatingView
            }
            val params =
                WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    ,
                    PixelFormat.TRANSLUCENT
                )
            if (MyPreferenceHelper.getInt(MyPreferenceHelper.PREF_GRAVITY, this) == 0) {
                params.gravity = Gravity.CENTER or Gravity.START
            } else {
                params.gravity = MyPreferenceHelper.getInt(MyPreferenceHelper.PREF_GRAVITY, this)
            }
            this.touchLayout?.setParams(params, this.windowManager!!, this)
            this.windowManager?.addView(this.touchLayout, params)
        }catch (e:Exception){

        }
    }


//    private fun showFloat(context: Context) {
//        synchronized(this) {
//            if (touchLayout != null) {
//                return
//            }
//            touchLayout = FloatView(context.applicationContext)//全应用
//            touchLayout?.setFloatMoveListener(this)
//            touchLayout?.layoutParams = getParams()
//            if (windowManager == null) {
//                return
//            }
//
//            //Build Params
//            val mWindowParams = WindowManager.LayoutParams()
//            mWindowParams.gravity = Gravity.TOP or Gravity.START
//            val metrics = DisplayMetrics()
//            windowManager?.defaultDisplay?.getMetrics(metrics)
//            //mWindowParams.horizontalMargin = getAbsoluteLeft() as Float / metrics.widthPixels.toFloat()
//
//            mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT
//            mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT
//            mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//            mWindowParams.flags = (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                    //or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                    //or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                    //or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//                    )
//            mWindowParams.format = PixelFormat.TRANSLUCENT
//
//            touchLayout?.setParams(mWindowParams,windowManager)
//
//            windowManager!!.addView(touchLayout,mWindowParams)
//
//            //主体绘制完成后 显示toast
//            touchLayout?.viewTreeObserver?.addOnGlobalLayoutListener {
//                if (isFirst) {
//                   // showToastDialog()
//                    isFirst = false
//                }
//            }
//        }
//    }

    private fun getParams(): FrameLayout.LayoutParams {
        val params = FrameLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.BOTTOM or Gravity.END
        params.setMargins(params.leftMargin, params.topMargin, dp2px(MARGIN_EDGE.toFloat()), dp2px(100f))
        return params
    }

    private fun dp2px(value: Float): Int {
        val scale = resources.displayMetrics.densityDpi.toFloat()
        return (value * (scale / 160) + 0.5f).toInt()
    }

    private fun getAbsoluteLeft(): Int {
        return touchLayout?.width!!
    }

}