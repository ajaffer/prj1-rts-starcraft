package edu.drexel.cs680.prj1.giveorders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.drexel.cs680.prj1.executeorders.ExecuteOrders;
import edu.drexel.cs680.prj1.perception.AgentState;
import edu.drexel.cs680.prj1.perception.Perception;
import edu.drexel.cs680.prj1.strategy.Strategy;
import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType.UnitTypes;

public class GiveOrders {

	private JNIBWAPI bwapi;
	public static GiveOrders instance;

	public GiveOrders(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
	}

	// public void sendOrders() {
	// if (Strategy.instance.currentState.equals(Strategy.States.Attack)) {
	// attackEnemy();
	// } else if (Strategy.instance.currentState == Strategy.States.Defend) {
	// defend();
	// } else if (Strategy.instance.currentState == Strategy.States.Build) {
	// build();
	// } else if (Strategy.instance.currentState == Strategy.States.Explore) {
	// explore();
	// }
	// }

	private void explore() {
		// TODO Auto-generated method stub

	}

	private void gatherMinerals() {
		// TODO make a Zerg Drone gather minerals. Use half of what's idle
		System.out.println("Gathering minerals...");
	}

	private void gatherGas() {
		// TODO make a Zerg Drone gather minerals. Use half of what's idle
		System.out.println("Gathering gas...");
	}

	private void buildExtractor() {
		// TODO make a Zerg Drone create an extractor. Needs to find a gas
		// source, though
		System.out.println("Attempting to create extractor...");
	}

	private void build() {
		// TODO Auto-generated method stub

		/**
		 * This is the build state, the AI is in this state because of the
		 * following factors that may not be present
		 */

		// TODO move logic to Strategy, This module should not know of
		// Percetpion
		// check if there are enough resources
		// if(Perception.instance.totalMinerals < 100) // this is from the
		// Strategy "enoughResourcesAvailable" method
		// gatherMinerals();

		// TODO move logic to Strategy
		// if(Perception.instance.buildingExtractor < 1)
		// buildExtractor();

		// if(Perception.instance.totalGas< 100)
		// gatherGas();

	}

	private void defend() {
		// TODO Auto-generated method stub

	}

	// Set<Unit> allEnemyUnits = Perception.instance.allVisibleEnemyUnits();
	// Set<Unit> allIdleZerglings = Perception.instance.listOfUnitsByType
	// .get(UnitTypes.Zerg_Zergling);
	public void attackEnemy(Set<Unit> allEnemyUnits,
			Set<Unit> allIdleZerglings) {
		System.out.println(String.format("Enemy/Player:%d/%d",
				allEnemyUnits.size(), allIdleZerglings.size()));
		ExecuteOrders.instance
				.moveCloseToEnemy(allIdleZerglings, allEnemyUnits);
		attack(allIdleZerglings, allEnemyUnits);
	}

	private void attack(Set<Unit> allIdleZerglings, Set<Unit> allEnemyUnits) {
		for (Unit unit : allIdleZerglings) {
			for (Unit enemy : allEnemyUnits) {
				System.out.println(String.format("Reached Enemy!!, Now Attack Enemy Unit#>>>%d<<<",
						unit.getID()));
				bwapi.attack(unit.getID(), enemy.getX(), enemy.getY());
				break;
			}
		}
	}
	
//	public void attackSpecific(Set<Unit> allIdleZerglings, Set<Unit> allEnemyUnits) {
//		for (Unit unit : allIdleZerglings) {
//			for (Unit enemy : allEnemyUnits) {
//				System.out.println(String.format("Attack Enemy Unit#>>>%d<<<",
//						unit.getID()));
//				bwapi.attack(unit.getID(), enemy.getX(), enemy.getY());
//				break;
//			}
//		}
//	}

	public void morpToDrones(Set<Unit> larvae) {
		if (larvae==null) {
			return;
		}
		
		for (Unit larva : larvae) {
			ExecuteOrders.instance.morphToDrone(larva);
		}
	}

	public Set<Unit> collectMinerals(Set<Unit> drones, Set<Unit> minerals) {
//		System.out.println("collectMinerals!");
		Set<Unit> claimedMinerals = new HashSet<Unit>();
		
		if (drones==null || drones.isEmpty() || minerals==null || minerals.isEmpty()){
			return claimedMinerals;
		}
		for (Unit drone : drones) {
			for (Unit mineral : minerals) {
				double distance = Math.sqrt(Math.pow(
						mineral.getX() - drone.getX(), 2)
						+ Math.pow(mineral.getY() - drone.getY(), 2));
//				System.out.println("distance: " + distance);
				if (distance < 300) {
					bwapi.rightClick(drone.getID(), mineral.getID());
					claimedMinerals.add(mineral);
					break;
				}
			}
		}
		return claimedMinerals;
	}

	public int buildSpawingPools(Set<Unit> drones, Set<Unit> overlords) {
		int poolDrone = drones.toArray(new Unit[0])[0].getID();

		for (Unit overlord : overlords) {
			ExecuteOrders.instance.buildSpawinPool(poolDrone, overlord);
		}
		return poolDrone;
	}

	public static void morphToOverlord(Set<Unit> larvae) {
		for (Unit larva : larvae) {
			ExecuteOrders.instance.morphToOverlord(larva);
		}
	}

	public void spawnZerglings(Set<Unit> larvae, Set<Unit> completedSpawingPools) {
		if (completedSpawingPools==null || completedSpawingPools.isEmpty() || larvae==null || larvae.isEmpty()){
			return;
		}
		
		for (Unit completedSpawningPool : completedSpawingPools) {
			for (Unit larva : larvae) {
				bwapi.morph(larva.getID(), UnitTypes.Zerg_Zergling.ordinal());
			}				
		}
	}

	public void moveZerglingsToEnemenyLocation() {
		// TODO Auto-generated method stub
		
	}

	public void attackEnemenyLocation() {
		// TODO Auto-generated method stub
		
	}

	public void sendPatrol(Set<Unit> patrolers) {
		// TODO Auto-generated method stub
		// send to random area in the map that is walkable
		
		
//		// add to Strategy ===
					
		for(Unit u: patrolers)
		{
			int[] newPatrolTile = Strategy.instance.getAvailablePatrolTile();
			
			ExecuteOrders.instance.patrolTile(u.getID(), newPatrolTile[0], newPatrolTile[1]);
			//bwapi.patrol(u.getID(), newPatrolTile[0], newPatrolTile[1]);
		}
				
	}


	public void returnToBase(Set<Unit> setToReturn)
	{
		System.out.println("Returning to base...nothing found");
		int[] destCoordinates = new int[2];
		
		destCoordinates[0]=bwapi.getMyUnits().get(UnitTypes.Zerg_Hatchery.ordinal()).getX();
		destCoordinates[1]=bwapi.getMyUnits().get(UnitTypes.Zerg_Hatchery.ordinal()).getY();
		
		for(Unit u: setToReturn)
			bwapi.patrol(u.getID(), destCoordinates[0], destCoordinates[1]);
			//bwapi.move(u.getID(), destCoordinates[0], destCoordinates[1]);
	}
}
