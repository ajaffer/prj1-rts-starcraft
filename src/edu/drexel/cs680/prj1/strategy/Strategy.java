package edu.drexel.cs680.prj1.strategy;

import java.util.List;
import java.util.Set;

import edu.drexel.cs680.prj1.giveorders.GiveOrders;
import edu.drexel.cs680.prj1.perception.AgentState;
import edu.drexel.cs680.prj1.perception.Perception;
import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType.UnitTypes;

public class Strategy {

	public States consumeState = States.MORPH_DRONES;
	public States produceState = States.SPAWN_OVERLORDS;

	private static final int ENEMY_UNIT_SAFE_COUNT = 10;
	private static final int MIN_HATCHERIES = 5;
	private static final int MIN_DRONES = 5;
	private static final int MIN_SPAWNING_POOL = 1;

	private JNIBWAPI bwapi;

	/** FSM States */
	public enum States {
		MORPH_DRONES, SPAWN_POOLS, SPAWN_OVERLORDS, SPAWN_ZERGLINS, PAUSE, ATTACK, PATROL
	};

	public static Strategy instance;

	public Strategy(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
	}

	public void updateFSM() {
		States lastConsumeState = consumeState, lastProduceState = produceState;

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
		if (enoughZerglings()) {
			produceState = States.PATROL;
		}
		if (enemyLocated()) {
			produceState = States.ATTACK;
		}

//		if (!lastConsumeState.equals(consumeState)) {
//			System.out.println(String.format("Consume State>>>%s<<<",
//					consumeState));
//		}
//		if (!lastProduceState.equals(produceState)) {
//			System.out.println(String.format("Produce State>>>%s<<<",
//					produceState));
//		}
	}

	private boolean enemyLocated() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean enoughZerglings() {
		// TODO Auto-generated method stub
		return false;
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
			
			
		case PATROL:
			patrol();
			
		case ATTACK:
			attack();
			

		default:
			break;
		}

	}

	private void patrol() {
		// TODO Auto-generated method stub
		// Send out a few zerglings to different corners to locate enemy
		
	}

	private void attack() {
		// TODO implement the following stubs
		GiveOrders.instance.moveZerglingsToEnemenyLocation();
		GiveOrders.instance.attackEnemenyLocation();
		
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
		int drones;
		drones = Perception.instance.setOfUnitsByType.get(
				UnitTypes.Zerg_Drone.ordinal()).size();
		if (drones > MIN_DRONES)
			return true;
		else
			return false;
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
