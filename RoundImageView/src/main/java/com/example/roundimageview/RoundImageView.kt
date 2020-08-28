package com.example.roundimageview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View.MeasureSpec
import androidx.appcompat.widget.AppCompatImageView

class RoundImageView : AppCompatImageView {
    /*
    * 图片的类型
    * 圆形、圆角、椭圆*/
    companion object {
        const val TYPE_CIRCLE = 0
        const val TYPE_ROUND = 1
        const val TYPE_OVAL = 2
    }
    //类型
    private var type = 0
    //圆形半径
    private var xRadius: Float = 0f
    //view的宽度
    private var xWidth = 0
    //圆角半径
    private var xCornerRadius: Float = 0f
    private var xLeftTopCornerRadius = 0f
    private var xLeftBottomCornerRadius: Float = 0f
    private var xRightTopCornerRadius: Float = 0f
    private var xRightBottomCornerRadius: Float = 0f
    //渲染颜色
    private var xBitmapShader: BitmapShader? = null
    //缩放
    private var xMatrix: Matrix = Matrix()
    //绘图的paint
    private var xBitmapPaint: Paint = Paint()
    //圆角图片区域
    private var xRoundRect: RectF = RectF()
    private var xRoundPath:Path = Path()


    /**
     * 构造函数RoundImageView.
     * @param context 一个参数
     */
    constructor(context: Context) : super(context) {
        RoundImageView(context, null)
    }

    /**
     * 两个参数
     * @param context current activity
     * @param attrs 传入Layout
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, 0, 0)
        type = a.getInt(R.styleable.RoundImageView_type, TYPE_OVAL)
        //则不管写的是dp还是sp还是px,都会乘以denstiy.
        //xRadius = a.getDimension(R.styleable.RoundImageView_radius, 0)
        xCornerRadius = a.getDimension(R.styleable.RoundImageView_corner_radius,
            dp2px(10).toFloat()
        )
        xLeftTopCornerRadius = a.getDimension(R.styleable.RoundImageView_LeftTop_corner_radius, 0f)
        xLeftBottomCornerRadius = a.getDimension(R.styleable.RoundImageView_LeftBottom_corner_radius, 0f)
        xRightTopCornerRadius = a.getDimension(R.styleable.RoundImageView_RightTop_corner_radius, 0f)
        xRightBottomCornerRadius = a.getDimension(R.styleable.RoundImageView_RightBottom_corner_radius, 0f)
        a.recycle()
        init()
    }

    private fun init() {
        //val xRoundPath = Path()
       // val xMatrix = Matrix()
       // val xBitmapPaint = Paint()
        xBitmapPaint.setAntiAlias(true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.e("TAG", "onMeasure方法")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        /*
        * 如果类型是圆形，则强制改变view的宽高一致，以最小值为准*/
        if (type == TYPE_CIRCLE) {//
            xWidth = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
            xRadius = xWidth / 2f
            //setMeasuredDimension(xWidth, xWidth)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        Log.e("TAG", "onSizeChanged方法")
        super.onSizeChanged(w, h, oldw, oldh)
        if (type == TYPE_ROUND || type == TYPE_OVAL) {
            xRoundRect = RectF(0f,0f,w.toFloat(),h.toFloat())
        }
    }

    /*
    * 初始化BitmapShader
    * */
    private fun setUpShader() {
        Log.e("TAG", "setUpShader方法")
        var bmp = drawableToBitmap(drawable)
        //初始化   (着色器，在X方向，在Y方向) 如果渲染器超出原始边界范围，会复制范围内边缘染色
        xBitmapShader = BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        var scaleWidth = 1.0f
        var scaleHeight = 1.0f
        if (type == TYPE_CIRCLE) {
            // 拿到bitmap宽或高的小值
            val bSize = Math.min(bmp.width, bmp.height)
            scaleWidth = xWidth * 1.0f / bSize
            scaleHeight = scaleWidth
            //使缩放后的图片居中
            val dx: Float = (bmp.width * scaleWidth - xWidth) / 2
            val dy: Float = (bmp.height * scaleHeight - xWidth) / 2
            xMatrix.setTranslate(-dx,-dy)//像素点相对画布向右-dx sp,向下移动-dy sp
        } else if (type == TYPE_ROUND ||type == TYPE_OVAL) {
            scaleWidth = Math.max(width * 1.0f / bmp.width, height * 1.0f / bmp.height)
            scaleHeight = scaleWidth
            val dx: Float = (bmp.width * scaleWidth - width) / 2
            val dy: Float = (bmp.height * scaleHeight - height) / 2
            xMatrix.setTranslate(-dx,-dy)
        }
        //缩放
        xMatrix.preScale(scaleWidth, scaleHeight)
        //设置变换矩阵
        xBitmapShader?.setLocalMatrix(xMatrix)
        //设置shader
        xBitmapPaint.setShader(xBitmapShader)
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        Log.e("TAG", "drawableToBitmap方法")
        if (drawable is BitmapDrawable) {
            val bd = drawable
            return bd.bitmap
        }
        var bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        var canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicHeight, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onDraw(canvas: Canvas) {
        Log.e("TAG", "onDraw方法")

        if (drawable == null) { return }
        setUpShader()

        if (type == TYPE_ROUND) {
            setRoundPath()
            canvas.drawPath(xRoundPath, xBitmapPaint)
        } else if (type == TYPE_CIRCLE){
            setCirclePath()
            canvas.drawPath(xRoundPath, xBitmapPaint)
        }else if (type == TYPE_OVAL){
            setOvalPath()
            canvas.drawPath(xRoundPath,xBitmapPaint)
        }
    }
    //椭圆画笔路径
    private fun setOvalPath() {
        xRoundPath.reset()
        xRoundPath.addOval(xRoundRect,Path.Direction.CW)
    }

    //圆形画笔路径
    private fun setCirclePath() {
        xRoundPath.reset()
        xRoundPath.addCircle(xRadius, xRadius, xRadius, Path.Direction.CW)
    }

    /**
     * 圆角画笔路径
     * 两种构造方法
     */
    private fun setRoundPath() {
        xRoundPath.reset()

        /**
         * 如果四个圆角大小都是默认值0，
         * 则将四个圆角大小设置为mCornerRadius的值
         * 构建统一圆角大小
         */
        if (xLeftTopCornerRadius == 0f && xLeftBottomCornerRadius == 0f && xRightTopCornerRadius == 0f && xRightBottomCornerRadius == 0f) {
            xRoundPath.addRoundRect(
                xRoundRect, xCornerRadius, xCornerRadius, Path.Direction.CW) //顺时针
        } else {//定制每个角圆角大小
            var radii = floatArrayOf(
                xLeftTopCornerRadius, xLeftTopCornerRadius,
                xRightTopCornerRadius, xRightTopCornerRadius,
                xRightBottomCornerRadius, xRightBottomCornerRadius,
                xLeftBottomCornerRadius, xLeftBottomCornerRadius)
            xRoundPath.addRoundRect(xRoundRect, radii, Path.Direction.CW)
        }
    }

    //dp转px
    private fun dp2px(i: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            i.toFloat(), resources.displayMetrics
        ).toInt()
    }

    /**
     * 设置图片类型:
     * imageType=0 圆形图片
     * imageType=1 圆角图片
     * 默认为圆形图片
     */
    fun setType(imageType: Int):RoundImageView {
        Log.e("TAG", "setType方法")
        if (type != imageType) {
            type = imageType
           /* if (type != TYPE_ROUND && type != TYPE_CIRCLE && type != TYPE_OVAL) {
                type = TYPE_OVAL
            }*/
            requestLayout()
        }
        return this
    }

    /**
     * 设置圆角图片的圆角大小
     */
    fun setCornerRadius(cornerRadius: Int): RoundImageView {
        var cornerRadius = dp2px(cornerRadius)
        if (xCornerRadius != cornerRadius.toFloat()) {
            xCornerRadius = cornerRadius.toFloat()
            //invalidate()
        }
        return this
    }

    /**
     * 设置圆角图片的左上圆角大小
     */
     fun setLeftTopCornerRadius(cornerRadius: Int): RoundImageView {
        var cornerRadius = cornerRadius
        cornerRadius = dp2px(cornerRadius)
        if (xLeftTopCornerRadius != cornerRadius.toFloat()) {
            xLeftTopCornerRadius = cornerRadius.toFloat()
            //invalidate()
        }
        return this
    }

    /**
     * 设置圆角图片的右上圆角大小
     */
    fun setRightTopCornerRadius(cornerRadius: Int): RoundImageView {
        var cornerRadius = cornerRadius
        cornerRadius = dp2px(cornerRadius)
        if (xRightTopCornerRadius != cornerRadius.toFloat()) {
            xRightTopCornerRadius = cornerRadius.toFloat()
            //invalidate()
        }
        return this
    }

    /**
     * 设置圆角图片的左下圆角大小
     */
    fun setLeftBottomCornerRadius(cornerRadius: Int): RoundImageView {
        var cornerRadius = cornerRadius
        cornerRadius = dp2px(cornerRadius)
        if (xLeftBottomCornerRadius != cornerRadius.toFloat()) {
            xLeftBottomCornerRadius = cornerRadius.toFloat()
            //invalidate()
        }
        return this
    }

    /**
     * 设置圆角图片的右下圆角大小
     */
    fun setRightBottomCornerRadius(cornerRadius: Int): RoundImageView {
        var cornerRadius = cornerRadius
        cornerRadius = dp2px(cornerRadius)
        if (xRightBottomCornerRadius != cornerRadius.toFloat()) {
            xRightBottomCornerRadius = cornerRadius.toFloat()
            //invalidate()
        }
        return this
    }

}

