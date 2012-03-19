package edu.drexel.cs680.prj1.giveorders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.drexel.cs680.prj1.executeorders.ExecuteOrders;
import edu.drexel.cs680.prj1.logistics.Logistics;
import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType.UnitTypes;

public class GiveOrders {

	private JNIBWAPI bwapi;
	public static GiveOrders instance;
	
	private List<PatrolLocation> patrolLocations;

	private Random r;

	public GiveOrders(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
		r = new Random();
		patrolLocations = new ArrayList<PatrolLocation>();
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
			Logistics.Squadron sqaud) {
		System.out.println(String.format("Enemy/Squad:%d/%d",
				allEnemyUnits.size(), sqaud.units.size()));
		ExecuteOrders.instance
				.moveCloseToEnemy(sqaud, allEnemyUnits);
		attack(sqaud.units, allEnemyUnits);
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

	public void sendKamakaze(Unit k) {
		PatrolLocation patrolLocation = getKamakazeTile(k);
		ExecuteOrders.instance.patrolTile(k.getID(), patrolLocation.x, patrolLocation.y);

		System.out.println(String.format("Kamakazee sent to: %s", patrolLocation));
	}
	
	// send to random area in the map that is walkable
	public void sendPatrol(Set<Unit> patrolers) {
		List <PatrolLocation> corners = getCorners();
		
		for(Unit u: patrolers)
		{
			PatrolLocation patrolLocation;
			
			if (corners.isEmpty()) {
				patrolLocation = getAvailablePatrolTile();
				if (patrolLocation==null) {
					System.out.println(String.format("Could not find patrol location for: %s", u));
					continue;
				}
			} else {
				patrolLocation = corners.remove(0);
			}
			
			ExecuteOrders.instance.patrolTile(u.getID(), patrolLocation.x, patrolLocation.y);
		}
	}

	private List<PatrolLocation> getCorners() {
		List<PatrolLocation> corners = new ArrayList<PatrolLocation>();
		int w = bwapi.getMap().getWalkWidth();
		int h = bwapi.getMap().getWalkHeight();

		int x = 0, y = 0;
		corners.add(new PatrolLocation(x*8, y*8));
		
		x = w; y = 0;
		corners.add(new PatrolLocation(x*8, y*8));
		
		x = w; y = h;
		corners.add(new PatrolLocation(x*8, y*8));
		
		x = 0; y = h;
		corners.add(new PatrolLocation(x*8, y*8));
		
		return corners;
	}

	private class PatrolLocation{
		int x,y;
		public PatrolLocation(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public boolean equals(Object obj) {
			if (obj instanceof PatrolLocation) {
				PatrolLocation other = (PatrolLocation)obj;
				return (other.x == x && other.y == y);
			}
			
			return false;
		}
		
		public String toString() {
			return String.format("%d:%d", x, y);
		}
	}
	
	private PatrolLocation getKamakazeTile(Unit k) {
		int w = bwapi.getMap().getWalkWidth();
		int h = bwapi.getMap().getWalkHeight();
		
		int x = (int)((1 - (float)(k.getTileX()/w)) * w) * 8;
		int y = (int)((1 - (float)(k.getTileY()/h)) * h) * 8;
		
		PatrolLocation patrolLocation = new PatrolLocation(x, y);
		return patrolLocation;
	}
	
	private PatrolLocation getAvailablePatrolTile(){
		int w = bwapi.getMap().getWalkWidth();
		int h = bwapi.getMap().getWalkHeight();
		
//		PatrolLocation patrolLocation = new PatrolLocation(r.nextInt(w), r.nextInt(h));
		PatrolLocation patrolLocation = new PatrolLocation(r.nextInt(w)*8, r.nextInt(h)*8);
		int count = 0;
		while (!patrolLocations.contains(patrolLocation) && ++count < 500) {
			patrolLocation = new PatrolLocation(r.nextInt(w)*8, r.nextInt(h)*8);
			System.out.println(String.format("location %s, was it already seen? %s", patrolLocation, patrolLocations.contains(patrolLocation)));
			return patrolLocation;
		}
		patrolLocations.add(patrolLocation);
		
		return null;

		/*
	// returns an available tile from the map that hasn't been patrolled yet	
	
		int randomX, randomY, maxX, maxY, totalTiles = 0;
		boolean foundTile = false;
//		totalTiles = 5000*5000;
		int[] dest = new int[2];
		for(int i = 0; i<totalTiles+1;)
		{
			//randomX = (int) (Perception.instance.mapXmax/2+(Math.random()*50-Math.random()*50));
			//randomY = (int) (Perception.instance.mapYmax/2+(Math.random()*50-Math.random()*50));
		
			randomX = (int) Math.round(Math.random()*10000);
			randomY = (int) Math.round(Math.random()*10000);
		
			if(patrolMap[randomX][randomY]==0)
			{
				dest[0]=randomX;
				dest[1]=randomY;
				patrolMap[randomX][randomY]=1;
				return dest;
			}
		}
		
		
		//int step=(int) Math.round((Math.random()*3));
		
		
		dest[0]=0;
		dest[1]=0;
		return dest;
		*/
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
