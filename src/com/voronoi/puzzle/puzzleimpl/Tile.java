package com.voronoi.puzzle.puzzleimpl;

import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;

public class Tile
{
	private PointF 	targetPos_				= new PointF( 0, 0 );
	private PointF 	currentPos_				= new PointF( 0, 0 );
	private int 	number_ 				= -1;
	private Bitmap	bmp_ 					= null;
	private int 	rotationAngle_ 			= 0;
	private static final int rotationRatio_ = 90;
	private static final int fullRound_		= 360;
	private ArrayList<PointF> vertexes_;
	private boolean isHighlighted_			= false;

	public void setHighlighted( boolean val )
	{
		isHighlighted_ = val;
	}
	
	public boolean isHighlighted()
	{
		return isHighlighted_;
	}
	
	public Bitmap getBitmap()
	{
		return bmp_;
	}

	public void setBitmap(Bitmap bmp)
	{
		this.bmp_ = bmp;
	}

	public int getNumber()
	{
		return number_;
	}

	public void setNumber(int number)
	{
		this.number_ = number;
	}

	public PointF getCurrentPos()
	{
		return currentPos_;
	}

	public void setCurrentPos(PointF currentPos)
	{
		this.currentPos_ = currentPos;
	}

	public PointF getTargetPos()
	{
		return targetPos_;
	}

	public void setTargetPos(PointF targetPos)
	{
		this.targetPos_ = targetPos;
	}

	public boolean IsOnTargetPos()
	{
		return targetPos_.equals( currentPos_ ) && (rotationAngle_ == 0);
	}

	public void Move( PointF newPos )
	{
		currentPos_ = newPos;
	}

	public void Rotate()
	{
		rotationAngle_ += rotationRatio_;
		if( rotationAngle_ == fullRound_ )
		{
			rotationAngle_ = 0;
		}
	}

	public ArrayList<PointF> getVertexes()
	{
		return vertexes_;
	}

	public void setVertexes( ArrayList<PointF> vertexes )
	{
		this.vertexes_ = vertexes;
	}

	public boolean contains( PointF point )
	{
		Region region = getRegionForPath( getPathFromVertexes() );
		
		return region.contains( (int)point.x, (int)point.y);
	}

	public boolean intersectsWith( Tile other )
	{
		Region region = getRegionForPath( getPathFromVertexes() );
		Region regionOther = getRegionForPath( other.getPathFromVertexes() );
		
		return region.op( regionOther, Region.Op.INTERSECT ); 
	}
	
	private Path getPathFromVertexes() 
	{
		Path path = new Path();
		
		Iterator<PointF> it = vertexes_.iterator();
		if( it.hasNext() )
		{
			PointF firstVertex = it.next();
			path.moveTo( firstVertex.x + currentPos_.x, firstVertex.y + currentPos_.y );
		}
		
		while( it.hasNext() )
		{
			PointF nextVertex = it.next();
			path.lineTo( nextVertex.x + currentPos_.x, nextVertex.y + currentPos_.y );
		}
		
		path.close();
		
		return path;
	}
	
	private Region getRegionForPath( Path path )
	{
		Region region	= new Region();
		RectF rectF 	= new RectF();
		path.computeBounds(rectF, true);
		region.setPath( path, new Region( (int)rectF.left, (int)rectF.top, (int)rectF.right, (int)rectF.bottom) );
		
		return region;
	}

}
