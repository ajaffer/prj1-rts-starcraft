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
	private static final int MIN_SPAWINING_POOL = 1;
	
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
		
		/**
		 *  Basic strategy:
		 *  
		 *  While not being attacked:
		 *  	Try to Build,
		 *  	Then Explore
		 *  		Then Attack
		 *  	otherwise just build
		 *  
		 *  If attacked then defend/rebuild/repair
		 */
		
		States lastState = currentState;		
		
		if (enemyNearby()) {
			currentState = States.Defend;
		}
		else if (!enoughBuildingsAvailable() || !enoughBuildingsAvailable() || !enoughResourcesAvailable())
		{
			currentState = States.Build;			
		}
		else if (enoughAttackersAvailable()) {
			currentState = States.Attack;
		}
		else if (lowEnemyCount()) {			
			currentState = States.Explore;			
		}  		 		
		else
			currentState = States.Build;	
			
		if(!lastState.equals(currentState))
			System.out.println("State changed to: " + currentState.toString());
	}
	
	private boolean enoughResourcesAvailable()
	{
		int Minerals, Gas = 0;
		Minerals = Perception.instance.totalMinerals;
		Gas = Perception.instance.totalGas;
		
		if(Minerals<100 || Gas < 100)
			return false;
		else
			return true;
	}

	private boolean enemyNearby() {
		// TODO Auto-generated method stub
		
		// if the enemy appears in the window, then...
		// this is assumed with the number of VISIBLE units
		
		int count = 0;		
		count = Perception.instance.totalEnemyUnits;
		if(count>0)
		{
			// TODO - testing
			System.out.println("Enemy in sight!  This is how many: " + count);
			return true;
		}
		else
			return false;
	}

	private boolean enoughAttackersAvailable() {	
		int drones;
		drones = Perception.instance.listOfUnitsIdleByType.get(
				UnitTypes.Zerg_Drone).size();
		if(drones  > MIN_DRONES)
			return true;
		else
			return false;
	}

	private boolean enoughBuildingsAvailable() {
		/**
		 * TODO Please use an appropriate Zerg Building type, I just Chose
		 * Hatchery, is that a good choice?
		 * possibly lairs, they have different capabilities
		 */
		int hatcheries, drones = 0;
		
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
