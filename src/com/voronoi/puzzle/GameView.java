package com.voronoi.puzzle;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View
{
	public GameView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
	}
	
	@Override
	protected void onMeasure(int widthSpec, int heightSpec)
	{	
		int measuredWidth = MeasureSpec.getSize(widthSpec);
		int measuredHeight = MeasureSpec.getSize(heightSpec);

		setMeasuredDimension( measuredWidth, measuredHeight);
	}
	
	protected void onSizeChanged (int w, int h, int oldw, int oldh)
	{
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		int eventaction = event.getAction();

		switch (eventaction) {
		case MotionEvent.ACTION_DOWN: 
			// finger touches the screen
			onTouchPressed(event);
			break;

		case MotionEvent.ACTION_MOVE:
			// finger moves on the screen
			onTouchDragged(event);
			break;

		case MotionEvent.ACTION_UP:   
			// finger leaves the screen
			onTouchReleased(event);
			break;
		}

		return true; 
	}
	
	public void onTouchPressed(MotionEvent event)
	{	
		invalidate();
	}


	public void onTouchDragged(MotionEvent event)
	{
		invalidate();
	}

	public void onTouchReleased(MotionEvent event)
	{

		invalidate();
	}

}
