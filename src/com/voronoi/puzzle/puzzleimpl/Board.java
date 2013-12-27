package com.voronoi.puzzle.puzzleimpl;

import java.util.ArrayList;

import android.graphics.PointF;

public class Board 
{
	public void setSize( float width, float height )
	{
		width_ 	= width;
		height_ = height;
	}

	public float getWidth()
	{
		return width_;
	}

	public float getHeight()
	{
		return height_;
	}

	public void addTile( Tile tile )
	{
		tiles_.add( tile );
	}

	public void removeTile( Tile tile )
	{
		tiles_.remove( tile );
	}

	public Tile getTileAtPos( PointF pos )
	{
		Tile seekedTile = null;
		
		for (Tile tile : tiles_)
		{
			if( tile.contains( pos ) )
			{
				seekedTile = tile;
				break;
			}
		}
		
		return seekedTile;
	}

	public boolean checkMoveIsAllowedForTile( Tile tile, PointF newPos )
	{
		if( ! contains( newPos ) )
		{
			return false;
		}
		
		// check if tile will overlap other tiles
		PointF currentPosTmp = tile.getCurrentPos();
		tile.Move(newPos);
		
		for (Tile other : tiles_)
		{
			if( tile.intersectsWith( other ) )
			{
				tile.Move( currentPosTmp );
				return false;
			}
		}
		
		return true;
	}
	
	public boolean contains( PointF point )
	{
		return ( point.x < width_ ) && ( point.y < height_ );
	}
	
	public void clear()
	{
		tiles_.clear();
	}

	public void forEachTile(TileHandler handler)
	{
		for (Tile tile : tiles_)
		{
			handler.processTile(tile);
		}
	}
	
	public ArrayList<Tile> getTiles()
	{
		return tiles_;
	}
	
	public boolean containsTile( Tile tile )
	{
		return tiles_.contains( tile );
	}
	
	private ArrayList<Tile> tiles_ = new ArrayList<Tile>();
	
	private float width_ 	= 0;
	private float height_ 	= 0;
}
