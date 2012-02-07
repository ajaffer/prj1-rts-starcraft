package edu.drexel.cs680.prj1.strategy;

import java.util.List;

import edu.drexel.cs680.prj1.giveorders.GiveOrders;
import edu.drexel.cs680.prj1.perception.AgentState;
import edu.drexel.cs680.prj1.perception.Perception;
import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;
import eisbot.proxy.types.UnitType.UnitTypes;

public class Strategy {

	public States currentState;

	private static final int ENEMY_UNIT_SAFE_COUNT = 10;
	private static final int MIN_HATCHERIES = 5;
	private static final int MIN_DRONES = 5;
	private JNIBWAPI bwapi;

	/** FSM States */
	public enum States {
		CollectMinerals, Build, Explore, Attack, Defend
	};
	
	public static Strategy instance;

	public Strategy(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
		this.currentState = States.Build;		
	}
	
	
	//TODO change this method appropriate to StarCraft
	public void updateState() {
		States prevState = currentState;
		currentState = States.CollectMinerals;	
		
		
		

//		if(enemyNearby())
//			System.out.println("Yo! look out!");
		
//		if (!enoughBuildingsAvailable())
//			currentState = States.Build;
//		if (lowEnemyCount() && enoughBuildingsAvailable()) {
//			currentState = States.Explore;
//		}  
		if (enemyNearby()) {
			currentState = States.Attack;
		} 
//		if (enemyNearby()) {
//			currentState = States.Defend;
//		}
//		else
//			currentState = States.Build;	
		
		if (prevState != currentState) {
			System.out.println(String.format("State >>>%s<<<", currentState));
		}
		
	}

	private boolean enemyNearby() {
		// TODO Auto-generated method stub
		
		// if the enemy appears in the window, then...
		// this is assumed with the number of VISIBLE units
//		System.out.println("checking if enemy nearby");
		int count = 0;		
		count = Perception.instance.totalEnemyUnits;
		if(count>0)
		{
			// TODO - testing
//			System.out.println("Enemy in sight!  This is how many: " + count);
			return true;
		}
		else
			return false;
	}

	private boolean enoughAttackersAvailable() {		
		return Perception.instance.listOfUnitsIdleByType.get(
				UnitTypes.Zerg_Zergling).size() > MIN_DRONES;
	}

	private boolean enoughBuildingsAvailable() {
		/**
		 * TODO Please use an appropriate Zerg Building type, I just Chose
		 * Hatchery, is that a good choice?
		 * possibly lairs, they have different capabilities
		 */
		int hatcheries, lairs = 0;
		
		hatcheries = Perception.instance.unitAvailableCountByType
				.get(UnitTypes.Zerg_Hatchery.ordinal());
		if (hatcheries > MIN_HATCHERIES)
			return true;
		else
			return false;
	}

	private boolean lowEnemyCount() {
		return Perception.instance.enemyUnitCountsByType.size() < ENEMY_UNIT_SAFE_COUNT;
	}

	//TODO Remove this
	public void makeDecision() {
		for (Unit unit : bwapi.getMyUnits()) {
			if (unit.getTypeID() == UnitTypes.Zerg_Larva.ordinal()) {
				if (bwapi.getSelf().getMinerals() >= 50
						&& !AgentState.morphedDrone) {
					GiveOrders.morphDrone(unit.getID(),
							UnitTypes.Zerg_Drone.ordinal());
				}
			}
		}
	}

}
