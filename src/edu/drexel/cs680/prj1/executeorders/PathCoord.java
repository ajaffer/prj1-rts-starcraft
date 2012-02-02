package edu.drexel.cs680.prj1.executeorders;

public class PathCoord {

	public double x, y;
	public Heuristic heur;
	
	public PathCoord(double xVal, double yVal)
	{
		x = xVal;
		y = yVal;
	}
	public PathCoord(double xVal, double yVal, PathCoord start, PathCoord goal)
	{
		int g, h;
		double xDiffh, xDiffg, yDiffh, yDiffg = 0;
		x = xVal;
		y = yVal;
		xDiffh = Math.abs(goal.x - xVal);
		yDiffh = Math.abs(goal.y - yVal);
		
		xDiffg = Math.abs(start.x - xVal);
		yDiffg = Math.abs(start.y - yVal);
		
		h = (int) (Math.round(xDiffh) + Math.round(yDiffh));
		g = (int) (Math.round(xDiffg) + Math.round(yDiffg));
		
		heur = new Heuristic(g,h);
	}
}
