package com.example.myapplication.views.diyimage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import com.example.diyimagelibrary.R;


/**
 *  自定义圆角 圆形 带描边 可控制单个角度为圆角的自定义ImageView
 */

public class DIYImageView extends ImageView{

    private static final String TAG = "DIYImageView";

    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;
    private static final boolean DEFAULT_BORDER_OVERLAY = false;

    /**
     * 图片的类型，圆形or圆角
     */
    private int type;
    public static final int TYPE_CIRCLE = 0;
    public static final int TYPE_ROUND = 1;

    /***
     *  描边的颜色
     */
    private int mBorderColor = DEFAULT_BORDER_COLOR;

    /***
     *  描边的宽度
     */
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;

    /***
     *  是否有描边
     */
    private boolean mBorderOverlay = DEFAULT_BORDER_OVERLAY;

    /**
     * 圆角大小的默认值
     */
    private static final int BODER_RADIUS_DEFAULT = 5;
    /**
     * 圆角的大小
     */
    private int mBorderRadius;

    /**
     * 描边的Paint
     */
    private Paint mBorderPaint;
    /**
     * 描边的的某个角填充成正方形的Paint
     */
    private Paint mBorderOverPaint;
    /**
     * 绘图的Paint
     */
    private Paint mBitmapPaint;
    /**
     * 圆形的中心点
     */
    private Point pointCircle = new Point();
    /**
    * 圆形的半径
    */
    private int mRadius;
    /**
     * 3x3 矩阵，主要用于缩小放大
     */
    private Matrix mMatrix;
    /**
     * 渲染图像，使用图像为绘制图形着色
     */
    private BitmapShader mBitmapShader;
    /**
     * view的宽度
     */
    private int mWidth;
    /**
     * 圆角图片的范围
     */
    private RectF mRoundRect;
    /**
     * 描边圆角图片的范围
     */
    private RectF mRoundRectBorder;



    /*四个角的圆角*/
    private boolean isCornerTopLeft = true;     //左上角
    private boolean isCornerTopRight = true;    //右上角
    private boolean isCornerBottomLeft = true;  //左下角
    private boolean isCornerBottomRight = true; //右下角

    public DIYImageView(Context context) {
        super(context);
    }

    public DIYImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public DIYImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        获取自定义属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DIYImageView);
        mBorderRadius = array.getDimensionPixelSize(R.styleable.DIYImageView_diy_round_radius,
                dip2pix(BODER_RADIUS_DEFAULT,context));//圆角默认10
        type = array.getInt(R.styleable.DIYImageView_diy_image_Type,TYPE_CIRCLE);//默认圆形
        isCornerTopLeft = array.getBoolean(R.styleable.DIYImageView_diy_is_corner_top_left,true);
        isCornerTopRight = array.getBoolean(R.styleable.DIYImageView_diy_is_corner_top_right,true);
        isCornerBottomLeft = array.getBoolean(R.styleable.DIYImageView_diy_is_corner_bottom_left,true);
        isCornerBottomRight = array.getBoolean(R.styleable.DIYImageView_diy_is_corner_bottom_right,true);
//        描边
        mBorderColor = array.getColor(R.styleable.DIYImageView_diy_border_color,DEFAULT_BORDER_COLOR);
        mBorderOverlay = array.getBoolean(R.styleable.DIYImageView_diy_border_overlay,DEFAULT_BORDER_OVERLAY);
        mBorderWidth = array.getDimensionPixelSize(R.styleable.DIYImageView_diy_border_width,DEFAULT_BORDER_WIDTH);

        if (!mBorderOverlay)
        {
            mBorderWidth = 0;
        }

        array.recycle();

        mMatrix = new Matrix();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mBorderOverPaint = new Paint();
        mBorderOverPaint.setAntiAlias(true);
        mBorderOverPaint.setColor(mBorderColor);
        mBorderOverPaint.setStrokeWidth(mBorderWidth);

        super.setScaleType(ScaleType.CENTER_CROP);
    }

    /***
     *  设置是否有描边
     * @param mBorderOverlay 是否有描边
     */
    public void setmBorderOverlay(boolean mBorderOverlay) {
        this.mBorderOverlay = mBorderOverlay;
    }

    /***
     *  设置中描边宽度
     * @param mBorderWidth 描边宽度
     */
    public void setmBorderWidth(int mBorderWidth) {
        this.mBorderWidth = mBorderWidth;
    }

    /***
     * 设置描边颜色
     * @param fillColorRes 描边颜色
     */
    public void setmBorderColorRes(@ColorRes int fillColorRes) {
        setmBorderColor(ContextCompat.getColor(getContext(),fillColorRes));
    }

    /***
     * 设置描边颜色
     * @param mBorderColor 描边颜色
     */
    public void setmBorderColor(@ColorInt int mBorderColor) {
        this.mBorderColor = mBorderColor;
    }

    /***
     * 设置左上角圆角大小
     * @param cornerTopLeft 左上角圆角大小
     */
    public void setCornerTopLeft(boolean cornerTopLeft) {
        isCornerTopLeft = cornerTopLeft;
    }

    /***
     * 设置右上角圆角大小
     * @param cornerTopRight 右上角圆角大小
     */
    public void setCornerTopRight(boolean cornerTopRight) {
        isCornerTopRight = cornerTopRight;
    }

    /***
     * 设置左下角圆角大小
     * @param cornerBottomLeft  左下角圆角大小
     */
    public void setCornerBottomLeft(boolean cornerBottomLeft) {
        isCornerBottomLeft = cornerBottomLeft;
    }

    /***
     * 设置右下角圆角大小
     * @param cornerBottomRight 右下角圆角大小
     */
    public void setCornerBottomRight(boolean cornerBottomRight) {
        isCornerBottomRight = cornerBottomRight;
    }

    /***
     * 设置图片类型
     * @param type 圆形,0; 圆角 1.
     */
    public void setType(int type) {
        this.type = type;
    }

    /***
     * 设置图片圆角大小 大于0
     * @param mBorderRadius 圆角大小 大于0
     */
    public void setmBorderRadius(int mBorderRadius) {
        if (mBorderRadius<0){
            return;
        }
        this.mBorderRadius = mBorderRadius;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        Log.e(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /**
         * 如果类型是圆形，则强制改变view的宽高一致，以小值为准
         */
        if (type == TYPE_CIRCLE)
        {
            mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            pointCircle.set(mWidth / 2,mWidth / 2);
            mRadius = mWidth / 2;
            setMeasuredDimension(mWidth, mWidth);
        }

    }

    /***
     * 将dip的圆角转换为pix的圆角
     * @param dip
     * @return
     */
    private int dip2pix(int dip,Context context) {
       int radius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
               dip, context.getResources().getDisplayMetrics());
        return radius;
    }

    /**
     * 初始化BitmapShader
     */
    private void setUpShader()
    {
        Drawable drawable = getDrawable();
        if (drawable == null)
        {
            return;
        }

        if (!mBorderOverlay)
        {
            mBorderWidth=0;
        }

        // drawable转bitmap
        Bitmap bmp = drawableToBitamp(drawable);
        // 将bmp作为着色器，就是在指定区域内绘制bmp
        mBitmapShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;
        if (type == TYPE_CIRCLE)
        {
            // 拿到bitmap宽或高的小值
            int bSize = Math.min(bmp.getWidth(), bmp.getHeight());
//            scale = (mWidth * 1.0f-mBorderWidth*2)/ bSize;
            scale = (mWidth * 1.0f-mBorderWidth)/ bSize;
        } else if (type == TYPE_ROUND)
        {
            // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
//            scale = Math.max((getWidth() * 1.0f-mBorderWidth*2) / bmp.getWidth(),
//                    (getHeight()* 1.0f-mBorderWidth*2) / bmp.getHeight());
            scale = Math.max((getWidth() * 1.0f-mBorderWidth) / bmp.getWidth(),
                    (getHeight()* 1.0f-mBorderWidth) / bmp.getHeight());
        }
        // shader的变换矩阵，我们这里主要用于放大或者缩小
        mMatrix.setScale(scale, scale);

        //将图片移到中间
        float translateX =  (getWidth()-bmp.getWidth()*scale)/2;
        float translateY =  (getHeight()-bmp.getHeight()*scale)/2;
        mMatrix.postTranslate(translateX,translateY);

        // 设置变换矩阵
        mBitmapShader.setLocalMatrix(mMatrix);
        // 设置shader
        mBitmapPaint.setShader(mBitmapShader);
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitamp(Drawable drawable)
    {
        if (drawable instanceof BitmapDrawable)
        {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }


    /***
     *  绘制
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        if (getDrawable() == null)
        {
            return;
        }
        setUpShader();

        if (type == TYPE_ROUND)
        {
            canvas.drawRoundRect(mRoundRect, mBorderRadius, mBorderRadius,mBitmapPaint);
            //        图片描边
            if (mBorderWidth != 0) {
                canvas.drawRoundRect(mRoundRect,0,0,mBitmapPaint);
                canvas.drawRoundRect(mRoundRectBorder, mBorderRadius, mBorderRadius, mBorderPaint);
            }
            //哪个角不是圆角我再把你用矩形画出来,有描边无需画图片的直角，无描边再画图片直角
            if (!isCornerTopLeft) {
                canvas.drawRect(mRoundRect.left,mRoundRect.top,mRoundRect.left+mBorderRadius,mRoundRect.top+mBorderRadius,mBitmapPaint);
                //图片描边的角，画成直角
                if (mBorderWidth != 0) {
                    //第一条画横线，第二条画竖线
                    canvas.drawLine(mRoundRectBorder.left-mBorderWidth/2,mRoundRectBorder.top,mRoundRectBorder.left+mBorderRadius,mRoundRectBorder.top,mBorderOverPaint);
                    canvas.drawLine(mRoundRectBorder.left,mRoundRectBorder.top,mRoundRectBorder.left,mRoundRectBorder.top+mBorderRadius,mBorderOverPaint);
                }
            }
            if (!isCornerTopRight) {
                canvas.drawRect(mRoundRect.right - mBorderRadius,mRoundRect.top, mRoundRect.right,mRoundRect.top+mBorderRadius, mBitmapPaint);
                //图片描边的角，画成直角
                if (mBorderWidth != 0) {
                    //第一条画横线，第二条画竖线
                    canvas.drawLine(mRoundRectBorder.right-mBorderRadius,mRoundRectBorder.top,mRoundRectBorder.right+mBorderWidth/2,mRoundRectBorder.top,mBorderOverPaint);
                    canvas.drawLine(mRoundRectBorder.right,mRoundRectBorder.top,mRoundRectBorder.right,mRoundRectBorder.top+mBorderRadius,mBorderOverPaint);
                }
            }
            if (!isCornerBottomLeft) {
                canvas.drawRect(mRoundRect.left, mRoundRect.bottom - mBorderRadius,mRoundRect.left+mBorderRadius, mRoundRect.bottom, mBitmapPaint);
                //图片描边的角，画成直角
                if (mBorderWidth != 0) {
                    //第一条画横线，第二条画竖线
                    canvas.drawLine(mRoundRectBorder.left-mBorderWidth/2,mRoundRectBorder.bottom,mRoundRectBorder.left+mBorderRadius,mRoundRectBorder.bottom,mBorderOverPaint);
                    canvas.drawLine(mRoundRectBorder.left,mRoundRectBorder.bottom,mRoundRectBorder.left,mRoundRectBorder.bottom-mBorderRadius,mBorderOverPaint);
                }
            }
            if (!isCornerBottomRight) {
                canvas.drawRect(mRoundRect.right - mBorderRadius, mRoundRect.bottom - mBorderRadius, mRoundRect.right, mRoundRect.bottom, mBitmapPaint);
                //图片描边的角，画成直角
                if (mBorderWidth != 0) {
                    //第一条画横线，第二条画竖线
                    canvas.drawLine(mRoundRectBorder.right-mBorderRadius,mRoundRectBorder.bottom,mRoundRectBorder.right+mBorderWidth/2,mRoundRectBorder.bottom,mBorderOverPaint);
                    canvas.drawLine(mRoundRectBorder.right,mRoundRectBorder.bottom,mRoundRectBorder.right,mRoundRectBorder.bottom-mBorderRadius,mBorderOverPaint);
                }
            }
        } else
        {
            canvas.drawCircle(pointCircle.x, pointCircle.y, mRadius, mBitmapPaint);
            //        图片描边
            if (mBorderWidth != 0) {
                canvas.drawCircle(pointCircle.x, pointCircle.y, mRadius-mBorderWidth/2, mBorderPaint);
            }
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        // 圆角图片的范围
        if (type == TYPE_ROUND){
            mRoundRect = new RectF(0, 0, getWidth(), getHeight());
            mRoundRectBorder = new RectF(0, 0, getWidth(), getHeight());
            if (mBorderOverlay) {
                mRoundRectBorder.inset(mBorderWidth/2,mBorderWidth/2);
                mRoundRect.inset(mBorderWidth, mBorderWidth);
            }
        }
    }




}
