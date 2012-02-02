package edu.drexel.cs680.prj1.executeorders;

public class Heuristic {
	
	private int g, h;
	public int f;
	
	public Heuristic(int gVal, int hVal)
	{
		g = gVal;
		h = hVal;
		f = g + h;
	}
	public void setG(int gVal)
	{
		g = gVal;
		f = g + h;
	}
	public void setH(int hVal)
	{
		h = hVal;
		f = g + h;
	}
	

}
