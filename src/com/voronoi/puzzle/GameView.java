package com.voronoi.puzzle;

import java.util.ArrayList;
import java.util.Iterator;

import com.voronoi.puzzle.diagramimpl.Cell;
import com.voronoi.puzzle.diagramimpl.Diagram;
import com.voronoi.puzzle.puzzleimpl.Board;
import com.voronoi.puzzle.puzzleimpl.Tile;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View
{
	public GameView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		init();
	}

	private void init() 
	{
		initGestureDetector();
		initPaints(); 
	}

	private void initPaints() 
	{
		tilePaint_	= new Paint( Paint.ANTI_ALIAS_FLAG );
		tilePaint_.setColor( getResources().getColor(R.color.black) );
		tilePaint_.setStrokeWidth( 3 );
		tilePaint_.setPathEffect(new DashPathEffect(new float[] {10,5}, 0));
		tilePaint_.setStyle(Style.STROKE);
		
		tileHighlightedPaint_	= new Paint( Paint.ANTI_ALIAS_FLAG );
		tileHighlightedPaint_.setColor( getResources().getColor(R.color.orange) );
		tileHighlightedPaint_.setStrokeWidth( 4 );
		tileHighlightedPaint_.setStyle(Style.STROKE);
		
		cellPaint_	= new Paint(Paint.ANTI_ALIAS_FLAG);		
		cellPaint_.setColor( getResources().getColor(R.color.black) );
		cellPaint_.setStrokeWidth( 3 );
		cellPaint_.setStyle(Style.STROKE);
	}
	
	private void initGestureDetector()
	{
		gestureDetector_ = new GestureDetector( getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) 
            {
            	Tile tile = board_.getTileAtPos( toBoardPos( new PointF( e.getX(), e.getY() ) ) );
        		if( ( tile != null ) && ! tile.isOnTargetPos() )
        		{
        			tile.rotate();
            		invalidate();
        		}
                
        		return true;
            }
        });
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

		//draw diagram
		if( showDiagram_ )
		{
			drawCells( canvas );
		}
		
		drawBorders( canvas );
		drawTiles( canvas );
	}

	private void drawTiles( Canvas canvas )
	{
		for( Tile tile: board_.getTiles() )
		{
			if( tile.isOnTargetPos() )
			{
				drawTile( canvas, tile );
			}
		}
		
		for( Tile tile: board_.getTiles() )
		{
			if( ! tile.isOnTargetPos() )
			{
				drawTile( canvas, tile );
			}
		}
	}
	
	private void  drawBorders(Canvas canvas)
	{
		int height = getMeasuredHeight();
		int width = getMeasuredWidth();
		
		canvas.drawRect( 0, 0, width-1, height-1, cellPaint_ );
	}
	
	public void drawCells(Canvas canvas)
	{
		Iterator<Cell> cellsIter = (Iterator<Cell>)diagram_.getCellIterator();
		while( cellsIter.hasNext() )
		{
			Cell cell = (Cell)cellsIter.next();
			canvas.drawPath( cell.getCellPath(), cellPaint_ );
		}
	}

	private void drawTile(Canvas canvas, Tile tile)
	{
		if( tile == null )
		{
			return;
		}
		
		canvas.drawBitmap( 
				tile.getBitmap(), 
				tile.getCurrentPos().x,
				tile.getCurrentPos().y,
				tilePaint_ );
		
		if( ! tile.isOnTargetPos() )
		{
			canvas.drawPath( tile.getBordersPolygonForCurrentPos(), tile.isHighlighted() ? tileHighlightedPaint_ : tilePaint_ );
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if ( gestureDetector_.onTouchEvent(event) )
		{
			return true;
		}
		
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

	private PointF toBoardPos( PointF viewPos )
	{
		return viewPos;
	}
	
	public void onTouchPressed(MotionEvent event)
	{
		PointF boardPos = toBoardPos( new PointF( event.getX(), event.getY() ) );
		
		Tile tile = board_.getTileAtPos( boardPos );
		if( ( tile == null ) || tile.isOnTargetPos() )
		{
			return;
		}
		
		selectedTile_ = tile;
		board_.onTileSelected( selectedTile_ );
		
		moveCalculator_.setTileStartPos( selectedTile_.getCurrentPos() );
		moveCalculator_.setFingerPressedPos( boardPos );
		
		selectedTile_.setHighlighted( true );
		
		invalidate();
	}

	public void onTouchDragged(MotionEvent event)
	{
		if( selectedTile_ == null )
		{
			return;
		}
		
		PointF fingerPos = new PointF( event.getX(), event.getY() );

		selectedTile_.move( moveCalculator_.getTileNewPos( toBoardPos( fingerPos ) ) );
		if( selectedTile_.distanceToTarget() < 10 )
		{
			selectedTile_.moveToTarget();
			selectedTile_.pin();
		}
		
		if( selectedTile_.isOnTargetPos() )
		{
			// NotifyTileTargeted( selectedTile_ );
		}

		invalidate();
	}

	public void onTouchReleased(MotionEvent event)
	{
		if( selectedTile_ == null )
		{
			return;
		}
		
		selectedTile_.setHighlighted( false );
		selectedTile_ = null;
		
		invalidate();
	}
	
	private class TileMovePosCalculator
	{
		public void setTileStartPos( PointF startPos )
		{
			reset();
			tileStartPos_ = startPos;
		}
		
		public void setFingerPressedPos( PointF fingerPressPos )
		{
			reset();
			fingerStartPos_ = fingerPressPos;
		}
		
		private void reset() 
		{
			isDeltaCalculated_ = false;
		}
		
		public PointF getTileNewPos( PointF fingerCurrentPos )
		{
			PointF calculatedPos = new PointF();
			calculatedPos.x = fingerCurrentPos.x - getDelta().x;
			calculatedPos.y = fingerCurrentPos.y - getDelta().y;
			
			return calculatedPos;
		}
		
		private PointF getDelta()
		{
			if( ! isDeltaCalculated_ )
			{
				delta_ = new PointF();
				delta_.x = fingerStartPos_.x - tileStartPos_.x;
				delta_.y = fingerStartPos_.y - tileStartPos_.y;
				
				isDeltaCalculated_ = true;
			}
			
			return delta_;
		}
		
		private PointF 	tileStartPos_;
		private PointF 	fingerStartPos_;
		private PointF 	delta_;
		private boolean isDeltaCalculated_ = false;
		
	};

	public void setDiagram( Diagram diagram )
	{
		diagram_ = diagram;
		
		resize( (int) diagram_.getWidth(), (int) diagram_.getHeight() );
	}

	public void resize( int width, int height ) 
	{
		getLayoutParams().height	= height;
		getLayoutParams().width		= width;
		
		invalidate();
	}
	
	public void setImage( Bitmap bmp )
	{
		if( bmp == null )
		{
			return;
		}
		
		bgImgColor_	= new BitmapDrawable( getResources(), bmp );
		bgImgColor_.setAlpha(170);
		
		bgImgMono_	= new BitmapDrawable( getResources(), toGrayscale(bmp) );
		bgImgMono_.setAlpha(170);
	}
	
	private Bitmap toGrayscale(Bitmap bmpOriginal)
	{        
	    int width, height;
	    height = bmpOriginal.getHeight();
	    width = bmpOriginal.getWidth();    

	    Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
	    Canvas c = new Canvas(bmpGrayscale);
	    Paint paint = new Paint();
	    ColorMatrix cm = new ColorMatrix();
	    cm.setSaturation(0);
	    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
	    paint.setColorFilter(f);
	    c.drawBitmap(bmpOriginal, 0, 0, paint);
	    return bmpGrayscale;
	}
	
	public void addTile( Tile tile )
	{
		board_.addTile(tile);
	}
	
	public void removeTile( Tile tile )
	{
		board_.removeTile(tile);
	}
	
	/*public void createTiles()
	{
		tiles_.clear();
		
		if( (image_ == null) || (diagram_ == null) )
		{
			return;
		}
		
		TilesCreator tc = new TilesCreator();
		tiles_ = tc.getTiles(diagram_, image_);
	}*/

	public void reinit()
	{
		clearBoard();
	}

	private void clearBoard()
	{
		board_.clear();
	}
	
	public void showDiagram( boolean show )
	{
		showDiagram_ = show;
		
		invalidate();
	}
	
	@SuppressWarnings("deprecation")
	public void showImage( int imageShowOptions )
	{
		Drawable bgImg = null;
		if( imageShowOptions == ImageColorful )
		{
			bgImg = bgImgColor_;
		}
		else if( imageShowOptions == ImageMonochrome )
		{
			bgImg = bgImgMono_;
		}
		
		setBackgroundDrawable(bgImg);
		
		invalidate();
	}
	
	public void showTilesNumbers( boolean show )
	{
		showTilesNumbers_ = show;
		
		invalidate();
	}

	public static final int ImageNone 		= 0;
	public static final int ImageColorful 	= 1;
	public static final int ImageMonochrome = 2;
	
	private TileMovePosCalculator	moveCalculator_ 	= new TileMovePosCalculator();
	private GestureDetector 		gestureDetector_;
	private BitmapDrawable 			bgImgColor_;
	private BitmapDrawable 			bgImgMono_;
	private boolean 				showDiagram_ 		= false;
	private boolean 				showTilesNumbers_	= false;
	private Diagram 				diagram_;
	private Board					board_ 				= new Board();
	private Tile					selectedTile_;
	
	private Paint 					cellPaint_;
	private Paint 					tilePaint_;
	private Paint 					tileHighlightedPaint_;
	
}
