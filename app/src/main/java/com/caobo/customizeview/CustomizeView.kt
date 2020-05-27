package com.caobo.customizeview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.math.*

/**
 * Created by jaynm
 * 关注公众号：码农专栏  获取更多学习资料
 * on 2020-05-26.
 */
class CustomizeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 时针宽度
    private val mHourPointWidth = 15f

    // 分针宽度
    private val mMinutePointWidth = 10f

    // 秒针宽度
    private val mSecondPointWidth = 4f

    // 指针矩形弧度
    private val mPointRange = 20F

    // 刻度与数字间距
    private val mNumberSpace = 10f

    // 表盘宽度
    private val mCircleWidth = 4.0F

    // 设置表盘整点刻度尺寸
    private val scaleMax = 50

    // 设置表盘非整点刻度尺寸
    private val scaleMin = 25

    // View宽度
    private var mWidth = 0

    // View高度
    private var mHeight = 0

    // 圆半径，默认200像素
    private var radius = 300.0F

    // 画笔
    private val mPaint: Paint by lazy {
        Paint()
    }
    private val mRect: Rect by lazy {
        Rect()
    }


    // 初始化画笔属性
    init {
        mPaint.textSize = 35F
        mPaint.typeface = Typeface.DEFAULT_BOLD
        mPaint.isAntiAlias = true

//        // 设置圆心X轴位置
//        val centerX: Float = width / 2F
//        // 设置圆心Y轴位置
//        val centerY: Float = height / 2F
//        // 设置圆半径，默认设置为1/4屏幕宽度，可添加为自定义属性
//        val radius: Float = width / 4F
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        mWidth = onMeasuredSpec(widthMeasureSpec) + (mCircleWidth * 2).toInt()
        mHeight = onMeasuredSpec(heightMeasureSpec) + (mCircleWidth * 2).toInt()

        radius = (mWidth - mCircleWidth * 2) / 2
        setMeasuredDimension(mWidth, mHeight)
    }

    private fun onMeasuredSpec(measureSpec: Int): Int {

        var specViewSize = 0
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        when (specMode) {
            MeasureSpec.EXACTLY -> {
                specViewSize = specSize
            }
            MeasureSpec.AT_MOST -> {
                // 计算半径以宽高最小值为准
                specViewSize = min((radius * 2).toInt(), specSize)
            }
        }
        return specViewSize
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 设置圆心X轴位置
        val centerX: Float = (mWidth / 2).toFloat()
        // 设置圆心Y轴位置
        val centerY: Float = (mHeight / 2).toFloat()

        /** 第一步：绘制最外层的圆 **/
        drawClock(canvas, centerX, centerY)

        /** 第二步：表盘一共60个刻度，1到12点整数属于长刻度，其余属于短刻度 **/
        drawClockScale(canvas, centerX, centerY)

        /** 第三步：绘制表盘数字 **/
        drawClockNumber(canvas, centerX, centerY)

        /** 第四步：绘制指针 **/
        drawPointer(canvas, centerX, centerY)

        postInvalidateDelayed(1000)

    }

    /**
     * 绘制表盘
     */
    private fun drawClock(canvas: Canvas, centerX: Float, centerY: Float) {

        // 设置外层圆画笔宽度
        mPaint.strokeWidth = mCircleWidth
        // 设置画笔颜色
        mPaint.color = Color.BLACK
        // 设置画笔空心风格
        mPaint.style = Paint.Style.STROKE
        // 绘制圆方法
        canvas.drawCircle(centerX, centerY, radius, mPaint)
    }

    /**
     * 绘制表盘刻度
     */
    private fun drawClockScale(canvas: Canvas, centerX: Float, centerY: Float) {
        for (index in 1..60) {
            // 刻度绘制以12点钟为准，每次将表盘旋转6°，后续绘制都以12点钟为基准绘制
            canvas.rotate(6F, centerX, centerY)
            // 绘制长刻度线
            if (index % 5 == 0) {
                // 设置长刻度画笔宽度
                mPaint.strokeWidth = 4.0F
                // 绘制刻度线
                canvas.drawLine(
                    centerX,
                    centerY - radius,
                    centerX,
                    centerY - radius + scaleMax,
                    mPaint
                )

//                // 测量绘制数字
//                mPaint.strokeWidth = 1.0F
//                mPaint.style = Paint.Style.FILL
//                mPaint.getTextBounds((index / 5).toString(), 0, (index / 5).toString().length, mRect)
//                val width = mRect.width()
//                canvas.drawText(
//                    (index / 5).toString(),
//                    centerX - mRect.width() / 2,
//                    (centerY - radius + scaleMax + mRect.height() + 8),
//                    mPaint
//                )

            }
            // 绘制短刻度线
            else {
                // 设置短刻度画笔宽度
                mPaint.strokeWidth = 2.0F
                canvas.drawLine(
                    centerX,
                    centerY - radius,
                    centerX,
                    centerY - radius + scaleMin,
                    mPaint
                )
            }
        }
    }

    /**
     * 绘制表盘数字
     */
    private fun drawClockNumber(canvas: Canvas, centerX: Float, centerY: Float) {
        var x = 0.0F // 数字x坐标
        var y = 0.0F // 数字y坐标
        // 设置画笔宽度
        mPaint.strokeWidth = 1.0F
        // 设置画笔实心风格
        mPaint.style = Paint.Style.FILL

        // 数字距离表盘边界距离：半径-刻度线长度-数字距离刻度线间距
        var tempSpace = radius - scaleMax - mNumberSpace

        // 遍历绘制表盘数字
        for (num in 1..12) {
            mPaint.getTextBounds((num).toString(), 0, (num).toString().length, mRect)
            if (num == 1 || num == 2) {
                x =
                    (centerX + tempSpace * sin(Math.toRadians(num * 30.0)) - mRect.width()).toFloat()
                y =
                    (centerY - tempSpace * cos(Math.toRadians(num * 30.0)) + mRect.height()).toFloat()
            } else if (num == 4 || num == 5) {
                x =
                    (centerX + tempSpace * sin(Math.toRadians(num * 30.0)) - mRect.width()).toFloat()
                y = (centerY - tempSpace * cos(Math.toRadians(num * 30.0))).toFloat()
            } else if (num == 7 || num == 8) {
                x = (centerX + tempSpace * sin(Math.toRadians(num * 30.0))).toFloat()
                y = (centerY - tempSpace * cos(Math.toRadians(num * 30.0))).toFloat()
            } else if (num == 10 || num == 11) {
                x = (centerX + tempSpace * sin(Math.toRadians(num * 30.0))).toFloat()
                y =
                    (centerY - tempSpace * cos(Math.toRadians(num * 30.0)) + mRect.height()).toFloat()
            } else if (num == 3) {
                x =
                    (centerX + tempSpace * sin(Math.toRadians(num * 30.0)) - mRect.width()).toFloat()
                y =
                    (centerY - tempSpace * cos(Math.toRadians(num * 30.0)) + mRect.height() / 2).toFloat()
            } else if (num == 6) {
                x =
                    (centerX + tempSpace * sin(Math.toRadians(num * 30.0)) - mRect.width() / 2).toFloat()
                y = (centerY - tempSpace * cos(Math.toRadians(num * 30.0))).toFloat()
            } else if (num == 9) {
                x = (centerX + tempSpace * sin(Math.toRadians(num * 30.0))).toFloat()
                y =
                    (centerY - tempSpace * cos(Math.toRadians(num * 30.0)) + mRect.height() / 2).toFloat()
            } else if (num == 12) {
                x =
                    (centerX + tempSpace * sin(Math.toRadians(num * 30.0)) - mRect.width() / 2).toFloat()
                y =
                    (centerY - tempSpace * cos(Math.toRadians(num * 30.0)) + mRect.height()).toFloat()
            }

            canvas.drawText(num.toString(), x, y, mPaint)
        }

        // TODO：如果上面for循环里面看不懂，可以查看下面单独的步骤
//        mPaint.getTextBounds(("12").toString(), 0, ("12").toString().length, mRect)
//        canvas.drawText(
//            "12",
//            (width / 2 + tempSpace * sin(Math.toRadians(0.0)) - mRect.width() / 2).toFloat(),
//            (height / 2 - tempSpace * cos(Math.toRadians(0.0)) + mRect.height()).toFloat(),
//            mPaint
//        )
//        mPaint.getTextBounds(("1").toString(), 0, ("1").toString().length, mRect)
//        canvas.drawText(
//            "1",
//            (width / 2 + tempSpace * sin(Math.toRadians(30.0)) - mRect.width()).toFloat(),
//            (height / 2 - tempSpace * cos(Math.toRadians(30.0)) + mRect.height()).toFloat(), mPaint
//        )
//        mPaint.getTextBounds(("2").toString(), 0, ("2").toString().length, mRect)
//        canvas.drawText(
//            "2",
//            (width / 2 + tempSpace * sin(Math.toRadians(60.0)) - mRect.width()).toFloat(),
//            (height / 2 - tempSpace * cos(Math.toRadians(60.0)) + mRect.height()).toFloat(), mPaint
//        )
//        mPaint.getTextBounds(("3").toString(), 0, ("3").toString().length, mRect)
//        canvas.drawText(
//            "3",
//            (width / 2 + tempSpace * sin(Math.toRadians(90.0)) - mRect.width()).toFloat(),
//            (height / 2 - tempSpace * cos(Math.toRadians(90.0)) + mRect.height() / 2).toFloat(),
//            mPaint
//        )
//        mPaint.getTextBounds(("4").toString(), 0, ("4").toString().length, mRect)
//        canvas.drawText(
//            "4",
//            (width / 2 + tempSpace * sin(Math.toRadians(120.0)) - mRect.width()).toFloat(),
//            (height / 2 - tempSpace * cos(Math.toRadians(120.0))).toFloat()
//            , mPaint
//        )
//        mPaint.getTextBounds(("5").toString(), 0, ("5").toString().length, mRect)
//        canvas.drawText(
//            "5",
//            (width / 2 + tempSpace * sin(Math.toRadians(150.0)) - mRect.width()).toFloat(),
//            (height / 2 - tempSpace * cos(Math.toRadians(150.0))).toFloat()
//            , mPaint
//        )
//        mPaint.getTextBounds(("6").toString(), 0, ("6").toString().length, mRect)
//        canvas.drawText(
//            "6",
//            (width / 2 + tempSpace * sin(Math.toRadians(180.0)) - mRect.width() / 2).toFloat(),
//            (height / 2 - tempSpace * cos(Math.toRadians(180.0))).toFloat(),
//            mPaint
//        )
//        mPaint.getTextBounds(("7").toString(), 0, ("7").toString().length, mRect)
//        canvas.drawText(
//            "7",
//            (width / 2 + tempSpace * sin(Math.toRadians(210.0))).toFloat(),
//            (height / 2 - tempSpace * cos(Math.toRadians(210.0))).toFloat()
//            , mPaint
//        )
//        mPaint.getTextBounds(("8").toString(), 0, ("8").toString().length, mRect)
//        canvas.drawText(
//            "8",
//            (width / 2 + tempSpace * sin(Math.toRadians(240.0))).toFloat(),
//            (height / 2 - tempSpace * cos(Math.toRadians(240.0))).toFloat()
//            , mPaint
//        )
//        mPaint.getTextBounds(("9").toString(), 0, ("9").toString().length, mRect)
//        canvas.drawText(
//            "9",
//            ((width / 2 + tempSpace * sin(Math.toRadians(270.0))).toFloat()),
//            (height / 2 - tempSpace * cos(Math.toRadians(270.0)) + mRect.height() / 2).toFloat()
//            ,
//            mPaint
//        )
//        mPaint.getTextBounds(("10").toString(), 0, ("10").toString().length, mRect)
//        canvas.drawText(
//            "10",
//            (width / 2 + tempSpace * sin(Math.toRadians(300.0))).toFloat(),
//            (height / 2 - tempSpace * cos(Math.toRadians(300.0)) + mRect.height()).toFloat()
//            , mPaint
//        )
//        mPaint.getTextBounds(("11").toString(), 0, ("11").toString().length, mRect)
//        canvas.drawText(
//            "11",
//            (width / 2 + tempSpace * sin(Math.toRadians(330.0))).toFloat(),
//            (height / 2 - tempSpace * cos(Math.toRadians(330.0)) + mRect.height()).toFloat()
//            , mPaint
//        )
    }

    /**
     * 第四步：绘制指针
     */
    private fun drawPointer(canvas: Canvas, centerX: Float, centerY: Float) {

        // 获取当前时间：时分秒
        val calendar = Calendar.getInstance()
        val hour = calendar[Calendar.HOUR]
        val minute = calendar[Calendar.MINUTE]
        val second = calendar[Calendar.SECOND]
        // 计算时分秒转过的角度
        val angleHour = (hour + minute.toFloat() / 60) * 360 / 12
        val angleMinute = (minute + second.toFloat() / 60) * 360 / 60
        val angleSecond = second * 360 / 60

        // 绘制时针
        canvas.save()
        // 旋转到时针的角度
        canvas.rotate(angleHour, centerX, centerY)
        val rectHour = RectF(
            centerX - mHourPointWidth / 2,
            centerY - radius / 2,
            centerX + mHourPointWidth / 2,
            centerY + radius / 6
        )
        // 设置时针画笔属性
        mPaint.color = Color.BLUE
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mHourPointWidth
        canvas.drawRoundRect(rectHour, mPointRange, mPointRange, mPaint)
        canvas.restore()

        // 绘制分针
        canvas.save()
        // 旋转到分针的角度
        canvas.rotate(angleMinute, centerX, centerY)
        val rectMinute = RectF(
            centerX - mMinutePointWidth / 2,
            centerY - radius * 3.5f / 5,
            centerX + mMinutePointWidth / 2,
            centerY + radius / 6
        )
        // 设置分针画笔属性
        mPaint.color = Color.BLACK
        mPaint.strokeWidth = mMinutePointWidth
        canvas.drawRoundRect(rectMinute, mPointRange, mPointRange, mPaint)
        canvas.restore()

        // 绘制秒针
        canvas.save()
        // 旋转到分针的角度
        canvas.rotate(angleSecond.toFloat(), centerX, centerY)
        val rectSecond = RectF(
            centerX - mSecondPointWidth / 2,
            centerY - radius + 10,
            centerX + mSecondPointWidth / 2,
            centerY + radius / 6
        )
        // 设置秒针画笔属性
        mPaint.strokeWidth = mSecondPointWidth
        mPaint.color = Color.RED
        canvas.drawRoundRect(rectSecond, mPointRange, mPointRange, mPaint)
        canvas.restore()

        // 绘制原点
        mPaint.style = Paint.Style.FILL
        canvas.drawCircle(
            centerX,
            centerY, mSecondPointWidth * 4, mPaint
        )
    }
}