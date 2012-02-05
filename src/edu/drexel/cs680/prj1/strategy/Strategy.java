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
		Build, Explore, Attack, Defend
	};
	
	public static Strategy instance;

	public Strategy(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
		this.currentState = States.Build;
	}
	
	
	//TODO change this method appropriate to StarCraft
	public void updateState() {
		//currentState = States.Build;  States.Build means low enemy count

		if (lowEnemyCount() && enoughBuildingsAvailable()) {
			currentState = States.Explore;
		}  

		else if (enoughAttackersAvailable()) {
			currentState = States.Attack;
		} 
		else if (enemyNearby()) {
			currentState = States.Defend;
		}
		else
			currentState = States.Build;
	}

	private boolean enemyNearby() {
		// TODO Auto-generated method stub
		
		// if the enemy appears in the window, then...
		// this is assumed with the number of VISIBLE units
		if(Perception.instance.totalEnemyUnits>0)
			return true;
		else
			return false;
	}

	private boolean enoughAttackersAvailable() {		
		return Perception.instance.listOfUnitsIdleByType.get(
				UnitTypes.Zerg_Drone).size() > MIN_DRONES;
	}

	private boolean enoughBuildingsAvailable() {
		/**
		 * TODO Please use an appropriate Zerg Building type, I just Chose
		 * Hatchery, is that a good choice?
		 */
		return Perception.instance.unitAvailableCountByType
				.get(UnitTypes.Zerg_Hatchery.ordinal()) > MIN_HATCHERIES;
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
