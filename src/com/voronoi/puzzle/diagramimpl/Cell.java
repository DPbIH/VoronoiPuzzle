package com.voronoi.puzzle.diagramimpl;

import java.util.*;

import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;

public class Cell implements Cloneable 
{
	public  Cell(float x, float y)
	{
		borders_ = new ArrayList<Border>();
		kernel_ = new PointF(x, y);
	}

	public Cell(PointF unPunto)
	{
		borders_ = new ArrayList<Border>();
		kernel_ = unPunto;
	}

	public Cell(PointF unPunto, boolean col)
	{
		borders_ = new ArrayList<Border>();
		kernel_ = unPunto;
	}

	// this method is based on  Jordan curve theorem. http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html 
	public boolean contains( PointF point )
	{
		boolean isInside = false;
		PointF polygon[] = (PointF[])getVertexes().toArray();
		float minX = polygon[0].x, maxX = polygon[0].x;
		float minY = polygon[0].y, maxY = polygon[0].y;
		for (int n = 1; n < polygon.length; n++) 
		{
			PointF q = polygon[n];
			minX = Math.min(q.x, minX);
			maxX = Math.max(q.x, maxX);
			minY = Math.min(q.y, minY);
			maxY = Math.max(q.y, maxY);
		}

		if (point.x < minX || point.x > maxX || point.y < minY || point.y > maxY) {
			return false;
		}

		int i = 0, j = polygon.length - 1;
		for ( ; i < polygon.length; j = i++)
		{
			if ( (polygon[i].y > point.y) != (polygon[j].y > point.y) &&
					point.x < (polygon[j].x - polygon[i].x) * (point.y - polygon[i].y) / (polygon[j].y - polygon[i].y) + polygon[i].x )
			{
				isInside = !isInside;
			}
		}

		return isInside;
	}

	public PointF TopLeft()
	{
		return CornerPoint( Corner.TOPLEFT );
	}

	public PointF BottomRight()
	{
		return CornerPoint( Corner.BOTTOMRIGHT );
	}

	private enum Corner { TOPLEFT, BOTTOMRIGHT }

	private PointF CornerPoint( Corner corner )
	{
		PointF cornerPoint = new PointF();

		ArrayList<PointF> vertexes = getVertexes();
		Iterator<PointF> it = vertexes.iterator();

		if( it.hasNext() )
		{
			float x, y;

			PointF point = (PointF)it.next();
			x = point.x;
			y = point.y;

			while( it.hasNext() )
			{
				point = (PointF)it.next();

				if(corner == Corner.BOTTOMRIGHT )
				{
					x = Math.max( x, point.x );
					y = Math.max( y, point.y );
				}
				else if( corner == Corner.TOPLEFT )
				{
					x = Math.min( x, point.x );
					y = Math.min( y, point.y );
				}
			}

			cornerPoint.set(x, y);
		}

		return cornerPoint;
	}

	public ArrayList<PointF> getVertexes()
	{   
		ArrayList<PointF>  vertexes = new ArrayList<PointF> ();

		Iterator<Border> bordersIter = borders_.iterator();
		while(bordersIter.hasNext())
		{
			Border border= (Border)bordersIter.next();
			PointF point1st = border.getPointFirst();
			PointF point2nd = border.getPointSecond();

			if(!(vertexes.contains(point1st)))
			{
				vertexes.add(point1st);
			}

			if(!(vertexes.contains(point2nd)))
			{
				vertexes.add(point2nd);
			}

			class VertexesComparator implements Comparator<PointF>
			{
				public int compare(PointF lhs, PointF rhs)
				{
					if(getAngle(lhs, kernel_) < getAngle(rhs, kernel_)){return -1;}
					if(areEqual(getAngle(lhs, kernel_), getAngle(rhs, kernel_))){return 0;}
					return 1;
				}
			}

			Collections.sort(vertexes, new VertexesComparator() );
		}
		return vertexes;
	}
	
	public Path getCellPath()
	{
		final float MinimalDistance	= 100;
		final float Radius			= 15;

		Path cellPath = new Path();
		
		ArrayList<PointF> vertexes = getVertexes();
		vertexes.add( vertexes.get(0) ); // making loop of the array
		
		PointF prevVertex = null;
		for( PointF currVertex: vertexes )
		{
			if( prevVertex == null )
			{
				cellPath.moveTo( currVertex.x, currVertex.y );
			}
			else
			{
				Border border = getBorderFromVertexes( currVertex, prevVertex );
				
				boolean borderIsTooShort = getDistance(prevVertex, currVertex) < MinimalDistance;
				boolean borderIsEdge     = ( border != null ) ? border.isEdge() : false;
				
				if( borderIsTooShort || borderIsEdge )
				{
					cellPath.lineTo( currVertex.x, currVertex.y );
				}
				else
				{
					PointF midPoint = getMidPoint( prevVertex, currVertex );
					PointF arcStart = getPointByOffsetFromStart( midPoint, prevVertex, Radius );
					PointF arcEnd   = getPointByOffsetFromStart( midPoint, currVertex, Radius );
					
					RectF  ovalRect = new RectF(
							midPoint.x - Radius, midPoint.y - Radius,
							midPoint.x + Radius, midPoint.y + Radius );
					
					int startAngle = (int) (180 / Math.PI * Math.atan2( arcStart.y - midPoint.y, arcStart.x - midPoint.x) );
					int sweepAngle = (getDistance( new PointF(0,0), arcStart ) > getDistance( new PointF(0,0), arcEnd )) ? 180 : -180;
					
					cellPath.lineTo( arcStart.x, arcStart.y );
					cellPath.arcTo( ovalRect, startAngle, sweepAngle );
					cellPath.lineTo( arcEnd.x, arcEnd.y );
					cellPath.lineTo( currVertex.x, currVertex.y );
				}
			}
			
			prevVertex = currVertex;
		}
		
		cellPath.close();
		
		return cellPath;
	}
	
	private Border getBorderFromVertexes( PointF vertex1, PointF vertex2 )
	{
		Border result = null;
		
		for( Border border: borders_ )
		{
			if( border.containsVertex(vertex1) && border.containsVertex(vertex2) )
			{
				result = border;
				break;
			}
		}
		
		return result;
	}

	private float getDistance(PointF p1, PointF p2) 
	{
		float distance = (float)Math.sqrt(
				Math.pow(p2.x-p1.x, 2) + 
				Math.pow(p2.y-p1.y, 2) );
		
		return distance;
	}

	private PointF getMidPoint(PointF point1st, PointF point2nd)
	{
		PointF midPoint = new PointF();
		midPoint.x = (point1st.x + point2nd.x)/2;
		midPoint.y = (point1st.y + point2nd.y)/2;

		return midPoint;
	}
	
	private PointF getPointByOffsetFromStart( PointF pointStart, PointF pointEnd, float distance )
	{
		//x[3] = x1 + d * (x2-x1) / sqrt( (x2-x1)^2 + (y2 - y1)^2 )
		//y[3] = y1 + d * (y2-y1) / sqrt( (x2-x1)^2 + (y2 - y1)^2 )
		
		PointF point = new PointF();
		
		point.x = pointStart.x + distance * ( pointEnd.x - pointStart.x ) / getDistance(pointStart, pointEnd);
		point.y = pointStart.y + distance * ( pointEnd.y - pointStart.y ) / getDistance(pointStart, pointEnd);
		
		return point;
	}

	private float getAngle(PointF p1, PointF p2)
	{
		float result = 0;

		if( ! areEqual(p1, p2) ) 
		{
			float b = (p2.x - p1.x);
			float a = (p1.y - p2.y);
			result = (float)(Math.atan2(a, b) + Math.PI);
		}

		return result;

	}

	private boolean areEqual(float lhs, float rhs)
	{
		if((lhs != 0) && (rhs != 0))
		{
			return ((Math.abs(lhs - rhs) / 
					Math.max(Math.abs(lhs), Math.abs(rhs))) <= EPS);
		}
		else
		{
			return (Math.abs(lhs - rhs) <= EPS);
		}
	}

	public ArrayList<Border> getBorders()
	{
		return  borders_;
	}

	public Iterator<Border> getBordersIt()
	{
		return borders_.iterator();
	}

	public PointF getKernel()
	{
		return kernel_;
	}

	private boolean areEqual(PointF pointUno, PointF pointDue)
	{
		float numUnoX = pointUno.x;
		float numUnoY = pointUno.y;
		float numDueX = pointDue.x;
		float numDueY = pointDue.y;
		boolean ics, yps;

		if((numUnoX != 0) && (numDueX != 0))
		{
			ics = ((Math.abs(numUnoX - numDueX) / 
					Math.max(Math.abs(numUnoX), Math.abs(numDueX))) <= EPS);
		}
		else
		{
			ics = (Math.abs(numUnoX - numDueX) <= EPS);
		}

		if((numUnoY != 0) && (numDueY != 0))
		{
			yps = ((Math.abs(numUnoY - numDueY) / 
					Math.max(Math.abs(numUnoY), Math.abs(numDueY))) <= EPS);
		}
		else
		{
			yps = (Math.abs(numUnoY - numDueY) <= EPS);
		}

		return (ics && yps);


	}

	public Object clone()
	{
		Cell clone  = null;
		try
		{
			clone = (Cell)super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			e.printStackTrace();
		}


		return clone;
	}

	public Border getBorder( Cell neighborCell )
	{
		Border border = null;

		for( Border nextBorder: borders_ )
		{
			if( nextBorder.getSecondCell(this) == neighborCell )
			{
				border = nextBorder;
				break;
			}
		}

		return border;
	}


	private ArrayList<Border>	borders_;
	private PointF 				kernel_;
	private static final double	EPS		= 1E-13;
	
}
