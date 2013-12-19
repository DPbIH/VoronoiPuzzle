package com.voronoi.puzzle;

import java.util.ArrayList;

import com.voronoi.puzzle.diagramimpl.Diagram;
import com.voronoi.puzzle.puzzleimpl.Board;
import com.voronoi.puzzle.puzzleimpl.Tile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
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
		int measuredWidth 	= MeasureSpec.getSize(widthSpec);
		int measuredHeight 	= MeasureSpec.getSize(heightSpec);

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

	public Diagram getDiagram()
	{
		return diagram_;
	}

	public void setDiagram( Diagram diagram )
	{
		this.diagram_ = diagram;

		float scale_ratio = getScaleRatio( getWidth(), getHeight(),
				(int)diagram_.getWidth(), (int)diagram_.getHeight() );

		diagram_.scale( scale_ratio );

		invalidate();
	}
	
	public Bitmap getImage()
	{
		return image_;
	}
	
	public void setImage( Bitmap bmp )
	{
		float scale_ratio = getScaleRatio( getWidth(), getHeight(),
				bmp.getWidth(), bmp.getHeight() );

		image_ = Bitmap.createScaledBitmap( 
				bmp,
				(int)(bmp.getWidth() * scale_ratio), 
				(int)(bmp.getHeight() * scale_ratio),
				true);

		invalidate();
	}

	private float getScaleRatio( int maxX, int maxY, int x, int y )
	{
		return Math.min( (float) maxX / x, (float) maxY / y );
	}

	public void reinit()
	{
		clearBoard();

	}

	private void clearBoard()
	{
		board_.clear();
	}

	private BitmapDrawable 	bgImgColor_;
	private BitmapDrawable 	bgImgMono_;
	private boolean 		showDiagram_;
	private boolean 		showImageColor_;
	private boolean 		showImageMono_;
	private boolean 		showTilesNumbers_;
	private Diagram 		diagram_	= null;
	private Bitmap 			image_		= null;
	private Board			board_ 		= new Board();
	private ArrayList<Tile>	tiles_ 		= new ArrayList<Tile>();

}
