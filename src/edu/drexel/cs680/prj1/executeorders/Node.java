package edu.drexel.cs680.prj1.executeorders;

public class Node {
	public int g,h;
	public int x,y;
	public Node parent;
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Node) {
			Node other = (Node)o;
			return (other.x == x && other.y == y);
		}
		return false;
	}

	public String toString() {
		return String.format("x:%d,y:%d,g:%d,h:%d", x, y, g, h);
	}
}
