package com.voronoi.puzzle;

import java.util.ArrayList;

import com.voronoi.puzzle.diagramimpl.Diagram;
import com.voronoi.puzzle.puzzleimpl.Board;
import com.voronoi.puzzle.puzzleimpl.Tile;
import com.voronoi.puzzle.puzzleimpl.TileHandler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
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
		tilePaint_ = new Paint( Paint.ANTI_ALIAS_FLAG );
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

		drawTiles( canvas );
	}

	private void drawTiles( Canvas canvas )
	{
		for( Tile tile: board_.getTiles() )
		{
			drawTile( canvas, tile );
		}
		
	}

	private void drawTile(Canvas canvas, Tile tile)
	{
		canvas.drawBitmap( 
				tile.getBitmap(), 
				tile.getCurrentPos().x,
				tile.getCurrentPos().y,
				tilePaint_ );
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

	private PointF toBoardPos( PointF viewPos )
	{
		return viewPos;
	}
	
	public void onTouchPressed(MotionEvent event)
	{
		selectedTile_ = board_.getTileAtPos( toBoardPos( new PointF( event.getX(), event.getY() ) ) );
		if( selectedTile_ == null )
		{
			return;
		}
		
		selectedTile_.setHighlighted( true );
		
		invalidate();
	}


	public void onTouchDragged(MotionEvent event)
	{
		if( selectedTile_ == null )
		{
			return;
		}
		
		PointF boardPos = toBoardPos( new PointF( event.getX(), event.getY() ) );
		
		selectedTile_.Move( boardPos );
		
		invalidate();
	}

	public void onTouchReleased(MotionEvent event)
	{
		if( selectedTile_ == null )
		{
			return;
		}
		
		selectedTile_.setHighlighted( false );
		
		invalidate();
	}

	public Diagram getDiagram()
	{
		return diagram_;
	}

	public void setDiagram( Diagram diagram )
	{
		this.diagram_ = diagram;
	}
	
	public Bitmap getImage()
	{
		return image_;
	}
	
	public void setImage( Bitmap bmp )
	{
		image_ = bmp;
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
	private Tile			selectedTile_;
	
	private Paint 			tilePaint_ 	= null;

}
