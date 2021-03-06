package com.voronoi.puzzle.diagramimpl;

import android.graphics.PointF;

public class Border                                                                                                                                                                                                                                                                                                                           
{
	public Border(Cell cellaUno, Cell cellaDue, PointF limit1, PointF limit2)
	{
		setCellFirst( cellaUno );                                                                           
		setCellSecond( cellaDue );
		setPointFirst( limit1 );
		setPointSecond( limit2 );
	} 

	public boolean containsVertex( PointF vertex )
	{
		return vertex.equals(p1) || vertex.equals(p2);
	}
	
	public PointF GetIntersection(Cell cella)
	{
		float a1, b1, c1;

		float a2, b2, c2;

		float a3, b3, c3;

		float x1, y1, x2, y2;

		float dx, dy;  

		x1 = p1.x;
		x2 = p2.x;
		y1 = p1.y;
		y2 = p2.y;
		dx = x2-x1;
		dy = y2-y1;

		if(Math.abs(dx) > Math.abs(dy))
		{
			a1 = -dy/dx;
			b1 = 1;
			c1 = -a1*x1 - y1; 
		}
		else
		{
			a1 = 1;
			b1 = -dx/dy;
			c1 = -b1*y1 - x1;
		}

		x1 = cell1.getKernel().x;  
		x2 = cella.getKernel().x;
		y1 = cell1.getKernel().y;  
		y2 = cella.getKernel().y;
		dx = x2-x1;  
		dy = y2-y1;

		if(Math.abs(dx) > Math.abs(dy))
		{
			a2 = -dy/dx;
			b2 = 1;
			c2 = -a2*x1 - y1; 
		}
		else
		{
			a2 = 1;
			b2 = -dx/dy;
			c2 = -b2*y1 - x1; 
		}

		float xm = (cell1.getKernel().x + cella.getKernel().x) / 2;
		float ym = (cell1.getKernel().y + cella.getKernel().y) / 2;

		a3 = b2;
		b3 = -a2;       
		c3 = -xm*a3 - ym*b3;

		if(sonoUguali(a1*b3, a3*b1))
		{
			return null;
		}

		float denom = a1*b3 - b1*a3;

		float y0 = (c1*a3 - c3*a1) / denom;

		float x0 = (c3*b1 - c1*b3) / denom;

		float xmax = Math.max(p1.x, p2.x);
		float xmin = Math.min(p1.x, p2.x);
		float ymax = Math.max(p1.y, p2.y);
		float ymin = Math.min(p1.y, p2.y);

		if((isMaggiorUguale(x0, xmin)) && (isMinorUguale(x0, xmax)) && 
				(isMaggiorUguale(y0, ymin)) && (isMinorUguale(y0, ymax)))
		{
			return new PointF(x0, y0);
		}
		else
			return null;
	} 

	public Cell getSecondCell(Cell cella)
	{
		if(cella == cell1)
		{
			return cell2;
		}
		else if(cella == cell2)
		{
			return cell1;
		}
		
		return null;
	}

	public boolean isEdge()
	{
		return ( cell2 == null ) || ( cell1 == null );
	}

	public PointF getPointFirst()
	{
		return p1;
	}

	public PointF getPointSecond()
	{
		return p2;
	}

	public void setPointFirst(PointF punto)
	{
		this.p1 = punto;
	}

	public void setPointSecond(PointF punto)
	{
		this.p2 = punto;
	}

	public void setCellFirst(Cell sito)
	{
		cell1 = sito;
	}

	public void setCellSecond(Cell sito)
	{
		cell2 = sito;
	}

	public boolean isMinorUguale(float u, float d)
	{
		if((u != 0) && (d != 0))
		{
			return ((Math.abs(u-d) / Math.max(Math.abs(u), Math.abs(d)) <= EPSILON) || (u < d));           
		}
		else
		{
			return ((Math.abs(u-d) <= EPSILON) || (u < d));
		}
	}


	public boolean isMaggiorUguale(float u, float d)
	{
		if((u != 0) && (d != 0))
		{
			return (((Math.abs(u-d) / Math.max(Math.abs(u), Math.abs(d))) <= EPSILON) || (u > d));
		}   
		else
		{
			return ((Math.abs(u-d) <= EPSILON) || (u > d));
		}
	}

	public boolean sonoUguali(float num, float numero)
	{
		if((num != 0) && (numero != 0))
		{
			return ((Math.abs(num - numero) / 
					Math.max(Math.abs(num), Math.abs(numero))) <= EPSILON);
		}
		else
		{
			return (Math.abs(num - numero) <= EPSILON);
		}
	}

	private PointF p1, p2;
	private Cell cell1, cell2;
	private static final double EPSILON = 1E-9;
}
