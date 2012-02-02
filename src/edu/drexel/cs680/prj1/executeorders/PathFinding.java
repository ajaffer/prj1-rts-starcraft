package edu.drexel.cs680.prj1.executeorders;

import java.util.ArrayList;

public class PathFinding {

	private ArrayList<PathCoord> goalPath;
	PathCoord start, goal;
	public String strAlgName;
	
	public PathFinding(double xStart, double yStart, double xGoal, double yGoal, String strAlgorithm)
	{
		strAlgName = strAlgorithm;
		PathCoord start0 = new PathCoord(xStart, yStart);
		PathCoord goal0 = new PathCoord(xGoal, yGoal);
		start = new PathCoord(xStart, yStart, start0, goal0);
		goal = new PathCoord(xGoal, yGoal);
		
		if(strAlgName.equals("AStar"))
			StartAStar();
	}
	
	public void StartAStar()
	{
		ArrayList<PathCoord> listOpen = new ArrayList<PathCoord>();
		ArrayList<PathCoord> listClosed = new ArrayList<PathCoord>();
		ArrayList<PathCoord> listOK = new ArrayList<PathCoord>();
		
		
		listOpen.add(start);
		
		while(listOpen.size()>0)
		{
			
		}
	}
	
	public void GetChildren()
	{
		PathCoord newPath1 = new PathCoord(self.x+1,self.y);
		PathCoord newPath2 = new PathCoord(self.x,self.y+1);
		PathCoord newPath3 = new PathCoord(self.x-1,self.y);
		PathCoord newPath4 = new PathCoord(self.x,self.y-1);
		double x;
		if(test.getMap().isWalkable((int)(self.x+1), (int)(self.y)))
			
		
	}
	//bwapi.getMap().isWalkable(wx, wy);
}
