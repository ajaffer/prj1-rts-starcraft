package edu.drexel.cs680.prj1.executeorders.pathfinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import edu.drexel.cs680.prj1.executeorders.Node;
import eisbot.proxy.JNIBWAPI;

public class AStar implements PathFinding {
	
	private Queue<Node> open;
	private List<Node> closed;
	private Node goal;
	private Node start;
	
	private JNIBWAPI bwapi;
	
	private void init(Node start, Node goal, JNIBWAPI bwapi) {
		Comparator<Node> comparator = new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				int result = (o1.g + o1.h) - (o2.g + o2.h);
				return result;
			}
		};
		
		open = new PriorityQueue<Node>(100, comparator);
		open.add(start);
		closed = new ArrayList<Node>();
		this.goal = goal;
		this.start = start;
		this.bwapi = bwapi;
	}
	
	public List<Node> calc(Node start, Node goal, JNIBWAPI bwapi) {
		init(start, goal, bwapi);
		
		System.out.println(String.format("Starting AStar, start: %s, goal: %s", start, goal));
		List<Node> path = null;
		while (!open.isEmpty()) {
			System.out.println(String.format("open list size: %d", open.size()));
			Node n = open.remove();
			System.out.println(String.format("Removed Node: %s", n));
			if (n.equals(goal)){
				System.out.println("Found Goal!");
				path = getPath(n);
				return path;
			}
			closed.add(n);
			System.out.println(String.format("closed list size: %d", closed.size()));
			List<Node> children = getChildren(n);
			children.removeAll(closed);
			System.out.println(String.format("found %d children", children.size()));
			for (Node m : children) {
				m.parent = n;
				m.g = n.g + 1;
				m.h = heuristic(m);
				open.add(m);
			}
		}
		
		return path;
	}
	
	private int heuristic(Node m) {
		int h = (Math.abs(m.x - goal.x) + Math.abs(m.y = goal.y));
		return h;
	}
	
	private List<Node> getPath(Node n) {
		List<Node> path = new ArrayList<Node>();
		
		while(n.parent!=null){
			path.add(n);
			n = n.parent;
		}
		return path;
	}
	
	private List<Node> getChildren(Node n) {
		List<Node> children = new ArrayList<Node>();

		int x = n.x+8, y = n.y;
		if (bwapi.getMap().isWalkable(x, y)) {
			Node nodeRight = new Node(x, y);
			children.add(nodeRight);
		}else {
//			System.out.println(String.format("Not walkable: %d,%d", n.x+1, n.y));
		}
		
		x = n.x-8; y = n.y;
		if (bwapi.getMap().isWalkable(x, y)) {
			Node nodeLeft = new Node(x, y);
			children.add(nodeLeft);
		} else {		
//			System.out.println(String.format("Not walkable: %d,%d", n.x-1, n.y));
		}

		x = n.x; y = n.y-8;
		if (bwapi.getMap().isWalkable(x, y)) {
			Node nodeUp = new Node(x, y);
			children.add(nodeUp);
		} else {		
//			System.out.println(String.format("Not walkable: %d,%d", n.x, n.y-1));
		}

		x = n.x; y = n.y+8;
		if (bwapi.getMap().isWalkable(x, y)) {
			Node nodeDown = new Node(x, y);
			children.add(nodeDown);
		} else {		
//			System.out.println(String.format("Not walkable: %d,%d", n.x, n.y+1));
		}		
		
		if(children.size()>0) {
			System.out.println(String.format("Node %s has %d children", n, children.size()));
		} else {
			System.out.println(String.format("Node %s has NO children", n));
		}
		
		return children;
	}
}