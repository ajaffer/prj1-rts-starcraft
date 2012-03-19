package edu.drexel.cs680.prj1.pathfinding;

import java.util.ArrayList;
import java.util.List;

import edu.drexel.cs680.prj1.executeorders.Node;
import eisbot.proxy.JNIBWAPI;

public class LRTAStar implements PathFinding{
	
	Node[][] hMap;
	Node goal;
	Node start;
	JNIBWAPI bwapi;
	
	private int mapWidth() {
		int w = bwapi.getMap().getWalkWidth();
		return w;
	}
	
	private int mapHeight() {
		int h = bwapi.getMap().getWalkHeight();
		return h;
	}
	
	private void initHMap() {
		int h = mapHeight();
		int w = mapWidth();
		
		hMap = new Node[w][h];
		
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++){
				Node n = new Node(i, j);
				n.h = getManhattanDistance(n, goal);

				hMap[i][j] = n;
			}
		}
		
		System.out.println(String.format("hMap initialized with w/h %d/%d", w, h));
	}
	
	public LRTAStar(Node start, Node goal, JNIBWAPI bwapi) {
		this.goal = goal;
		this.start = start;
		this.bwapi = bwapi;
		
		initHMap();
	}
	
	

	private Node getHMapNode(Node lookup) {
//		int h = mapHeight();
//		int w = mapWidth();
//
//		int i = w /lookup.x;
//		int j = h /lookup.y;

		Node l = hMap[lookup.x][lookup.y];
		return l;
	}
	
	private int getManhattanDistance(Node s, Node g) {
		int distance = Math.abs(s.x-g.x) + Math.abs(s.y-g.y);
		return distance;
	}

	@Override
	public List<Node> calc(int ns, int nt) {
		List<Node> path = new ArrayList<Node>();
		Node n = getHMapNode(start);
		System.out.println(String.format("hmap node: %s, for start node: %s", n, start));
		
		path.add(n);
		while (!(n.equals(goal))){
			Node t = smallestNeighbor(n);
			System.out.println(String.format("LRTA*, @ %s", t));
			
			n.h = t.h + 1;
			path.add(t);

			n = smallestNeighbor(n);
		}
		
		System.out.println(String.format("LRTA*, reached Goal, path size %d", path.size()));
		
		return path;
	}
	
	Node smallestNeighbor(Node n) {
		List<Node> ns = neighbors(n);
		
		Node minH = ns.get(0);
		
		for (Node neighbor : ns){
			if (neighbor.h < minH.h) {
				minH = neighbor;
			}
		}
		return minH;
	}
	
	List<Node> neighbors(Node lookup) {
		List<Node> ns = new ArrayList<Node>();
		
		int h = mapHeight();
		int w = mapWidth();

		int i = lookup.x;
		int j = lookup.y;
		
		if (i>1) {
			Node left = hMap[i-1][j];
			ns.add(left);
		}
		if (i<w-2) {
			Node right = hMap[i+1][j];
			ns.add(right);
		}
		if (j>1) {
			Node top = hMap[i][j-1];
			ns.add(top);
		}
		if (j<h-2) {
			Node bottom = hMap[i][j+1];
			ns.add(bottom);
		}
		
		
		return ns;
	}
}
