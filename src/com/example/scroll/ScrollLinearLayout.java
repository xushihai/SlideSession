package com.example.scroll;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.Toast;

public class ScrollLinearLayout extends ViewGroup {
	private int scrollState;
	private int STATE_REST=1;//不是滑动状态，是空闲状态，此状态下子控件可以触发触摸点击事件
	private int STATE_SCROLL=2;//滑动状态，子控件不能触发触摸点击事件，而是响应本控件的滑动处理
	private float lastX;//记录上一次的移动到的点的X坐标
	private int mScreenWidth;
	private VelocityTracker mVelocityTracker;//速度追踪器
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action=ev.getAction();
		float x=ev.getX();
		if(action==MotionEvent.ACTION_MOVE&&scrollState!=STATE_REST)//表示用户正在滑动控件且滑动距离超过了指定距离，就应该响应本控件的触摸事件
			return true;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			lastX=x;//当点击控件的时候，如果控件还没滚动完成就继续响应本控件的触摸事件，否则就响应子控件的点击事件
			scrollState=mScroller.isFinished()?STATE_REST:STATE_SCROLL;
			break;

		case MotionEvent.ACTION_MOVE:
			float diffX=Math.abs(x-lastX);
			if(diffX>100)
				scrollState=STATE_SCROLL;//只有当滑动超过指定距离后才能认为用户意图滑动这个控件，此时应该返回true不响应孩子们的点击事件，而去 响应该控件的触摸事件
			break;
		case MotionEvent.ACTION_UP://当用户松开按键的时候清除滑动状态，让子控件也可以参与响应触摸事件
			scrollState=STATE_REST;
			break;
		}
		return scrollState!=STATE_REST;//如果按下的时候滑动结束，那么状态就为STATE_REST，此时就应该返回false响应ViewGroup的孩子的点击事件，否则返回true不响应孩子们的点击事件
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x=event.getX();
		float y=event.getY();
		if(mVelocityTracker==null){
			mVelocityTracker=VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if(mScroller.isFinished()){
				mScroller.abortAnimation();
			}
			lastX=x;
			break;

		case MotionEvent.ACTION_MOVE:
			float diffX=lastX-x;
			scrollBy((int)(diffX), 0);
			lastX=x;
			break;
		
		case MotionEvent.ACTION_UP:
			mVelocityTracker.computeCurrentVelocity(1000);
			float velocityX=mVelocityTracker.getXVelocity();
			mVelocityTracker.recycle();
			mVelocityTracker=null;
			if(velocityX<-600){//这个速度方向向右为正，方向向左为负。速度的大小为1000个毫秒内移动的像素的个数
				//如果速度小于-600，那么手指移动的方向就向左，此时就应该把第二屏的内容也全显示出来，不是单独的显示一屏，而是只要第二屏的内容能全显示出来，第一屏的内容也可以显示一部分
				Toast.makeText(getContext(), "手指向左滑动，速率小于-600", Toast.LENGTH_SHORT).show();
				
				int medium=(getMeasuredWidth()-mScreenWidth)/2;
				scrollTo(medium*2,0);
				
			}else if(velocityX>600){
				//如果速度大于600，那么就是手指向右滑动，需要显示第一屏的全部内容。
				Toast.makeText(getContext(), "手指向右滑动，速率大于600", Toast.LENGTH_SHORT).show();
				int dx=-getScrollX();
//				mScroller.startScroll(getScrollX(), 0, dx, 0, Math.abs(dx)*2);
				scrollTo(0,0);
				
			}else{
				//如果速率小于600，也就是慢慢滑动的话，也分为向左滑动向右滑动，但是向左滑动的时候如果没有超过第二屏的内容的一半宽就还是显示第一屏，所以判断超过了第二屏的内容的一半没，
				//如果超过了就显示第二屏的全部内容，否则显示第一屏的全部内容
				int medium=(getMeasuredWidth()-mScreenWidth)/2;//这就是第二屏的全部内容的一半的宽度
				if(getScrollX()>medium)//如果父控件的左上角对应于该内容视图的X坐标大于是第二屏的全部内容的一半的宽度，那么就应该显示第二屏的全部内容
					scrollTo(medium*2, 0);
				else//否则显示第一屏的内容
					scrollTo(0, 0);
			}
			//控件内容滑动，打个比方就相当于控件是相框，控件内容就相当于一张很大的相片，getScrollX()就是相片的左上角所在的相片的X坐标
			//我们滑动的是相片，并不是相框。所有的参考点都是相框的左上角
			
			scrollState=STATE_REST;//滑动结束，清除滑动状态
			break;
		}
		return true;
	}
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		super.computeScroll();
		if(mScroller.computeScrollOffset()){
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}
	Scroller mScroller;
	public ScrollLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller=new Scroller(context);
		mScreenWidth=MainActivity.screenWidth;
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
			int width=0;//记录该控件下的所有的子控件的宽度，因为我们这个布局是从左往右，不换行等各个子控件紧紧相连，所以记录每个子控件的宽度（包括左右margin）就是这个控件的宽度
			int height=0;//记录该控件下的所有的子控件的高度，因为我们这个布局是从左往右，不换行等各个子控件紧紧相连，方向是水平方向，所以只要记录下所有子控件中高度最大的控件的高度（包括上下margin）就行
			for(int i=0;i<getChildCount();i++){
				View v=getChildAt(i);
				ViewGroup.LayoutParams lp=v.getLayoutParams();			
					MarginLayoutParams marginLayoutParams = (MarginLayoutParams) lp;//获取子控件的margin属性，用于计算该控件的宽高	
					measureChild(v, widthMeasureSpec, heightMeasureSpec);//测量子控件的宽高
					//使用margin属性
					int vHeight=v.getMeasuredHeight()+marginLayoutParams.topMargin+marginLayoutParams.bottomMargin;
					width+=marginLayoutParams.leftMargin+v.getMeasuredWidth()+marginLayoutParams.rightMargin;
					height=(vHeight>height)?vHeight:height;//选择高度最高的高度作为该控件的高度
					Log.i("TAG",v.getMeasuredWidth()+"   "+v.getMeasuredHeight());
					
			}
			//必须使用该方法设置控件的大小否则会抛出异常
			setMeasuredDimension(width, height);//效果类似LinearLayout的水平方向，视图内容宽度超过屏幕宽度
			Log.i("TAG",width+"   "+height);
		}
		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			int widthSum=0;
			//l是相对于父控件的左边的X位置，t是相对于父控件的上面的Y位置....，我们的子控件的范围就是【左上角(l,t) 右下角(r,b)】的矩形内
			
			//他的孩子个数是他的直接孩子的个数，那些嵌套控件不算，比如他下面有个RelativeLayout和一个TextView
			//RelativeLayout下面有三个TextView，这个ViewGroup他的孩子为2，那三个textview算他的孙子，不算孩子
			Toast.makeText(getContext(), "count:"+getChildCount(), Toast.LENGTH_SHORT).show();
			for(int i=0;i<getChildCount();i++){
				View v=getChildAt(i);
				int width=v.getMeasuredWidth();
				int height=v.getMeasuredHeight();
				ViewGroup.LayoutParams lp=v.getLayoutParams();
				
					MarginLayoutParams m=(MarginLayoutParams) lp;
					int left=l+widthSum+m.leftMargin;//计算每个子控件的左上角的X坐标
					int right=left+v.getMeasuredWidth()+m.rightMargin;//计算每个子控件的右下角的X坐标
					int top=t+m.topMargin;//计算每个子控件的左上角的Y坐标
					int bottom=top+v.getMeasuredHeight()+m.bottomMargin;//计算每个子控件的右下角的Y坐标
					v.layout(left, top, right, bottom);//设置每个子控件的位置
					Log.i("TAG",l+" "+t+" "+r+" "+b+"  "+v.getMeasuredWidth()+"   "+v.getMeasuredHeight());
					widthSum+=v.getMeasuredWidth()+m.leftMargin+m.rightMargin;
			}
		}
}
