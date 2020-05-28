## 一、前言

Android 自定义View是高级进阶不可或缺的内容，日常工作中，经常会遇到产品、UI设计出花里胡哨的界面。当系统自带的控件不能满足开发需求时，就只能自己动手撸一个效果。

本文就带自定义View初学者手动撸一个效果，通过自定义View实现钟表功能，**每行代码都有注释，保证易懂，看不懂你留言打我！！！**

## 二、实现效果

### 1、先看效果图
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200527170644942.gif#pic_center)


### 2、下载地址：[点击下载](https://github.com/jaynm888/ClockCustomizeView.git)
### 3、步骤分析

实现以上效果，主要分为四个步骤：

1. 绘制外层表盘
2. 绘制刻度线
3. 绘制刻度数字
4. 绘制指针

## 三、代码实现
### 1、绘制外层表盘

外层表盘就是一个空心圆，只要获取圆的x、y轴位置、圆的半径，使用Canvas.drawCircle()方法即可完成。

```kotlin
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
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200527164305615.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2pheW5t,size_16,color_FFFFFF,t_70#pic_center)

### 2、绘制刻度线

**绘制思路分析**

看到效果图上密密麻麻刻度线后，先不要着急上手，屡清楚绘制思路。绘制刻度线一定要结合Canvas几何变换思路完成，千万不要局限于效果图的表面（如果对Canvas相关API不熟悉的朋友，建议先了解下）。

1. 假设以12点钟为例，那么刻度线就是一条笔直的竖线，调用Canvas.drawLine()方法完成绘制。
2. 如果每绘制完成一个刻度，把表盘逆时针/顺时针旋转一定角度，将下次需要绘制刻度线的位置旋转到12点钟位置，那么每次绘制刻度线的startX、startY、stopX、stopY一致（一致仅代表所有长刻度一致，所有短刻度一致）。
3. 观察表盘共有60个刻度线（12长，48短），那么每次旋转角度**degrees=360/60**

听完以上分析，是否觉得绘制刻度线很简单，只要在60个刻度遍历判断长短，即可轻松出效果。

```kotlin
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
```

### 3、绘制数字新方案
热心网友指导我绘制数字新方案，真的是高手如云阿。

首先将坐标位置（0,0）设置到圆心位置，这步是在绘制外层圆的时候，已经设置了。这样的好处是后期减少很多计算的步骤，新方案已经在代码中更改！

```kotlin
	canvas.translate(centerX, centerY)
```

主要是通过canvas几何变换方式，先将圆点平移到12点钟位置，然后逆时针旋转数字对应的角度，然后开始绘制数字文本。这样的话，绘制数字文本就和绘制刻度线可以一并完成，使得代码清晰很多。
需要注意的是，记得在使用几何变换前后分别调用**canvas.restore()和canvas.restore()方法**。

其中相关坐标计算方式：

>**1、平移 y 轴距离 = - 半径 + 刻度线长度 + 刻度与文本间距 + 文本高度 / 2** 
>（因为坐标原点在圆心，需要平移到12点钟位置，所以半径为负数）
>**2、旋转角度 = - 6 * 数字大小**
>**3、文本 x 轴距离 = 文本宽度 / 2 ；**
>**4、文本 y 轴距离 = 文本高度 / 2 ；**

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200527164347315.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2pheW5t,size_16,color_FFFFFF,t_70#pic_center)


附上绘制刻度线和文本的完整代码：
```kotlin
/**
 * 绘制表盘刻度线和数字文本
 */
private fun drawClockScale(canvas: Canvas) {
    for (index in 1..60) {
        // 刻度绘制以12点钟为准，每次将表盘旋转6°，后续绘制都以12点钟为基准绘制
        canvas.rotate(6F, 0F, 0F)
        // 绘制长刻度线
        if (index % 5 == 0) {
            // 设置长刻度画笔宽度
            mPaint.strokeWidth = 4.0F
            // 绘制刻度线
            canvas.drawLine(0F, -radius, 0F, -radius + scaleMax, mPaint)
            /** 绘制文本 **/
            canvas.save()
            // 设置画笔宽度
            mPaint.strokeWidth = 1.0F
            // 设置画笔实心风格
            mPaint.style = Paint.Style.FILL
            mPaint.getTextBounds(
                (index / 5).toString(),
                0,
                (index / 5).toString().length,
                mRect
            )
            canvas.translate(0F, -radius + mNumberSpace + scaleMax + (mRect.height() / 2))
            canvas.rotate((index * -6).toFloat())
            canvas.drawText(
                (index / 5).toString(), -mRect.width() / 2.toFloat(),
                mRect.height().toFloat() / 2, mPaint
            )
            canvas.restore()
        }
        // 绘制短刻度线
        else {
            // 设置短刻度画笔宽度
            mPaint.strokeWidth = 2.0F
            canvas.drawLine(0F, -radius, 0F, -radius + scaleMin, mPaint)
        }
    }
}
```

### 4、绘制指针

指针绘制具体分以下步骤：

1. 首先获取当前时间
2. 根据当前时间计算指针旋转过的角度
3. 利用Canvas.rotate()旋转画布
4. 使用Canvas.drawRoundRect()绘制指针矩形
5. 绘制圆点

```kotlin
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
        (centerX).toFloat(),
        (centerY).toFloat(), mSecondPointWidth * 4, mPaint
    )
}
```
以上就已经完成表盘指针绘制工作，效果图如下：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200527164358620.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2pheW5t,size_16,color_FFFFFF,t_70#pic_center)
### 6、onMeasure测量View宽高

MeasureSpecMode有三个属性：**EXACTLY、AT_MOST、UNSPECIFIED**

根据View在xml设置宽高类型不同会触发相应的方法，这里对onMeasure()测量不做具体讲解，如果对自定义View测量宽高onMeasure()方法不熟悉的，请自己补习。onMeasure()方法也是自定义View学习过程中非常重要的一个环节！

```kotlin
override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    // 根据表盘半径 + 表盘圆环宽度计算View宽度和高度
    mWidth = onMeasuredSpec(widthMeasureSpec) + (mCircleWidth * 2).toInt()
    mHeight = onMeasuredSpec(heightMeasureSpec) + (mCircleWidth * 2).toInt()
    // 计算最终View表盘的半径
    radius = (mWidth - mCircleWidth * 2) / 2
    // 设置View最终宽高
    setMeasuredDimension(mWidth, mHeight)
}
```

```kotlin
private fun onMeasuredSpec(measureSpec: Int): Int {
    // 临时值，用于计算后返回
    var specViewSize = 0
    val specMode = MeasureSpec.getMode(measureSpec)
    val specSize = MeasureSpec.getSize(measureSpec)

    when (specMode) {
        MeasureSpec.EXACTLY -> {
            // 一般为固定尺寸或者match_parent
            specViewSize = specSize
        }
        MeasureSpec.AT_MOST -> {
            // 计算半径以宽高最小值为准
            specViewSize = min((radius * 2).toInt(), specSize)
        }
    }
    return specViewSize
}
```


到此为止，如果想让表盘和指针动起来，还需要在onDraw()方法里调用**postInvalidateDelayed(1000)方法，或者启动一个线程**，使每秒钟刷新重绘一次布局，这样就可以让指针实时更新时间。最后贴上onDraw()方法里面的代码：

```kotlin
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
    // 刷新表盘指针
    postInvalidateDelayed(1000)
}
```

## 总结

自定view在Android开发过程中应用极其广泛，为了更好的掌握，建议从自定义View绘制流程、Canvas、Paint、Path、onLayout()、onMeasure()、onDraw()系统化学习，然后上手多做练习，这样势必会对自定义View有很好的提升！

看完文章，是不是觉得这个效果其实也很简单，案例中相关属性可以使用自定义属性，因为练习案例，所以这里在View中直接写死了，感兴趣的朋友可以使用自定义属性尝试实现。这个案例基本上将自定义View中Canvas、Paint常见的API方法以及onMeasure()测量方法都应用到了，算是一个上手练习自定义View的好案例，希望看完文章对你学习有所帮助！

前文说过，保证每个自定义View初学者都能看懂，因为每行代码都会添加注释，如果没看懂的留言打我！！！

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200527164416919.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2pheW5t,size_16,color_FFFFFF,t_70#pic_center)