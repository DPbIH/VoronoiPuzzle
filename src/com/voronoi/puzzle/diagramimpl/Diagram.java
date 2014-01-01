package com.voronoi.puzzle.diagramimpl;

import java.util.*;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;

import android.graphics.PointF;
import android.graphics.RectF;

public class Diagram 
{
	public Diagram(int sizeX, int sizeY)
	{
		width_ = sizeX;
		height_ = sizeY;
		
		createCellsList();
	}
	
	public Diagram()
	{
		createCellsList();
	}

	private void createCellsList() {
		cellsList_ = new ArrayList<Cell>();
	}

	private void setDiagramSize(int x, int y)
	{
		this.clear();
		
		width_ 	= x;
		height_	= y;
	}
	
	public void generate( int cellsCount )
	{
		clear();
		
		Random generator = new Random();
		for(int j = 1; j <= cellsCount; j++)
		{
			int coordX =  generator.nextInt( width_ );
			int coordY = generator.nextInt( height_ );
			Cell nextCell = new Cell( coordX, coordY );
			addCell( nextCell );
		}
	}
	
	public void scale( int newWidth, int newHeight )
	{
		float xMultiplier = (float)newWidth/width_;
		float yMultiplier = (float)newHeight/height_;
				
		ArrayList<Cell> cellsArrayCopy = new ArrayList<Cell>( getCellsArray() );
		
		setDiagramSize(newWidth, newHeight);
		
		Iterator<Cell> iter = cellsArrayCopy.iterator();
		while( iter.hasNext() )
		{
			PointF oldKernel = iter.next().getKernel();
			PointF newKernel = new PointF(oldKernel.x * xMultiplier, oldKernel.y * yMultiplier);
			addCell( new Cell( newKernel ) );
		}
	}
	
	public void scale( float ratio )
	{
		int newWidth 	= (int) (ratio * width_);
		int newHeight	= (int) (ratio * height_);
		
		scale( newWidth, newHeight );
	}

	public void addCell(Cell newCell)
	{
		Iterator<Cell> cellsIt;
		Iterator<Border> bordersIt;
		yyy = 0;

		ArrayList<Cell> madeCells = new ArrayList<Cell>(); 
		Iterator<Cell> madeCellsIt;

		Iterator<Border> listaFront;

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
			PointF p2 = new PointF(width_, 0);
			PointF p3 = new PointF(width_, height_);
			PointF p4 = new PointF(0, height_);

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


			if( GetDistance( nextCell.getKernel(), border1.getPointFirst() ) < 
					GetDistance( newCell.getKernel(), border1.getPointFirst() ) )
			{
				limit = border1.getPointSecond();
				border1.setPuntoDue(point1);
			}
			else
			{
				limit = border1.getPointFirst();
				border1.setPuntoUno(point1);
			}

			if(border1.isEdge())
			{
				addBorder(newCell,new Border(newCell, null, point1, limit));
			}

			ArrayList<Border> oldFront = new ArrayList<Border>();
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
				if( GetDistance( border.getPointFirst(), newCell.getKernel() ) <
						GetDistance( border.getPointFirst(), nextCell.getKernel() ) )
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

				if( GetDistance( nextCell.getKernel(), border3.getPointFirst() ) < 
						GetDistance( newCell.getKernel(), border3.getPointFirst() ) )
				{
					limit = border3.getPointSecond();
					border3.setPuntoDue(point3);
				}
				else
				{
					limit = border3.getPointFirst();
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
					if( GetDistance( border.getPointFirst(), newCell.getKernel() ) < 
							GetDistance( border.getPointFirst(), nextCell.getKernel() ) )
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
				if( GetDistance( nextCell.getKernel(), border2.getPointFirst() ) <
						GetDistance( newCell.getKernel(), border2.getPointFirst() ) )
				{
					limit = border2.getPointSecond();
					border2.setPuntoDue(point2);
				}
				else
				{
					limit = border2.getPointFirst();
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

					if( GetDistance( nextCell.getKernel(), border3.getPointFirst() ) < 
							GetDistance( newCell.getKernel(), border3.getPointFirst() ) )
					{
						limit = border3.getPointSecond();
						border3.setPuntoDue(point3);
					}
					else
					{
						limit = border3.getPointFirst();
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


						if( GetDistance( border.getPointFirst(), newCell.getKernel() ) < 
								GetDistance( border.getPointFirst(), nextCell.getKernel() ) )
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

			Iterator<Border> listaFrontiereCella = newCell.getBordersIt();
			while(listaFrontiereCella.hasNext())
			{            
				border = (Border) listaFrontiereCella.next();
				if(isEqual(border.getPointFirst().x, width_) && 
						isEqual(border.getPointSecond().x, width_))
				{
					if(edge2)
					{
						point1 = border2.getPointFirst();
						point2 = border2.getPointFirst();

						if(point1.y < border2.getPointSecond().y)
							point1 = border2.getPointSecond();

						if(point2.y > border2.getPointSecond().y) 
							point2 = border2.getPointSecond();

						if(point1.y < border.getPointFirst().y) 
							point1 = border.getPointFirst();

						if(point2.y > border.getPointFirst().y) 
							point2 = border.getPointFirst();

						if(point1.y < border.getPointSecond().y)
							point1 = border.getPointSecond();

						if(point2.y > border.getPointSecond().y) 
							point2 = border.getPointSecond();


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

				if(isEqual(border.getPointFirst().x, 0) &&
						isEqual(border.getPointSecond().x, 0))
				{
					if(edge4)
					{
						point1 = border4.getPointFirst();
						point2 = border4.getPointFirst();


						if(point1.y < border4.getPointSecond().y)
							point1 = border4.getPointSecond();

						if(point2.y > border4.getPointSecond().y) 
							point2 = border4.getPointSecond();

						if(point1.y < border.getPointFirst().y)
							point1 = border.getPointFirst();

						if(point2.y > border.getPointFirst().y) 
							point2 = border.getPointFirst();

						if(point1.y < border.getPointSecond().y)
							point1 = border.getPointSecond();

						if(point2.y > border.getPointSecond().y)
							point2 = border.getPointSecond();


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

				if(isEqual(border.getPointFirst().y,0)
						&& isEqual(border.getPointSecond().y, 0))
				{
					if(edge1)
					{
						point1 = border1.getPointFirst();
						point2 = border1.getPointFirst();


						if(point1.x > border1.getPointSecond().x)
							point1 = border1.getPointSecond();

						if(point2.x < border1.getPointSecond().x) 
							point2 = border1.getPointSecond();

						if(point1.x > border.getPointFirst().x) 
							point1 = border.getPointFirst();

						if(point2.x < border.getPointFirst().x) 
							point2 = border.getPointFirst();

						if(point1.x > border.getPointSecond().x)
							point1 = border.getPointSecond();

						if(point2.x < border.getPointSecond().x) 
							point2 = border.getPointSecond();


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

				if(isEqual(border.getPointFirst().y, height_) && 
						isEqual(border.getPointSecond().y, height_))
				{
					if(edge3)
					{
						point1 = border3.getPointFirst();
						point2 = border3.getPointFirst();


						if(point1.x > border3.getPointSecond().x)
							point1 = border3.getPointSecond();

						if(point2.x < border3.getPointSecond().x)
							point2 = border3.getPointSecond();

						if(point1.x > border.getPointFirst().x)
							point1 = border.getPointFirst();

						if(point2.x < border.getPointFirst().x) 
							point2 = border.getPointFirst();

						if(point1.x > border.getPointSecond().x) 
							point1 = border.getPointSecond();

						if(point2.x < border.getPointSecond().x)
							point2 = border.getPointSecond();


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
		return sito.getBorders().add(frontier);
	}

	public boolean deleteBorder(Cell sito, Border frontier)
	{
		return sito.getBorders().remove(frontier);
	}

	public void deleteCell(Cell cell)
	{
		if(cell == null) return;

		if(cellsList_.contains(cell))
		{
			cellsList_.remove(cell);
			ArrayList<Cell> trasporto = (ArrayList<Cell>)cellsList_.clone();
			this.clear();
			Iterator<Cell> listaTrasporto = trasporto.iterator();
			while(listaTrasporto.hasNext())
			{
				Cell elementoCorrente = (Cell)listaTrasporto.next();
				elementoCorrente.getBorders().clear();
				addCell(elementoCorrente);
			}

		}
	}

	public void clear()
	{
		cellsList_.clear();
	}

	public Iterator<Cell> getCellIterator()
	{
		return cellsList_.iterator();
	}

	public ArrayList<Cell> getCellsArray()
	{
		return cellsList_;
	}


	public Cell getCellByCoordinates(PointF unPunto)
	{
		for(Iterator<Cell> listaSiti = this.getCellIterator(); listaSiti.hasNext();)
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
		for(Iterator<Cell> listaSiti = this.getCellIterator(); listaSiti.hasNext();)
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

	public float getWidth()
	{
		return width_;
	}
	
	public float getHeight()
	{
		return height_;
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

		ArrayList<Cell> trasportoBis = (ArrayList<Cell>)getCellsArray().clone();
		this.clear();
		DISTANZA_MIN = 1;
		trasportoBis.add(cell);
		Iterator<Cell> listaTrasporto = trasportoBis.iterator();
		while(listaTrasporto.hasNext())
		{
			Cell elementoCorrente = (Cell)listaTrasporto.next();
			elementoCorrente.getBorders().clear();
			addCell(elementoCorrente);
		}
		DISTANZA_MIN = 5;
	}

	public String[] toStringArray()
	{
		int idx = 0;
		
		String[] cells = new String[ cellsList_.size() + 1 ];
		cells[idx++] = width_ + "|" + height_; // put size
		
		for( Cell cell: cellsList_ )
		{
			PointF kernel = cell.getKernel();
			float x = kernel.x;
			float y = kernel.y;
			cells[idx++] = x + "|" + y;
		}
		
		return cells;
	}
	
	public void fromStringArray( String[] cells )
	{
		boolean readSize = false;
		
		for( String cell: cells )
		{
			StringTokenizer separator = new StringTokenizer(cell, "|");
			try
			{
				float x 	= Float.parseFloat(separator.nextToken());
				float y 	= Float.parseFloat(separator.nextToken());
				
				if( ! readSize )
				{
					setDiagramSize( (int)x, (int)y );
					readSize = true;
					continue;
				}
				PointF pos 	= new PointF(x, y);
				
				this.addCell( new Cell(pos) );
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
	
	//Metodi per il savetaggio dei punti
	public void save(FileWriter file) throws IOException                                                   
	{
		PrintWriter out = new PrintWriter(file);
		Iterator<Cell> listaCelle = cellsList_.iterator();
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
		Iterator<Cell> it = cellsList_.iterator();
		PointF punto;
		ArrayList<Cell> siti = new ArrayList<Cell>();

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

		ArrayList<Cell> trasporto = (ArrayList<Cell>)cellsList_.clone();
		this.clear();
		Iterator<Cell> listaTrasporto = trasporto.iterator();
		
		while(listaTrasporto.hasNext())
		{
			Cell elementoCorrente = (Cell)listaTrasporto.next();
			punto = elementoCorrente.getKernel();
			cella = new Cell(punto);
			cella.setColore(elementoCorrente.getColore());
			addCell(cella);
		}
	}


	private ArrayList<Cell> cellsList_;
	private static  int DISTANZA_MIN = 5; // Distanza minima tra due siti
	private int width_ = 0, height_ = 0;
	private static final double EPSI = 1E-14;
	private int yyy;


}
