package edu.drexel.cs680.prj1.pathfinding;

import java.util.List;

import edu.drexel.cs680.prj1.executeorders.Node;
import edu.drexel.cs680.prj1.logistics.Logistics.Squadron;
import eisbot.proxy.JNIBWAPI;

public class PathFindingUtil {
	private JNIBWAPI bwapi;
	public static PathFindingUtil instance;

	public enum ALGO {
		ASTAR,
		TBASTAR,
		LRTASTAR
	}

	public PathFindingUtil(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
	}

//	public List<Node> findPath(int xStart, int yStart, int xGoal,
//			int yGoal) {
//		ALGO a = ALGO.TBASTAR;
//		List<Node> path = findPath(xStart, yStart, xGoal, yGoal, a, null);
//		if (path == null) {
//			System.err.println("Path is null!!");
//		} else {
//			System.out.println(String.format("algo:%s path has %d steps", a, path.size()));
//		}
//		
//		return path;
//	}

	public List<Node> findPath(int xStart, int yStart, int xGoal,
			int yGoal, ALGO algo, Squadron squad) {
		List<Node> path = null;
		Node start = new Node(xStart, yStart);
		Node goal = new Node(xGoal, yGoal);

		switch (algo) {
		case ASTAR:
			PathFinding aStar = new AStar(start, goal, bwapi);
			path = aStar.calc(Integer.MAX_VALUE, Integer.MAX_VALUE);
			break;

		case TBASTAR:
			PathFinding tbaStar = new TBAStar(start, goal, bwapi, squad);
			path = tbaStar.calc(100, 100);
			break;
			
		case LRTASTAR:
			LRTAStar lrtaStar = new LRTAStar(start, goal, bwapi);
			path = lrtaStar.calc(-1, -1);
			break;
			
		default:
			aStar = new AStar(start, goal, bwapi);
			path = aStar.calc(Integer.MAX_VALUE, Integer.MAX_VALUE);
			break;
		}

		return path;
	}
}
