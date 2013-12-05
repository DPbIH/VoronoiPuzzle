package com.voronoi.puzzle.diagramimpl;

import java.util.*;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.lang.Exception;

import android.graphics.PointF;
import android.graphics.RectF;

public class Diagram 
{
	public Diagram(int sizeX, int sizeY)
	{
		maxX = sizeX;
		maxY = sizeY;
		cellsList_ = new ArrayList();
	}

	public void setDiagramSize(int x, int y)
	{
		this.clear();
		maxX = x;
		maxY = y;
	}
	
	public void generate( int cellsCount )
	{
		clear();
		
		Random generator = new Random();
		for(int j = 1; j <= cellsCount; j++)
		{
			int coordX =  generator.nextInt( maxX );
			int coordY = generator.nextInt( maxY );
			Cell nextCell = new Cell( coordX, coordY );
			addCell( nextCell );
		}
	}
	
	public void scale( int newWidth, int newHeight )
	{
		
	}

	public void addCell(Cell newCell)
	{
		Iterator cellsIt;
		Iterator bordersIt;
		yyy = 0;

		ArrayList madeCells = new ArrayList(); 
		Iterator madeCellsIt;

		Iterator listaFront;

		Cell nextCell, cell2, madeCell;
		Border border, border1, border2, border3, border4, newBorder;
		PointF point1, point2, point3, point, limit;

		boolean loop = false; 
		boolean doneFlag = false;

		border1 = border2 = border3 = border4 = border = null;
		point1 = point2 = point3 = null;
		nextCell = cell2 = null;

		if(getCellByCoordinates(newCell.getKernel()) != null)
		{
			return;
		}

		cellsList_.add(newCell);
		if(cellsList_.size() == 1)
		{
			PointF p1 = new PointF(0, 0);
			PointF p2 = new PointF(maxX, 0);
			PointF p3 = new PointF(maxX, maxY);
			PointF p4 = new PointF(0, maxY);

			this.addBorder(newCell,new Border(newCell, null, p1, p4));
			this.addBorder(newCell,new Border(newCell, null, p4, p3));
			this.addBorder(newCell,new Border(newCell, null, p3, p2));
			this.addBorder(newCell,new Border(newCell, null, p2, p1));

			return; 
		}


		cellsIt = cellsList_.iterator();
		while(cellsIt.hasNext())
		{
			doneFlag = false;
			
			nextCell = (Cell)cellsIt.next();

			madeCellsIt = madeCells.iterator();
			while(madeCellsIt.hasNext()) 
			{
				madeCell = (Cell)madeCellsIt.next();
				if(nextCell == madeCell)
				{
					doneFlag = true;
					break;
				}

			}

			if(doneFlag)
			{
				continue;
			}

			border1 = border2 = null;
			bordersIt = nextCell.getBordersIt();
			while(bordersIt.hasNext())
			{
				border = (Border)bordersIt.next();
				point = border.GetIntersection(newCell);

				if(point == null)
				{
					continue;
				}

				if(border1 == null)
				{
					border1 = border;
					point1 = point;
				}
				else
				{
					border2 = border;
					point2 = point;
					break;
				}

			} 


			// Serve per scorrere nella listaCelle
			if((border1 == null) || (border2 == null))
			{
				continue;
			}

			// la nuova frontiera fra cella e cella1
			newBorder = new Border(newCell, nextCell, point1, point2);
			// per ogni frontiera trovata si aggiunge la nuova frontiera e il nuovo vicino
			addBorder(newCell, newBorder);
			addBorder(nextCell, newBorder); 


			if( GetDistance( nextCell.getKernel(), border1.getPuntoUno() ) < 
					GetDistance( newCell.getKernel(), border1.getPuntoUno() ) )
			{
				limit = border1.getPuntoDue();
				border1.setPuntoDue(point1);
			}
			else
			{
				limit = border1.getPuntoUno();
				border1.setPuntoUno(point1);
			}

			if(border1.isEdge())
			{
				addBorder(newCell,new Border(newCell, null, point1, limit));
			}

			ArrayList oldFront = new ArrayList();
			bordersIt = nextCell.getBordersIt();
			while(bordersIt.hasNext())
			{
				border = (Border)bordersIt.next();

				if(border == border1) 
					continue;
				if(border == border2) 
					continue;
				if(border == newBorder) 
					continue;

				// Scorre sui punti frontiera della cella e verifica
				// se sono stati aggiornati, eventualmente gli elimina.
				if( GetDistance( border.getPuntoUno(), newCell.getKernel() ) <
						GetDistance( border.getPuntoUno(), nextCell.getKernel() ) )
				{
					oldFront.add(border);
					if(border.isEdge())
					{
						addBorder(newCell,border);
						border.setCellaUno(newCell);
					}


				}
			}


			bordersIt = oldFront.iterator();
			while(bordersIt.hasNext())
			{
				this.deleteBorder(nextCell,(Border)bordersIt.next());
			}


			cell2 = nextCell; //conserva il riferimento alla prima cella visionata
			madeCells.add(nextCell);

			//Trova uno delle frontiere della cella sucessiva che
			//non sia però quella comune alla cella precedente(cella1)
			while((!(border1.isEdge())) && (!loop))
			{
				nextCell = border1.getVicinoDi(nextCell);

				bordersIt = nextCell.getBordersIt();
				while(bordersIt.hasNext())
				{
					border3 = (Border)bordersIt.next();
					point3 = border3.GetIntersection(newCell);

					if((point3 != null) && (border3 != border1))
						break;
				}


				if(border3 == border2)
				{
					point3 = point2;
					loop = true;
				} 

				newBorder = new Border(newCell, nextCell, point1, point3);
				this.addBorder(newCell, newBorder);
				this.addBorder(nextCell, newBorder);

				if( GetDistance( nextCell.getKernel(), border3.getPuntoUno() ) < 
						GetDistance( newCell.getKernel(), border3.getPuntoUno() ) )
				{
					limit = border3.getPuntoDue();
					border3.setPuntoDue(point3);
				}
				else
				{
					limit = border3.getPuntoUno();
					border3.setPuntoUno(point3);
				}



				if(border3.isEdge())
					this.addBorder(newCell, new Border(newCell, null, point3, limit));

				oldFront.clear(); //Serve per reinizilizzare l'oldFront
				bordersIt = nextCell.getBordersIt();
				while(bordersIt.hasNext())
				{
					border = (Border)bordersIt.next();
					if(border == border1) 
						continue;
					if(border == border3) 
						continue;
					if(border == newBorder) 
						continue;

					// Controllo sui punti consecutivi dello scheletro della
					// cella successiva(analogamente a prima). Se un punto é più
					// vicino alla nuova cella(quella entrante o corrente) che
					// alla cella alla quale appartenevano  vuol dire che non le 
					// appartengono più!
					if( GetDistance( border.getPuntoUno(), newCell.getKernel() ) < 
							GetDistance( border.getPuntoUno(), nextCell.getKernel() ) )
					{
						oldFront.add(border);

						if(border.isEdge())
						{
							this.addBorder(newCell, border);
							border.setCellaUno(newCell);
						}

					}
				}


				bordersIt = oldFront.iterator();
				while(bordersIt.hasNext())
				{
					this.deleteBorder(nextCell, (Border)bordersIt.next());
				}



				border1 = border3;
				point1 = point3;
				madeCells.add(nextCell);

			} 



			nextCell = cell2;
			if(!loop)
			{
				if( GetDistance( nextCell.getKernel(), border2.getPuntoUno() ) <
						GetDistance( newCell.getKernel(), border2.getPuntoUno() ) )
				{
					limit = border2.getPuntoDue();
					border2.setPuntoDue(point2);
				}
				else
				{
					limit = border2.getPuntoUno();
					border2.setPuntoUno(point2);
				}



				if(border2.isEdge())
					this.addBorder(newCell, new Border(newCell, null, point2, limit));
			}
			if(!loop)
			{

				while((!border2.isEdge()))
				{
					nextCell = border2.getVicinoDi(nextCell);       
					bordersIt = nextCell.getBordersIt();
					while(bordersIt.hasNext())
					{
						border3 = (Border)bordersIt.next();
						point3 = border3.GetIntersection(newCell);

						if((point3 != null) && (border3 != border2))
							break;
					}



					if(border3 == border1)
					{
						throw new NullPointerException();
					}
					//   System.out.println("Problema, Hai beccato lo" +
							//     "stesso punto di rottura di prima" + "Ciclo infinito!!");

					yyy++;
					if(yyy > 20)
					{
						throw new NullPointerException();
					}

					newBorder = new Border(newCell, nextCell, point2, point3);
					this.addBorder(newCell, newBorder);
					this.addBorder(nextCell, newBorder);

					if( GetDistance( nextCell.getKernel(), border3.getPuntoUno() ) < 
							GetDistance( newCell.getKernel(), border3.getPuntoUno() ) )
					{
						limit = border3.getPuntoDue();
						border3.setPuntoDue(point3);
					}
					else
					{
						limit = border3.getPuntoUno();
						border3.setPuntoUno(point3);
					}


					if(border3.isEdge())
						addBorder(newCell, new Border(newCell, null, point3, limit));

					oldFront.clear();
					bordersIt = nextCell.getBordersIt();
					while(bordersIt.hasNext())
					{

						border = (Border)bordersIt.next();
						if(border == border2) 
							continue;
						if(border == border3) 
							continue;
						if(border == newBorder) 
							continue;


						if( GetDistance( border.getPuntoUno(), newCell.getKernel() ) < 
								GetDistance( border.getPuntoUno(), nextCell.getKernel() ) )
						{
							oldFront.add(border);

							if(border.isEdge())
							{
								this.addBorder(newCell, border);
								border.setCellaUno(newCell);
							}

						}
					}


					listaFront = oldFront.iterator();
					while(listaFront.hasNext())
					{
						this.deleteBorder(nextCell, (Border)listaFront.next());
					}

					madeCells.add(nextCell);
					border2 = border3;
					point2 = point3;

				} 
			}

			oldFront.clear();
			boolean edge1 = false, edge2 = false, edge3 = false, edge4 = false;

			Iterator listaFrontiereCella = newCell.getBordersIt();
			while(listaFrontiereCella.hasNext())
			{            
				border = (Border) listaFrontiereCella.next();
				if(isEqual(border.getPuntoUno().x, maxX) && 
						isEqual(border.getPuntoDue().x, maxX))
				{
					if(edge2)
					{
						point1 = border2.getPuntoUno();
						point2 = border2.getPuntoUno();

						if(point1.y < border2.getPuntoDue().y)
							point1 = border2.getPuntoDue();

						if(point2.y > border2.getPuntoDue().y) 
							point2 = border2.getPuntoDue();

						if(point1.y < border.getPuntoUno().y) 
							point1 = border.getPuntoUno();

						if(point2.y > border.getPuntoUno().y) 
							point2 = border.getPuntoUno();

						if(point1.y < border.getPuntoDue().y)
							point1 = border.getPuntoDue();

						if(point2.y > border.getPuntoDue().y) 
							point2 = border.getPuntoDue();


						border.setPuntoUno(point1);
						border.setPuntoDue(point2);

						oldFront.add(border2);
						border2 = border;
					}
					else
					{
						edge2 = true;
						border2 = border;
					}
				} 

				if(isEqual(border.getPuntoUno().x, 0) &&
						isEqual(border.getPuntoDue().x, 0))
				{
					if(edge4)
					{
						point1 = border4.getPuntoUno();
						point2 = border4.getPuntoUno();


						if(point1.y < border4.getPuntoDue().y)
							point1 = border4.getPuntoDue();

						if(point2.y > border4.getPuntoDue().y) 
							point2 = border4.getPuntoDue();

						if(point1.y < border.getPuntoUno().y)
							point1 = border.getPuntoUno();

						if(point2.y > border.getPuntoUno().y) 
							point2 = border.getPuntoUno();

						if(point1.y < border.getPuntoDue().y)
							point1 = border.getPuntoDue();

						if(point2.y > border.getPuntoDue().y)
							point2 = border.getPuntoDue();


						border.setPuntoUno(point1);
						border.setPuntoDue(point2);

						oldFront.add(border4);
						border4=border;
					}
					else
					{
						edge4 = true;
						border4 = border;
					}
				}

				if(isEqual(border.getPuntoUno().y,0)
						&& isEqual(border.getPuntoDue().y, 0))
				{
					if(edge1)
					{
						point1 = border1.getPuntoUno();
						point2 = border1.getPuntoUno();


						if(point1.x > border1.getPuntoDue().x)
							point1 = border1.getPuntoDue();

						if(point2.x < border1.getPuntoDue().x) 
							point2 = border1.getPuntoDue();

						if(point1.x > border.getPuntoUno().x) 
							point1 = border.getPuntoUno();

						if(point2.x < border.getPuntoUno().x) 
							point2 = border.getPuntoUno();

						if(point1.x > border.getPuntoDue().x)
							point1 = border.getPuntoDue();

						if(point2.x < border.getPuntoDue().x) 
							point2 = border.getPuntoDue();


						border.setPuntoUno(point1);
						border.setPuntoDue(point2);

						oldFront.add(border1);
						border1=border;
					}
					else
					{
						edge1 = true;
						border1 = border;
					}
				} 

				if(isEqual(border.getPuntoUno().y, maxY) && 
						isEqual(border.getPuntoDue().y, maxY))
				{
					if(edge3)
					{
						point1 = border3.getPuntoUno();
						point2 = border3.getPuntoUno();


						if(point1.x > border3.getPuntoDue().x)
							point1 = border3.getPuntoDue();

						if(point2.x < border3.getPuntoDue().x)
							point2 = border3.getPuntoDue();

						if(point1.x > border.getPuntoUno().x)
							point1 = border.getPuntoUno();

						if(point2.x < border.getPuntoUno().x) 
							point2 = border.getPuntoUno();

						if(point1.x > border.getPuntoDue().x) 
							point1 = border.getPuntoDue();

						if(point2.x < border.getPuntoDue().x)
							point2 = border.getPuntoDue();


						border.setPuntoUno(point1);
						border.setPuntoDue(point2);

						oldFront.add(border3);
						border3=border;
					}
					else
					{
						edge3 = true;
						border3 = border;
					}
				} 

			} 



			listaFront = oldFront.iterator();
			while(listaFront.hasNext())
			{
				border = (Border) listaFront.next();
				this.deleteBorder(newCell, border);
			}

		} 

	}
	
	private static float GetDistance(PointF point1, PointF point2)
	{
	    float a = point2.x - point1.x;
	    float b = point2.y - point1.y;
	    return (float)Math.sqrt(a * a + b * b);
	}

	public boolean addBorder(Cell sito, Border frontier)
	{
		Cell p = frontier.getVicinoDi(sito);
		return sito.getVfrontiere().add(frontier);
	}

	public boolean deleteBorder(Cell sito, Border frontier)
	{
		Cell p = frontier.getVicinoDi(sito);
		return sito.getVfrontiere().remove(frontier);
	}

	public void deleteCell(Cell sito)
	{
		if(sito == null) return;

		if(cellsList_.contains(sito))
		{
			cellsList_.remove(sito);
			ArrayList trasporto = (ArrayList)cellsList_.clone();
			this.clear();
			Iterator listaTrasporto = trasporto.iterator();
			while(listaTrasporto.hasNext())
			{
				Cell elementoCorrente = (Cell)listaTrasporto.next();
				elementoCorrente.getVfrontiere().clear();
				addCell(elementoCorrente);
			}

		}
	}

	public void clear()
	{
		cellsList_.clear();
	}

	public Iterator getCellIterator()
	{
		return cellsList_.iterator();
	}

	public ArrayList getCellsArray()
	{
		return cellsList_;
	}


	public Cell getCellByCoordinates(PointF unPunto)
	{
		for(Iterator listaSiti = this.getCellIterator(); listaSiti.hasNext();)
		{
			Cell currentSite = (Cell)listaSiti.next();
			// Qua esiste un'inconsistenza con i pixel, non sempre!
			if( GetDistance( currentSite.getKernel(), unPunto) < (DISTANZA_MIN / 2 - 0.1))            
			{
				return currentSite;
			}
		}
		return null;
	}

	public Cell selectCellForDragging(PointF unPunto)
	{
		Cell tempSito = null;
		for(Iterator listaSiti = this.getCellIterator(); listaSiti.hasNext();)
		{
			Cell currentSite = (Cell)listaSiti.next();
			if( GetDistance( currentSite.getKernel(), unPunto) < (DISTANZA_MIN * 30 / 0.6 )) // Qua esiste un problema di tunning           
			{
				if(tempSito == null)
				{
					tempSito = currentSite;
				}
				else
				{
					if( GetDistance( currentSite.getKernel(), unPunto) < GetDistance( tempSito.getKernel(), unPunto) )
					{
						tempSito = currentSite;
					}
				}
			}              
		}
		return tempSito;
	}

	public PointF getSize()
	{
		return new PointF( maxX, maxY );
	}

	public boolean isEqual(double num, int numIntero)
	{
		double numero = (double)numIntero;
		if((num != 0) && (numero != 0))
		{
			return ((Math.abs(num - numero) / 
					Math.max(Math.abs(num), Math.abs(numero))) <= EPSI);
		}
		else
		{
			return (Math.abs(num - numero) <= EPSI);
		}
	}


	public void addDraggedCell(Cell cell)
	{
		if(cell == null) return;

		ArrayList trasportoBis = (ArrayList)getCellsArray().clone();
		this.clear();
		DISTANZA_MIN = 1;
		trasportoBis.add(cell);
		Iterator listaTrasporto = trasportoBis.iterator();
		while(listaTrasporto.hasNext())
		{
			Cell elementoCorrente = (Cell)listaTrasporto.next();
			elementoCorrente.getVfrontiere().clear();
			addCell(elementoCorrente);
		}
		DISTANZA_MIN = 5;
	}

	//Metodi per il savetaggio dei punti
	public void save(FileWriter file) throws IOException                                                   
	{
		PrintWriter out = new PrintWriter(file);
		Iterator listaCelle = cellsList_.iterator();
		while(listaCelle.hasNext())
		{
			Cell cella = (Cell)(listaCelle.next());
			PointF kernel = cella.getKernel();
			double x = kernel.x;
			double y = kernel.y;
			boolean colore = cella.getColore();
			out.println(x + "|" + y + "|" + Boolean.toString(colore));
		}
		file.close();
	}

	public void load(FileReader file) throws IOException 
	{
		BufferedReader input = new BufferedReader(file);
		String riga;
		while((riga = input.readLine()) != null)
		{
			StringTokenizer divisore = new StringTokenizer(riga, "|");
			try
			{
				float x = Float.parseFloat(divisore.nextToken());
				float y = Float.parseFloat(divisore.nextToken());
				boolean colore = (Boolean.valueOf(divisore.nextToken())).booleanValue();
				PointF p = new PointF(x, y);
				Cell cella = new Cell (p);
				cella.setColore(colore);
				this.addCell(cella);
			}
			catch(NumberFormatException ec)
			{
				//questo va gestito!!!
			}
			catch(NoSuchElementException ecc)
			{
				//questo va gestito!!!
			}
		}
	}

	public void deleteArea(RectF unRettangolo, float zoom)
	{
		Cell cella = null;
		RectF rett = unRettangolo;
		Iterator it = cellsList_.iterator();
		PointF punto;
		ArrayList siti = new ArrayList();

		while(it.hasNext())
		{
			cella = (Cell)it.next();
			float x = (cella.getKernel()).x * zoom;
			float y = (cella.getKernel()).y * zoom;

			if(rett.contains(x, y))
			{
				siti.add(cella);                   
			}
		}
		
		for(int k = 0; k < siti.size(); k++)
		{
			cellsList_.remove((Cell)siti.get(k));
		}

		ArrayList trasporto = (ArrayList)cellsList_.clone();
		this.clear();
		Iterator listaTrasporto = trasporto.iterator();
		
		while(listaTrasporto.hasNext())
		{
			Cell elementoCorrente = (Cell)listaTrasporto.next();
			punto = elementoCorrente.getKernel();
			cella = new Cell(punto);
			cella.setColore(elementoCorrente.getColore());
			addCell(cella);
		}
	}


	private ArrayList cellsList_;
	private static  int DISTANZA_MIN = 5; // Distanza minima tra due siti
	private int maxX, maxY;
	private static final double EPSI = 1E-14;
	private int yyy;


}
