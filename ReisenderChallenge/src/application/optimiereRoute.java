package application;

public class optimiereRoute extends Thread{

	private long deadline;
	double besteLange;
	int[] reihenfolge;
	double[][] entfernungMatrix;
	
	
	public optimiereRoute(long deadline, int[] reihenfolge, double[][] entfernungMatrix)
	{
		this.deadline = deadline;
		this.reihenfolge = reihenfolge;
		this.entfernungMatrix = entfernungMatrix;
		besteLange = lange(reihenfolge);
	}
	
	public void setReihenfolge(int[] reihenfolge)
	{
		this.reihenfolge = reihenfolge;
	}
	
	public int[] getReihenfolge()
	{
		return reihenfolge;
	}
	
	@Override
	public void run()
	{
		long zeitFurEineSchleife = 10;
		long puffer = 100;
		boolean verandert = true;
		
		while (System.currentTimeMillis()+zeitFurEineSchleife < deadline+puffer && verandert)
		{
			long start = System.currentTimeMillis();
			verandert = false;
			for (int i=0; i<reihenfolge.length-1; i++)
			{
				for (int j=i+1; j<reihenfolge.length; j++)
				{
					verandert = twoOpt(i, j);
				}
			}
			zeitFurEineSchleife = (System.currentTimeMillis()-start + zeitFurEineSchleife)/2;
		}
	}
	
	private boolean twoOpt(int pos1, int pos2)
	{
		int[] tempWeg = new int[reihenfolge.length];
		for (int i = 0; i < pos1; i++)
			tempWeg[i] = reihenfolge[i];
		for (int i = 0; i <= pos2-pos1; i++)
			tempWeg[pos1 + i] = reihenfolge[pos2 - i];
		for (int i = pos2 + 1; i < reihenfolge.length; i++)
			tempWeg[i] = reihenfolge[i];
		double d = lange(tempWeg);
		if (d < besteLange)
		{
			reihenfolge = tempWeg;
			besteLange = d;
			return true;
		}
		return false;
	}
	
	private double lange(int[] reihenfolge)
	{
		double lange = 0;
		for (int i = 0; i < reihenfolge.length -1; i++)
			lange += entfernungMatrix[reihenfolge[i]][reihenfolge[i+1]];
		return lange;
	}
	
	private String arrayToString(int[] array)
	{
		String ausgabe = "";
		for (int i=0; i<array.length; i++)
			ausgabe += array[i] + ", ";
		return ausgabe;
	}
}
