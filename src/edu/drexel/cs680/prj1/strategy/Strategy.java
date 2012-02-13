package edu.drexel.cs680.prj1.strategy;

import java.util.HashSet;
import java.util.Set;

import edu.drexel.cs680.prj1.giveorders.GiveOrders;
import edu.drexel.cs680.prj1.perception.Perception;
import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType.UnitTypes;

public class Strategy {

	public States consumeState = States.MORPH_DRONES;
	public States produceState = States.SPAWN_OVERLORDS;
	public States actionState = States.PAUSE;

	private static final int ENEMY_UNIT_SAFE_COUNT = 10;
	private static final int MIN_HATCHERIES = 5;
	private static final int MIN_DRONES = 5;
	private static final int MIN_SPAWNING_POOL = 1;
	private static final int MIN_ZERGLINGS_TO_PATROL = 20;
	private static final int MIN_ATTACKERS = 20;
	
	private Set<Unit>attackers;

	private JNIBWAPI bwapi;

	/** FSM States */
	public enum States {
		MORPH_DRONES, SPAWN_POOLS, SPAWN_OVERLORDS, SPAWN_ZERGLINS, PAUSE, ATTACK, PATROL, DEFEND
	};

	public static Strategy instance;

	public Strategy(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
	}

	public void updateFSM() {
		States lastConsumeState = consumeState, lastProduceState = produceState, lastActionState = actionState;

		if (Perception.instance.totalMinerals >= 50
				&& !Perception.instance.morphedDrone) {
			consumeState = States.MORPH_DRONES;
		} else {
			consumeState = States.PAUSE;
		}

		if (Perception.instance.totalMinerals >= 200
				&& Perception.instance.poolDrone < 0) {
			consumeState = States.SPAWN_POOLS;
		} else {
			consumeState = States.PAUSE;
		}

		if (Perception.instance.supplyUsed + 2 >= Perception.instance.supplyTotal
				&& Perception.instance.supplyTotal > Perception.instance.supplyCap) {
			produceState = States.SPAWN_OVERLORDS;
		} else if (Perception.instance.totalMinerals >= 50) {
			produceState = States.SPAWN_ZERGLINS;
		} else {
			produceState = States.PAUSE;
		}
//		if (enoughZerglings()) {
//			actionState = States.PATROL;
//		}
		if (enemyLocated() && enoughAttackersAvailable()) {
			actionState = States.ATTACK;
		}
		
		//System.out.println("Current State produce state: " + produceState.toString());

		if (!lastConsumeState.equals(consumeState)) {
			System.out.println(String.format("Consume State>>>%s<<<",
					consumeState));
		}
		if (!lastProduceState.equals(produceState)) {
			System.out.println(String.format("Produce State>>>%s<<<",
					produceState));
		}
		if (!lastActionState.equals(actionState)) {
			System.out.println(String.format("Action State>>>%s<<<",
					produceState));
		}
	}

	private boolean enemyLocated() {
		// TODO Auto-generated method stub
		return true;
	}

	private boolean enoughZerglings() {
		// TODO Auto-generated method stub
		
		
		Set<Unit> zerglings = Perception.instance.setOfUnitsByType.get(UnitTypes.Zerg_Zergling.ordinal());
		if(zerglings==null)
		{			
			return false;
		}
		
		if(zerglings.size()>=MIN_ZERGLINGS_TO_PATROL)
		{
		//	System.out.println("Enough zerglings!");
			return true;
			
		}
		else
		{
	//		System.out.println("No t Enough zerglings!");
			return false;
		}
	}

	public void apply() {
		claimMinerals();

		switch (consumeState) {

		case MORPH_DRONES:
			morphToDrones();
			break;

		case SPAWN_POOLS:
			buildSpawingPools();
			break;

		default:
			break;
		}

		switch (produceState) {

		case SPAWN_OVERLORDS:
			spawnOverlords();
			break;

		case SPAWN_ZERGLINS:
			spawnZerglings();
			break;
			
		default:
			break;
		}
		
		switch (actionState) {
		
		case PATROL:
			patrol();
			break;
			
		case ATTACK:
			attack();
			break;
			
		case DEFEND:
			defend();
			break;
			
			
		default:
			break;
		}

	}

	private void defend() {
		// TODO Auto-generated method stub
		
	}

	private void patrol() {
		// TODO Auto-generated method stub
		// Send out a few zerglings to different corners to locate enemy
		boolean located = false;
		Set<Unit> patrolers = getSomePatrolers();
		System.out.println("Sending patrol!!!");
		int[] destCoordinates;
		destCoordinates = new int[2];
//		destCoordinates = GiveOrders.instance.sendPatrol(patrolers);
		GiveOrders.instance.sendPatrol(patrolers);
		
//		//while(!enemyLocated())
//		while(!located)
//		{
//			for(Unit u: patrolers)
//				if(u.isMoving())
//					continue;
//				else
//				{
//					for(Unit e: bwapi.getEnemyUnits())
//							if(distance(destCoordinates[0],destCoordinates[1],e.getX(),e.getY())<5)
//							{
//								located=true;
//								break;
//								
//							}
//					
//					
//					
//				}
//		}
//		
//		if(located)
//		{
//			Set<Unit> seenEnemies = null;
//			for(Unit e: bwapi.getEnemyUnits())
//				if(distance(destCoordinates[0],destCoordinates[1],e.getX(),e.getY())<5)
//					seenEnemies.add(e);
//			
//			GiveOrders.instance.attackSpecific(patrolers, seenEnemies);
//		}			
//		else
//			GiveOrders.instance.returnToBase(patrolers);
//		
	}
	
	private double distance(int x1, int y1, int x2, int y2)
	{
		return Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
	}

	private Set<Unit> getSomePatrolers() {
		// TODO Auto-generated method stub
		System.out.println("Getting patrollers...");
		Set<Unit> idleZerglings = Perception.instance.setOfUnitsByType.get(UnitTypes.Zerg_Zergling.ordinal());
		System.out.println("Total Zerglings used " + idleZerglings.size());
		return idleZerglings;
	}
	
	

	private void attack() {
		System.out.println("Attack!!");
		// TODO implement the following stubs
		Set<Unit> enemyUnits = getDiscoveredEnemyUnits();
		Set<Unit> attackers = getSomeAttackers();
		
		GiveOrders.instance.attackEnemy(enemyUnits, attackers);
		
//		for (Unit unit : bwapi.getMyUnits()) {
//			if (unit.getTypeID() == UnitTypes.Zerg_Zergling.ordinal()
//					&& unit.isIdle()) {
//				for (Unit enemy : bwapi.getEnemyUnits()) {
//					bwapi.attack(unit.getID(), enemy.getX(), enemy.getY());
//					break;
//				}
//			}
//		}
	}

	private Set<Unit> getSomeAttackers() {
		// TODO replace with actual implementation
		return getSomePatrolers();
	}

	private Set<Unit> getDiscoveredEnemyUnits() {
		// TODO replace with actual implementation
		Set<Unit> enemyUnits = new HashSet<Unit>();
		enemyUnits.addAll( bwapi.getEnemyUnits());
				
		return enemyUnits;		
	}

	private void spawnZerglings() {
		Set<Unit> larvae = Perception.instance.setOfUnitsByType
				.get(UnitTypes.Zerg_Larva.ordinal());
		Set<Unit> completedSpawingPools = Perception.instance
				.getCompletedZerglingSpawingPool();
		GiveOrders.instance.spawnZerglings(larvae, completedSpawingPools);
	}

	private void morphToDrones() {
		System.out.println("morph to drones");
		Set<Unit> larvae = Perception.instance.setOfUnitsByType
				.get(UnitTypes.Zerg_Larva.ordinal());
		System.out.println("larvae : " + larvae.size());
		if (larvae != null && larvae.size() > 0) {
			System.out.println("morph to drones 2");
			GiveOrders.instance.morpToDrones(larvae);
			Perception.instance.morphedDrone = true;
		}
	}

	private void spawnOverlords() {
		if (Perception.instance.totalMinerals >= 100) {
			Set<Unit> larvae = Perception.instance.setOfUnitsByType
					.get(UnitTypes.Zerg_Larva.ordinal());
			GiveOrders.morphToOverlord(larvae);
			Perception.instance.supplyCap = Perception.instance.supplyTotal;
		}
	}

	private void buildSpawingPools() {
		System.out.println("build spawing pools <<<<<<<<<");
		Set<Unit> drones = Perception.instance.setOfUnitsByType
				.get(UnitTypes.Zerg_Drone.ordinal());
		Set<Unit> overlords = Perception.instance.setOfUnitsByType
				.get(UnitTypes.Zerg_Overlord.ordinal());
		System.out.println("<<<<<<<<< drones " + drones.size() + " overlords: "
				+ overlords.size() + " <<<<<<<<<<<");

		Perception.instance.poolDrone = GiveOrders.instance.buildSpawingPools(
				drones, overlords);
		System.out.println("Perception.instance.totalMinerals: "
				+ Perception.instance.totalMinerals);
		System.out.println("Perception.instance.poolDrone "
				+ Perception.instance.poolDrone);
	}

	private void claimMinerals() {
		// System.out.println("HEY!");

		Set<Unit> drones = Perception.instance.getDrones();
		Set<Unit> minerals = Perception.instance.getUnclaimedMinerals();

		// if (drones!=null && minerals !=null) {
		// System.out.println("drones " + drones.size() + " minerals " +
		// minerals.size());
		// } else {
		// System.out.println("BAD!!!");
		// }

		Set<Unit> claimedMinerals = GiveOrders.instance.collectMinerals(drones,
				minerals);
		if (claimedMinerals != null && !claimedMinerals.isEmpty()) {
			Perception.instance.claimed.addAll(claimedMinerals);
			// System.out.println("claimedMineralIDs: " +
			// claimedMinerals.size());
		}

		// System.out.println("end");
	}

	private boolean enoughResourcesAvailable() {
		// System.out.println("checking enough resources!: " +
		// currentState.toString());
		int Minerals, Gas = 0;
		Minerals = Perception.instance.totalMinerals;
		Gas = Perception.instance.totalGas;

		if (Minerals < 100 || Gas < 100)
			return false;
		else
			return true;
	}

	private boolean enemyNearby() {
		// if the enemy appears in the window, then...
		// this is assumed with the number of VISIBLE units
		int allEnemyUnits = Perception.instance.allVisibleEnemyUnits().size();
		// System.out.println("checking enemies!: " + allEnemyUnits);
		if (allEnemyUnits > 0) {
			// TODO - testing
			// System.out.println("Enemy in sight!  This is how many: " +
			// count);
			return true;
		} else
			return false;
	}

	private boolean enoughAttackersAvailable() {
		//TODO replace with actual implementation
		return (attackers.size() > MIN_ATTACKERS);
		
		
//		int drones;
//		drones = Perception.instance.setOfUnitsByType.get(
//				UnitTypes.Zerg_Drone.ordinal()).size();
//		if (drones > MIN_DRONES)
//			return true;
//		else
//			return false;
	}

	private boolean enoughBuildingsAvailable() {
		/**
		 * TODO Please use an appropriate Zerg Building type, I just Chose
		 * Hatchery, is that a good choice? possibly lairs, they have different
		 * capabilities
		 */
		int hatcheries, drones, spawnpool = 0;
		// this causes a bug, do we need a spawning pool?
		// spawnpool = Perception.instance.unitAvailableCountByType
		// .get(UnitTypes.Zerg_Spawning_Pool.ordinal());
		hatcheries = Perception.instance.unitAvailableCountByType
				.get(UnitTypes.Zerg_Hatchery.ordinal());

		// System.out.println("checking enough Buildings");

		if ((hatcheries < MIN_HATCHERIES) || (spawnpool < MIN_SPAWNING_POOL))
			return false;
		else
			return true;
	}

	private boolean lowEnemyCount() {
		return Perception.instance.enemyUnitVisibleCountsByType.size() < ENEMY_UNIT_SAFE_COUNT;
	}

}
