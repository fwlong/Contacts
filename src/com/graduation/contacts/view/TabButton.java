package com.graduation.contacts.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;

public class TabButton extends Button {
	
	Context mContext;
	Drawable mIconDrawable;
	Bitmap mIconBitmap;
	String iconStr;
	
	Paint mPaint;
	int textHeight;
	int textWidth;
	int drawableHeight;
	int drawableWidth;

	public TabButton(Context context) {
		this(context,null);
	}
	
	public TabButton(Context context,AttributeSet attr){
		super(context,attr);
		init();
		
	}
	
	public void setIcon(int resId){
		mIconDrawable = getResources().getDrawable(resId);
		mIconBitmap = drawable2Bitmap(mIconDrawable);
		initDrawableRect(mIconBitmap);
		this.invalidate();
	}
	
	public void setIcon(Drawable drawable){
		this.mIconDrawable = drawable;
		mIconBitmap = drawable2Bitmap(drawable);
		initDrawableRect(mIconBitmap);
		this.invalidate();
	}
	
	public Drawable getIcon(){
		return mIconDrawable;
	}
	
	public void setText(String str){
		this.iconStr = str;
		initTextRect(mPaint, str);
		this.invalidate();
	}
	
	public String getText(){
		return iconStr;
	}
	
	private void init(){
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(18);
		mPaint.setTextAlign(Align.CENTER);
		
	}
	
	private Bitmap drawable2Bitmap(Drawable mDrawable){
		int width = mDrawable.getIntrinsicWidth();
		int height = mDrawable.getIntrinsicWidth();
		Bitmap.Config config =Config.ARGB_8888;
		Bitmap bitmap = Bitmap.createBitmap(width, height, config);
		Canvas ca = new Canvas(bitmap);
		mDrawable.setBounds(0, 0, width, height);
		mDrawable.draw(ca);
		return bitmap;
	}
	
	private void initDrawableRect(Bitmap mBitmap){
		drawableWidth = mBitmap.getWidth();
		drawableHeight = mBitmap.getHeight();
	}
	
	private void initTextRect(Paint mPaint,String text){
		if(mPaint==null||text==null){
			return;
		}else{
			Rect textBonds = new Rect();
			mPaint.getTextBounds(text, 0, text.length(), textBonds);
			textWidth = textBonds.width();
			textHeight = textBonds.height();
		}
	}

	@Override
	public void draw(Canvas canvas) {
		int mTotalHeight = drawableHeight+textHeight;
		if(iconStr!=null){
			int startH = (this.getHeight()+mTotalHeight)/2;
			int startW = this.getWidth()/2;
			
			canvas.drawText(iconStr, startW, startH, mPaint);
		}
		
		if(mIconBitmap!=null){
			int startH = (this.getHeight()-mTotalHeight)/2;
			int startW = (this.getWidth()-drawableWidth)/2;
			canvas.drawBitmap(mIconBitmap, startW, startH, new Paint());
		}
		
		
		super.draw(canvas);
	}
	
	

}
