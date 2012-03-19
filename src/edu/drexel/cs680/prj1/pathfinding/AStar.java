package edu.drexel.cs680.prj1.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import org.apache.commons.lang3.ArrayUtils;

import edu.drexel.cs680.prj1.executeorders.Node;
import eisbot.proxy.JNIBWAPI;

public class AStar implements PathFinding {
	
	Queue<Node> open;
	List<Node> closed;
	Node goal;
	Node start;
	
	JNIBWAPI bwapi;
	
	public AStar(Node start, Node goal, JNIBWAPI bwapi) {
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
	
	public List<Node> calc(int ns, int nt) {
		
		System.out.println(String.format("A*: Started @%s, goal: %s", start, goal));
		List<Node> path = null;
		int steps = 0;
		while (!open.isEmpty() && steps++ < ns) {
//			System.out.println(String.format("open list size: %d", open.size()));
			Node n = open.remove();
//			System.out.println(String.format("Removed Node: %s", n));
			if (n.equals(goal)){
				System.out.println("A*: Found Goal!");
				path = getPath(n);
				if (!(path.contains(start))){
					System.out.println(String.format("A* path does not contain start node: %s", start));
				}
				if (!(path.contains(goal))){
					System.out.println(String.format("A* path does not contain goal node: %s", goal));
				}
				return path;
			}
			closed.add(n);
//			System.out.println(String.format("Closed Node: %s", n));
//			System.out.println(String.format("closed list size: %d", closed.size()));
			List<Node> children = getChildren(n);
//			children.removeAll(closed);
//			System.out.println(String.format("found %d children", children.size()));
			for (Node m : children) {
//				if(!closed.contains(m)) { // added check to determine if node closed
					m.parent = n;
					m.g = n.g + 1;
					m.h = heuristic(m);
					open.add(m);
//					System.out.println(String.format("Opened Node: %s, isclosed?%s", m, closed.contains(m)));
					
//				}
			}
		}
		if (path == null && open.isEmpty()){
			System.out.println("A*: Goal not found!");
			throw new IllegalStateException("A*: Goal not found!");
		}
		
		return path;
	}
	
	int heuristic(Node m) {
		int h = (Math.abs(m.x - goal.x) + Math.abs(m.y - goal.y));
		return h;
	}
	
	List<Node> getPath(Node n) {
		List<Node> path = new ArrayList<Node>();
		
		while(n.parent!=null){
			path.add(n);
			n = n.parent;
		}
		Collections.reverse(path);
		return path;
	}
	
	enum Direction {up, down, right, left};
	Node getNode(Node n,  Direction d) {
		Node node = null;
		
		for (int i=1; i<2; i++) {
			int x = d==Direction.right?n.x+i:d==Direction.left?n.x-i:n.x;
			int y = d==Direction.down?n.y+i:d==Direction.up?n.y-i:n.y;
			
			// testing new op here
			if (bwapi.getMap().isWalkable(x, y)) {			
//				System.out.println(String.format("great %d, %d is walkable!", x, y));
				node = new Node(x, y);
				return node;
			} else {
//				System.out.println(String.format("oops %d, %d is NOT walkable!", x, y));
				
			}
		}
		return node;
	}
	
	List<Node> getChildren(Node n) {
		List<Node> children = new ArrayList<Node>();	
		
//		System.out.println("here....");
//		System.out.println(String.format("width %d", bwapi.getMap().getWidth()));
//		System.out.println(String.format("height %d", bwapi.getMap().getHeight()));
//		System.out.println(String.format("walk width %d", bwapi.getMap().getWalkWidth()));
//		System.out.println(String.format("walk height %d", bwapi.getMap().getWalkHeight()));
//		System.out.println("here end....");
	
		for (Direction d : Direction.values()) {
			Node node = getNode(n, d);
			if (node!=null && !closed.contains(node) && !open.contains(node)) {
				children.add(node);
			}
		}

		if(children.size()>0) {
//			System.out.println(String.format("Node %s has %d children", n, children.size()));
		} else {
//			System.out.println(String.format("Node %s has NO children", n));
		}
		
		return children;
	}
}