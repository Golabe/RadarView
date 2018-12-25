package com.github.golabe.radarview.library

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class RadarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var gridColor: Int = 0
    private var sweepColor: Int = 0
    private var showGrid: Boolean = false
    private var ringCount: Int = 0
    private var gridBorderWidth: Int = 0
    private lateinit var gridPaint: Paint
    private var mWidth: Float = 0F
    private var mHeight: Float = 0F
    private var mRadius: Float = 0F
    private var gap: Float = 0F
    private var centerX: Float = 0F
    private var centerY: Float = 0F
    private lateinit var sweepPaint: Paint
    private var model = 0
    private lateinit var sweepGradient: SweepGradient
    private var animationValue = 0.0F
    private var duration = 0
    private var progressTextColor = 0
    private lateinit var progressPaint: Paint

    companion object {
        const val PROGRESS = 0x02
        const val SCAN = 0x01
        private fun changeAlpha(color: Int, alpha: Int): Int {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return Color.argb(alpha, red, green, blue)
        }
    }

    init {
        if (attrs != null) {
            val a = getContext().obtainStyledAttributes(attrs, R.styleable.RadarView)
            a.let {
                gridColor = it.getColor(R.styleable.RadarView_gridColor, Color.BLACK)
                sweepColor = it.getColor(R.styleable.RadarView_sweepColor, Color.BLACK)
                showGrid = it.getBoolean(R.styleable.RadarView_showGrid, false)
                ringCount = it.getInteger(R.styleable.RadarView_ringCount, 0)
                gridBorderWidth = dp2px(it.getDimension(R.styleable.RadarView_gridBorderWidth, 0.5F))
                model = it.getInt(R.styleable.RadarView_model, PROGRESS)
                duration = it.getInt(R.styleable.RadarView_duration, 500)
                progressTextColor = it.getColor(R.styleable.RadarView_progressTextColor, Color.BLACK)
                it.recycle()
            }
        }
        init()
    }

    private fun init() {
        gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        gridPaint.color = gridColor
        gridPaint.strokeWidth = gridBorderWidth.toFloat()
        gridPaint.style = Paint.Style.STROKE
        gridPaint.strokeCap = Paint.Cap.ROUND

        progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        progressPaint.color = progressTextColor

        sweepPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        animator = ValueAnimator.ofFloat(0F, 1F)
        animator.interpolator = LinearInterpolator()
        animator.duration = duration.toLong()
        animator.addUpdateListener { animation ->
            animationValue = animation.animatedValue as Float
            invalidate()
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                if (model == SCAN && isRunning) {
                    animator.start()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
    }

    private lateinit var animator: ValueAnimator
    private var isRunning = false
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measureWidth(widthMeasureSpec, dp2px(100F)), measureHeight(heightMeasureSpec, dp2px(100F)))
    }

    private fun measureWidth(measureSpec: Int, defaultSize: Int): Int {
        var result = 0
        val model = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        if (model == MeasureSpec.EXACTLY) {
            result = size
        } else {
            result = defaultSize + paddingLeft + paddingRight
            if (model == MeasureSpec.AT_MOST) {
                result = Math.min(result, size)
            }
        }
        result = Math.max(result, suggestedMinimumWidth)
        return result

    }

    private fun measureHeight(measureSpec: Int, defaultSize: Int): Int {
        var result = 0
        val model = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        if (model == MeasureSpec.EXACTLY) {
            result = size
        } else {
            result = defaultSize + paddingTop + paddingRight
            if (model == MeasureSpec.AT_MOST) {
                result = Math.min(result, size)
            }
        }
        result = Math.max(result, suggestedMinimumHeight)
        return result

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        mHeight = h.toFloat()
        gap = w / 24F
        mRadius = w / 2F - gap
        centerX = w / 2F
        centerY = h / 2F

    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (showGrid) {
            canvas.drawLine(gap, mHeight / 2, mWidth - gap, mHeight / 2, gridPaint)
            canvas.drawLine(mWidth / 2, gap, mWidth / 2, mHeight - gap, gridPaint)
            if (ringCount > 0) {
                for (i in 0..ringCount) {
                    canvas.drawCircle(centerX, centerY, mRadius / ringCount * i, gridPaint)
                }
            }
        }

        if (isRunning) {
            sweepGradient = SweepGradient(
                centerX, centerY, intArrayOf(
                    Color.TRANSPARENT, changeAlpha(sweepColor, 0), changeAlpha(sweepColor, 168),
                    changeAlpha(sweepColor, 255), changeAlpha(sweepColor, 255)
                ), floatArrayOf(0.0f, 0.6f, 0.99f, 0.998f, 1f)
            )
            sweepPaint.shader = sweepGradient
            canvas.save()
            canvas.rotate(-90 + animationValue * 360, centerX, centerY)
            canvas.drawCircle(centerX, centerY, mRadius, sweepPaint)
            canvas.restore()

            if (model == PROGRESS) {
                val progress = "${(animationValue * 100).toInt()}"
                val measureText = progressPaint.measureText(progress)
                progressPaint.textSize = width / 10F
                val metrics = Paint.FontMetrics()
                progressPaint.getFontMetrics(metrics)
                val offset = (metrics.descent + metrics.ascent) / 2
                canvas.drawText(progress, centerX - measureText / 2, centerY - offset, progressPaint)
            }
        }

    }

    private fun dp2px(dimens: Float): Int {
        return (context.resources.displayMetrics.density * dimens + 0.5F).toInt()
    }


    fun start() {
        animator.start()
        isRunning = true
    }

    fun stop() {
        isRunning = false
        animator.cancel()
    }


    fun setModel(model: Int) {
        this.model = model
        invalidate()
    }

    fun getModel(): Int {
        return model
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isRunning=false
        animator.removeAllUpdateListeners()
    }

    fun setProgressTextColor(color: Int) {
        this.progressTextColor = color
        invalidate()
    }

    fun getProgressTextColor() = progressTextColor
    fun setDuration(duration: Int) {
        this.duration = duration
        invalidate()
    }

    fun getDuration() = duration
    fun setSweepColor(color: Int) {
        this.sweepColor = color
        invalidate()
    }

    fun getSweepColor() = sweepColor
    fun setGridBorderWidth(border: Float) {
        this.gridBorderWidth = border.toInt()
        invalidate()
    }

    fun getGridBorderWidth() = gridBorderWidth

    fun setGridColor(color: Int) {
        this.gridColor = color
        invalidate()
    }

    fun getGridColor() = gridColor

    fun setRingCount(count: Int) {
        this.ringCount = count
        invalidate()
    }

    fun getRingCount() = ringCount

    fun setShowGrid(flag: Boolean) {
        this.showGrid = flag
        invalidate()
    }

    fun getShowGrid() = showGrid


}