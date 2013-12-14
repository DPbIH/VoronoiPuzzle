package com.voronoi.puzzle.puzzleimpl;

import android.graphics.Bitmap;
import android.graphics.PointF;

public class Tile {
	private PointF 	targetPos_				= new PointF( 0, 0 );
	private PointF 	currentPos_				= new PointF( 0, 0 );
	private int 	number_ 				= -1;
	private Bitmap	bmp_ 					= null;
	private int 	rotationAngle_ 			= 0;
	private static final int rotationRatio_ = 90;
	private static final int fullRound_		= 360;
	
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

}
