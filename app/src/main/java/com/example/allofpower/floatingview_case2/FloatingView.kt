package com.example.allofpower.floatingview_case2

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.PointF
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.allofpower.R
import com.example.allofpower.WindowManagerHelper
import com.example.allofpower.services.FloatingService
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

class FloatingView : FrameLayout, View.OnTouchListener, View.OnClickListener {
    private var windowManager: WindowManager? = null
    private var bIconCamera: ImageView? = null
    private var bTextCamera: TextView? = null
    private val imageViews = ArrayList<ImageView>()
    private var buttonMenu: RelativeLayout? = null
    private var buttonWidth = 150
    private var subButtonWidth = 120
    private var radius1: Int = 0
    private var buttonsDistance = 0f
    private var newSize: Int = 0
    private var noLimitParams: WindowManager.LayoutParams? = null
    private var limitParams: WindowManager.LayoutParams? = null
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var isCenter: Boolean = false
    private var isLeft: Boolean = false
    private var isRight: Boolean = false
    private var isTop: Boolean = false
    private var pushServiceScreenRecorder: FloatingService? = null
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var removeView: RelativeLayout? = null
    private var removeImg: ImageView? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val metrics = WindowManagerHelper.getScreenSize()
        Log.e("HVV1312", "---- ${metrics.widthPixels}")
        Log.e("HVV1312", "---- ${metrics.heightPixels}")
        try {

            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.e("HVV1312", "ACTION DOWN")
                    initialX = noLimitParams?.x!!
                    initialY = noLimitParams?.y!!
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY

                    return true
                }
                MotionEvent.ACTION_UP -> {
                    Log.e("HVV1312", "ACTION UP")

                    if (abs(this.noLimitParams?.x!! - this.initialX) >= 20 || abs(this.noLimitParams?.y!! - this.initialY) >= 20) {
                        correctButtonPosition()
                        MyPreferenceHelper.putIntValue(MyPreferenceHelper.PREF_X, this.noLimitParams?.x!!, context)
                        MyPreferenceHelper.putIntValue(MyPreferenceHelper.PREF_Y, this.noLimitParams?.y!!, context)
                        MyPreferenceHelper.putIntValue(
                            MyPreferenceHelper.PREF_GRAVITY,
                            this.noLimitParams?.gravity!!,
                            context
                        )
                        return true
                    }
                    v?.callOnClick()
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    Log.e("HVV1312", "ACTION MOVE")
                    noLimitParams?.x = initialX + (event.rawX - initialTouchX).toInt()
                    noLimitParams?.y = initialY + (event.rawY - initialTouchY).toInt()
                    getButtonScreenLocationWith2Option(
                        (noLimitParams?.x!!.toFloat() + buttonsDistance).toInt(),
                        noLimitParams?.y!!
                    )

                    windowManager?.updateViewLayout(this, noLimitParams)
                    if (opacityLayout.isAttachedToWindow)
                        windowManager?.removeViewImmediate(opacityLayout)
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun onClick(v: View?) {
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        imageViews.clear()
        imageViews.add(findViewById(R.id.top_floating))
        imageViews.add(findViewById(R.id.center_floating))
        imageViews.add(findViewById(R.id.bottom_floating))
        bIconCamera = rootView.findViewById(R.id.widget_image)
        bTextCamera = rootView.findViewById(R.id.widget_timer)
        if (this.buttonMenu == null) {
            buttonMenu = rootView.findViewById(R.id.button_overlay)
        }
        for (view in this.imageViews) {
            view.setOnClickListener(null)
        }

        this.buttonMenu?.setOnTouchListener(this)
        this.buttonMenu?.setOnClickListener(this)
        this.buttonMenu?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onGlobalLayout() {
                buttonWidth = buttonMenu?.width!!
                if (buttonWidth > 0) {
                    buttonMenu?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                    setDistances()
                    setButtons()
                    increaseLayout()
                }
            }
        })
        this.imageViews[0].viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onGlobalLayout() {
                this@FloatingView.subButtonWidth = this@FloatingView.imageViews[0].width
                if (this@FloatingView.subButtonWidth > 0) {
                    this@FloatingView.imageViews[0].viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (this.mWidth != 0 && this.mHeight != 0) {
            setMeasuredDimension(this.mWidth, this.mHeight)
            if (this.noLimitParams != null) {
                layoutParams = this.noLimitParams
            }
        }
    }

    private fun setDistances() {
        radius1 = (subButtonWidth.toDouble() * 1.1).toInt()
        for (i in 0 until imageViews.size) {
            var angle: Int
            val point = PointF()
            val avgAngle = 180 / (this.imageViews.size - 1)
            if (this.isCenter) {
                angle = avgAngle * i
            } else {
                angle = avgAngle * i //- 90
            }
            val x = Math.cos(angle.toDouble() * 0.017453292519943295).toFloat() * radius1.toFloat()
            val y = (-Math.sin(angle.toDouble() * 0.017453292519943295)).toFloat() * radius1.toFloat()
            /**
             * if X < Width/2 -> x or -x
             */
            if (MyPreferenceHelper.getInt(MyPreferenceHelper.PREF_X, context) < ((MyPreferenceHelper.getInt(
                    MyPreferenceHelper.PREF_WIDTH,
                    context
                ) / 2))
            ) {
                point.x = x
            } else {
                point.x = -x
            }
            /**
             * if Y < 0 -> -y or y
             */
            if (MyPreferenceHelper.getInt(MyPreferenceHelper.PREF_X, context) < 0) {
                point.y = -y
            } else {
                point.y = y
            }

            if (Math.abs(point.x) > this.buttonsDistance) {
                this.buttonsDistance = Math.abs(point.x)
            }
            if (Math.abs(point.y) > this.buttonsDistance) {
                this.buttonsDistance = Math.abs(point.y)
            }
        }
    }

    private fun setButtons() {
        var lParams = buttonMenu?.layoutParams as LayoutParams
        lParams.gravity = Gravity.START or Gravity.CENTER
        buttonMenu?.layoutParams = lParams
        for (view in imageViews) {
            lParams = view.layoutParams as LayoutParams
            lParams.gravity = Gravity.START or Gravity.CENTER
            view.layoutParams = lParams
        }
    }

    private val dis = 1.2
    private fun increaseLayout() {
        this.newSize =
            (((this.buttonWidth / 2).toFloat() + this.buttonsDistance + this.subButtonWidth.toFloat()).toDouble() * dis).toInt()
        setMeasure(this.newSize, this.newSize, this.limitParams!!)
        this.windowManager?.updateViewLayout(this, this.limitParams)
    }

    private fun setMeasure(viewWidth: Int, viewHeight: Int, mParams: WindowManager.LayoutParams) {
        this.mWidth = viewWidth
        this.mHeight = viewHeight
        this.noLimitParams = mParams
        setMeasuredDimension(viewWidth, viewHeight)
    }

    private fun getGravity(): Int {
        return Gravity.CENTER
    }

    private fun getStatusBarHeight(): Int {
        return ceil((25 * context.applicationContext.resources.displayMetrics.density).toDouble()).toInt()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun correctButtonPosition() {

        if (!isCenter) {
            /**
             * math.abs -> |-9| -> 9
             */
            if ((Math.abs(noLimitParams?.y!!) + radius1) + subButtonWidth > (MyPreferenceHelper.getDisplayHeight(
                    context
                ) / 2)
            ) {
                /**
                 *  Over size top || bot
                 *   1.1.1 -> over Top
                 *   1.1.2 -> over Bot
                 */
                if (isTop) {
                    //1.1.1
                    this.noLimitParams?.y =
                        (-(MyPreferenceHelper.getDisplayHeight(context) / 2)) + (this.radius1 + this.subButtonWidth)
                } else {
                    //1.1.2
                    this.noLimitParams?.y =
                        (MyPreferenceHelper.getDisplayHeight(context) / 2) - (this.radius1 + this.subButtonWidth)
                }
            }
            if (this.isRight) {
                /**
                 * 1.2 - over right
                 */
//                if ((this.noLimitParams?.x!! + (this.newSize / 2)) + (this.buttonWidth / 2) > MyPreferenceHelper.getDisplayWidth(context)
//                ) {
                this.noLimitParams?.x =
                    (MyPreferenceHelper.getDisplayWidth(context)) - (this.buttonWidth / 2)
                //}

            }
            /**
             *  1.3 - over left
             */
            else if (this.isLeft) {
                //if (this.noLimitParams?.x!! < (-((this.newSize / 2) - (this.buttonWidth / 2)))) {
                this.noLimitParams?.x = -((this.newSize / 2) - (this.buttonWidth / 2))
                //}
            }
            innerClosing()
            this.windowManager?.updateViewLayout(this, this.limitParams)
        } else if ((Math.abs(this.noLimitParams?.y!!) + (this.newSize / 2)) + (this.buttonWidth / 2) > MyPreferenceHelper.getDisplayHeight(
                context
            ) / 2
        ) {
            if (this.isTop) {
                this.noLimitParams?.y =
                    ((-MyPreferenceHelper.getInt(
                        MyPreferenceHelper.PREF_HEIGHT,
                        context
                    )) / 2) + this.subButtonWidth
            } else {
                this.noLimitParams?.y =
                    (MyPreferenceHelper.getInt(
                        MyPreferenceHelper.PREF_HEIGHT,
                        context
                    ) / 2) - this.subButtonWidth
            }
            innerClosing()
            this.windowManager?.updateViewLayout(this, this.limitParams)
        }

    }

    fun setParams(
        mParams: WindowManager.LayoutParams,
        mWm: WindowManager,
        mPushServiceScreenRecorder: FloatingService
    ) {
        this.pushServiceScreenRecorder = mPushServiceScreenRecorder
        this.noLimitParams = mParams
        this.windowManager = mWm

        val params =
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                //or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                ,
                PixelFormat.TRANSLUCENT
            )
        params.gravity = Gravity.CENTER or Gravity.START
        this.limitParams = params
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun closeSectorMenu() {
        for (i in 0 until this.imageViews.size) {
            var angle: Int
            (this.imageViews[i]).setOnClickListener(null)
            val point = PointF()
            val avgAngle = 180 / (this.imageViews.size - 1)
            when (i) {
                0 -> angle = (avgAngle * i) - 60
                1 -> angle = (avgAngle * i) - 90
                2 -> angle = (avgAngle * i) - 120
                else -> angle = (avgAngle * i) - 90
            }
            val x = (Math.cos((angle.toDouble()) * 0.017453292519943295).toFloat()) * (this.radius1.toFloat())
            val y = ((-Math.sin((angle.toDouble()) * 0.017453292519943295)).toFloat()) * (this.radius1.toFloat())

            if (MyPreferenceHelper.getInt(MyPreferenceHelper.PREF_X, context) < ((MyPreferenceHelper.getInt(
                    MyPreferenceHelper.PREF_WIDTH,
                    context
                ) / 2).toFloat())
            ) {
                point.x = x
            } else {
                point.x = -x
            }
            point.y = -y
            val objectAnimatorX = ObjectAnimator.ofFloat(this.imageViews[i], View.TRANSLATION_X, point.x, 0.0f)
            val objectAnimatorY = ObjectAnimator.ofFloat(this.imageViews[i], View.TRANSLATION_Y, point.y, 0.0f)
            val animatorSet = AnimatorSet()
            animatorSet.duration = 300
            animatorSet.play(objectAnimatorX).with(objectAnimatorY)
            animatorSet.start()
        }
        this.buttonMenu?.tag = java.lang.Boolean.valueOf(false)
        if (opacityLayout.isAttachedToWindow)
            windowManager?.removeViewImmediate(opacityLayout)

        setMeasure(buttonMenu!!.width, buttonMenu!!.width, this.limitParams!!)
        this.windowManager?.updateViewLayout(this, this.limitParams)
    }

    private lateinit var opacityLayout: RelativeLayout

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun showOpacity() {
        opacityLayout = RelativeLayout(context)
        opacityLayout.setBackgroundResource(R.drawable.opacitylayout)
        var params: WindowManager.LayoutParams?
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
           WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )
        windowManager?.addView(opacityLayout, params)
        opacityLayout.setOnClickListener {
            closeSectorMenu()
            if (opacityLayout.isAttachedToWindow)
                windowManager?.removeViewImmediate(opacityLayout)
        }
        windowManager?.removeView(this)
        windowManager?.addView(this, params)

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun innerClosing() {
        val isShowing = this.buttonMenu?.tag.toString()
        if (isShowing != "false") {
            closeSectorMenu()
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun showMenu() {
        val isShowing = this.buttonMenu?.tag.toString()
        if (isShowing == "null" || isShowing == "false") {
            showSectorMenu()
            showOpacity()
        } else {
            closeSectorMenu()
            if (opacityLayout.isAttachedToWindow)
                windowManager?.removeViewImmediate(opacityLayout)
        }
    }

    private fun showSectorMenu() {
        for (i in 0 until this.imageViews.size) {
            val angle: Int
            val point = PointF()
            val avgAngle = 180 / (imageViews.size - 1)
            // option 2 no center
            angle = when (i) {
                0 -> (avgAngle * i) - 60
                1 -> (avgAngle * i) - 90
                2 -> (avgAngle * i) - 120
                else -> (avgAngle * i) - 90
            }
            val x = (cos((angle.toDouble()) * 0.017453292519943295).toFloat()) * (this.radius1.toFloat() + 8)
            val y = ((-sin((angle.toDouble()) * 0.017453292519943295)).toFloat()) * (this.radius1.toFloat() + 8)

            if (MyPreferenceHelper.getLastX(context) < (MyPreferenceHelper.getDisplayWidth(context) / 2).toFloat()
            ) {
                point.x = x
            } else {
                point.x = -x
            }
            point.y = -y

            if (abs(point.x) > this.buttonsDistance) {
                this.buttonsDistance = abs(point.x)
            }
            if (abs(point.y) > this.buttonsDistance) {
                this.buttonsDistance = abs(point.y)
            }
            val objectAnimatorX =
                ObjectAnimator.ofFloat(this.imageViews[i], "translationX", 0.0f, point.x)
            val objectAnimatorY =
                ObjectAnimator.ofFloat(this.imageViews[i], "translationY", 0.0f, point.y)
            val animatorSet = AnimatorSet()
            animatorSet.duration = 300
            animatorSet.play(objectAnimatorX).with(objectAnimatorY)
            animatorSet.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    (this@FloatingView.imageViews[i]).setOnClickListener(this@FloatingView)
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            animatorSet.start()
        }
        this.buttonMenu?.tag = java.lang.Boolean.valueOf(true)
        setMeasure(newSize, newSize, this.noLimitParams!!)
        this.windowManager?.updateViewLayout(this, this.noLimitParams)
    }

    private fun getButtonScreenLocationWith2Option(x: Int, y: Int) {
        val screenPart = MyPreferenceHelper.getInt(MyPreferenceHelper.PREF_WIDTH, context) / 2
        this.isTop = y <= 0  // find top bot oftion
        if (x < screenPart) {
            this.isLeft = true
            this.isRight = false
            this.isCenter = false
        } else {
            this.isLeft = false
            this.isRight = true
            this.isCenter = false
        }
    }


}