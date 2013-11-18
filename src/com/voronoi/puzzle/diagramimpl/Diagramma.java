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

public class Diagramma 
{
	public Diagramma(int sizeX, int sizeY)
	{
		maxX = sizeX;
		maxY = sizeY;
		celle = new ArrayList();
	}

	public void setDiagramSize(int x, int y)
	{
		this.clear();
		maxX = x;
		maxY = y;
	}

	public void aggiungiCellaRossa(Cella cella)
	{
		cella.setColore(true);
		aggiungiCella(cella);
	}

	public void aggiungiCellaBlu(Cella cella)
	{
		cella.setColore(false);
		aggiungiCella(cella);
	}

	public void aggiungiCella(Cella cella)
	{
		Iterator listaCelle;
		Iterator listaFrontiere;
		yyy = 0;

		ArrayList celleFatte = new ArrayList(); 
		Iterator listaCelleFatte;

		Iterator listaFront;

		Cella cella1, cella2, cellaFatta;
		Frontiera front, front1, front2, front3, front4, newFront;
		PointF punto1, punto2, punto3, punto, limit;

		boolean loop = false; 
		boolean doneFlag = false;

		front1 = front2 = front3 = front4 = front = null;
		punto1 = punto2 = punto3 = null;
		cella1 = cella2 = null;

		if(trovaSito(cella.getKernel()) != null)
		{
			return;
		}

		celle.add(cella);
		if(celle.size() == 1)
		{
			PointF p1 = new PointF(0, 0);
			PointF p2 = new PointF(maxX, 0);
			PointF p3 = new PointF(maxX, maxY);
			PointF p4 = new PointF(0, maxY);

			this.aggiungiFrontiera(cella,new Frontiera(cella, null, p1, p4));
			this.aggiungiFrontiera(cella,new Frontiera(cella, null, p4, p3));
			this.aggiungiFrontiera(cella,new Frontiera(cella, null, p3, p2));
			this.aggiungiFrontiera(cella,new Frontiera(cella, null, p2, p1));

			return; 
		}


		listaCelle = celle.iterator(); //reinizializzazione della listaCelle 
		while(listaCelle.hasNext())
		{
			// la cella sucessiva...cella1
			cella1 = (Cella)listaCelle.next();
			doneFlag = false;


			listaCelleFatte = celleFatte.iterator(); //inizzializzazione di listaCelleFatte
			while(listaCelleFatte.hasNext()) 
			{
				cellaFatta = (Cella)listaCelleFatte.next();
				if(cella1 == cellaFatta)
				{
					doneFlag = true;
					break;
				}

			}

			if(doneFlag) continue;

			//Per le altre celle  continua con le istruzioni:
			front1 = front2 = null;
			listaFrontiere = cella1.getFrontiere();
			while(listaFrontiere.hasNext())
			{
				front = (Frontiera)listaFrontiere.next();
				punto = front.trovaIntersezione(cella);

				if(punto == null) continue;

				//if(punto != null)
					//{
					if(front1 == null)
					{
						front1 = front;
						punto1 = punto;
					}
					else
					{
						front2 = front;
						punto2 = punto;
						break;
					}
					//}
			} 


			// Serve per scorrere nella listaCelle
			if((front1 == null) || (front2 == null))
			{
				continue;
			}

			// la nuova frontiera fra cella e cella1
			newFront = new Frontiera(cella, cella1, punto1, punto2);
			// per ogni frontiera trovata si aggiunge la nuova frontiera e il nuovo vicino
			aggiungiFrontiera(cella, newFront);
			aggiungiFrontiera(cella1, newFront); 


			if( GetDistance( cella1.getKernel(), front1.getPuntoUno() ) < 
					GetDistance( cella.getKernel(), front1.getPuntoUno() ) )
			{
				limit = front1.getPuntoDue();
				front1.setPuntoDue(punto1);
			}
			else
			{
				limit = front1.getPuntoUno();
				front1.setPuntoUno(punto1);
			}

			if(front1.isEdge())
			{
				aggiungiFrontiera(cella,new Frontiera(cella, null, punto1, limit));
			}

			ArrayList oldFront = new ArrayList();
			listaFrontiere = cella1.getFrontiere();
			while(listaFrontiere.hasNext())
			{
				front = (Frontiera)listaFrontiere.next();

				if(front == front1) 
					continue;
				if(front == front2) 
					continue;
				if(front == newFront) 
					continue;

				// Scorre sui punti frontiera della cella e verifica
				// se sono stati aggiornati, eventualmente gli elimina.
				if( GetDistance( front.getPuntoUno(), cella.getKernel() ) <
						GetDistance( front.getPuntoUno(), cella1.getKernel() ) )
				{
					oldFront.add(front);
					if(front.isEdge())
					{
						aggiungiFrontiera(cella,front);
						front.setCellaUno(cella);
					}


				}
			}


			listaFrontiere = oldFront.iterator();
			while(listaFrontiere.hasNext())
			{
				this.cancellaFrontiera(cella1,(Frontiera)listaFrontiere.next());
			}


			cella2 = cella1; //conserva il riferimento alla prima cella visionata
			celleFatte.add(cella1);

			//Trova uno delle frontiere della cella sucessiva che
			//non sia però quella comune alla cella precedente(cella1)
			while((!(front1.isEdge())) && (!loop))
			{
				cella1 = front1.getVicinoDi(cella1);

				listaFrontiere = cella1.getFrontiere();
				while(listaFrontiere.hasNext())
				{
					front3 = (Frontiera)listaFrontiere.next();
					punto3 = front3.trovaIntersezione(cella);

					if((punto3 != null) && (front3 != front1))
						break;
				}


				if(front3 == front2)
				{
					punto3 = punto2;
					loop = true;
				} 

				newFront = new Frontiera(cella, cella1, punto1, punto3);
				this.aggiungiFrontiera(cella, newFront);
				this.aggiungiFrontiera(cella1, newFront);

				if( GetDistance( cella1.getKernel(), front3.getPuntoUno() ) < 
						GetDistance( cella.getKernel(), front3.getPuntoUno() ) )
				{
					limit = front3.getPuntoDue();
					front3.setPuntoDue(punto3);
				}
				else
				{
					limit = front3.getPuntoUno();
					front3.setPuntoUno(punto3);
				}



				if(front3.isEdge())
					this.aggiungiFrontiera(cella, new Frontiera(cella, null, punto3, limit));

				oldFront.clear(); //Serve per reinizilizzare l'oldFront
				listaFrontiere = cella1.getFrontiere();
				while(listaFrontiere.hasNext())
				{
					front = (Frontiera)listaFrontiere.next();
					if(front == front1) 
						continue;
					if(front == front3) 
						continue;
					if(front == newFront) 
						continue;

					// Controllo sui punti consecutivi dello scheletro della
					// cella successiva(analogamente a prima). Se un punto é più
					// vicino alla nuova cella(quella entrante o corrente) che
					// alla cella alla quale appartenevano  vuol dire che non le 
					// appartengono più!
					if( GetDistance( front.getPuntoUno(), cella.getKernel() ) < 
							GetDistance( front.getPuntoUno(), cella1.getKernel() ) )
					{
						oldFront.add(front);

						if(front.isEdge())
						{
							this.aggiungiFrontiera(cella, front);
							front.setCellaUno(cella);
						}

					}
				}


				listaFrontiere = oldFront.iterator();
				while(listaFrontiere.hasNext())
				{
					this.cancellaFrontiera(cella1, (Frontiera)listaFrontiere.next());
				}



				front1 = front3;
				punto1 = punto3;
				celleFatte.add(cella1);

			} 



			cella1 = cella2;
			if(!loop)
			{
				if( GetDistance( cella1.getKernel(), front2.getPuntoUno() ) <
						GetDistance( cella.getKernel(), front2.getPuntoUno() ) )
				{
					limit = front2.getPuntoDue();
					front2.setPuntoDue(punto2);
				}
				else
				{
					limit = front2.getPuntoUno();
					front2.setPuntoUno(punto2);
				}



				if(front2.isEdge())
					this.aggiungiFrontiera(cella, new Frontiera(cella, null, punto2, limit));
			}
			if(!loop)
			{

				while((!front2.isEdge()))
				{
					cella1 = front2.getVicinoDi(cella1);       
					listaFrontiere = cella1.getFrontiere();
					while(listaFrontiere.hasNext())
					{
						front3 = (Frontiera)listaFrontiere.next();
						punto3 = front3.trovaIntersezione(cella);

						if((punto3 != null) && (front3 != front2))
							break;
					}



					if(front3 == front1)
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

					newFront = new Frontiera(cella, cella1, punto2, punto3);
					this.aggiungiFrontiera(cella, newFront);
					this.aggiungiFrontiera(cella1, newFront);

					if( GetDistance( cella1.getKernel(), front3.getPuntoUno() ) < 
							GetDistance( cella.getKernel(), front3.getPuntoUno() ) )
					{
						limit = front3.getPuntoDue();
						front3.setPuntoDue(punto3);
					}
					else
					{
						limit = front3.getPuntoUno();
						front3.setPuntoUno(punto3);
					}


					if(front3.isEdge())
						aggiungiFrontiera(cella, new Frontiera(cella, null, punto3, limit));

					oldFront.clear();
					listaFrontiere = cella1.getFrontiere();
					while(listaFrontiere.hasNext())
					{

						front = (Frontiera)listaFrontiere.next();
						if(front == front2) 
							continue;
						if(front == front3) 
							continue;
						if(front == newFront) 
							continue;


						if( GetDistance( front.getPuntoUno(), cella.getKernel() ) < 
								GetDistance( front.getPuntoUno(), cella1.getKernel() ) )
						{
							oldFront.add(front);

							if(front.isEdge())
							{
								this.aggiungiFrontiera(cella, front);
								front.setCellaUno(cella);
							}

						}
					}


					listaFront = oldFront.iterator();
					while(listaFront.hasNext())
					{
						this.cancellaFrontiera(cella1, (Frontiera)listaFront.next());
					}

					celleFatte.add(cella1);
					front2 = front3;
					punto2 = punto3;

				} 
			}

			oldFront.clear();
			boolean edge1 = false, edge2 = false, edge3 = false, edge4 = false;

			Iterator listaFrontiereCella = cella.getFrontiere();
			while(listaFrontiereCella.hasNext())
			{            
				front = (Frontiera) listaFrontiereCella.next();
				if(isUguale(front.getPuntoUno().x, maxX) && 
						isUguale(front.getPuntoDue().x, maxX))
				{
					if(edge2)
					{
						punto1 = front2.getPuntoUno();
						punto2 = front2.getPuntoUno();

						if(punto1.y < front2.getPuntoDue().y)
							punto1 = front2.getPuntoDue();

						if(punto2.y > front2.getPuntoDue().y) 
							punto2 = front2.getPuntoDue();

						if(punto1.y < front.getPuntoUno().y) 
							punto1 = front.getPuntoUno();

						if(punto2.y > front.getPuntoUno().y) 
							punto2 = front.getPuntoUno();

						if(punto1.y < front.getPuntoDue().y)
							punto1 = front.getPuntoDue();

						if(punto2.y > front.getPuntoDue().y) 
							punto2 = front.getPuntoDue();


						front.setPuntoUno(punto1);
						front.setPuntoDue(punto2);

						oldFront.add(front2);
						front2 = front;
					}
					else
					{
						edge2 = true;
						front2 = front;
					}
				} 

				if(isUguale(front.getPuntoUno().x, 0) &&
						isUguale(front.getPuntoDue().x, 0))
				{
					if(edge4)
					{
						punto1 = front4.getPuntoUno();
						punto2 = front4.getPuntoUno();


						if(punto1.y < front4.getPuntoDue().y)
							punto1 = front4.getPuntoDue();

						if(punto2.y > front4.getPuntoDue().y) 
							punto2 = front4.getPuntoDue();

						if(punto1.y < front.getPuntoUno().y)
							punto1 = front.getPuntoUno();

						if(punto2.y > front.getPuntoUno().y) 
							punto2 = front.getPuntoUno();

						if(punto1.y < front.getPuntoDue().y)
							punto1 = front.getPuntoDue();

						if(punto2.y > front.getPuntoDue().y)
							punto2 = front.getPuntoDue();


						front.setPuntoUno(punto1);
						front.setPuntoDue(punto2);

						oldFront.add(front4);
						front4=front;
					}
					else
					{
						edge4 = true;
						front4 = front;
					}
				}

				if(isUguale(front.getPuntoUno().y,0)
						&& isUguale(front.getPuntoDue().y, 0))
				{
					if(edge1)
					{
						punto1 = front1.getPuntoUno();
						punto2 = front1.getPuntoUno();


						if(punto1.x > front1.getPuntoDue().x)
							punto1 = front1.getPuntoDue();

						if(punto2.x < front1.getPuntoDue().x) 
							punto2 = front1.getPuntoDue();

						if(punto1.x > front.getPuntoUno().x) 
							punto1 = front.getPuntoUno();

						if(punto2.x < front.getPuntoUno().x) 
							punto2 = front.getPuntoUno();

						if(punto1.x > front.getPuntoDue().x)
							punto1 = front.getPuntoDue();

						if(punto2.x < front.getPuntoDue().x) 
							punto2 = front.getPuntoDue();


						front.setPuntoUno(punto1);
						front.setPuntoDue(punto2);

						oldFront.add(front1);
						front1=front;
					}
					else
					{
						edge1 = true;
						front1 = front;
					}
				} 

				if(isUguale(front.getPuntoUno().y, maxY) && 
						isUguale(front.getPuntoDue().y, maxY))
				{
					if(edge3)
					{
						punto1 = front3.getPuntoUno();
						punto2 = front3.getPuntoUno();


						if(punto1.x > front3.getPuntoDue().x)
							punto1 = front3.getPuntoDue();

						if(punto2.x < front3.getPuntoDue().x)
							punto2 = front3.getPuntoDue();

						if(punto1.x > front.getPuntoUno().x)
							punto1 = front.getPuntoUno();

						if(punto2.x < front.getPuntoUno().x) 
							punto2 = front.getPuntoUno();

						if(punto1.x > front.getPuntoDue().x) 
							punto1 = front.getPuntoDue();

						if(punto2.x < front.getPuntoDue().x)
							punto2 = front.getPuntoDue();


						front.setPuntoUno(punto1);
						front.setPuntoDue(punto2);

						oldFront.add(front3);
						front3=front;
					}
					else
					{
						edge3 = true;
						front3 = front;
					}
				} 

			} 



			listaFront = oldFront.iterator();
			while(listaFront.hasNext())
			{
				front = (Frontiera) listaFront.next();
				this.cancellaFrontiera(cella, front);
			}

		} 

	}
	
	private static float GetDistance(PointF point1, PointF point2)
	{
	    float a = point2.x - point1.x;
	    float b = point2.y - point1.y;
	    return (float)Math.sqrt(a * a + b * b);
	}

	public boolean aggiungiFrontiera(Cella sito, Frontiera frontier)
	{
		Cella p = frontier.getVicinoDi(sito);
		return sito.getVfrontiere().add(frontier);
	}

	public boolean cancellaFrontiera(Cella sito, Frontiera frontier)
	{
		Cella p = frontier.getVicinoDi(sito);
		return sito.getVfrontiere().remove(frontier);
	}

	public void cancellaCella(Cella sito)
	{
		if(sito == null) return;

		if(celle.contains(sito))
		{
			celle.remove(sito);
			ArrayList trasporto = (ArrayList)celle.clone();
			this.clear();
			Iterator listaTrasporto = trasporto.iterator();
			while(listaTrasporto.hasNext())
			{
				Cella elementoCorrente = (Cella)listaTrasporto.next();
				elementoCorrente.getVfrontiere().clear();
				aggiungiCella(elementoCorrente);
			}

		}
	}

	public void clear()
	{
		celle.clear();
	}

	public Iterator getCelle()
	{
		return celle.iterator();
	}

	public ArrayList getVCelle()
	{
		return celle;
	}


	public Cella trovaSito(PointF unPunto)
	{
		for(Iterator listaSiti = this.getCelle(); listaSiti.hasNext();)
		{
			Cella currentSite = (Cella)listaSiti.next();
			// Qua esiste un'inconsistenza con i pixel, non sempre!
			if( GetDistance( currentSite.getKernel(), unPunto) < (DISTANZA_MIN / 2 - 0.1))            
			{
				return currentSite;
			}
		}
		return null;
	}

	public Cella siteIsDragged(PointF unPunto, double zom)
	{
		Cella tempSito = null;
		for(Iterator listaSiti = this.getCelle(); listaSiti.hasNext();)
		{
			Cella currentSite = (Cella)listaSiti.next();
			if( GetDistance( currentSite.getKernel(), unPunto) < (DISTANZA_MIN * 30 / (zom * 0.6))) // Qua esiste un problema di tunning           
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

	public boolean isUguale(double num, int numIntero)
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


	public void aggiungiCellaDragged(Cella sito)
	{
		if(sito == null) return;

		ArrayList trasportoBis = (ArrayList)getVCelle().clone();
		this.clear();
		DISTANZA_MIN = 1;
		trasportoBis.add(sito);
		Iterator listaTrasporto = trasportoBis.iterator();
		while(listaTrasporto.hasNext())
		{
			Cella elementoCorrente = (Cella)listaTrasporto.next();
			elementoCorrente.getVfrontiere().clear();
			aggiungiCella(elementoCorrente);
		}
		DISTANZA_MIN = 5;
	}

	//Metodi per il savetaggio dei punti
	public void saveDati(FileWriter file) throws IOException                                                   
	{
		PrintWriter out = new PrintWriter(file);
		Iterator listaCelle = celle.iterator();
		while(listaCelle.hasNext())
		{
			Cella cella = (Cella)(listaCelle.next());
			PointF kernel = cella.getKernel();
			double x = kernel.x;
			double y = kernel.y;
			boolean colore = cella.getColore();
			out.println(x + "|" + y + "|" + Boolean.toString(colore));
		}
		file.close();
	}

	public void loadDati(FileReader file) throws IOException 
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
				Cella cella = new Cella (p);
				cella.setColore(colore);
				this.aggiungiCella(cella);
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

	public void cancellaArea(RectF unRettangolo, float zoom)
	{
		Cella cella = null;
		RectF rett = unRettangolo;
		Iterator it = celle.iterator();
		PointF punto;
		ArrayList siti = new ArrayList();

		while(it.hasNext())
		{
			cella = (Cella)it.next();
			float x = (cella.getKernel()).x * zoom;
			float y = (cella.getKernel()).y * zoom;

			if(rett.contains(x, y))
			{
				siti.add(cella);                   
			}
		}
		
		for(int k = 0; k < siti.size(); k++)
		{
			celle.remove((Cella)siti.get(k));
		}

		ArrayList trasporto = (ArrayList)celle.clone();
		this.clear();
		Iterator listaTrasporto = trasporto.iterator();
		
		while(listaTrasporto.hasNext())
		{
			Cella elementoCorrente = (Cella)listaTrasporto.next();
			punto = elementoCorrente.getKernel();
			cella = new Cella(punto);
			cella.setColore(elementoCorrente.getColore());
			aggiungiCella(cella);
		}
	}

	public void help()
	{
		celle.remove(celle.size() - 1);
		ArrayList trasporto = (ArrayList)celle.clone();
		this.clear();
		Iterator listaTrasporto = trasporto.iterator();
		while(listaTrasporto.hasNext())
		{
			Cella elementoCorrente = (Cella)listaTrasporto.next();
			elementoCorrente.getVfrontiere().clear();
			aggiungiCella(elementoCorrente);

		}
	}


	private ArrayList celle;
	private static  int DISTANZA_MIN = 5; // Distanza minima tra due siti
	private int maxX, maxY;
	private static final double EPSI = 1E-14;
	private int yyy;


}
