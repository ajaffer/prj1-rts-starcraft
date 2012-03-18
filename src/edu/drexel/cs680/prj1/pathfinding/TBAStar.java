package edu.drexel.cs680.prj1.pathfinding;

import java.util.ArrayList;
import java.util.List;

import edu.drexel.cs680.prj1.executeorders.ExecuteOrders;
import edu.drexel.cs680.prj1.executeorders.Node;
import edu.drexel.cs680.prj1.logistics.Logistics.Squadron;
import eisbot.proxy.JNIBWAPI;

public class TBAStar extends AStar {
	private Node loc;
	private List<Node> aStartPath;
	private Squadron squad;
	
	public TBAStar(Node start, Node goal, JNIBWAPI bwapi, Squadron squad) {
		super(start, goal, bwapi);
		this.squad = squad;
	}
	
	public List<Node> calc(int ns, int nt) {
		List<Node> currentPath = new ArrayList<Node>();
		
		loc = start;
		while (!loc.equals(goal)) {
			List<Node> newPath;
			if (aStartPath == null){
				aStartPath = super.calc(ns, nt);
			}
			if (aStartPath != null){
				newPath = aStartPath;
			}else {
				newPath = pathTracer(loc, nt);
			}
			
			if (newPath.contains(loc)) {
				currentPath = newPath;
				stepForward(currentPath);
			} else {
				stepBackward(currentPath);
			}
		}
		
		return currentPath;
	}
	
	private List<Node> pathTracer(Node loc, int nt) {
		Node n = open.peek();
		List<Node> newPath = new ArrayList<Node>();
		int steps = 0;
		while (n != null && !n.equals(goal) && steps++ < nt) {
			newPath.add(n);
			Node p = n.parent;
			p.child = n;
			n = p;
		}
		return null;
	}
	
	private void stepForward(List<Node> path) {
		loc = loc.child;
		ExecuteOrders.instance.moveSquad(squad, loc);
	}
	
	private void stepBackward(List<Node> path) {
		loc = loc.parent;
		ExecuteOrders.instance.moveSquad(squad, loc);
	}
	
}
