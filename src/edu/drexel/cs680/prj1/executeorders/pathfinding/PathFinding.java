package edu.drexel.cs680.prj1.executeorders.pathfinding;

import java.util.List;

import edu.drexel.cs680.prj1.executeorders.Node;
import eisbot.proxy.JNIBWAPI;

public interface PathFinding {

	public List<Node> calc(Node start, Node goal, JNIBWAPI bwapi);
	
}

