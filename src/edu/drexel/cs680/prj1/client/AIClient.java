package edu.drexel.cs680.prj1.client;

import java.util.HashSet;

import edu.drexel.cs680.prj1.executeorders.PathFinding;
import edu.drexel.cs680.prj1.giveorders.GiveOrders;
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
	PathFinding pathFinding;

//	/** has drone 5 been morphed */
//	private boolean morphedDrone = false;
//	
//	/** has a drone been assigned to building a pool? */
//	private int poolDrone = -1;
//
//	/** when should the next overlord be spawned? */
//	private int supplyCap = 0;

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
		
		perception = new Perception(bwapi);
		strategy = new Strategy(bwapi);
		giveOrders = new GiveOrders(bwapi);
		pathFinding = new PathFinding(bwapi);
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
	}
	
	/**
	 * Called each game cycle.
	 */
	public void gameUpdate() {
		perception.collectData();
		
		strategy.updateState();
		strategy.makeDecision();
		
		giveOrders.sendOrders();
		
		
		
		// collect minerals
		for (Unit unit : bwapi.getMyUnits()) {
			if (unit.getTypeID() == UnitTypes.Zerg_Drone.ordinal()) {
				if (unit.isIdle() && unit.getID() != AgentState.poolDrone) {
					
					for (Unit minerals : bwapi.getNeutralUnits()) {
						if (minerals.getTypeID() == UnitTypes.Resource_Mineral_Field.ordinal() && !claimed.contains(minerals.getID())) {
							double distance = Math.sqrt(Math.pow(minerals.getX() - unit.getX(), 2) + Math.pow(minerals.getY() - unit.getY(), 2));
							
							if (distance < 300) {
								bwapi.rightClick(unit.getID(), minerals.getID());
								claimed.add(minerals.getID());
								break;
							}
						}
					}					
				}
			}
		}
		
		// build a spawning pool
		if (bwapi.getSelf().getMinerals() >= 200 && AgentState.poolDrone < 0) {
			for (Unit unit : bwapi.getMyUnits()) {
				if (unit.getTypeID() == UnitTypes.Zerg_Drone.ordinal()) {
					AgentState.poolDrone = unit.getID();
					break;
				}
			}
			
			// build the pool under the overlord
			for (Unit unit : bwapi.getMyUnits()) {
				if (unit.getTypeID() == UnitTypes.Zerg_Overlord.ordinal()) {
					bwapi.build(AgentState.poolDrone, unit.getTileX(), unit.getTileY(), UnitTypes.Zerg_Spawning_Pool.ordinal());
				}				
			}
		}
		
		// spawn overlords
		if (bwapi.getSelf().getSupplyUsed() + 2 >= bwapi.getSelf().getSupplyTotal() && bwapi.getSelf().getSupplyTotal() > AgentState.supplyCap) {			
			if (bwapi.getSelf().getMinerals() >= 100) {
				for (Unit larva : bwapi.getMyUnits()) {
					if (larva.getTypeID() == UnitTypes.Zerg_Larva.ordinal()) {
						bwapi.morph(larva.getID(), UnitTypes.Zerg_Overlord.ordinal());
						AgentState.supplyCap = bwapi.getSelf().getSupplyTotal();
					}
				}									
			}
		}
		// spawn zerglings
		else if (bwapi.getSelf().getMinerals() >= 50) {
			for (Unit unit : bwapi.getMyUnits()) {
				if (unit.getTypeID() == UnitTypes.Zerg_Spawning_Pool.ordinal() && unit.isCompleted()) {
					for (Unit larva : bwapi.getMyUnits()) {
						if (larva.getTypeID() == UnitTypes.Zerg_Larva.ordinal()) {
							bwapi.morph(larva.getID(), UnitTypes.Zerg_Zergling.ordinal());
						}
					}					
				}
			}
		}

		// attack
		for (Unit unit : bwapi.getMyUnits()) {
			if (unit.getTypeID() == UnitTypes.Zerg_Zergling.ordinal() && unit.isIdle()) {
				for (Unit enemy : bwapi.getEnemyUnits()) {
					bwapi.attack(unit.getID(), enemy.getX(), enemy.getY());
					break;
				}
			}
		}
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
