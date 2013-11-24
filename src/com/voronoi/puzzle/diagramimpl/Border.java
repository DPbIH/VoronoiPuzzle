package com.voronoi.puzzle.diagramimpl;

import android.graphics.PointF;

public class Border                                                                                                                                                                                                                                                                                                                           
{
	public Border(Cell cellaUno, Cell cellaDue, PointF limit1, PointF limit2)
	{
		this.cella1 = cellaUno;                                                                           
		this.cella2 = cellaDue;
		p1 = limit1;
		p2 = limit2;
	} 

	public PointF trovaIntersezione(Cell cella)
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

		x1 = cella1.getKernel().x;  
		x2 = cella.getKernel().x;
		y1 = cella1.getKernel().y;  
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

		float xm = (cella1.getKernel().x + cella.getKernel().x) / 2;
		float ym = (cella1.getKernel().y + cella.getKernel().y) / 2;

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

	public Cell getVicinoDi(Cell cella)
	{
		if(cella == cella1)
		{
			return cella2;
		}
		else if(cella == cella2)
		{
			return cella1;
		}
		
		return null;
	}

	public boolean isEdge()
	{
		return (cella2 == null);
	}

	public PointF getPuntoUno()
	{
		return p1;
	}

	public PointF getPuntoDue()
	{
		return p2;
	}

	public void setPuntoUno(PointF punto)
	{
		this.p1 = punto;
	}

	public void setPuntoDue(PointF punto)
	{
		this.p2 = punto;
	}

	public void setCellaUno(Cell sito)
	{
		cella1 = sito;
	}

	public void setCellaDue(Cell sito)
	{
		cella2 = sito;
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
	private Cell cella1, cella2;
	private static final float EPSILON = 1F-9;
}
