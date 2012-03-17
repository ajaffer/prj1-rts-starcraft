package edu.drexel.cs680.prj1.pathfinding;

import java.util.ArrayList;
import java.util.List;

import edu.drexel.cs680.prj1.executeorders.Node;
import eisbot.proxy.JNIBWAPI;

public class TBAStar extends AStar {

	private static final int NS = 3;
	private static final int NT = 3;
	
	public TBAStar() {
	}
	
	public List<Node> calc(Node start, Node goal, JNIBWAPI bwapi) {
		init(start, goal, bwapi);
		List<Node> currentPath = new ArrayList<Node>();
		
		Node loc = start;
		while (!loc.equals(goal)) {
			aStar();
			List<Node> newPath = pathTracer(loc);
			if (newPath.contains(loc)) {
				currentPath = newPath;
				stepForward(currentPath);
			} else {
				stepBackward(currentPath);
			}
		}
		
		return currentPath;
	}
	
	private List<Node> pathTracer(Node loc) {
		return null;
	}
	
	private void stepForward(List<Node> path) {
		
	}
	
	private void stepBackward(List<Node> path) {
		
	}
	
	private void aStar() {
		int counter = 0;
		while (!open.isEmpty() && counter++ < NS ) {
//			System.out.println(String.format("open list size: %d", open.size()));
			Node n = open.remove();
//			System.out.println(String.format("Removed Node: %s", n));
			if (n.equals(goal)){
				System.out.println("Found Goal!");
				break;
//				path = getPath(n);
//				return path;
			}
			closed.add(n);
//			System.out.println(String.format("closed list size: %d", closed.size()));
			List<Node> children = getChildren(n);
//			children.removeAll(closed);
//			System.out.println(String.format("found %d children", children.size()));
			for (Node m : children) {
				if(!closed.contains(m)) { // added check to determine if node closed
					m.parent = n;
					m.g = n.g + 1;
					m.h = heuristic(m);
					open.add(m);
				}
			}
		}
	}
}
