package edu.drexel.cs680.prj1.strategy;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.drexel.cs680.prj1.executeorders.Node;
import edu.drexel.cs680.prj1.giveorders.GiveOrders;
import edu.drexel.cs680.prj1.logistics.Logistics;
import edu.drexel.cs680.prj1.pathfinding.PathFindingUtil;
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
	private static final int MIN_ZERGLINGS_TO_PATROL = 2;
	private static final int MIN_ATTACKERS = 5;
	private static final int MIN_PATROLLERS = 10;
	
//	public int[][] patrolMap;
	
//	private Set<Unit>attackers;
	private Set<Unit>patrolers;
//	private Set<Unit>defenders;
 
	private JNIBWAPI bwapi;
	
	public boolean patrolOut;

	/** FSM States */
	public enum States {
		MORPH_DRONES, SPAWN_POOLS, SPAWN_OVERLORDS, SPAWN_ZERGLINS, PAUSE, ATTACK, PATROL, DEFEND
	};

	public static Strategy instance;

	public Strategy(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
		
		patrolers = new HashSet<Unit>();
//		attackers = new HashSet<Unit>();
		
		patrolOut = false;
		int x, y;
		//x = Perception.instance.mapXmax;
		//y = Perception.instance.mapYmax;
//		x = 5000;
//		y = 5000;
//		patrolMap = new int[x+1][y+1];
//		
//		//initialize patrol map
//		for(int i=0;i<x+1;i++)
//			for(int j=0;j<y+1;j++)
//				patrolMap[i][j]=0;
		
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
//		if (patrolers!=null && !patrolers.isEmpty()) {
//			actionState = States.PATROL;			
//		}		
		
		if (enemyLocated()) {
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
					actionState));
		}
	}

	private void displayMapWalkable() {
		System.out.println("here....");
		System.out.println(String.format("wlkable.length %d", bwapi.getMap().walkable.length));
		System.out.println(String.format("width %d", bwapi.getMap().getWidth()));
		System.out.println(String.format("height %d", bwapi.getMap().getHeight()));
		System.out.println(String.format("walk width %d", bwapi.getMap().getWalkWidth()));
		System.out.println(String.format("walk height %d", bwapi.getMap().getWalkHeight()));
		System.out.println("here end....");

		
//		Map map = bwapi.getMap();
//		int i = 0;
//		for (boolean walkable : map.walkable) {
//			i++;
//			if (walkable){
//				System.out.print(i + ", ");
//			}
//		}
	}
	
	private Random r = new Random();
	
//	private void astarTest(){
//		Set<Unit> drones = Perception.instance.setOfIdleUnitsByType.get(UnitTypes.Zerg_Drone.ordinal());
//		if (drones!=null) {
//			Unit drone = drones.toArray(new Unit[0])[r.nextInt(drones.size())];
////			Unit drone2 = drones.toArray(new Unit[0])[2];
//			List<Unit> enemies = bwapi.getEnemyUnits();
//			if (enemies!=null && !enemies.isEmpty()) {
//				Unit enemy = enemies.get(r.nextInt(enemies.size()));
////				List<Node> path = PathFindingUtil.instance.findPath(drone.getTileX(), drone.getTileY(), enemy.getTileX(), enemy.getTileY());
//				List<Node> path = PathFindingUtil.instance.findPath(drone.getTileX(), drone.getTileY(), 101, 17);
//				
//				if (path!=null) {
//					System.out.println("Ging to look for enemy type " + enemy.getTypeID());
//					for (Node moveTo : path) {			
//						System.out.println(String.format("moving to %d,%d", moveTo.x, moveTo.y));
//						bwapi.move(drone.getID(), (int) moveTo.x,
//								(int) moveTo.y);
//					}
//				}
//			}
//		}
//	}
	
	public void apply() {
//		displayMapWalkable();
		establishPatrolers();
//		updatePatrollers();
		establishAttackers();
		claimMinerals();
		patrol();
//		astarTest();
		
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
		
//		case PATROL:
//			patrol();
//			break;
			
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

	private void establishPatrolers()
	{
		if (patrolers.size() < MIN_PATROLLERS) {
//			Set<Unit> overlords = Perception.instance.setOfIdleUnitsByType.get(UnitTypes.Zerg_Overlord.ordinal());
//			if (overlords!=null) {
//				patrolers.addAll(overlords);
//			}

			Set<Unit> zerglings = Perception.instance.setOfUnitsByType.get(UnitTypes.Zerg_Zergling.ordinal());
			if (zerglings!=null && zerglings.size() >= MIN_PATROLLERS) {
				patrolers.addAll(zerglings);
				Logistics.instance.idlePatrollers.addAll(patrolers);
				System.out.println(String.format("patrolers:%d", patrolers.size()));
			}
		}		
	}
	
//	private void updatePatrollers() {
//		System.out.println(String.format("Idle Patrollers: %d", Logistics.instance.idlePatrollers.size()));
//		System.out.println(String.format("Running Patrollers: %d", Logistics.instance.runningPatrollers.size()));
//		for (Unit n : Logistics.instance.runningPatrollers) {
//			if (!n.isPatrolling()){
//				Logistics.instance.runningPatrollers.remove(n);
//				Logistics.instance.idlePatrollers.add(n);
//			}
//		}
//	}
	
	private void establishAttackers()
	{
		if (!(patrolers.isEmpty())) {
			Set<Unit> idleZerglings = Perception.instance.setOfIdleUnitsByType.get(UnitTypes.Zerg_Zergling.ordinal());

			if (idleZerglings != null) {
				idleZerglings.removeAll(patrolers);
//				idleZerglings.removeAll(attackers);
				Logistics.instance.addUnits(idleZerglings);
//				attackers.addAll(idleZerglings);
//				System.out.println(String.format("attackers:%d", attackers.size()));
			}
		}
	}
	
	
	private void patrol() {
		if (patrolOut) {
//			System.out.println("patrol already out");
			return;
		}
		
		if (patrolers.size() >= MIN_PATROLLERS && Logistics.instance.noOfSquads() > 0){
			System.out.println("send patrol");
			patrolOut=true;
//			Set<Unit> p = Logistics.instance.idlePatrollers;
			GiveOrders.instance.sendPatrol(patrolers);
//			Logistics.instance.idlePatrollers.remove(p);
//			Logistics.instance.runningPatrollers.addAll(p);
//			System.out.println("patrol was sent!!!");
		}
	}
	
	private double distance(int x1, int y1, int x2, int y2)
	{
		return Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
	}

	private Set<Unit> getIdleZerglings() {
		Set<Unit> idleZerglings = Perception.instance.setOfUnitsByType.get(UnitTypes.Zerg_Zergling.ordinal());
		return idleZerglings;
	}
	
	private void attack() {
		Set<Unit> enemyUnits = getDiscoveredEnemyUnits();
		if (enemyUnits != null && !enemyUnits.isEmpty()) {
//			System.out.println(String.format("Attack %d enemies!!", enemyUnits.size()));
			GiveOrders.instance.attackEnemy(enemyUnits, Logistics.instance.getSquad());
		}
		
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

	private int[] enemyTypes = new int[]{UnitTypes.Zerg_Hatchery.ordinal(), UnitTypes.Zerg_Hydralisk.ordinal(), 
			UnitTypes.Zerg_Hydralisk_Den.ordinal(), UnitTypes.Zerg_Drone.ordinal(), UnitTypes.Zerg_Egg.ordinal(),
			UnitTypes.Zerg_Overlord.ordinal()};

	private boolean enemyLocated() {
		Set<Unit> enemyUnits = getDiscoveredEnemyUnits();
		return (enemyUnits != null && !enemyUnits.isEmpty());
		
//		return Perception.instance.allVisibleEnemyUnits().size() > 0;
//		java.util.Map<Integer, List<Unit>> listOfVisibleEnemyUnitsByType = Perception.instance.getListOfVisibleEnemyUnitsByType();
//		if (listOfVisibleEnemyUnitsByType!=null) {
//			for (int enemyType : enemyTypes) {
//				List<Unit> enemies = listOfVisibleEnemyUnitsByType.get(enemyType);
//				if (enemies != null) {
//					return true;
//				}
//			}
//		}

//		return false;
	}

	
	private Set<Unit> getDiscoveredEnemyUnits() {
		return Perception.instance.allVisibleEnemyUnits();
		
//		Set<Unit> enemyUnits = new HashSet<Unit>();
//		java.util.Map<Integer, List<Unit>> listOfVisibleEnemyUnitsByType = Perception.instance.getListOfVisibleEnemyUnitsByType();
//		if (listOfVisibleEnemyUnitsByType!=null) {
//			for (int enemyType : enemyTypes) {
//				List<Unit> enemies = listOfVisibleEnemyUnitsByType.get(enemyType);
//				if (enemies != null) {
//					enemyUnits.addAll(enemies);
//				}
//			}
//		}
				
//		return enemyUnits;		
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
		return (Logistics.instance.noOfSquads() > 0);
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
