package edu.drexel.cs680.prj1.executeorders;

import java.util.ArrayList;

public class PathBox {

	public PathCoord parent;
	public PathCoord self;
	public boolean hasParent=false;
	public ArrayList<PathCoord> listChildren;	
	
	public PathBox(PathCoord coords, boolean isChild)
	{
		hasParent=isChild;
		self = coords;
	}
	
	public PathBox(PathCoord coords, boolean isChild, PathCoord parentVal)
	{
		hasParent=isChild;
		parent = parentVal;
		self = coords;
	}
	
	
}
