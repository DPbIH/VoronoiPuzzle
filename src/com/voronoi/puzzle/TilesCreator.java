package com.voronoi.puzzle;

import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import com.voronoi.puzzle.diagramimpl.Cell;
import com.voronoi.puzzle.diagramimpl.Diagram;
import com.voronoi.puzzle.puzzleimpl.Tile;

public class TilesCreator 
{

	public ArrayList<Tile> getTiles( Diagram diagram, Bitmap bmp )
	{
		if( (diagram == null) || (bmp == null) )
		{
			return null;
		}

		ArrayList<Tile> tiles = new ArrayList<Tile>();

		Bitmap scaledBmp = getScaledBitmap( bmp, (int)diagram.getWidth(), (int)diagram.getHeight() );

		Iterator<Cell> cellsIt = diagram.getCellIterator();
		while( cellsIt.hasNext() )
		{
			Cell nextCell = cellsIt.next();

			Tile newTile = new Tile();

			newTile.setTargetPos( nextCell.TopLeft() );
			newTile.setNumber( diagram.getCellsArray().indexOf( nextCell ) );
			newTile.setBitmap( getBitmapSlice( scaledBmp, nextCell ) );
			newTile.setBordersPolygon( nextCell.getCellPath() );

			tiles.add( newTile );
		}

		return tiles;
	}

	private Bitmap getBitmapSlice( Bitmap bmp, Cell cell ) 
	{
		PointF topleft 		= cell.TopLeft();
		PointF bottomright 	= cell.BottomRight();
		int cellWidth 		= (int)(bottomright.x - topleft.x);
		int cellHeight 		= (int)(bottomright.y - topleft.y);

		Path mask 			= cell.getCellPath();
		Bitmap sliceBmp 	= Bitmap.createBitmap( bmp.getWidth(), bmp.getHeight(), Config.ARGB_8888 );
		Canvas canvas 		= new Canvas( sliceBmp );
		final int color		= 0xff424242;
		Paint paint 		= new Paint();
		Rect rect 			= new Rect( 
				(int)topleft.x, (int)topleft.y, 
				(int)bottomright.x, (int)bottomright.y );

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawPath(mask, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bmp, rect, rect, paint);

		return Bitmap.createBitmap( 
				sliceBmp, (int)topleft.x, 
				(int)topleft.y, cellWidth, cellHeight );
	}

	private ArrayList<PointF> getCellVertexesWithRelativeCoordinates( Cell cell )
	{
		ArrayList<PointF> vertexes = new ArrayList<PointF>();

		PointF posTopLeft = cell.TopLeft();

		Iterator<PointF> vertexesIt = cell.getVertexes().iterator();
		while( vertexesIt.hasNext() )
		{
			PointF nextVertex = vertexesIt.next();
			PointF vertexWithRelativeCoordinates = new PointF( 
					nextVertex.x - posTopLeft.x,
					nextVertex.y - posTopLeft.y 
					);

			vertexes.add( vertexWithRelativeCoordinates ); 
		}

		return vertexes;
	}

	private Bitmap getScaledBitmap( Bitmap src, int width, int height )
	{
		return Bitmap.createScaledBitmap( src, width, height, false);
	}
}
