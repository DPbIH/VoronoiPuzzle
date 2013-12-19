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
			newTile.setVertexes( getCellVertexesWithRelativeCoordinates( nextCell ) );

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

		Path mask 			= getCellPoly( cell );
		Bitmap sliceBmp 	= Bitmap.createBitmap( cellWidth, cellHeight, Config.ARGB_8888);

		Canvas mCanvas 		= new Canvas( sliceBmp );
		Paint paint 		= new Paint(Paint.ANTI_ALIAS_FLAG);

		mCanvas.drawPath( mask, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		mCanvas.drawBitmap( bmp, 0, 0, null );

		return sliceBmp;
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
	
	private Path getCellPoly( Cell cell )
	{
		Path poly = new Path();
		Iterator<PointF> it = cell.getVertexes().iterator();
		if( it.hasNext() )
		{
			PointF firstVertex = it.next();
			poly.moveTo( firstVertex.x, firstVertex.y );
		}
		
		while( it.hasNext() )
		{
			PointF nextVertex = it.next();
			poly.lineTo( nextVertex.x, nextVertex.y );
		}
		poly.close();

		return poly;
	}

	private Bitmap getScaledBitmap( Bitmap src, int width, int height )
	{
		return Bitmap.createScaledBitmap( src, width, height, false);
	}
}