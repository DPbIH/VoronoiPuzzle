package com.voronoi.puzzle;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import com.voronoi.puzzle.diagramimpl.Cell;
import com.voronoi.puzzle.diagramimpl.Diagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
		initDiagram();
		initPaints();
	}

	private void initDiagram() {
		diagram_ = new Diagram( this.getWidth(), this.getHeight() );
	}

	private void initPaints() {
		cellPaint_   = new Paint(Paint.ANTI_ALIAS_FLAG);
		borderPaint_ = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		cellPaint_.setColor( getResources().getColor(R.color.orange) );
		cellPaint_.setStrokeWidth( lineWidth_ );
		cellPaint_.setStyle(Style.STROKE);
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
	}
	
	protected void onSizeChanged (int w, int h, int oldw, int oldh)
	{
		diagram_.setDiagramSize(w, h);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		drawBorders(canvas);
		drawCells(canvas);
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
		if( isEraserEnabled() )
		{
			return;
		}
		
		float x = event.getX();
		float y = event.getY();
		
		selectedCell_ = diagram_.getCellByCoordinates( new PointF(x, y) );

		if( selectedCell_ == null && ( ! draggingEnabled_ ) )
		{
			Cell newCell = new Cell( x, y );
			addCell( newCell );
		}
		
		invalidate();
	}


	public void onTouchDragged(MotionEvent event)
	{
		if( isEraserEnabled() )
		{
			return;
		}
		
		float x 		= event.getX();
		float y 		= event.getY();

		int indiceCella = 0;
		
		if(draggedCell_ == null)
		{
			draggedCell_ = diagram_.selectCellForDragging( new PointF(x, y) );
			if(draggedCell_ == null)
			{
				setDraggingEnabled(false);
				return;
			}
		}
		else
		{
			diagram_.deleteCell( draggedCell_ );
			draggedCell_ = new Cell( x, y );
			
			Cell cell = diagram_.getCellByCoordinates( new PointF(x, y) );
			if( cell == null )
			{
				diagram_.addDraggedCell( draggedCell_ );
			}

			invalidate();
		}

	}

	public void onTouchReleased(MotionEvent event)
	{
		if( isEraserEnabled() )
		{
			selectedCell_ = diagram_.selectCellForDragging( new PointF( event.getX(), event.getY() ) );

			if( selectedCell_ != null )
			{
				diagram_.deleteCell(selectedCell_);
			}
		}
		else
		{
			draggedCell_ = null;
		}

		invalidate();
	}

	
	public void generatePuzzle( int piecesCount )
	{
		diagram_.generate(piecesCount);
		invalidate();
	}

	@SuppressWarnings("deprecation")
	public void SetBackgroundImage( Uri imgUri ) throws FileNotFoundException
	{
		Bitmap bm = BitmapFactory.decodeStream( getContext().getContentResolver().openInputStream(imgUri) );
		bgImg_ = new BitmapDrawable(getResources(), bm);
		setBackgroundDrawable( bgImg_ );
		
		invalidate();
	}
	
	public void resetView()
	{
		diagram_.clear();
		bgImg_ = null;
		invalidate();
	}

	public void drawCells(Canvas canvas)
	{
		Iterator<Cell> cellsIter = (Iterator<Cell>)diagram_.getCellIterator();
		while( cellsIter.hasNext() )
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
		}
	}

	public void  drawBorders(Canvas canvas)
	{
		int height = getMeasuredHeight();
		int width = getMeasuredWidth();
		
		canvas.drawRect( 0, 0, width-1, height-1, borderPaint_ );
	}

	public void setEraserEnabled(boolean on)
	{
		eraserEnabled_ = on;
	}
	public boolean isEraserEnabled()
	{
		return eraserEnabled_;
	}

	public void setDraggingEnabled(boolean on)
	{
		draggingEnabled_ = on;
	}
	
	public boolean isDraggingEnabled()
	{
		return draggingEnabled_;
	}
	
	public void addCell(Cell newCell)
	{
		Cell cell = newCell;
		diagram_.addCell(cell);
	}

	private Diagram 		diagram_;
	private BitmapDrawable 	bgImg_;
	
	private int lineWidth_ 				= 3;

	private boolean eraserEnabled_   	= false;
	private boolean draggingEnabled_ 	= false;
	
	private Cell selectedCell_ 			= null;
	private Cell draggedCell_  			= null;
	
	Paint cellPaint_;
	Paint borderPaint_;
	
}
