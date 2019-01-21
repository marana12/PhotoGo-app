package com.example.julio.photogo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class ResizableImageView extends ImageView {
    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context,  attrs);
    }
    public ResizableImageView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d=getDrawable();
        if(d==null){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        return;
    }
        int imageHeight=d.getIntrinsicHeight();
        int imageWidth=d.getIntrinsicWidth();
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);
        float imageRatio =0.0F;
        if(heightSize>0){
            imageRatio=imageWidth/imageHeight;
        }
        float sizeRatio =0.0F;
        if(heightSize>0){
            sizeRatio=widthSize/heightSize;
             }
         int width;
         int heigth;
         if(imageRatio>=sizeRatio){
             width=widthSize;
             heigth=width * imageHeight / imageWidth;
              }else{
             heigth = heightSize;
             width=heigth* imageWidth/imageHeight;
         }
         setMeasuredDimension(width,heigth);

    }
}
