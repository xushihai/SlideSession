package com.example.scroll;



import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.Toast;

public class ScrollViewGroup extends ViewGroup {

private Context mContext;
	
	private static String TAG = "MultiViewGroup";
    private int curScreen = 0 ;
	private int totalScreen;
    private Scroller mScroller = null ;
    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;
    private int mTouchState = TOUCH_STATE_REST;

    public static int  SNAP_VELOCITY = 600 ;
    private int mTouchSlop = 0 ;
    private float mLastionMotionX = 0 ;
    private float mLastMotionY = 0 ;

    private VelocityTracker mVelocityTracker = null ;



	@Override
	public void computeScroll() {	
		// TODO Auto-generated method stub
		Log.e(TAG, "computeScroll");
		if (mScroller.computeScrollOffset()) {
			Log.e(TAG, mScroller.getCurrX() + "======" + mScroller.getCurrY());
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			Log.e(TAG, "### getleft is " + getLeft() + " ### getRight is " + getRight());
			postInvalidate();
		}
		else
			Log.i(TAG, "have done the scoller -----");
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onInterceptTouchEvent-slop:" + mTouchSlop);

		final int action = ev.getAction();

		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}
 
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "onInterceptTouchEvent move");
                final int xDiff = (int) Math.abs(mLastionMotionX - x);
                if (xDiff > mTouchSlop) {
                    mTouchState = TOUCH_STATE_SCROLLING;
                }
                break;

            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "onInterceptTouchEvent down");
                mLastionMotionX = x;
                mLastMotionY = y;
                Log.e(TAG, "scroller is finished" + mScroller.isFinished() + "");
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "onInterceptTouchEvent up or cancel");
                mTouchState = TOUCH_STATE_REST;
                break;
		}
		Log.e(TAG, mTouchState + "====" + TOUCH_STATE_REST);

		return mTouchState != TOUCH_STATE_REST;
	}


	public boolean onTouchEvent(MotionEvent event){
		Log.i(TAG, "--- onTouchEvent--> " );

		// TODO Auto-generated method stub
		Log.e(TAG, "onTouchEvent start");
		if (mVelocityTracker == null) {
			
			Log.e(TAG, "onTouchEvent start-------** VelocityTracker.obtain");
			
			mVelocityTracker = VelocityTracker.obtain();
		}
		
		mVelocityTracker.addMovement(event);
		super.onTouchEvent(event);
		
		float x = event.getX();
		float y = event.getY();

		switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(mScroller != null){
                    if(!mScroller.isFinished()){
                        mScroller.abortAnimation();
                    }
                }

                mLastionMotionX = x ;
                break ;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int)(mLastionMotionX - x );
                int deltaY = (int)(mLastMotionY - y);

                if (Math.abs(deltaX) < Math.abs(deltaY)) {
                    break;
                }
                scrollBy(deltaX, 0);

                Log.e(TAG, "--- MotionEvent.ACTION_MOVE--> detaX is " + deltaX );
                mLastionMotionX = x ;
                break ;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker  ;
                velocityTracker.computeCurrentVelocity(1000);
                int velocityX = (int) velocityTracker.getXVelocity() ;

                Log.e(TAG , "---velocityX---" + velocityX);

                if (velocityX > SNAP_VELOCITY && curScreen > 0) {
                	//快速向上一屏滑动
                    // Fling enough to move left
                    Log.e(TAG, "snap left");
                    snapToScreen(curScreen - 1);
                    Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
                }
                else if(velocityX < -SNAP_VELOCITY && curScreen < (totalScreen-1)){
                    Log.e(TAG, "snap right");
                  //快速向下一屏滑动
//                    snapToScreen(curScreen + 1);
                    int mudi=getMeasuredWidth()-MainActivity.screenWidth;
//                    mScroller.startScroll(mScroller.getCurrX(), 0, mudi-mScroller.getCurrX(), 0,Math.abs(mudi-mScroller.getCurrX()) * 2);
                   scrollTo(mudi, 0);
                    Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();
                }
                else{
                	
                	//慢慢向下一屏滑动
                	 int mudi=getMeasuredWidth()-MainActivity.screenWidth;
                	int bianjie=(getMeasuredWidth()-MainActivity.screenWidth)/2;//如果另一屏的视图超过一半滑动到视图中松开的时候就要滑动到另一屏视图显示完全的位置，否则滑到上一屏显示完全的位置
                	Log.i("TAG",bianjie+" bianjie   "+getScaleX());
                	//getScrollX():就是当前视图内容在视图左上角的X坐标视图是没有滚动，视图内容在滚动
                	Toast.makeText(getContext(), ""+getScrollX(), Toast.LENGTH_SHORT).show();
                	if(getScrollX()<bianjie)
                		snapToDestination();
                	else
                		
                		 scrollTo(mudi, 0);
                    Toast.makeText(getContext(), "3", Toast.LENGTH_SHORT).show();
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                mTouchState = TOUCH_STATE_REST ;
                break;
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST ;
                break;
		}
		return true ;
	}

	private void snapToDestination(){
		int scrollX = getScrollX() ;
		int scrollY = getScrollY() ;
		Log.e(TAG, "### onTouchEvent snapToDestination ### scrollX is " + scrollX);
		int destScreen = (getScrollX() + getWidth() / 2 ) / getWidth() ;
	    Log.e(TAG, "### onTouchEvent  ACTION_UP### dx destScreen " + destScreen);
		snapToScreen(destScreen);
	}

    //滑动到相应的View
    private void snapToScreen(int whichScreen){
	    curScreen = whichScreen ;
	    if(curScreen > totalScreen - 1)
	    	curScreen = totalScreen - 1 ;
	    
	    int dx = curScreen * getWidth() - getScrollX() ;
	    
	    Log.e(TAG, "### onTouchEvent  ACTION_UP### dx is " + dx);
	    
	    mScroller.startScroll(getScrollX(), 0, dx, 0, Math.abs(dx) * 2);
	    invalidate();
    }
		


	public ScrollViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		mScroller=new Scroller(context);

	}
	
/**
 * 由于需要用到MarginLayoutParams所以需要重写generateLayoutParams方法，返回MarginLayoutParams对象
 */
	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(
			ViewGroup.LayoutParams p)
	{
		return new MarginLayoutParams(p);
	}

	@Override
	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs)
	{
		return new MarginLayoutParams(getContext(), attrs);
	}

	@Override
	protected ViewGroup.LayoutParams generateDefaultLayoutParams()
	{
		return new MarginLayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
	}

	/**
	 * 负责设置子控件的测量模式和大小 根据所有子控件设置自己的宽和高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 获得它的父容器为它设置的测量模式和大小
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
		int width=0;
		int height=0;
		for(int i=0;i<getChildCount();i++){
			View v=getChildAt(i);
			ViewGroup.LayoutParams lp=v.getLayoutParams();			
				MarginLayoutParams marginLayoutParams = (MarginLayoutParams) lp;	
				measureChild(v, widthMeasureSpec, heightMeasureSpec);
				//使用margin属性
				int vHeight=v.getMeasuredHeight()+marginLayoutParams.topMargin+marginLayoutParams.bottomMargin;
				width+=marginLayoutParams.leftMargin+v.getMeasuredWidth()+marginLayoutParams.rightMargin;
				height=(vHeight>height)?vHeight:height;
				Log.i("TAG",v.getMeasuredWidth()+"   "+v.getMeasuredHeight());
				
		}
		setMeasuredDimension(width, height);//效果类似LinearLayout的水平方向，视图内容宽度超过屏幕宽度
		Log.i("TAG",width+"   "+height);
		
		if(width%MainActivity.screenWidth==0)
			totalScreen=width/MainActivity.screenWidth;
		else
			totalScreen=width/MainActivity.screenWidth+1;
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int widthSum=0;
		for(int i=0;i<getChildCount();i++){
			View v=getChildAt(i);
			int width=v.getMeasuredWidth();
			int height=v.getMeasuredHeight();
			ViewGroup.LayoutParams lp=v.getLayoutParams();
			
				MarginLayoutParams m=(MarginLayoutParams) lp;
				int left=l+widthSum+m.leftMargin;
				int right=left+v.getMeasuredWidth()+m.rightMargin;
				int top=t+m.topMargin;
				int bottom=top+v.getMeasuredHeight()+m.bottomMargin;
				v.layout(left, top, right, bottom);
				Log.i("TAG",l+" "+t+" "+r+" "+b+"  "+v.getMeasuredWidth()+"   "+v.getMeasuredHeight());
				widthSum+=v.getMeasuredWidth()+m.leftMargin+m.rightMargin;
		}
	}

}
