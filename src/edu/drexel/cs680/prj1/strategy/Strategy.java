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
	private static final int MIN_SPAWNING_POOL = 1;
		
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
		//this.currentState = States.Defend;
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
		System.out.println("checking! current state is " + currentState.toString());
		
		States lastState = currentState;		
		
		if(enemyNearby()==true){
			currentState = States.Defend;
		}else
		{
			if ((!enoughBuildingsAvailable()) || (!enoughResourcesAvailable()))
			{
				currentState = States.Build;			
			}
			else if (enoughAttackersAvailable()) 
			{
				currentState = States.Attack;
			}
			else if (lowEnemyCount()) 
			{			
				currentState = States.Explore;			
			}  		 		
			else
				currentState = States.Build;	
		}
		
		
		if(!lastState.equals(currentState))
			System.out.println("State changed to: " + currentState.toString());

		
	
	}
	
	private boolean enoughResourcesAvailable()
	{
		System.out.println("checking enough resources!: " + currentState.toString());
		int Minerals, Gas = 0;
		Minerals = Perception.instance.totalMinerals;
		Gas = Perception.instance.totalGas;
		
		if(Minerals<100 || Gas < 100)
			return false;
		else
			return true;
	}

	private boolean enemyNearby() {
		// if the enemy appears in the window, then...
		// this is assumed with the number of VISIBLE units
		int allEnemyUnits = Perception.instance.allVisibleEnemyUnits().size();
		System.out.println("checking enemies!: " + allEnemyUnits);
		if(allEnemyUnits>0)
		{
			// TODO - testing
//			System.out.println("Enemy in sight!  This is how many: " + count);
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
		int hatcheries, drones, spawnpool = 0;
		// this causes a bug, do we need a spawning pool?
//		spawnpool = Perception.instance.unitAvailableCountByType
//				.get(UnitTypes.Zerg_Spawning_Pool.ordinal());
		hatcheries = Perception.instance.unitAvailableCountByType
				.get(UnitTypes.Zerg_Hatchery.ordinal());
		
		System.out.println("checking enough Buildings");
		
		if ((hatcheries < MIN_HATCHERIES) || (spawnpool < MIN_SPAWNING_POOL))
			return false;
		else
			return true;
	}

	private boolean lowEnemyCount() {
		return Perception.instance.enemyUnitVisibleCountsByType.size() < ENEMY_UNIT_SAFE_COUNT;
	}

}
