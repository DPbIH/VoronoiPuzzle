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
		ArrayList<Tile> tiles = new ArrayList<Tile>();

		PointF diagramSize = diagram.getSize();
		Bitmap scaledBmp = getScaledBitmap( bmp, (int)diagramSize.x, (int)diagramSize.y );

		Iterator<Cell> cellsIt = diagram.getCellIterator();
		while( cellsIt.hasNext() )
		{
			Cell nextCell = cellsIt.next();

			Tile newTile = new Tile();

			newTile.setTargetPos( nextCell.TopLeft() );
			newTile.setNumber( diagram.getCellsArray().indexOf( nextCell ) );
			newTile.setBitmap( getBitmapSlice( scaledBmp, nextCell ) );

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

	private Path getCellPoly( Cell cell )
	{
		Path poly = new Path();
		Iterator<PointF> it = cell.getVertexes().iterator();
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
