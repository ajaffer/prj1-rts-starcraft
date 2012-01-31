package edu.drexel.cs680.prj1.perception;

public class AgentState {
	/** has drone 5 been morphed */
	public static boolean morphedDrone = false;
	
	/** has a drone been assigned to building a pool? */
	public static int poolDrone = -1;

	/** when should the next overlord be spawned? */
	public static int supplyCap = 0;	
	
	
	public static void reset() {
		morphedDrone = false;
		poolDrone = -1;
		supplyCap = 0;
	}
	
}
