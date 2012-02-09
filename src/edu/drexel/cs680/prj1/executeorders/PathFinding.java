package edu.drexel.cs680.prj1.executeorders;

import java.util.ArrayList;
import java.util.List;

import eisbot.proxy.JNIBWAPI;

public class PathFinding {

	private ArrayList<Node> goalPath;
	public boolean pathFound;
	PathCoord start, goal;
	public String strAlgName;

	private JNIBWAPI bwapi;
	public static PathFinding instance;

	public enum ALGO {
		ASTAR
	}

	public PathFinding(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
	}

	public List<Node> findPath(double xStart, double yStart, double xGoal,
			double yGoal) {
		return findPath(xStart, yStart, xGoal, yGoal, ALGO.ASTAR);
	}

	public List<Node> findPath(double xStart, double yStart, double xGoal,
			double yGoal, ALGO algo) {
		List<Node> path = null;

		pathFound = false;
		PathCoord start0 = new PathCoord(xStart, yStart);
		PathCoord goal0 = new PathCoord(xGoal, yGoal);
		start = new PathCoord(xStart, yStart, start0, goal0);
		goal = new PathCoord(xGoal, yGoal);

		switch (algo) {
		case ASTAR:
			path = StartAStar();
			break;

		default:
			path = StartAStar();
			break;
		}

		return path;
	}

	public List<Node> StartAStar() {
		ArrayList<Node> listOpen = new ArrayList<Node>();
		ArrayList<Node> listClosed = new ArrayList<Node>();
		ArrayList<Node> listOK = new ArrayList<Node>();
		Node testNode;

		// begin the operation
		int intLowestF = 0;
		Node nStart = new Node(start);
		listOpen.add(nStart);

		if (!FoundGoal(start))
			listClosed.add(nStart);
		else {
			pathFound = true;
			listOK.add(nStart);
			goalPath = listOK;
		}

		while (listOpen.size() > 0) {
			intLowestF = GetLowestFNode(listOpen);
			testNode = listOpen.remove(intLowestF);
			if (FoundGoal(testNode.self)) {
				listOK = PathToN(testNode);
				break;
			} else {
				listClosed.add(testNode);
				ArrayList<Node> listChildren;
				listChildren = GetChildren(testNode, listClosed);
				for (Node eachNode : listChildren) {
					listOpen.add(eachNode);
				}
			}
		}
		goalPath = listOK;

		return goalPath;
	}

	public boolean FoundGoal(PathCoord location) {
		double dist;
		dist = Math.abs(location.x - goal.x) + Math.abs(location.y - goal.y);

		if (dist <= 1)
			return true;
		else
			return false;
	}

	public int GetLowestFNode(ArrayList<Node> pathList) {
		int fVal = 0;
		int ptr = 0;

		if (pathList.size() == 1)
			return 0;
		else {
			fVal = pathList.get(0).self.getHeur();
			ptr++;

			for (int i = 1; i < pathList.size(); i++) {
				if (pathList.get(i).self.getHeur() < fVal) {
					ptr = i;
					fVal = pathList.get(i).self.getHeur();
				}
			}
			return ptr;
		}
	}

	public ArrayList<Node> PathToN(Node lastNode) {
		ArrayList<Node> listPath = new ArrayList<Node>();
		Node parNode;

		do {
			listPath.add(lastNode);
			parNode = lastNode.parent;
			lastNode = lastNode.parent;

		} while (parNode != null);

		return listPath;
	}

	public ArrayList<Node> GetChildren(Node parNode, ArrayList<Node> closedList) {
		ArrayList<Node> newList = new ArrayList<Node>();
		Node nodeRight, nodeLeft, nodeUp, nodeDown;

		PathCoord newPath1 = new PathCoord(parNode.self.x + 1, parNode.self.y,
				start, goal);
		PathCoord newPath2 = new PathCoord(parNode.self.x, parNode.self.y + 1,
				start, goal);
		PathCoord newPath3 = new PathCoord(parNode.self.x - 1, parNode.self.y,
				start, goal);
		PathCoord newPath4 = new PathCoord(parNode.self.x, parNode.self.y - 1,
				start, goal);

		if (bwapi.getMap().isWalkable((int) (parNode.self.x + 1),
				(int) (parNode.self.y))) {
			nodeRight = new Node(newPath1, parNode);
			nodeRight.self.heur.setG(1);
			if (!closedList.contains(nodeRight))
				newList.add(nodeRight);
		}
		if (bwapi.getMap().isWalkable((int) (parNode.self.x),
				(int) (parNode.self.y + 1))) {
			nodeUp = new Node(newPath2, parNode);
			nodeUp.self.heur.setG(1);
			if (!closedList.contains(nodeUp))
				newList.add(nodeUp);
		}
		if (bwapi.getMap().isWalkable((int) (parNode.self.x - 1),
				(int) (parNode.self.y))) {
			nodeLeft = new Node(newPath3, parNode);
			nodeLeft.self.heur.setG(1);
			if (!closedList.contains(nodeLeft))
				newList.add(nodeLeft);
		}
		if (bwapi.getMap().isWalkable((int) (parNode.self.x),
				(int) (parNode.self.y - 1))) {
			nodeDown = new Node(newPath4, parNode);
			nodeDown.self.heur.setG(1);
			if (!closedList.contains(nodeDown))
				newList.add(nodeDown);
		}

		return newList;
	}

	// public void GetChildren()
	// {
	// PathCoord newPath1 = new PathCoord(self.x+1,self.y);
	// PathCoord newPath2 = new PathCoord(self.x,self.y+1);
	// PathCoord newPath3 = new PathCoord(self.x-1,self.y);
	// PathCoord newPath4 = new PathCoord(self.x,self.y-1);
	// double x;
	// if(test.getMap().isWalkable((int)(self.x+1), (int)(self.y)))
	//
	//
	// }
	// bwapi.getMap().isWalkable(wx, wy);
}
