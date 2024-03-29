package com.leon.carfixfactory.ui.custom;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.util.Log;

import com.leon.carfixfactory.R;

/**
 * Created by leon
 * Date: 2019/8/1
 * Time: 10:09
 * Desc:圆形点击进度条
 */
public class CircleProgressBar extends AppCompatImageButton {

    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLOR_DRAWABLE_DIMENSION = 2;

    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;
    private static final int DEFAULT_FILL_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_MAX = 100;

    private final RectF mDrawableRect = new RectF();
    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();

    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;
    private int mFillColor = DEFAULT_FILL_COLOR;

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private float mDrawableRadius;
    private ColorFilter mColorFilter;
    private Paint paint;

    /**
     * 最大进度
     */
    private int max;

    /**
     * 当前进度
     */
    private int progress;
    /**
     * 进度的风格，实心或者空心
     */
    private int style = 2;
    public static final int STROKE = 0;
    public static final int FILL = 1;
    /**
     * 进度的风格，实心从下到上
     */
    public static final int FILL_UP = 2;
    private RectF oval = new RectF();
    private int centre;
    private int radius;

    public CircleProgressBar(Context context) {
        super(context);
        init();
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, defStyle, 0);
        mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleProgressBar_civ_border_width, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.CircleProgressBar_civ_border_color, DEFAULT_BORDER_COLOR);
        max = a.getInt(R.styleable.CircleProgressBar_max, DEFAULT_MAX);
        mFillColor = a.getColor(R.styleable.CircleProgressBar_civ_fill_color, DEFAULT_FILL_COLOR);
        a.recycle();
        init();
    }

    private void init() {
        super.setScaleType(SCALE_TYPE);
        paint = new Paint();
        progress = 0;
        setup();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap == null) {
            return;
        }
        /**
         * 画最外层的大圆环
         */
        paint.setColor(mBorderColor); // 设置圆环的颜色
        if (style == FILL_UP) {
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            paint.setStyle(Paint.Style.STROKE); // 设置空心
        }
        paint.setStrokeWidth(mBorderWidth); // 设置圆环的宽度
        paint.setAntiAlias(true); // 消除锯齿
        canvas.drawCircle(centre, centre, radius, paint); // 画出圆环

        /**
         * 画圆弧 ，画圆环的进度
         */
        paint.setStrokeWidth(mBorderWidth); // 设置圆环的宽度
        paint.setColor(mFillColor); // 设置进度的颜色
        switch (style) {
            case STROKE:
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, -90, 360 * progress / max, false, paint); // 根据进度画圆弧
                break;
            case FILL:
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (progress != 0) {
                    canvas.drawArc(oval, 0, 360 * progress / max, true, paint); // 根据进度画圆弧
                }
                break;
            case FILL_UP:
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (progress != 0) {
                    int angle = 360 * progress / max;
                    if (angle / 2 < 90) {
                        angle = Math.abs(90 - angle / 2);
                    } else {
                        angle = 360 - Math.abs(90 - angle / 2);
                    }
                    canvas.drawArc(oval, angle, 360 * progress / max, false, paint);
                }
                break;
        }
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, mDrawableRadius, mBitmapPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    public void setBorderColor(@ColorInt int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }
        mBorderColor = borderColor;
        invalidate();
    }

    public void setBorderColorResource(@ColorRes int borderColorRes) {
        setBorderColor(getContext().getResources().getColor(borderColorRes));
    }

    public void setFillColor(@ColorInt int fillColor) {
        if (fillColor == mFillColor) {
            return;
        }
        mFillColor = fillColor;
        invalidate();
    }

    public void setFillColorResource(@ColorRes int fillColorRes) {
        setFillColor(getContext().getResources().getColor(fillColorRes));
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }

        mBorderWidth = borderWidth;
        setup();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setup();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        mBitmap = uri != null ? getBitmapFromDrawable(getDrawable()) : null;
        setup();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (cf == mColorFilter) {
            return;
        }
        mColorFilter = cf;
        mBitmapPaint.setColorFilter(mColorFilter);
        invalidate();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLOR_DRAWABLE_DIMENSION, COLOR_DRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setup() {
        if (getWidth() == 0 && getHeight() == 0) {
            return;
        }
        if (mBitmap == null) {
            invalidate();
            return;
        }
        centre = getWidth() / 2; // 获取圆心的x坐标
        radius = centre - mBorderWidth / 2; // 圆环的半径
        oval.set(centre - radius, centre - radius, centre
                + radius, centre + radius);// 用于定义的圆弧的形状和大小的界限

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mDrawableRect.set(oval);
        mDrawableRect.inset(mBorderWidth, mBorderWidth);
        mDrawableRadius = Math.min(mDrawableRect.height() / 2.0f, mDrawableRect.width() / 2.0f);
        updateShaderMatrix();
        postInvalidate();
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;
        mShaderMatrix.set(null);
        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }
        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，
     * 需要同步 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            this.progress = 0;
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
            Log.i("TAG", progress + "");
        }

    }

    /**
     * 设置进度风格
     * 0空心，1实心，2实心从下到上
     *
     * @param style
     */
    public void setStyle(int style) {
        this.style = style;
    }
}
