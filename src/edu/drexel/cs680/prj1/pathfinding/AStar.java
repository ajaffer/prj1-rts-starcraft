package edu.drexel.cs680.prj1.pathfinding;

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
	
	private enum Direction {up, down, right, left};
	private Node getNode(Node n,  Direction d) {
		Node node = null;
		
		for (int i=1; i<4; i++) {
			int x = d==Direction.right?n.x+i:d==Direction.left?n.x-i:n.x;
			int y = d==Direction.down?n.y+i:d==Direction.up?n.y-i:n.y;
			if (bwapi.getMap().isWalkable(x, y)) {
				System.out.println(String.format("great %d, %d is walkable!", x, y));
				node = new Node(x, y);
				return node;
			} else {
				System.out.println(String.format("oops %d, %d is NOT walkable!", x, y));
				
			}
		}
		return node;
	}
	
	private List<Node> getChildren(Node n) {
		List<Node> children = new ArrayList<Node>();
		
//		System.out.println("here....");
//		System.out.println(String.format("width %d", bwapi.getMap().getWidth()));
//		System.out.println(String.format("height %d", bwapi.getMap().getHeight()));
//		System.out.println(String.format("walk width %d", bwapi.getMap().getWalkWidth()));
//		System.out.println(String.format("walk height %d", bwapi.getMap().getWalkHeight()));
//		System.out.println("here end....");
	
		for (Direction d : Direction.values()) {
			Node node = getNode(n, d);
			if (node!=null) {
				children.add(node);
			}
		}

		if(children.size()>0) {
			System.out.println(String.format("Node %s has %d children", n, children.size()));
		} else {
			System.out.println(String.format("Node %s has NO children", n));
		}
		
		return children;
	}
}