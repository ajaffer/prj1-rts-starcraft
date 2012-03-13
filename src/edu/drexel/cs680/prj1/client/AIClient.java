package edu.drexel.cs680.prj1.client;

import java.util.HashSet;

import edu.drexel.cs680.prj1.executeorders.ExecuteOrders;
import edu.drexel.cs680.prj1.giveorders.GiveOrders;
import edu.drexel.cs680.prj1.pathfinding.PathFindingUtil;
import edu.drexel.cs680.prj1.perception.AgentState;
import edu.drexel.cs680.prj1.perception.Perception;
import edu.drexel.cs680.prj1.strategy.Strategy;
import eisbot.proxy.BWAPIEventListener;
import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType.UnitTypes;
/**
 * AI Client using JNI-BWAPI for class project
 * CS-680 Game A.I. Drexel University
 */
public class AIClient implements BWAPIEventListener {

	/** reference to JNI-BWAPI */
	private JNIBWAPI bwapi;

	/** used for mineral splits */
	private HashSet<Integer> claimed = new HashSet<Integer>();
	
	/** Perception Module */
	Perception perception;
	
	/** Strategy Module*/
	Strategy strategy;
	
	/** Give Orders Module*/
	GiveOrders giveOrders;
	
	/** Pathfinding Module*/
	PathFindingUtil pathFinding;

	/** ExecuteOrders Module*/
	private ExecuteOrders executeOrders;

	/** has drone 5 been morphed */
	private boolean morphedDrone = false;
	
	/** has a drone been assigned to building a pool? */
	private int poolDrone = -1;

	/** when should the next overlord be spawned? */
	private int supplyCap = 0;


	/**
	 * Create a Java AI.
	 */
	public static void main(String[] args) {
		new AIClient();
	}

	/**
	 * Instantiates the JNI-BWAPI interface and connects to BWAPI.
	 */
	public AIClient() {
		bwapi = new JNIBWAPI(this);
		bwapi.start();
		
		//perception = new Perception(bwapi);	// moved to gameStarted section
		//strategy = new Strategy(bwapi);			// moved to gameStarted section
		//giveOrders = new GiveOrders(bwapi);		// moved to gameStarted section
		//pathFinding = new PathFinding(bwapi);	// moved to gameStarted section
	} 

	/**
	 * Connection to BWAPI established.
	 */
	public void connected() {
		bwapi.loadTypeData();
	}
	
	/**
	 * Called at the beginning of a game.
	 */
	public void gameStarted() {		
		System.out.println("Game Started");
		
		bwapi.enableUserInput();
		bwapi.enablePerfectInformation();
		bwapi.setGameSpeed(0);
		bwapi.loadMapData(true);

		// reset agent state
		claimed.clear();
		AgentState.reset();
		
		perception = new Perception(bwapi);
		strategy = new Strategy(bwapi);
		giveOrders = new GiveOrders(bwapi);
		executeOrders = new ExecuteOrders(bwapi);
		pathFinding = new PathFindingUtil(bwapi);
	}
	
	/**
	 * Called each game cycle.
	 */
	public void gameUpdate() {
		perception.collectData();
		//System.out.println(String.format("Perception:%s", perception.toString()));
		strategy.updateFSM();
		strategy.apply();
	}

	public void gameEnded() {}
	public void keyPressed(int keyCode) {}
	public void matchEnded(boolean winner) {}
	public void nukeDetect(int x, int y) {}
	public void nukeDetect() {}
	public void playerLeft(int id) {}
	public void unitCreate(int unitID) {}
	public void unitDestroy(int unitID) {}
	public void unitDiscover(int unitID) {}
	public void unitEvade(int unitID) {}
	public void unitHide(int unitID) {}
	public void unitMorph(int unitID) {}
	public void unitShow(int unitID) {}
}
