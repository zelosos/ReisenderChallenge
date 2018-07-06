package application;

import java.util.LinkedList;

import javafx.scene.canvas.GraphicsContext;

public class Reisender {
	private LinkedList<Linie> route;
	private LinkedList<Punkt> stadte;

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
		if (stadte.size() < 2)
			return null;
		for (int i=0; i<stadte.size()-1; i++)
			route.add(new Linie(stadte.get(i), stadte.get(i+1)));	
		return route;
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
