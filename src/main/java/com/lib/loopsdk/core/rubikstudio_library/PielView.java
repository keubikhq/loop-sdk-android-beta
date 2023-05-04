package com.lib.loopsdk.core.rubikstudio_library;

import static com.airbnb.lottie.L.TAG;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.RequiresApi;
import androidx.core.graphics.ColorUtils;

import com.lib.loopsdk.R;

import java.util.List;
import java.util.Random;


/**
 * Created by kiennguyen on 11/5/16.
 */

public class PielView extends View {
    private RectF mRange = new RectF();
    private int mRadius;

    private Paint mArcPaint;
    private Paint mBackgroundPaint;
    private TextPaint mTextPaint;

    private float mStartAngle = 0;
    private int mCenter;
    private int mPadding;
    private int mTopTextPadding;
    private int mMiddleTextPadding;
    private int mTopTextSize;
    private int mSecondaryTextSize;
    private int mRoundOfNumber = 4;
    private int mEdgeWidth = -1;
    private boolean isRunning = false;

    private int borderColor = 0;
    private int defaultBackgroundColor = 0;
    private Drawable drawableCenterImage;
    private int textColor = 0;

    private int predeterminedNumber = -1;

    float viewRotation;
    double fingerRotation;
    long downPressTime, upPressTime;
    double newRotationStore[] = new double[3];


    private List<LuckyItem> mLuckyItemList;

    private PieRotateListener mPieRotateListener;

    public interface PieRotateListener {
        void rotateDone(int index);
    }

    public PielView(Context context) {
        super(context);
    }

    public PielView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPieRotateListener(PieRotateListener listener) {
        this.mPieRotateListener = listener;
    }

    private void init() {
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);


        if (textColor != 0) mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14,
                getResources().getDisplayMetrics()));

        mRange = new RectF(mPadding, mPadding, mPadding + mRadius, mPadding + mRadius);
    }

    public int getLuckyItemListSize() {
        return mLuckyItemList.size();
    }

    public void setData(List<LuckyItem> luckyItemList) {
        this.mLuckyItemList = luckyItemList;
        invalidate();
    }

    public void setPieBackgroundColor(int color) {
        defaultBackgroundColor = color;
        invalidate();
    }

    public void setBorderColor(int color) {
        borderColor = color;
        invalidate();
    }

    public void setTopTextPadding(int padding) {
        mTopTextPadding = padding;
        invalidate();
    }
    public void setMiddleTextPadding(int padding) {
        mMiddleTextPadding = padding;
        invalidate();
    }


    public void setPieCenterImage(Drawable drawable) {
        drawableCenterImage = drawable;
        invalidate();
    }

    public void setTopTextSize(int size) {
        mTopTextSize = size;
        invalidate();
    }

    public void setSecondaryTextSizeSize(int size) {
        mSecondaryTextSize = size;
        invalidate();
    }


    public void setBorderWidth(int width) {
        mEdgeWidth = width;
        invalidate();
    }

    public void setPieTextColor(int color) {
        textColor = color;
        invalidate();
    }

    private void drawPieBackgroundWithBitmap(Canvas canvas, Bitmap bitmap) {
        canvas.drawBitmap(bitmap, null, new Rect(mPadding / 2, mPadding / 2,
                getMeasuredWidth() - mPadding / 2,
                getMeasuredHeight() - mPadding / 2), null);
    }

    /**
     * @param canvas
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mLuckyItemList == null) {
            return;
        }

        drawBackgroundColor(canvas, defaultBackgroundColor);

        init();

        float tmpAngle = mStartAngle;
        float sweepAngle = 360f / mLuckyItemList.size();

        for (int i = 0; i < mLuckyItemList.size(); i++) {

            if (mLuckyItemList.get(i).color != 0) {
                mArcPaint.setStyle(Paint.Style.FILL);
                mArcPaint.setColor(mLuckyItemList.get(i).color);
                canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);
            }
            borderColor=Color.parseColor("#ffffff");
            if (borderColor != 0 && mEdgeWidth > 0) {
                mArcPaint.setStyle(Paint.Style.STROKE);
                mArcPaint.setColor(borderColor);
                mArcPaint.setStrokeWidth(mEdgeWidth);
                canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);
            }

            int sliceColor = mLuckyItemList.get(i).color != 0 ? mLuckyItemList.get(i).color : defaultBackgroundColor;

            if (mLuckyItemList.get(i).amountBackground != 0)
                drawXImageBackground(canvas, tmpAngle, BitmapFactory.decodeResource(getResources(), mLuckyItemList.get(i).amountBackground));

            if (!TextUtils.isEmpty(mLuckyItemList.get(i).topText))
//                drawTopText(canvas, tmpAngle, sweepAngle, mLuckyItemList.get(i).topText, sliceColor,BitmapFactory.decodeResource(getResources(),R.drawable.npill));
                if(mLuckyItemList.get(i).isSmallDevice){
                    drawTopTextSmallScreen(canvas, tmpAngle, sweepAngle, mLuckyItemList.get(i).topText, sliceColor);
                }else {
                    drawTopText(canvas, tmpAngle, sweepAngle, mLuckyItemList.get(i).topText, sliceColor);
                }

            if (!TextUtils.isEmpty(mLuckyItemList.get(i).secondaryText))

                if(mLuckyItemList.get(i).isSmallDevice){
                    drawTopBTextSmallScreen(canvas, tmpAngle, sweepAngle, mLuckyItemList.get(i).secondaryText, sliceColor);
                }else {
                    drawTopBText(canvas, tmpAngle, sweepAngle, mLuckyItemList.get(i).secondaryText, sliceColor);

                }
            if (!TextUtils.isEmpty(mLuckyItemList.get(i).crossText))
                drawCrossImage(canvas, tmpAngle, BitmapFactory.decodeResource(getResources(), R.drawable.ic_close));

            if (mLuckyItemList.get(i).icon != null)
                if(mLuckyItemList.get(i).isSmallDevice){
                    drawBBImageSmallScreen(canvas, tmpAngle, mLuckyItemList.get(i).icon);
                }else {
                    drawBBImage(canvas, tmpAngle, mLuckyItemList.get(i).icon);
                }
            if (mLuckyItemList.get(i).iconTop != 0)
                drawTImage(canvas, tmpAngle, BitmapFactory.decodeResource(getResources(), mLuckyItemList.get(i).iconTop));

            if (mLuckyItemList.get(i).iconBottom != null) {
                if(mLuckyItemList.get(i).isSmallDevice){
                    drawBImageSmallScreen(canvas, tmpAngle, mLuckyItemList.get(i).iconBottom);
                }else {
                    drawBImage(canvas, tmpAngle, mLuckyItemList.get(i).iconBottom,100,100);
                }
            }

            if (mLuckyItemList.get(i).iconCenter != 0)
                drawCImage(canvas, tmpAngle, BitmapFactory.decodeResource(getResources(),mLuckyItemList.get(i).iconCenter));

            tmpAngle += sweepAngle;
        }

//        drawCenterImage(canvas, drawableCenterImage);
    }

    private void drawBackgroundColor(Canvas canvas, int color) {
        if (color == 0)
            return;
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(color);
        canvas.drawCircle(mCenter, mCenter, mCenter - 5, mBackgroundPaint);
    }

    /**
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());

        mPadding = getPaddingLeft() == 0 ? 10 : getPaddingLeft();
        mRadius = width - mPadding * 2;

        mCenter = width / 2;

        setMeasuredDimension(width, width);
    }

    /**
     * @param canvas
     * @param tmpAngle
     * @param bitmap
     */





    private void drawImage(Canvas canvas, float tmpAngle, Bitmap bitmap) {
        /*int imgWidth = mRadius / mLuckyItemList.size();

        float angle = (float) ((tmpAngle + 360f / mLuckyItemList.size() / 2) * Math.PI / 180);

        int x = (int) (mCenter + mRadius / 2 / 1.3 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 1.3 * Math.sin(angle));

        Rect rect = new Rect(x - imgWidth / 3, y - imgWidth / 3, x + imgWidth / 3, y + imgWidth / 3);

        canvas.drawBitmap(bitmap, null,rect, null);*/
        int imgWidth = (mRadius / mLuckyItemList.size()); //- mImagePadding;
        float angle = (float) ((tmpAngle + 360 / mLuckyItemList.size() / 2) * Math.PI / 180);
        //calculate x and y
        int x = (int) (mCenter + mRadius / 2 / 1.3 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 1.3 * Math.sin(angle));
        //create arc to draw
        Rect rect = new Rect(x - imgWidth / 2 , y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);
        //rotate main bitmap
        float px = rect.exactCenterX();
        float py = rect.exactCenterY();
        Matrix matrix = new Matrix();
        matrix.postTranslate(-bitmap.getWidth() / 2f,  -bitmap.getHeight() / 1.3f);
        matrix.postRotate(tmpAngle + 124);
        matrix.postTranslate(px, py);
        canvas.drawBitmap(bitmap, matrix, new Paint( Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG ));
        matrix.reset();
    }

    private void drawCrossImage(Canvas canvas, float tmpAngle, Bitmap bitmap) {


        bitmap  = getResizedBitmap(bitmap,25,25);
        int imgWidth = (mRadius / mLuckyItemList.size()); //- mImagePadding;
        float angle = (float) ((tmpAngle + 360 / mLuckyItemList.size() / 2) * Math.PI / 180);
        //calculate x and y
        int x = (int) (mCenter + mRadius / 2 / 1.37* Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 1.37 * Math.sin(angle));
        //create arc to draw
        Rect rect = new Rect(x - imgWidth / 2 , y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);
        //rotate main bitmap
        float px = rect.exactCenterX();
        float py = rect.exactCenterY();
        Matrix matrix = new Matrix();
        matrix.postTranslate(-bitmap.getWidth() / 2f,  -bitmap.getHeight() / 1.3f);
        matrix.postRotate(tmpAngle + 124);
        matrix.postTranslate(px, py);
        canvas.drawBitmap(bitmap, matrix, new Paint( Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG ));
        matrix.reset();
    }



    private void drawCImage(Canvas canvas, float tmpAngle, Bitmap bitmap) {

        int imgWidth = (mRadius / mLuckyItemList.size()); //- mImagePadding;
        float angle = (float) ((tmpAngle + 360 / mLuckyItemList.size() / 2) * Math.PI / 180);
        //calculate x and y
        int x = (int) (mCenter + mRadius / 2 / 1.3 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 1.3 * Math.sin(angle));
        //create arc to draw
        Rect rect = new Rect(x - imgWidth /2 , y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);
        //rotate main bitmap
        float px = rect.exactCenterX();
        float py = rect.exactCenterY();
        Matrix matrix = new Matrix();
        matrix.postTranslate(-bitmap.getWidth() / 2f,  -bitmap.getHeight() / 2f);
        matrix.postRotate(tmpAngle + 124);
        matrix.postTranslate(px, py);
        canvas.drawBitmap(bitmap, matrix, new Paint( Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG ));
        matrix.reset();

    }
    private void drawTImage(Canvas canvas, float tmpAngle, Bitmap bitmap) {

        int imgWidth = (mRadius / mLuckyItemList.size()); //- mImagePadding;
        float angle = (float) ((tmpAngle + 360 / mLuckyItemList.size() / 2) * Math.PI / 180);
        //calculate x and y
        int x = (int) (mCenter + mRadius / 2 / 1.15 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 1.15 * Math.sin(angle));
        //create arc to draw
        Rect rect = new Rect(x - imgWidth /2 , y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);
        //rotate main bitmap
        float px = rect.exactCenterX();
        float py = rect.exactCenterY();
        Matrix matrix = new Matrix();
        matrix.postTranslate(-bitmap.getWidth() / 2f,  -bitmap.getHeight() / 2f);
        matrix.postRotate(tmpAngle + 124);
        matrix.postTranslate(px, py);
        canvas.drawBitmap(bitmap, matrix, new Paint( Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG ));
        matrix.reset();

    }
    private void drawXImageBackground(Canvas canvas, float tmpAngle, Bitmap bitmap) {
        int imgWidth = (mRadius / mLuckyItemList.size()); //- mImagePadding;
        float angle = (float) ((tmpAngle + 360 / mLuckyItemList.size() / 2) * Math.PI / 180);
        //calculate x and y
        int x = (int) (mCenter + mRadius / 2 / 1.4 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 1.4 * Math.sin(angle));
        //create arc to draw
        Rect rect = new Rect(x - imgWidth /2 , y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);
        //rotate main bitmap
        float px = rect.exactCenterX();
        float py = rect.exactCenterY();
        Matrix matrix = new Matrix();
        matrix.postTranslate(-bitmap.getWidth() / 2f,  -bitmap.getHeight() / 2f);
        matrix.postRotate(tmpAngle + 124);
        matrix.postTranslate(px, py);
        canvas.drawBitmap(bitmap, matrix, new Paint( Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG ));
        matrix.reset();

    }
    private void drawBImage(Canvas canvas, float tmpAngle, Bitmap bitmap,int width,int height) {
        bitmap = getResizedBitmap(bitmap,width,height);


        int imgWidth = (mRadius / mLuckyItemList.size()); //- mImagePadding;
        float angle = (float) ((tmpAngle + 360 / mLuckyItemList.size() / 2) * Math.PI / 180);
        //calculate x and y
        int x = (int) (mCenter + mRadius / 2 / 1.6 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 1.6 * Math.sin(angle));
        //create arc to draw
        Rect rect = new Rect(x - imgWidth /2 , y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);
        //rotate main bitmap
        float px = rect.exactCenterX();
        float py = rect.exactCenterY();
        Matrix matrix = new Matrix();
        matrix.postTranslate(-bitmap.getWidth() / 2f,  -bitmap.getHeight() / 2f);
        matrix.postRotate(tmpAngle + 124);
        matrix.postTranslate(px, py);
        canvas.drawBitmap(bitmap, matrix, new Paint( Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG ));
        matrix.reset();

    }
    private void drawBImageSmallScreen(Canvas canvas, float tmpAngle, Bitmap bitmap) {
        bitmap = getResizedBitmap(bitmap,55,55);


        int imgWidth = (mRadius / mLuckyItemList.size()); //- mImagePadding;
        float angle = (float) ((tmpAngle + 360 / mLuckyItemList.size() / 2) * Math.PI / 180);
        //calculate x and y
        int x = (int) (mCenter + mRadius / 2 / 1.6 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 1.6 * Math.sin(angle));
        //create arc to draw
        Rect rect = new Rect(x - imgWidth /2 , y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);
        //rotate main bitmap
        float px = rect.exactCenterX();
        float py = rect.exactCenterY();
        Matrix matrix = new Matrix();
        matrix.postTranslate(-bitmap.getWidth() / 2f,  -bitmap.getHeight() / 2f);
//        matrix.postRotate(tmpAngle + 124);
        Log.d("", "drawBImageSmallScreen: "+mLuckyItemList.size());
        if(mLuckyItemList.size()==3){
            matrix.postRotate(tmpAngle + 200);
        }else{
            matrix.postRotate(tmpAngle + 124);
        }
        matrix.postTranslate(px, py);
        canvas.drawBitmap(bitmap, matrix, new Paint( Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG ));
        matrix.reset();

    }

    private void drawBBImage(Canvas canvas, float tmpAngle, Bitmap bitmap) {
        bitmap = getResizedBitmap(bitmap,100,100);

        int imgWidth = (mRadius / mLuckyItemList.size()); //- mImagePadding;
        float angle = (float) ((tmpAngle + 360 / mLuckyItemList.size() / 2) * Math.PI / 180);
        //calculate x and y
        int x = (int) (mCenter + mRadius / 2 /  1.4 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 /  1.4 * Math.sin(angle));
        //create arc to draw
        Rect rect = new Rect(x - imgWidth /2 , y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);
        //rotate main bitmap
        float px = rect.exactCenterX();
        float py = rect.exactCenterY();
        Matrix matrix = new Matrix();
        matrix.postTranslate(-bitmap.getWidth() / 2f,  -bitmap.getHeight() / 2f);
        Log.d("", "drawBBImage: "+mLuckyItemList.size());

        if(mLuckyItemList.size()==3){
            matrix.postRotate(tmpAngle + 150);
        }else{
            matrix.postRotate(tmpAngle + 120);
        }
        matrix.postTranslate(px, py);
        canvas.drawBitmap(bitmap, matrix, new Paint( Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG ));
        matrix.reset();

    }
    private void drawBBImageSmallScreen(Canvas canvas, float tmpAngle, Bitmap bitmap) {

        bitmap = getResizedBitmap(bitmap,60,60);

        int imgWidth = (mRadius / mLuckyItemList.size()); //- mImagePadding;
        float angle = (float) ((tmpAngle + 360 / mLuckyItemList.size() / 2) * Math.PI / 180);
        //calculate x and y
        int x = (int) (mCenter + mRadius / 2 / 1.4* Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 /  1.4 * Math.sin(angle));
        //create arc to draw
        Rect rect = new Rect(x - imgWidth /2 , y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);
        //rotate main bitmap
        float px = rect.exactCenterX();
        float py = rect.exactCenterY();
        Matrix matrix = new Matrix();
        matrix.postTranslate(-bitmap.getWidth() / 2f,  -bitmap.getHeight() / 2f);
        matrix.postRotate(tmpAngle + 120);
        matrix.postTranslate(px, py);
        canvas.drawBitmap(bitmap, matrix, new Paint( Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG ));
        matrix.reset();

    }


    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP

        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
//        if (bm != null && !bm.isRecycled()) {
//            bm.recycle();
//            bm = null;
//        }
        return resizedBitmap;
    }
    private void drawCenterImage(Canvas canvas, Drawable drawable) {
        Bitmap bitmap = LuckyWheelUtils.drawableToBitmap(drawable);
        bitmap = Bitmap.createScaledBitmap(bitmap, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), false);
        canvas.drawBitmap(bitmap, getMeasuredWidth() / 2 - bitmap.getWidth() / 2,
                getMeasuredHeight() / 2 - bitmap.getHeight() / 2, null);
    }

    private boolean isColorDark(int color) {
        double colorValue = ColorUtils.calculateLuminance(color);
        double compareValue = 1d;
        return colorValue < compareValue;
    }


    /**
     * @param canvas
     * @param tmpAngle
     * @param sweepAngle
     * @param mStr
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawTopText(Canvas canvas, float tmpAngle, float sweepAngle, String mStr, int backgroundColor, Bitmap bitmap) {

        if(mStr.length() == 1){
            bitmap = Bitmap.createScaledBitmap(bitmap, 35, 35, true);

        }else if(mStr.length() == 2){
            bitmap = Bitmap.createScaledBitmap(bitmap, 45, 35, true);

        }else if(mStr.length() == 3){
            bitmap = Bitmap.createScaledBitmap(bitmap, 55, 35, true);

        }else if(mStr.length() == 4){
            bitmap = Bitmap.createScaledBitmap(bitmap, 70, 35, true);

        }else if(mStr.length() == 5){
            bitmap = Bitmap.createScaledBitmap(bitmap, 85, 35, true);

        }else if(mStr.length() == 6){
            bitmap = Bitmap.createScaledBitmap(bitmap, 105, 35, true);

        }else if(mStr.length() == 7){
            bitmap = Bitmap.createScaledBitmap(bitmap, 120, 35, true);

        }else if(mStr.length() == 8){
            bitmap = Bitmap.createScaledBitmap(bitmap, 135, 35, true);

        }else if(mStr.length() == 9){
            bitmap = Bitmap.createScaledBitmap(bitmap, 150, 35, true);

        }else if(mStr.length() == 10){
            bitmap = Bitmap.createScaledBitmap(bitmap, 165, 35, true);

        }

        int imgWidth = (mRadius / mLuckyItemList.size()); //- mImagePadding;
        float angle = (float) ((tmpAngle + 360 / mLuckyItemList.size() / 2) * Math.PI / 180);
        //calculate x and y
        int x = (int) (mCenter + mRadius / 2 / 1.5* Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 1.5 * Math.sin(angle));
        //create arc to draw
        Rect rect = new Rect(x - imgWidth / 2 , y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);
        //rotate main bitmap

        float px = rect.exactCenterX();
        float py = rect.exactCenterY();
        Matrix matrix = new Matrix();
        matrix.postTranslate(-bitmap.getWidth() / 2f,  -bitmap.getHeight() / 6f);
        matrix.postRotate(tmpAngle + 124);
        matrix.postTranslate(px, py);

        canvas.drawBitmap(bitmap, matrix, new Paint( Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG ));
        matrix.reset();




        Path path = new Path();
        path.addArc(mRange, tmpAngle, sweepAngle);

        if (textColor == 0)
            mTextPaint.setColor(isColorDark(backgroundColor) ? 0xffffffff : 0xff000000);

        Typeface typeface = Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD);
//        Typeface typeface=Typeface.createFromAsset(getContext().getAssets(),
//                "fonts/montserrat_semi.ttf");
        mTextPaint.setTypeface(typeface);
        mTextPaint.setLetterSpacing(0.3f);
        mTextPaint.setTextSize(mTopTextSize);
        float textWidth = mTextPaint.measureText(mStr);
        int hOffset = (int) (mRadius * Math.PI / mLuckyItemList.size() / 2 - textWidth / 2);

        // '+ 15' will bring the text downwards
        int vOffset = (mRadius / 2 / 3) + 20;

        canvas.drawTextOnPath(mStr, path, hOffset, vOffset, mTextPaint);


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawCrossText(Canvas canvas, float tmpAngle, float sweepAngle, String mStr, int backgroundColor) {
        Path path = new Path();
        path.addArc(mRange, tmpAngle, sweepAngle);

        if (textColor == 0)
            mTextPaint.setColor(isColorDark(backgroundColor) ? 0xffffffff : 0xff000000);

        Typeface typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD);
        mTextPaint.setTypeface(typeface);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setLetterSpacing(0.1f);
        mTextPaint.setTextSize(mTopTextSize);
        float textWidth = mTextPaint.measureText(mStr);
        int hOffset = (int) (mRadius * Math.PI / mLuckyItemList.size() / 2 - textWidth / 2);

        // '- 15' will bring the text upwards
        int vOffset = (mRadius / 2 / 3) - 15;

        canvas.drawTextOnPath(mStr, path, hOffset, vOffset, mTextPaint);

    }




    /**
     * @param canvas
     * @param tmpAngle
     * @param mStr
     * @param backgroundColor
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawSecondaryText(Canvas canvas, float tmpAngle, String mStr, int backgroundColor) {
        canvas.save();
        int arraySize = mLuckyItemList.size();

        if (textColor == 0)
            mTextPaint.setColor(isColorDark(backgroundColor) ? 0xffffffff : 0xff000000);

        Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        mTextPaint.setTypeface(typeface);
        mTextPaint.setTextSize(mSecondaryTextSize);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setLetterSpacing(0.1f);
        float textWidth = mTextPaint.measureText(mStr);

        float initFloat = (tmpAngle + 360f / arraySize / 2);
        float angle = (float) (initFloat * Math.PI / 180);

        int x = (int) (mCenter + mRadius / 2 / 2 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 2 * Math.sin(angle));

        RectF rect = new RectF(x + textWidth, y,
                x - textWidth, y);

        Path path = new Path();
        path.addRect(rect, Path.Direction.CW);
        path.close();
        canvas.rotate(initFloat + (arraySize / 36f), x, y);
        canvas.drawTextOnPath(mStr, path, mTopTextPadding / 7f, mTextPaint.getTextSize() / 2.75f, mTextPaint);
        canvas.restore();
    }
    private void drawTopBTextSmallScreen(Canvas canvas, float tmpAngle, float sweepAngle, String mStr, int backgroundColor) {
        Path path = new Path();
        path.addArc(mRange, tmpAngle, sweepAngle);

        if (textColor == 0)
            mTextPaint.setColor(isColorDark(backgroundColor) ? 0xffffffff : 0xff000000);

//        Typeface typeface=Typeface.createFromAsset(getContext().getAssets(),
//                "font/montserrat_semi.ttf");
        Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        mTextPaint.setTypeface(typeface);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setLetterSpacing(0.25f);
        mTextPaint.setTextSize(mTopTextSize);
        float textWidth = mTextPaint.measureText(mStr);
        int hOffset = (int) (mRadius * Math.PI / mLuckyItemList.size() / 2 - textWidth / 2);

//        int vOffset = mTopTextPadding;
        // '- 15' will bring the text upwards
        int vOffset = (mTopTextPadding / 2 / 3) +90;
        canvas.drawTextOnPath(mStr, path, hOffset, vOffset, mTextPaint);
    }

    private void drawTopBText(Canvas canvas, float tmpAngle, float sweepAngle, String mStr, int backgroundColor) {
        Path path = new Path();
        path.addArc(mRange, tmpAngle, sweepAngle);

        if (textColor == 0)
            mTextPaint.setColor(isColorDark(backgroundColor) ? 0xffffffff : 0xff000000);

//        Typeface typeface=Typeface.createFromAsset(getContext().getAssets(),
//                "font/montserrat_semi.ttf");
        Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        mTextPaint.setTypeface(typeface);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setLetterSpacing(0.25f);
        mTextPaint.setTextSize(mTopTextSize);
        float textWidth = mTextPaint.measureText(mStr);
        int hOffset = (int) (mRadius * Math.PI / mLuckyItemList.size() / 2 - textWidth / 2);

//        int vOffset = mTopTextPadding;
        // '- 15' will bring the text upwards
        int vOffset = (mTopTextPadding / 2 / 3) +135;
        canvas.drawTextOnPath(mStr, path, hOffset, vOffset, mTextPaint);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawTopText(Canvas canvas, float tmpAngle, float sweepAngle, String mStr, int backgroundColor) {
        Path path = new Path();
        path.addArc(mRange, tmpAngle, sweepAngle);

        if (textColor == 0)
            mTextPaint.setColor(isColorDark(backgroundColor) ? 0xffffffff : 0xff000000);

//        Typeface typeface=Typeface.createFromAsset(getContext().getAssets(),
//                "font/montserrat_semi.ttf");
        Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        mTextPaint.setTypeface(typeface);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setLetterSpacing(0.1f);
        mTextPaint.setTextSize(mTopTextSize);
        float textWidth = mTextPaint.measureText(mStr);
        int hOffset = (int) (mRadius * Math.PI / mLuckyItemList.size() / 2 - textWidth / 2);

//        int vOffset = mTopTextPadding;
        // '- 15' will bring the text upwards
        int vOffset = (mTopTextPadding / 2 / 3) +80;

        canvas.drawTextOnPath(mStr, path, hOffset, vOffset, mTextPaint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawTopTextSmallScreen(Canvas canvas, float tmpAngle, float sweepAngle, String mStr, int backgroundColor) {
        Path path = new Path();
        path.addArc(mRange, tmpAngle, sweepAngle);

        if (textColor == 0)
            mTextPaint.setColor(isColorDark(backgroundColor) ? 0xffffffff : 0xff000000);

        Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        mTextPaint.setTypeface(typeface);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setLetterSpacing(0.1f);
        mTextPaint.setTextSize(mTopTextSize);
        float textWidth = mTextPaint.measureText(mStr);
        int hOffset = (int) (mRadius * Math.PI / mLuckyItemList.size() / 2 - textWidth / 2);

//        int vOffset = mTopTextPadding;
        // '- 15' will bring the text upwards
        int vOffset = (mTopTextPadding / 2 / 3) +55;

        canvas.drawTextOnPath(mStr, path, hOffset, vOffset, mTextPaint);
    }

    /**
     * @return
     */
    private float getAngleOfIndexTarget(int index) {
        if (mLuckyItemList == null) return  0f;
        return (360f / mLuckyItemList.size()) * index;
    }

    /**
     * @param numberOfRound
     */
    public void setRound(int numberOfRound) {
        mRoundOfNumber = numberOfRound;
    }


    public void setPredeterminedNumber(int predeterminedNumber) {
        this.predeterminedNumber = predeterminedNumber;
    }

    public void rotateTo(final int index) {
        Random rand = new Random();
        rotateTo(index, (rand.nextInt() * 3) % 2, true);
    }

    /**
     * @param index
     * @param rotation,  spin orientation of the wheel if clockwise or counterclockwise
     * @param startSlow, either animates a slow start or an immediate turn based on the trigger
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    public void rotateTo(final int index, @SpinRotation final int rotation, boolean startSlow) {
        if (isRunning) {
            return;
        }

        int rotationAssess = rotation <= 0 ? 1 : 1;

        //If the staring position is already off 0 degrees, make an illusion that the rotation has smoothly been triggered.
        // But this inital animation will just reset the position of the circle to 0 degreees.
        if (getRotation() != 0.0f) {
            setRotation(getRotation() % 360f);
            TimeInterpolator animationStart = startSlow ? new AccelerateInterpolator() : new LinearInterpolator();
            //The multiplier is to do a big rotation again if the position is already near 360.
            float multiplier = getRotation() > 200f ? 2 : 1;
            animate()
                    .setInterpolator(animationStart)
                    .setDuration(500L)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            isRunning = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            isRunning = false;
                            setRotation(0);
                            rotateTo(index, rotation, false);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    })
                    .rotation(360f * multiplier * rotationAssess)
                    .start();
            return;
        }

        // This addition of another round count for counterclockwise is to simulate the perception of the same number of spin
        // if you still need to reach the same outcome of a positive degrees rotation with the number of rounds reversed.
        if (rotationAssess < 0) mRoundOfNumber++;

        float targetAngle = ((360f * mRoundOfNumber * rotationAssess) + 270f - getAngleOfIndexTarget(index) - (360f / mLuckyItemList.size()) / 2);
        animate()
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(mRoundOfNumber * 1000 + 900L)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        isRunning = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isRunning = false;
                        setRotation(getRotation() % 360f);
                        if (mPieRotateListener != null) {
                            mPieRotateListener.rotateDone(index);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                })
                .rotation(targetAngle)
                .start();
    }

    public boolean touchEnabled = true;

    public boolean isTouchEnabled() {
        return touchEnabled;
    }

    public void setTouchEnabled(boolean touchEnabled) {
        this.touchEnabled = touchEnabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isRunning || !touchEnabled) {
            return false;
        }

        final float x = event.getX();
        final float y = event.getY();

        final float xc = getWidth() / 2.0f;
        final float yc = getHeight() / 2.0f;

        double newFingerRotation;


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                viewRotation = (getRotation() + 360f) % 360f;
                fingerRotation = Math.toDegrees(Math.atan2(x - xc, yc - y));
                downPressTime = event.getEventTime();
                return true;
            case MotionEvent.ACTION_MOVE:
                newFingerRotation = Math.toDegrees(Math.atan2(x - xc, yc - y));

                if (isRotationConsistent(newFingerRotation)) {
                    setRotation(newRotationValue(viewRotation, fingerRotation, newFingerRotation));
                }
                return true;
            case MotionEvent.ACTION_UP:
                newFingerRotation = Math.toDegrees(Math.atan2(x - xc, yc - y));
                float computedRotation = newRotationValue(viewRotation, fingerRotation, newFingerRotation);

                fingerRotation = newFingerRotation;

                // This computes if you're holding the tap for too long
                upPressTime = event.getEventTime();
                if (upPressTime - downPressTime > 700L) {
                    // Disregarding the touch since the tap is too slow
                    return true;
                }

                // These operators are added so that fling difference can be evaluated
                // with usually numbers that are only around more or less 100 / -100.
                if (computedRotation <= -250f) {
                    computedRotation += 360f;
                } else if (computedRotation >= 250f) {
                    computedRotation -= 360f;
                }

                double flingDiff = computedRotation - viewRotation;
                if (flingDiff >= 200 || flingDiff <= -200) {
                    if (viewRotation <= -50f) {
                        viewRotation += 360f;
                    } else if (viewRotation >= 50f) {
                        viewRotation -= 360f;
                    }
                }

                flingDiff = computedRotation - viewRotation;

                if (flingDiff <= -60 ||
                        //If you have a very fast flick / swipe, you an disregard the touch difference
                        (flingDiff < 0 && flingDiff >= -59 && upPressTime - downPressTime <= 200L)) {
                    if (predeterminedNumber > -1) {
                        rotateTo(predeterminedNumber, SpinRotation.CLOCKWISE, false);
                    } else {
                        rotateTo(predeterminedNumber, SpinRotation.CLOCKWISE, false);
//                        rotateTo(getFallBackRandomIndex(), SpinRotation.CLOCKWISE, false);
                    }
                }

                if (flingDiff >= 60 ||
                        //If you have a very fast flick / swipe, you an disregard the touch difference
                        (flingDiff > 0 && flingDiff <= 59 && upPressTime - downPressTime <= 200L)) {
                    if (predeterminedNumber > -1) {
                        rotateTo(predeterminedNumber, SpinRotation.CLOCKWISE, false);
                    } else {
                        rotateTo(predeterminedNumber, SpinRotation.CLOCKWISE, false);

//                        rotateTo(getFallBackRandomIndex(), SpinRotation.CLOCKWISE, false);
                    }
                }

                return true;
        }
        return super.onTouchEvent(event);
    }

    private float newRotationValue(final float originalWheenRotation, final double originalFingerRotation, final double newFingerRotation) {
        double computationalRotation = newFingerRotation - originalFingerRotation;
        return (originalWheenRotation + (float) computationalRotation + 360f) % 360f;
    }

    private int getFallBackRandomIndex() {
        Random rand = new Random();
        return rand.nextInt(mLuckyItemList.size() - 1) + 0;
    }

    /**
     * This detects if your finger movement is a result of an actual raw touch event of if it's from a view jitter.
     * This uses 3 events of rotation temporary storage so that differentiation between swapping touch events can be determined.
     *
     * @param newRotValue
     */
    private boolean isRotationConsistent(final double newRotValue) {
        double evalValue = newRotValue;

        if (Double.compare(newRotationStore[2], newRotationStore[1]) != 0) {
            newRotationStore[2] = newRotationStore[1];
        }
        if (Double.compare(newRotationStore[1], newRotationStore[0]) != 0) {
            newRotationStore[1] = newRotationStore[0];
        }

        newRotationStore[0] = evalValue;

        if (Double.compare(newRotationStore[2], newRotationStore[0]) == 0
                || Double.compare(newRotationStore[1], newRotationStore[0]) == 0
                || Double.compare(newRotationStore[2], newRotationStore[1]) == 0

                //Is the middle event the odd one out
                || (newRotationStore[0] > newRotationStore[1] && newRotationStore[1] < newRotationStore[2])
                || (newRotationStore[0] < newRotationStore[1] && newRotationStore[1] > newRotationStore[2])
        ) {
            return false;
        }
        return true;
    }


//    @IntDef({
//            SpinRotation.CLOCKWISE,
//            SpinRotation.COUNTERCLOCKWISE
//    })
    @interface SpinRotation {
        int CLOCKWISE = 0;
        int COUNTERCLOCKWISE = 1;
    }
}
