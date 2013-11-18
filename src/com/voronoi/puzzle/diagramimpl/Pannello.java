package com.voronoi.puzzle.diagramimpl;

import java.awt.*;
import java.util.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.text.NumberFormat;

import javax.swing.JLabel;

import java.text.*;
import java.awt.Image;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;



/**
 *@author Indrit Selimi.
 */
public class Pannello extends JPanel
{
	public Pannello(Diagramma  unDiagramma)
	{
		voronoi = unDiagramma;
	}

	public void SetBackgroundImage( BufferedImage img )
	{
		Image scaledImg = img.getScaledInstance(getDimensioneX(), getDimensioneY(), Image.SCALE_SMOOTH);
		bgImg = new BufferedImage(getDimensioneX(), getDimensioneY(), img.getType());
		bgImg.getGraphics().drawImage(scaledImg, 0, 0 , null);
		bgImg.getGraphics().dispose();
	}
	
	public BufferedImage getPuzzlePiece( Cella cell )
	{	
		Point2D.Double topleft = cell.TopLeft();
		Point2D.Double bottomright = cell.BottomRight();
		int xStart = (int)topleft.getX();
		int xEnd = (int)bottomright.getX();
		int yStart = (int)topleft.getY();
		int yEnd = (int)bottomright.getY();
		int width = xEnd - xStart;
		int height = yEnd - yStart;
		
		BufferedImage puzzlePiece = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
		
		for( int x = xStart; x < xEnd; x++ )
		{
			for( int y = yStart; y < yEnd; y++ )
			{
				int rgb = (new Color(255, 255, 255, 0)).getRGB();
				if( cell.contains(new Point2D.Double(x,y) ) )
				{
					rgb = bgImg.getRGB( x, y );
				}
				
				puzzlePiece.setRGB(x - xStart, y - yStart, rgb);
			}
		}
		
		
		return puzzlePiece;
	}

	public Dimension getPreferredSize()
	{
		if(voronoi == null) return new Dimension(600, 600);
		Dimension dim = voronoi.getSize();
		return new Dimension((int)(dim.getWidth()* zoom), (int)(dim.getHeight()* zoom));
	}

	public int getDimensioneX()
	{
		Dimension dimX = voronoi.getSize();
		return (int)(dimX.getWidth());
	}

	public int getDimensioneY()
	{
		Dimension dimY = voronoi.getSize();
		return (int)(dimY.getHeight());
	}


	public double getZoom()
	{
		return zoom;
	}

	public void setZoom(double factor)
	{
		zoom = factor;
	}

	public double calcolaArea(ArrayList lista)
	{
		int i,j;
		double area = 0;
		int N = lista.size();
		if(lista.isEmpty())
		{
			return 0;
		}
		for (i=0; i < N; i++)
		{
			j = (i + 1) % N;
			area += ( (((Point2D.Double)(lista.get(i))).getX() * ((Point2D.Double)(lista.get(j))).getY()) 
					- (((Point2D.Double)(lista.get(i))).getY() * ((Point2D.Double)(lista.get(j))).getX()) );
		}

		area = Math.abs(area / 2.0);
		return(area);
	}

	public String areaTotaleRosso()
	{
		ArrayList confini;        
		Cella currentSite;
		double areaTotaleRosso = 0;
		int indice = 0;
		if(voronoi.getVCelle().isEmpty())
		{
			return "0";
		}
		Iterator listaSiti = voronoi.getVCelle().iterator();
		while(listaSiti.hasNext())
		{
			ArrayList lista;
			currentSite = (Cella)listaSiti.next();             
			lista = currentSite.getVertexes();

			if(currentSite.getColore())
				areaTotaleRosso += calcolaArea(lista); 
		}

		DecimalFormat myFormatter = new DecimalFormat("#");
		return myFormatter.format(areaTotaleRosso);
	}

	public String areaTotaleBlue()
	{
		ArrayList confini;        
		Cella currentSite;
		double areaTotaleBlue = 0;
		int indice = 0;
		if(voronoi.getVCelle().isEmpty())
		{
			return "0";
		}
		Iterator listaSiti = voronoi.getVCelle().iterator();
		while(listaSiti.hasNext())
		{
			ArrayList lista;
			currentSite = (Cella)listaSiti.next();             
			lista = currentSite.getVertexes();

			if(!currentSite.getColore())
				areaTotaleBlue += calcolaArea(lista);
		}
		DecimalFormat myFormatter = new DecimalFormat("#");
		return myFormatter.format(areaTotaleBlue);
	}

	public void paintComponent(Graphics g)
	{	
		super.paintComponent(g);

		if(voronoi == null) return;

		if( bgImg != null )
		{
			g.drawImage(bgImg, 0, 0, null);
		}

		g2 = (Graphics2D)g;

		if(isAliasing)
		{
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}

		drawCelle(g2);

		if(isDragged) drawQuadrettiDragged(g2);
		drawBorders(g2);
	}

	public void drawCelle(Graphics2D gg)
	{
		Graphics2D g3 = gg;
		ArrayList frontiere;
		Frontiera frontiera;
		int x, y = 0;

		Iterator listaSiti = voronoi.getCelle();
		while(listaSiti.hasNext())
		{
			ArrayList lista;
			cellaFine = (Cella)listaSiti.next();
			if(cellaFine.getColore())
				g3.setPaint(new Color(255, 255, 100, 128));
			else
				g3.setPaint(new Color(255, 255, 255, 128));


			GeneralPath s = new GeneralPath();
			lista = cellaFine.getVertexes();

			if(!(lista.isEmpty()))
			{
				s.moveTo((float)(((Point2D.Double)(lista.get(0))).getX() * zoom), 
						(float)(((Point2D.Double)(lista.get(0))).getY() * zoom));
				for(int i = 1; i < lista.size(); i++)
				{
					s.lineTo((float)(((Point2D.Double)(lista.get(i))).getX()*zoom), 
							(float)(((Point2D.Double)(lista.get(i))).getY() * zoom) );

				}
				s.closePath();

				g3.fill(s);
				g3.setPaint(Color.black);
				g3.draw(s);
			}

			x = (int) (cellaFine.getKernel().getX()*zoom);
			y = (int) (cellaFine.getKernel().getY()*zoom);
			Ellipse2D.Double circle = new Ellipse2D.Double((x-diametro/2), (y-diametro/2), diametro, diametro);
			g3.setPaint(Color.yellow);
			g3.setStroke(new BasicStroke(1.0F));
			g3.fill(circle);
		}
	}

	public void  drawBorders(Graphics2D gg)
	{
		Graphics2D g3 = gg;
		Point2D.Double p1 = new Point2D.Double(0, 0);
		Point2D.Double p2 = new Point2D.Double(getDimensioneX()*zoom, 0);
		Point2D.Double p3 = new Point2D.Double(getDimensioneX()*zoom, getDimensioneY()*zoom);
		Point2D.Double p4 = new Point2D.Double(0, getDimensioneY()*zoom);

		Line2D.Double lineaNord = new Line2D.Double(p1, p2);
		Line2D.Double lineaEst = new Line2D.Double(p2, p3);
		Line2D.Double lineaSud = new Line2D.Double(p3, p4);
		Line2D.Double lineaOvest = new Line2D.Double(p4, p1);

		g3.setPaint(new Color(0, 0, 0));
		g3.setStroke(new BasicStroke(2.0F));
		g3.draw(lineaNord);
		g3.draw(lineaEst);
		g3.draw(lineaSud);
		g3.draw(lineaOvest);
	}


	public void drawQuadrettiDragged(Graphics2D gg)
	{
		Graphics2D g3 = gg;
		g3.setPaint(Color.white);
		g3.draw(rettangolo);
	}      

	public void setShowArea(boolean vero)
	{
		showArea = vero;    
	}

	public boolean getShowArea()
	{
		return showArea;

	}
	public void setShowPosition(boolean vero)
	{
		showPosition = vero;
	}

	public boolean getShowPosition(boolean vero)
	{
		return showPosition;
	}
	public void setAreaCancellabile(boolean on)
	{
		isCancellabile = on;
	}
	public boolean getAreaCancellabile()
	{
		return isCancellabile;
	}
	public void setP(Point2D punto)
	{
		p = punto;
	}

	public void setQ(Point2D punto)
	{
		q = punto;
	}
	public Point2D getP()
	{
		return p;
	}
	public RoundRectangle2D.Double getRect()
	{
		return rect;
	}
	public void setRect(RoundRectangle2D.Double rec)
	{
		rect = rec;
	}


	public BufferedImage getJPEG(int unIntero)
	{
		BufferedImage image = new BufferedImage((int)(Math.round(getDimensioneX() * zoom)), (int)(Math.round(getDimensioneY() * zoom)), unIntero);
		Graphics2D gImage = image.createGraphics();
		drawCelle(gImage);

		return image;
	}

	public void setDiametro(int unNumero)
	{
		diametro = unNumero;
	}

	public void setRettangolo(Rectangle2D.Double unRettDrag)
	{
		rettangolo = unRettDrag;
	}
	public Rectangle2D.Double getRettangolo()
	{
		return rettangolo;
	}
	public void setDragged(boolean on)
	{
		isDragged = on;
	}
	public boolean getDragged()
	{
		return isDragged;
	}

	public void setAliasing(boolean on)
	{
		isAliasing = on;
	}
	public boolean getAliasing()
	{
		return isAliasing;
	}




	private Point2D.Double kernel; //Sperimentale!!!
	private Graphics2D g2;
	private Diagramma voronoi;  


	private Point2D puntoUno;
	private Point2D puntoDue;
	private double zoom = 1.0;

	private ArrayList listaKernel;
	private ArrayList listaArea;


	private boolean showArea = false;
	private boolean showPosition = false;
	private static final double EPS = 1E-14;

	private Point2D p; //Molto Sperimentale!!
	private Point2D q; //Molto Sperimentale
	private boolean isCancellabile = false;//Molto Sperimentale
	private RoundRectangle2D.Double rect;
	private int diametro = 5;

	private Rectangle2D.Double rettangolo;
	private boolean isDragged = false;
	private boolean isAliasing = false;

	private  Cella cellaFine;
	private BufferedImage bgImg;
}
