package edu.drexel.cs680.prj1.pathfinding;

import java.util.List;

import edu.drexel.cs680.prj1.executeorders.Node;
import eisbot.proxy.JNIBWAPI;

public class PathFindingUtil {
	private JNIBWAPI bwapi;
	public static PathFindingUtil instance;

	public enum ALGO {
		ASTAR
	}

	public PathFindingUtil(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
	}

	public List<Node> findPath(int xStart, int yStart, int xGoal,
			int yGoal) {
		List<Node> path = findPath(xStart, yStart, xGoal, yGoal, ALGO.ASTAR);
		if (path == null) {
			System.err.println("Path is null!!");
		} else {
			System.out.println(String.format("algo:%s path has %d steps", ALGO.ASTAR, path.size()));
		}
		
		return path;
	}

	public List<Node> findPath(int xStart, int yStart, int xGoal,
			int yGoal, ALGO algo) {
		List<Node> path = null;
		Node start = new Node(xStart, yStart);
		Node goal = new Node(xGoal, yGoal);

		switch (algo) {
		case ASTAR:
			PathFinding aStar = new AStar();
			path = aStar.calc(start, goal, bwapi);
			break;

		default:
			aStar = new AStar();
			path = aStar.calc(start, goal, bwapi);
			break;
		}

		return path;
	}
}
