package edu.drexel.cs680.prj1.logistics;

import edu.drexel.cs680.prj1.perception.Perception;
import eisbot.proxy.JNIBWAPI;

public class Logistics {
/**
 * Used to determine whether something can be built with necessary resources
 */
	private boolean enoughZerglings;
	
	public static Logistics instance;
	public JNIBWAPI bwapi;
	
	public Logistics(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
		
		enoughZerglings = false;
	}
	
	public void updateLogistics()
	{
		
	}
	
}
