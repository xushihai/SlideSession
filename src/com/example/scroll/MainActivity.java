package com.example.scroll;



import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity
{
	public static int screenWidth;
//	ScrollViewGroup mll;//仿QQ会话Item的滑动删除效果
	ScrollLinearLayout mll;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		DisplayMetrics outMetrics=new DisplayMetrics();
		getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		screenWidth=outMetrics.widthPixels;
		setContentView(R.layout.text1);
//		mll=(ScrollViewGroup)findViewById(R.id.layout);
		mll=(ScrollLinearLayout)findViewById(R.id.layout);
	}
	public void delete(View v){
		
		//属性动画是真正把动画融到了对象里面，改变了对象的属性，而帧动画没有改变对象的属性
		ObjectAnimator obj=ObjectAnimator.ofFloat(mll, "X", mll.getX(),mll.getX()-mll.getMeasuredWidth());
		ObjectAnimator a=ObjectAnimator.ofFloat(mll, "alpha", 1.0f,0.0f);
		obj.setDuration(500);
		a.setDuration(500);
		obj.start();
		a.start();
		
	}
}
