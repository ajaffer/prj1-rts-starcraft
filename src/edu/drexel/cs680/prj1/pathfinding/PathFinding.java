package edu.drexel.cs680.prj1.pathfinding;

import java.util.List;

import edu.drexel.cs680.prj1.executeorders.Node;
import eisbot.proxy.JNIBWAPI;

public interface PathFinding {

	public List<Node> calc(int ns, int nt);
	
}

