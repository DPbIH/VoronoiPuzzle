package com.voronoi.puzzle;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import com.voronoi.puzzle.diagramimpl.Cell;
import com.voronoi.puzzle.diagramimpl.Diagram;
import com.voronoi.puzzle.diagramimpl.Border;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class EditorView extends View 
{
	public EditorView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		init();
	}
	
	private void init()
	{
		cellPaint_   = new Paint(Paint.ANTI_ALIAS_FLAG);
		kernelPaint_ = new Paint(Paint.ANTI_ALIAS_FLAG);
		quadPaint_   = new Paint(Paint.ANTI_ALIAS_FLAG);
		borderPaint_ = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		cellPaint_.setColor( getResources().getColor(R.color.black) );
		cellPaint_.setStrokeWidth( lineWidth_ );
		cellPaint_.setStyle(Style.STROKE);
		kernelPaint_.setColor( getResources().getColor(R.color.orange) );
		kernelPaint_.setStyle(Style.FILL);
		quadPaint_.setColor( getResources().getColor(R.color.white) );
		quadPaint_.setStyle(Style.FILL);
		borderPaint_.setColor( getResources().getColor(R.color.orange) );
		borderPaint_.setStrokeWidth( 5 );
		borderPaint_.setStyle(Style.STROKE);
	}

	@Override
	protected void onMeasure(int widthSpec, int heightSpec)
	{	
		int measuredWidth = MeasureSpec.getSize(widthSpec);
		int measuredHeight = MeasureSpec.getSize(heightSpec);

		setMeasuredDimension( measuredWidth, measuredHeight);
		
		/*if(diagram_ == null) return new Dimension(600, 600);
		Dimension dim = diagram_.getSize();
		return new Dimension((int)(dim.getWidth()* zoom), (int)(dim.getHeight()* zoom));*/
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		if( bgImg_ != null )
		{
			setBackgroundDrawable( bgImg_ );
		}
		
		drawBorders(canvas);
		
		if(diagram_ == null)
		{
			return;
		}

		drawCells(canvas);

		if(draggingEnabled_)
		{
			drawQuadrettiDragged(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		return super.onTouchEvent(event);
	}
	
	public void SetDiagram( Diagram diagram )
	{
		diagram_ = diagram;
		invalidate();
	}

	public void SetBackgroundImage( Uri imgUri ) throws FileNotFoundException
	{
		Bitmap bm = BitmapFactory.decodeStream( getContext().getContentResolver().openInputStream(imgUri) );
		bgImg_ = new BitmapDrawable(getResources(), bm);
		invalidate();
	}

	public void drawCells(Canvas canvas)
	{
		Iterator<Cell> cellsIter = (Iterator<Cell>)diagram_.getCelle();
		while(cellsIter.hasNext())
		{
			Cell cell;
			ArrayList<PointF> vertexes;
			cell = (Cell)cellsIter.next();
			vertexes = (ArrayList<PointF>)cell.getVertexes();
			
			Path path = new Path();

			if( ! vertexes.isEmpty() )
			{
				path.moveTo( vertexes.get(0).x, vertexes.get(0).y);
				
				for(int i = 1; i < vertexes.size(); i++)
				{
					path.lineTo( vertexes.get(i).x, vertexes.get(i).y );
				}
				
				path.close();

				canvas.drawPath( path, cellPaint_ );
			}

			canvas.drawCircle( cell.getKernel().x, cell.getKernel().y, radius_, kernelPaint_ );
		}
	}

	public void  drawBorders(Canvas canvas)
	{
		int height = getMeasuredHeight();
		int width = getMeasuredWidth();
		
		canvas.drawRect( 0, 0, width-1, height-1, borderPaint_ );
	}


	public void drawQuadrettiDragged(Canvas canvas)
	{
		canvas.drawRect( quad_, quadPaint_ );
	}

	public void setAreaCancellabile(boolean on)
	{
		eraserEnabled_ = on;
	}
	public boolean getAreaCancellabile()
	{
		return eraserEnabled_;
	}
	public void setP(PointF punto)
	{
		p = punto;
	}

	public void setQ(PointF punto)
	{
		q = punto;
	}
	public PointF getP()
	{
		return p;
	}
	public RectF getRect()
	{
		return rect;
	}
	public void setRect(RectF rec)
	{
		rect = rec;
	}

	public void setQuad(RectF unRettDrag)
	{
		quad_ = unRettDrag;
	}
	public RectF getQuad()
	{
		return quad_;
	}
	public void setDragged(boolean on)
	{
		draggingEnabled_ = on;
	}
	public boolean getDragged()
	{
		return draggingEnabled_;
	}

	private Diagram diagram_;  

	private PointF p;
	private PointF q;
	private RectF quad_;

	private RectF rect;
	private int radius_    = 3;
	private int lineWidth_ = 2;

	private boolean eraserEnabled_   = false;
	private boolean draggingEnabled_ = false;

	private BitmapDrawable bgImg_;
	
	Paint cellPaint_;
	Paint kernelPaint_;
	Paint quadPaint_;
	Paint borderPaint_;
}
