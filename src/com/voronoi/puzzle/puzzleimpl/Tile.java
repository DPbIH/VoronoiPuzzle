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
	private ArrayList<PointF> vertexes_;
	private boolean isHighlighted_			= false;
	private boolean pinned_					= false;

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
		return IsRotated() ? rotateBitmap( bmp_, rotationAngle_ ) : bmp_;
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
		if( pinned_ )
		{
			return;
		}
		
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
	
	public boolean IsRotated()
	{
		return ( rotationAngle_ != 0 );
	}
	
	public int GetRotationAngle()
	{
		return rotationAngle_;
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
	
	public float DistanceToTarget()
	{
		float distance = (float)Math.sqrt(
				Math.pow(targetPos_.x-currentPos_.x, 2) + 
				Math.pow(targetPos_.y-currentPos_.y, 2) );
		
		return distance;
	}
	
	public void MoveToTarget()
	{
		Move( getTargetPos() );
	}
	
	public void Pin()
	{
		pinned_ = true;
	}
	
	public void Unpin()
	{
		pinned_ = false;
	}
	
	public Path getPathFromVertexes() 
	{	
		Path path = new Path();
		
		Iterator<PointF> it = vertexes_.iterator();
		if( it.hasNext() )
		{
			PointF firstVertex = it.next();
			path.moveTo( firstVertex.x, firstVertex.y );
		}
		
		while( it.hasNext() )
		{
			PointF nextVertex = it.next();
			path.lineTo( nextVertex.x, nextVertex.y );
		}
		
		path.close();
		
		if( IsRotated() )
		{
			path = rotatePath( path, rotationAngle_ );
		}
		
		//now. move path to current position
		RectF bounds = new RectF();
		path.computeBounds(bounds, true);
		float dx = (float)Math.sqrt(Math.pow( currentPos_.x - bounds.left, 2) );
		float dy = (float)Math.sqrt(Math.pow( currentPos_.y - bounds.top, 2) );
		path.offset(dx, dy);
		
		return path;
	}
	
	private Region getRegionForPath( Path path )
	{
		Region region	= new Region();
		RectF bounds 	= new RectF();
		path.computeBounds(bounds, true);
		region.setPath( path, new Region( (int)bounds.left, (int)bounds.top, (int)bounds.right, (int)bounds.bottom) );
		
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
