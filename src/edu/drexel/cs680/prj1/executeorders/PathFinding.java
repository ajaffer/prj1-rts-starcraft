package edu.drexel.cs680.prj1.executeorders;

import java.util.ArrayList;

public class PathFinding {

	private ArrayList<PathCoord> goalPath;
	public boolean pathFound;
	PathCoord start, goal;
	public String strAlgName;
	
	public PathFinding(double xStart, double yStart, double xGoal, double yGoal, String strAlgorithm)
	{
		pathFound=false;
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
		PathCoord dummyPath;
		
		listOpen.add(start);
		if(!FoundGoal(start))
			listClosed.add(start);
		else
		{
			pathFound=true;
			return;
		}
		
		while(listClosed.size()>0)
		{
			
		}
		
	}
	
	public boolean FoundGoal(PathCoord location)
	{
		double dist;
		dist = Math.abs(location.x-goal.x)+Math.abs(location.y-goal.y);
		
		if(dist<=1)
			return true;
		else
			return false;
	}
	
	public PathCoord GetLowestFCoord(ArrayList<PathCoord> pathList)
	{
		int fVal=0;
		int ptr = 0;
		
		if(pathList.size()==1)
			return pathList.get(0);
		else
		{
			fVal = pathList.get(0).heur.f;
			ptr++;
			
			for(int i=1; i<pathList.size();i++)
			{
				if(pathList.get(i).heur.f < fVal)
				{
					ptr = i;
					fVal = pathList.get(i).heur.f;
				}
			}			
			return pathList.get(ptr);
		}
	}
	
//	public void GetChildren()
//	{
//		PathCoord newPath1 = new PathCoord(self.x+1,self.y);
//		PathCoord newPath2 = new PathCoord(self.x,self.y+1);
//		PathCoord newPath3 = new PathCoord(self.x-1,self.y);
//		PathCoord newPath4 = new PathCoord(self.x,self.y-1);
//		double x;
//		if(test.getMap().isWalkable((int)(self.x+1), (int)(self.y)))
//			
//		
//	}
	//bwapi.getMap().isWalkable(wx, wy);
}
