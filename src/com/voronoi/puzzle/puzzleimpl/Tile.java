package com.voronoi.puzzle.puzzleimpl;

import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.Bitmap;
import android.graphics.Matrix;
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
	private boolean isHighlighted_			= false;
	private boolean pinned_					= false;
	private Path 	borders_				= null;

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
		return isRotated() ? rotateBitmap( bmp_, rotationAngle_ ) : bmp_;
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

	public boolean isOnTargetPos()
	{
		return targetPos_.equals( currentPos_ ) && (rotationAngle_ == 0);
	}

	public void move( PointF newPos )
	{
		if( pinned_ )
		{
			return;
		}
		
		currentPos_ = newPos;
	}

	public void rotate()
	{
		rotationAngle_ += rotationRatio_;
		if( rotationAngle_ == fullRound_ )
		{
			rotationAngle_ = 0;
		}
	}
	
	public boolean isRotated()
	{
		return ( rotationAngle_ != 0 );
	}
	
	public int getRotationAngle()
	{
		return rotationAngle_;
	}
	
	public void setBordersPolygon( Path bordersPoly )
	{
		borders_ = bordersPoly;
	}
	
	public Path getBordersPolygon()
	{
		return borders_;
	}

	public boolean contains( PointF point )
	{
		Region region = getRegionForPath( getBordersPolygonForCurrentPos() );
		
		return region.contains( (int)point.x, (int)point.y);
	}

	public boolean intersectsWith( Tile other )
	{
		Region region = getRegionForPath( getBordersPolygonForCurrentPos() );
		Region regionOther = getRegionForPath( other.getBordersPolygonForCurrentPos() );
		
		return region.op( regionOther, Region.Op.INTERSECT ); 
	}
	
	public float distanceToTarget()
	{
		float distance = (float)Math.sqrt(
				Math.pow(targetPos_.x-currentPos_.x, 2) + 
				Math.pow(targetPos_.y-currentPos_.y, 2) );
		
		return distance;
	}
	
	public void moveToTarget()
	{
		move( getTargetPos() );
	}
	
	public void pin()
	{
		pinned_ = true;
	}
	
	public void unpin()
	{
		pinned_ = false;
	}
	
	public Path getBordersPolygonForCurrentPos() 
	{	
		Path path = getBordersPolygon();
		
		if( isRotated() )
		{
			path = rotatePath( path, rotationAngle_ );
		}
		
		//now. move path to current position
		RectF bounds = new RectF();
		path.computeBounds(bounds, true);
		float dx = currentPos_.x - bounds.left;
		float dy = currentPos_.y - bounds.top;
		path.offset(dx, dy);
		
		return path;
	}
	
	private Region getRegionForPath( Path path )
	{
		Region region	= new Region();
		RectF bounds 	= new RectF();
		path.computeBounds(bounds, true);
		region.setPath( path, 
				new Region( (int)bounds.left, (int)bounds.top,
						(int)bounds.right, (int)bounds.bottom) );
		
		return region;
	}
	
	private static Bitmap rotateBitmap(Bitmap source, float angle)
	{
	      Matrix matrix = new Matrix();
	      matrix.postRotate(angle);
	      
	      return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
	
	private static Path rotatePath( Path source, float angle )
	{
		Path rotatedPath = new Path( source );
		RectF bounds = new RectF();
		rotatedPath.computeBounds(bounds, true);

		Matrix matrix = new Matrix();
		matrix.postRotate(angle, 
				(bounds.left + bounds.right)/2,
				(bounds.top + bounds.bottom)/2 );
		
		rotatedPath.transform(matrix);

		return rotatedPath;
	}

}
