package edu.drexel.cs680.prj1.executeorders;

public class Node {
	public Node parent;
	public PathCoord self;
	
	public Node(PathCoord newSelf)
	{
		self = newSelf;
		parent = null;
	}
	public Node(PathCoord newSelf, Node theParent)
	{
		self = newSelf;
		parent = theParent;
	}
	
	public boolean isEqual(PathCoord otherCoord)
	{
		// determines whether two coordinates are the same or already exist
		if((self.x==otherCoord.x)&&(self.y==otherCoord.y))
			return true;
		else
			return false;
	}
	
	public void Notify()
	{
		System.out.println("using coordinates x: " + self.x + " and y: " + self.y);
	}

}
