package edu.drexel.cs680.prj1.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.drexel.cs680.prj1.executeorders.ExecuteOrders;
import edu.drexel.cs680.prj1.executeorders.Node;
import edu.drexel.cs680.prj1.logistics.Logistics.Squadron;
import eisbot.proxy.JNIBWAPI;

public class TBAStar extends AStar {
	private Node loc;
	private Squadron squad;
	
	public TBAStar(Node start, Node goal, JNIBWAPI bwapi, Squadron squad) {
		super(start, goal, bwapi);
		this.squad = squad;
	}
	
	public List<Node> calc(int ns, int nt) {
		System.out.println("TBA*: Started");
		List<Node> currentPath = null;
		List<Node> newPath = null;
		List<Node> aStarPath = null;
		
		loc = start;
//		newPath.add(start);
		while (!loc.equals(goal)) {
			System.out.println(String.format("1. TBA* @: %s", loc));
			
			if (aStarPath == null){
//				aStarPath = super.calc(ns, nt);
				try {
					aStarPath = super.calc(ns, nt);
				} catch (IllegalStateException e) {
					System.out.println("No Path Found!!");
//					e.printStackTrace();
					return null;
				}
				System.out.println(String.format("2. A* %d steps finished", ns));
			}
			if (aStarPath != null){
				System.out.println(String.format("3.1 A* finished, path size: %d", aStarPath.size()));
				newPath = new ArrayList<Node>();
				newPath.add(start);
				newPath.addAll(aStarPath);
				if (!(newPath.contains(start))) {
					System.out.println(String.format("TBA*, aStarPath does not contain start node %s: ", start));
				}
				if (!(newPath.contains(goal))) {
					System.out.println(String.format("TBA*, aStarPath path does not contain goal node %s: ", goal));
				}
			}else {
				System.out.println(String.format("3.2 TBA*, starting path tracer"));
				newPath = pathTracer(nt);
			}
			
			if (newPath == null && currentPath != null) {
				stepForward(currentPath);
			}
			if (newPath !=null && newPath.contains(loc)) {
				currentPath = newPath;
				stepForward(currentPath);
			} 
			if (newPath !=null && !newPath.contains(loc) && currentPath != null){
				stepBackward(currentPath);
			}
		}
		
		System.out.println(String.format("Found Goal, currentPath's length", currentPath.size()));
		return null;
	}
	
	private List<Node> tempPath = new ArrayList<Node>();
	private List<Node> pathTracer(int nt) {
		Node n;
		
		if (tempPath.isEmpty()){
			n = open.peek();
		} else {
			n = tempPath.get(tempPath.size()-1);
			n = n.parent;
		}
		int steps = 0;
		while (n != null && steps++ < nt) {
			System.out.println(String.format("1. PathTracer adding node: %s", n));
			tempPath.add(n);
			n = n.parent;
		}
		Collections.reverse(tempPath);
		
//		if (!(Collections.disjoint(newPath, t))){
//			newPath.addAll(t);
//		} else {
//			newPath = t;
//		}
		
//		if (newPath.contains(n)){
//			while (n != null) {
//				n = n.parent;
//			}
//			System.out.println(String.format("moved to the top of path: %s", n));
//		} else {
//			newPath = new ArrayList<Node>();
//			System.out.println("Starting a new path");
//		}
		
		
		if (n == null) {
			Node root = tempPath.get(tempPath.size()-1);
			if (!(root.equals(start))){
				System.out.println(String.format("1.B Incorrect State, Root node: %s is not the Start node: %s", root, start));
			}
			List<Node> newPath = tempPath;
			tempPath = new ArrayList<Node>();
			System.out.println(String.format("2. PathTracer finished, size: %d", newPath.size()));
			return newPath;
		} else {
			System.out.println(String.format("2. PathTracer not finished"));
			return null;
		}
	}
	
	private void stepForward(List<Node> path) {
		if (path.contains(loc)) {
			int index = path.indexOf(loc);
			if (index > -1 && index < path.size()-1) {
				loc = path.get(index+1);
				ExecuteOrders.instance.moveSquad(squad, loc);
				System.out.println(String.format("FORWARD:%s", loc));
			}else {
				System.out.println(String.format("FORWARD FAILED, index incorrect: %d, path.size: %d", index, path.size()));
			}
		}else {
			System.out.println(String.format("FORWARD FAILED, path doesn't containt current location: %s", loc));
		}
	}
	
	private void stepBackward(List<Node> path) {
		loc = loc.parent;
		if (loc != null){
			ExecuteOrders.instance.moveSquad(squad, loc);
			System.out.println(String.format("BACKWARD:%s", loc));
		} else {
			System.out.println("BACKWARD FAILED");
		}
	}
	
}
