package edu.drexel.cs680.prj1.executeorders;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Node {
	public int g,h;
	public int x,y;
	public Node parent;
//	public Node child;
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Node) {
			Node other = (Node)o;
			boolean isEqual = (other.x == x && other.y == y);
//			if (!isEqual) {
//				System.out.println(String.format("not equal: %s <==> %s ", this, other));
//			}
			return isEqual;
		}
		
		
		return false;
	}
	
	public int hashCode() {
		return new HashCodeBuilder().
				append(g).
				append(h).
				append(x).
				append(y).
				toHashCode();
	}

	public String toString() {
		return String.format("x:%d,y:%d,g:%d,h:%d", x, y, g, h);
	}
}
