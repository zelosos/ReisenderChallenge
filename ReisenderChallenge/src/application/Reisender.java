package application;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Random;
import javafx.scene.canvas.GraphicsContext;

public class Reisender {
	private LinkedList<Linie> route;
	private LinkedList<Punkt> stadte;
	private double[][] entfernungMatrix;
	private int[] besteReihenfolge = null;
	private double besteLange = Double.MAX_VALUE;

	public Reisender(LinkedList<Punkt> stadte)
	{
		this.stadte = stadte;
		route = new LinkedList<Linie>();
	}

	public LinkedList<Linie> berechneRoute(long startzeit)
	{
		/* Deine Lösung hier........................................
		 * 
		 * Hier ein Beispiel.
		 * Du kannst es sicherlich besser ;)
		 */
		//return berechneRouteNachsterPunkt();
		//return genericRoute(startzeit);
		//return thermodynamic_MonteCarlo(startzeit);
		return twoOpt(startzeit);
	}

	// -----------------------------------------------------------------------------------------------------
	private LinkedList<Linie> twoOpt(long startzeit)
	{
		long deadline = berechneEndzeit(startzeit);
		berechneEntfernungMatrix();
		//entfernungMatrixAusgabe();
		
		int[][] alleGreedyRouten = erzeugeAlleGreedyRouten();
		int besterIndex = 0;
		double d=besteLange;
		for (int i=0; i<alleGreedyRouten.length; i++) // beste Route ermitteln aus Greedy
		{
			d = lange(alleGreedyRouten[i]);
			if (d<besteLange)
			{
				besteReihenfolge = alleGreedyRouten[i];
				besteLange = d;
				besterIndex = i;
			}
		}
		
		long zeitFurEineSchleife = 10;
		optimiereRoute[] optimierer = new optimiereRoute[alleGreedyRouten.length]; // optimierer erstellen
		for (int i=0; i<alleGreedyRouten.length; i++)
			optimierer[i] = new optimiereRoute(deadline, alleGreedyRouten[i], entfernungMatrix);
		int derzeitigerIndex = 0;
		
		// optimierer laufen lassen, solange Zeit verbleibt
		long start = System.currentTimeMillis();
		ArrayList<Integer> laufendeOptimierer = new ArrayList<Integer>();
		for (int i=0; i<optimierer.length; i++)
		{
			if (derzeitigerIndex<alleGreedyRouten.length)
			{
				optimierer[derzeitigerIndex].run();
				laufendeOptimierer.add(derzeitigerIndex);
				derzeitigerIndex++;
			}
		}
		for (int opt: laufendeOptimierer)
		{
			try
			{
				optimierer[opt].join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			d = lange(optimierer[opt].getReihenfolge());
			if (d < besteLange) // wenn besser, dann merken
			{
				besteLange = d;
				besterIndex = opt;
				besteReihenfolge = optimierer[opt].getReihenfolge();
			}
		}
		return konvertiereZuList(besteReihenfolge);
	}
	
	private int[][] erzeugeAlleGreedyRouten()
	{
		int[][] routen = new int[stadte.size()][];
		for (int i=0; i<routen.length; i++)
		{
			routen[i] = erzeugeGreedyReihenfolge(i); 
		}
		return routen;
	}
	
	private LinkedList<Linie> berechneRouteNachsterPunkt()
	{
		if (stadte.size() < 2)
			return null;
		Punkt startpunkt = stadte.get(0);
		LinkedList<Punkt> verbleibendeStadte = (LinkedList<Punkt>)stadte.clone();
		verbleibendeStadte.remove(0);

		while (verbleibendeStadte.size() > 0)
		{
			int besterTreffer = 0;
			double besteDistanz = Double.MAX_VALUE;
			for (int i=0; i<verbleibendeStadte.size(); i++)
			{
				if (startpunkt.distanzZu(verbleibendeStadte.get(i)) < besteDistanz)
				{
					besteDistanz = startpunkt.distanzZu(verbleibendeStadte.get(i));
					besterTreffer = i;
				}	
			}
			route.add(new Linie(startpunkt, verbleibendeStadte.get(besterTreffer)));
			startpunkt = verbleibendeStadte.get(besterTreffer);
			verbleibendeStadte.remove(besterTreffer);
		}
		return route;
	}
	
	private LinkedList<Linie> thermodynamic_MonteCarlo(long startzeit)
	{
		long deadline = berechneEndzeit(startzeit);
		double avgLange = 0.521405433*700*(stadte.size()-1);
		berechneEntfernungMatrix();
		//entfernungMatrixAusgabe();
		Random random = new Random(1234);
		int stadteAnzahl = stadte.size();
		
		int[] weg = erzeugeRandomReihenfolge(random);
		double temperatur = 0.00001;
		long zeitFurEineSchleife = 100;
		int durchgang = 1;

		while (System.currentTimeMillis() + zeitFurEineSchleife < deadline)
		{
			long start = System.currentTimeMillis();
			for (int ersterIndex = 0; ersterIndex < stadteAnzahl; ersterIndex++)
			{
				double wegLange = lange(weg);
				int zweiterIndex = 0;
				while (ersterIndex == zweiterIndex)
					zweiterIndex = random.nextInt(stadteAnzahl-1); // 0 <= j <= stadteAnzahl i!=j
				int min = Integer.min(ersterIndex, zweiterIndex);
				int max = Integer.max(ersterIndex, zweiterIndex);
				int[] tempWeg = new int[stadteAnzahl];
				for (int i = 0; i < min; i++)
					tempWeg[i] = weg[i];
				for (int i = 0; i <= max-min; i++)
					tempWeg[min + i] = weg[max - i];
				for (int i = max + 1; i < stadteAnzahl; i++)
					tempWeg[i] = weg[i];
				double tempWegLange = lange(tempWeg);
				if (tempWegLange < wegLange)
				{
					weg = tempWeg;
					//System.out.println("Beste Strecke " + tempWegLange);
				}
				else
				{
					double x = 0;
					while (x==0)
						x = random.nextDouble(); //0<x<1
					if (x < Math.exp((wegLange - tempWegLange)/ temperatur)) // Berechne Möglichkeit für die Annahme eines schlechteren Ergebnisses
					{
						weg = tempWeg;
						//System.out.println("Akzeptiere schlechteres Ergebnis");
					}
				}
			}
			zeitFurEineSchleife = (System.currentTimeMillis()-start + zeitFurEineSchleife)/2;
			durchgang++;
			besteReihenfolge = weg;
		}
		System.out.println("Durchgang: " + durchgang);
		return konvertiereZuList(besteReihenfolge);
	}
	
	private LinkedList<Linie> genericRoute(long startzeit)
	{
		long deadline = berechneEndzeit(startzeit);
		//deadline += 10*1000;
		double avgLange = 0.521405433*700*(stadte.size()-1);
		berechneEntfernungMatrix();
		//entfernungMatrixAusgabe();
		
		Random random = new Random(1234);
		int populationAnzahl = stadte.size()*2;
		int[][] population = erzeugeGreedyPopulation(populationAnzahl, random);
		double[] bewertung = bewertePopulation(population);
		double[] uberlebensChance = berechneUberlebenschance(bewertung);
		
		long zeitFurEineSchleife = 10;
		double v=0;
		double d=besteLange;
		System.out.println(d);
		while (System.currentTimeMillis()+zeitFurEineSchleife < deadline)
		{
			long start = System.currentTimeMillis();
			//System.out.println("Beste Strecke: " + besteLange);
			//System.out.println("Beste Reihenfolge: " + arrayToString(besteReihenfolge));
			//System.out.println("population: " + arrayToString(population[0]));
			double variation = besteLange/(avgLange*2);
			if (v != variation)
			{
				v = variation;
				System.out.println(variation);
			}
			if (d > besteLange)
			{
				d = besteLange;
				System.out.println(besteLange);
			}
			population = nachsteGeneration(population, uberlebensChance, variation, random); 
			bewertung = bewertePopulation(population);
			uberlebensChance = berechneUberlebenschance(bewertung);
			zeitFurEineSchleife = (System.currentTimeMillis()-start + zeitFurEineSchleife)/2;
		}
		
		//System.out.println(lange(besteReihenfolge) + " : " + arrayToString(besteReihenfolge));
		return konvertiereZuList(besteReihenfolge);
	}
	
	private void entfernungMatrixAusgabe()
	{
		for (int i=0; i<entfernungMatrix.length; i++)
		{
			String ausgabe = "";
			for (int j=0; j<entfernungMatrix.length; j++)
			{
				ausgabe += Math.round(entfernungMatrix[i][j]) + ", ";
			}
			System.out.println(ausgabe);
		}
	}
	
	private String arrayToString(int[] array)
	{
		String ausgabe = "";
		for (int i=0; i<array.length; i++)
			ausgabe += array[i] + ", ";
		return ausgabe;
	}
	
	private int[][] nachsteGeneration(int[][] derzeitigePopulation, double[] uberlebensChance, double variationRelativ, Random random)
	{
		
		if (variationRelativ >= 1)
			return erzeugeRandomPopulation(derzeitigePopulation.length-1, random);
		variationRelativ /= 2;
		int[][] population = new int[derzeitigePopulation.length][];
		for (int i=0; i<derzeitigePopulation.length; i++)
		{
			population[i] = wahleUberlebenden(derzeitigePopulation, uberlebensChance, random.nextDouble());
			population[i] = mutiere(population[i], variationRelativ, random); 
		}
		return population;
	}
	
	private int[] mutiere(int[] reihenfolge, double variation, Random random)
	{
		double zufallswert = random.nextDouble();
		reihenfolge = reihenfolge.clone();
		for (int i=0; i<reihenfolge.length; i++)
		{
			if (zufallswert<variation)
			{
				int randomindex = random.nextInt(reihenfolge.length);
				int temp = reihenfolge[i];
				reihenfolge[i] = reihenfolge[randomindex];
				reihenfolge[randomindex] = temp;
			}
			zufallswert = random.nextDouble();
		}
		return reihenfolge;
	}
	
	private int[] wahleUberlebenden(int[][] population, double[] uberlebensChance, double wahrscheinlichkeitsWert)
	{
		double summe = 0;
		int index = 0;
		while ((summe += uberlebensChance[index]) <= wahrscheinlichkeitsWert)
			index++;
		if (index > population.length)
			index = population.length;
		return population[index];
	}
	
	private double[] berechneUberlebenschance(double[] bewertung)
	{
		double[] uberlebensChance = new double[bewertung.length];
		double summe = 0;
		for (int i=0; i<bewertung.length; i++)
		{
			uberlebensChance[i] = bewertung[i]/besteLange;
			summe += uberlebensChance[i];
		}
		for (int i=0; i<bewertung.length; i++) // normalisieren;
			uberlebensChance[i] = uberlebensChance[i]/summe;  
		return uberlebensChance;
	}
	
	private double[] bewertePopulation(int[][] population)
	{
		double[] bewertung = new double[population.length];
		double minLange = Double.MAX_VALUE;
		int minIndex = 0;
		for (int i=0; i<bewertung.length; i++)
		{
			bewertung[i] = lange(population[i]);
			if (bewertung[i] < minLange)
			{
				minLange = bewertung[i];
				minIndex = i;
			}
		}
		//System.out.println("lange " + lange(population[0]));
		//System.out.println("Beste Min: " + besteLange);
		//System.out.println("Dieses Min: " + minLange);
		//System.out.println();
		
		if (minLange < besteLange)
		{
			besteReihenfolge = population[minIndex];
			besteLange = minLange;
		}
		return bewertung;
	}
	
	private int[][] erzeugeRandomPopulation(int populationAnzahl, Random random)
	{
		int[][] population = new int[populationAnzahl][];
		for (int i=0; i<populationAnzahl; i++)
			population[i] = erzeugeRandomReihenfolge(random);
		return population;
	}
	
	private int[][] erzeugeGreedyPopulation(int populationAnzahl, Random random)
	{
		int[][] population = new int[populationAnzahl][];
		for (int i=0; i<populationAnzahl; i++)
			population[i] = erzeugeGreedyReihenfolge(random.nextInt(stadte.size() -1));
		return population;
	}
	
	private int[] erzeugeGreedyReihenfolge(int startIndex)
	{
		int[] reihenfolge = new int[stadte.size()];
		boolean[] verwendet = new boolean[stadte.size()];
		for (int i=0; i<reihenfolge.length; i++)
			verwendet[i]= false ;
		reihenfolge[0] = startIndex;
		verwendet[startIndex] = true;
		for (int i=1; i<reihenfolge.length; i++)
		{
			int nachsterIndex = 0;
			double besteDistanz = Double.MAX_VALUE;
			for (int j=0; j<reihenfolge.length; j++)
			{
				if (!verwendet[j] && entfernungMatrix[reihenfolge[i-1]][j] < besteDistanz)
				{
					besteDistanz = entfernungMatrix[reihenfolge[i-1]][j];
					nachsterIndex = j;
				}
			}
			//System.out.println(nachsterIndex);
			reihenfolge[i] = nachsterIndex;
			verwendet[nachsterIndex] = true;
		}
		//System.out.println(arrayToString(reihenfolge));
		return reihenfolge;
	}
	
	private int[] erzeugeRandomReihenfolge(Random random)
	{
		int[] reihenfolge = new int[stadte.size()];
		LinkedList<Integer> verbleibend = new LinkedList<Integer>();
		for (int i=0; i<reihenfolge.length; i++)
			verbleibend.add(i);
		int randomZahl;
		for (int i=0; i<reihenfolge.length; i++)
		{
			randomZahl = random.nextInt(verbleibend.size());
			reihenfolge[i] = verbleibend.remove(randomZahl);
		}
		return reihenfolge;
	}
	
	private LinkedList<Linie> konvertiereZuList(int[] reihenfolge)
	{
		LinkedList<Linie> liste = new LinkedList<Linie>();
		for (int i=0; i<reihenfolge.length -1; i++)
			liste.add(new Linie(stadte.get(reihenfolge[i]), stadte.get(reihenfolge[i+1])));
		return liste;
	}
	
	
	
	private void berechneEntfernungMatrix()
	{
		if (stadte == null)
			return;
		entfernungMatrix = new double[stadte.size()][stadte.size()];
		for (int i=0; i<stadte.size(); i++)
		{
			entfernungMatrix[i][i] = 0; 
			for (int j=i+1; j<stadte.size(); j++)
			{
				double entfernung = stadte.get(i).distanzZu(stadte.get(j));
				entfernungMatrix[i][j]= entfernung;
				entfernungMatrix[j][i]= entfernung;
			}
		}
	}
	
	private double lange(int[] reihenfolge)
	{
		double lange = 0;
		for (int i = 0; i < reihenfolge.length -1; i++)
			lange += entfernungMatrix[reihenfolge[i]][reihenfolge[i+1]];
		return lange;
	}
	
	private long berechneEndzeit(long startzeit)
	{
		long korrekturfaktor = 10;
		return verbleibendeZeit(startzeit) + startzeit - korrekturfaktor;
	}
	
	
	// nur zur Hilfe :)
	private long verbleibendeZeit(long startZeit)
	{
		double zeitMultiplier = 6/1000.0;
		long vorhandeneZeit = (long)(zeitMultiplier*stadte.size()*stadte.size()*1000)/1;
		long verbrauchteZeit = System.currentTimeMillis() - startZeit;
		return vorhandeneZeit - verbrauchteZeit;
	}

	public double score(int seitenLange)
	{
		int stadteAnzahl = stadte.size();
		long startZeit = System.currentTimeMillis();
		route = berechneRoute(startZeit);
		long endeZeit = System.currentTimeMillis();
		double berechnungszeitInSek = (endeZeit - startZeit)/1000.0;
		if (berechnungszeitInSek == 0)
			berechnungszeitInSek += 0.001;

		double zeitMultiplier = 6/1000.0;
		double vorhandeneZeitInSekunden = zeitMultiplier*stadteAnzahl*stadteAnzahl;
		double verbleibendeSekunden = vorhandeneZeitInSekunden - berechnungszeitInSek;

		String fehler = "";
		if (stadteAnzahl != stadte.size())
			fehler += "Städteanzahl hat sich verändert! ";
		if (!alleStadteBereist())
			fehler += "Nicht alle Städte bereist! ";
		if (!routeIstZusammenhangend())
			fehler += "Route ist nicht zusammenhängend! ";
		if (verbleibendeSekunden < 0)
			fehler += "Zeitlimit überschritten! ";;
			if (fehler != "")
			{
				System.out.println(fehler);
				printInfo(verbleibendeSekunden, 0);
				return 0;
			}

			double avgDistance = 0.521405433*seitenLange;
			double score = (avgDistance * (stadteAnzahl-1) + verbleibendeSekunden) /langeDerRoute();
			printInfo(verbleibendeSekunden, score);
			return score;
	}

	private void printInfo(double verbleibendeSekunden, double score)
	{
		System.out.println("Städteanzahl: " + (stadte.size()));
		System.out.println("Routenlänge: " + langeDerRoute());
		System.out.println("verbleibende Senkunden: " + verbleibendeSekunden);
		System.out.println("Score: " + score);
		System.out.println();
	}

	public double langeDerRoute()
	{
		return langeDerRoute(route);
	}

	public double langeDerRoute(LinkedList<Linie> r)
	{
		if (r == null)
			return 0;
		double lange = 0;
		for (Linie linie: r)
			lange += linie.lange();
		return lange;
	}

	public boolean alleStadteBereist()
	{
		if (route == null)
			return false;
		for (Punkt stadt: stadte)
			if (!stadtInRouteEnthalten(stadt))
				return false;
		return true;
	}

	private boolean stadtInRouteEnthalten(Punkt stadt)
	{
		if (route == null)
			return false;
		for (Linie linie: route)
		{
			if (stadt.equals(linie.getStart()))
				return true;
			if (stadt.equals(linie.getEnde()))
				return true;
		}
		return false;
	}

	private boolean routeIstZusammenhangend()
	{
		return routeIstZusammenhangend(route);
	}

	private boolean routeIstZusammenhangend(LinkedList<Linie> r)
	{
		if (r == null)
			return false;
		if (r.size() < 2)
			return true;
		for (int i=0; i < r.size()-1; i++)
			if (!r.get(i).getEnde().equals(r.get(i+1).getStart())) // Wenn Ende von Strecke i != Start von Strecke i+1
				return false;
		return true;
	}

	public void zeichneAlles(GraphicsContext graphischeElemente)
	{
		zeichneStadte(graphischeElemente);
		zeichneRoute(graphischeElemente);
	}

	public void zeichneStadte(GraphicsContext gc)
	{
		if (stadte == null)
			return;
		for (Punkt stadt: stadte)
			stadt.zeichne(gc);
	}

	public void zeichneRoute(GraphicsContext gc)
	{
		if (route == null)
			return;
		for (Linie strecke: route)
			strecke.zeichne(gc);
	}
}
