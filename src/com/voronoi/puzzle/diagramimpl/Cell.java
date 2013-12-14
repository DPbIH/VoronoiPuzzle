package com.voronoi.puzzle.diagramimpl;

import java.util.*;

import android.graphics.PointF;

public class Cell implements Cloneable 
{
	public  Cell(float x, float y)
	{
		border = new ArrayList<Border>();
		side = new PointF(x, y);
	}
	
	public Cell(PointF unPunto)
	{
		border = new ArrayList<Border>();
		side = unPunto;
	}

	public Cell(PointF unPunto, boolean col)
	{
		border = new ArrayList<Border>();
		side = unPunto;
		this.colore = col;
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
		Border front;
		ArrayList<PointF>  listaPunti = new ArrayList<PointF> ();
		Iterator<Border> listaElementi = border.iterator();
		while(listaElementi.hasNext())
		{
			front= (Border)listaElementi.next();
			PointF puntoUno = front.getPuntoUno();
			PointF puntoDue = front.getPuntoDue();

			if(!(listaPunti.contains(puntoUno)))
			{
				listaPunti.add(puntoUno);
			} 
			if(!(listaPunti.contains(puntoDue)))
			{
				listaPunti.add(puntoDue);
			}

			class ConfrontaAngoli implements Comparator<Object>
			{
				public int compare(Object a, Object b)
				{
					PointF puntoA =  (PointF)a;
					PointF puntoB =  (PointF)b;

					if(getAngle(puntoA, side) < getAngle(puntoB, side)){return -1;}
					if(areEqual(getAngle(puntoA, side), getAngle(puntoB, side))){return 0;}
					return 1;
				}
			}

			Collections.sort(listaPunti, new ConfrontaAngoli() );
		}
		return listaPunti;
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

	public ArrayList<Border> getVfrontiere()
	{
		return  border;
	}

	public Iterator<Border> getBordersIt()
	{
		return border.iterator();
	}

	public PointF getKernel()
	{
		return side;
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

	public void setColore(boolean rossoblu)
	{
		colore = rossoblu;
	}

	public boolean getColore()
	{
		return colore;
	}


	public Object clone()
	{
		Cell clonata  = null;
		try
		{
			clonata = (Cell)super.clone();
			clonata.setColore(this.colore);
		}
		catch(CloneNotSupportedException e){e.printStackTrace();}


		return clonata;
	}


	private ArrayList<Border> border;
	private PointF side;
	private static final double EPS = 1E-13;
	private boolean colore = false;
}
