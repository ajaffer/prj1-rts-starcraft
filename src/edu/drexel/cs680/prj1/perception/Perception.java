package edu.drexel.cs680.prj1.perception;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType.UnitTypes;

public class Perception {

	private static final double MIN_RADIUS = 50;

	public JNIBWAPI bwapi;

	public Map<Integer, Integer> unitAvailableCountByType;
	public Map<Integer, Integer> enemyUnitVisibleCountsByType;
	public Map<Integer, Set<Unit>> setOfUnitsByType;
	public Map<Integer, Set<Unit>> setOfIdleUnitsByType;
	public Map<Integer, Set<Unit>> setOfNeutralUnitsByType;
	
	public int mapXmax, mapYmax;
	
//	public List<Unit> listOfAttackers;
	public Map<Unit, Integer> lastCommandByUnit;
	
	// used in Strategy
//	public int totalEnemyUnits;
//	public int totalMilitary;
	public int totalMinerals;
//	public int totalExtractors;
	public int totalGas;
	public boolean morphedDrone;
	public boolean patrolSent;

	public int poolDrone = -1;

	public HashSet<Unit> claimed;

	public int supplyUsed;

	public int supplyTotal;

	public int supplyCap;

//	public int armyDrone;
//	public int armyZergling;
//	public int buildingExtractor;
	
	
	public static Perception instance;

	public Perception(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
		
		unitAvailableCountByType = new HashMap<Integer, Integer>();
		enemyUnitVisibleCountsByType = new HashMap<Integer, Integer>();
		setOfUnitsByType = new HashMap<Integer, Set<Unit>>();
		setOfIdleUnitsByType = new HashMap<Integer, Set<Unit>>();
		setOfNeutralUnitsByType = new HashMap<Integer, Set<Unit>>();
		
		mapXmax = bwapi.getMap().getWidth();
		mapYmax = bwapi.getMap().getHeight();
				
//		listOfAttackers = new ArrayList<Unit>();
		lastCommandByUnit = new HashMap<Unit, Integer>();
		claimed = new HashSet<Unit>();
		
		totalMinerals = 0;
		totalGas = 0;
		patrolSent = false;
//		totalExtractors = 0;
//		armyDrone = 0;		// drone army is 5 Zerg Drones for gathering		
//		armyZergling = 0;   // Zergling army is 20 Zerglings for attacking
//		buildingExtractor = 0;
		

//		enemyUnitCountsByType = new HashMap<Integer, Integer>();
		morphedDrone = false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		Set<Unit> zerglings = setOfUnitsByType.get(UnitTypes.Zerg_Zergling.ordinal());
		if (zerglings!=null && !zerglings.isEmpty()) {
			builder.append(String.format("zerglings:%s, ", zerglings.size()));
		}
		builder.append(String.format("visible enemies:%s, ", allVisibleEnemyUnits().size()));
//		builder.append(String.format("lastCommands:%s, ", Util.toString(lastCommandByUnit)));
		builder.append(String.format("minerals:%d, ", totalMinerals));
		builder.append(String.format("gas:%d, ", totalGas));
//		builder.append(String.format("isAttackingEnemyNearbyHatchery:%s, ", isEnemyNearbyHatchery()));
		
		String str = builder.toString();
		return str;
	}
	

	
	public void collectData() {
	
		updateAvailableMinerals();
		updateAvailableGas();
		updateAvailableUnitCountsByType();
		updateEnemyUnitVisibleCountsByType();
		updateSetOfUnitsByType();
		updateSetOfNeutralUnitsByType();
		updateSetOfIdleUnitsByType();
		updateSupplyUsed();
		updateSupplyTotal();
//		updateLastCommandsByUnit();
//////		updateEnemyUnitCount();
	}

	private void updateSetOfNeutralUnitsByType() {
		List<Unit> neutralUnits = bwapi.getNeutralUnits();
		for (Unit neutralUnit : neutralUnits) {
			addUnitByType(neutralUnit, setOfNeutralUnitsByType);
		}
//		System.out.println("neutralUnits: " + setOfNeutralUnitsByType.size());
	}

	private void updateSupplyTotal() {
		supplyTotal = bwapi.getSelf().getSupplyTotal();		
	}

	private void updateSupplyUsed() {
		supplyUsed = bwapi.getSelf().getSupplyUsed();		
	}

	private void updateAvailableMinerals()
	{
		totalMinerals = bwapi.getSelf().getMinerals();
	}
	
	private void updateAvailableGas()
	{
		totalGas = bwapi.getSelf().getCumulativeGas();
	}
	
//	private void updateEnemyUnitCount()
//	{
//		int count = 0;
//		for(Unit eachUnit: bwapi.getEnemyUnits())
//		{
//			if(eachUnit.isVisible())
//				count++;
//		}
//		System.out.println(String.format("totalEnemyUnits:%d", totalEnemyUnits));
//		totalEnemyUnits = count;
//	}
			

	public Set<Unit> allVisibleEnemyUnits() {
		Set<Unit> enemyUnits = new HashSet<Unit>();
		
		Collection<List<Unit>> metaListOfEnemyUnits = getListOfVisibleEnemyUnitsByType().values();
		for (List<Unit> enemies : metaListOfEnemyUnits) {
			enemyUnits.addAll(enemies);
		}
		return enemyUnits;
	}
	
	public Map<Integer, List<Unit>> getListOfVisibleEnemyUnitsByType() {
		Map<Integer, List<Unit>> listOfEnemyUnitsByType = new HashMap<Integer, List<Unit>>();
		List<Unit> enemyUnits = bwapi.getEnemyUnits();
		for (Unit enemy : enemyUnits) {
			if (!enemy.isVisible()) {
				continue;
			}
			List<Unit> enemyUnitList = listOfEnemyUnitsByType.get(enemy.getTypeID());
			if (enemyUnitList == null) {
				enemyUnitList = new ArrayList<Unit>();
			}
			enemyUnitList.add(enemy);
			listOfEnemyUnitsByType.put(enemy.getTypeID(), enemyUnitList);
		}
		
		return listOfEnemyUnitsByType;
	}

	private void updateEnemyUnitVisibleCountsByType() {
		for (Unit enemy : bwapi.getEnemyUnits()) {
			if (enemy.isVisible()) {
				incrementUnitType(enemy.getTypeID(), enemyUnitVisibleCountsByType);
			}
		}
	}

	private void updateLastCommandsByUnit() {
		for (Unit u : bwapi.getMyUnits()) {
			lastCommandByUnit.put(u, u.getLastCommandID());
		}
	}

	private void updateSetOfUnitsByType() {
		for (Unit u : bwapi.getMyUnits()) {
			addUnitByType(u, setOfUnitsByType);
		}
	}

	private void updateSetOfIdleUnitsByType() {
		for (Unit u : bwapi.getMyUnits()) {
			if (u.isIdle()) {
				addUnitByType(u, setOfIdleUnitsByType);
			}
		}
	}

	private void addUnitByType(Unit u, Map<Integer, Set<Unit>> setOfUnitsByType) {
		Set<Unit> availableUnits = setOfUnitsByType.get(u.getTypeID());
		if (availableUnits == null) {
			availableUnits = new HashSet<Unit>();
			setOfUnitsByType.put(u.getTypeID(), availableUnits);
		}
		
		availableUnits.add(u);
	}

	private void updateAvailableUnitCountsByType() {
		for (Unit u : bwapi.getMyUnits()) {
			incrementUnitType(u.getTypeID(), unitAvailableCountByType);
		}
	}

	private void incrementUnitType(int typeID, Map<Integer,Integer> unitByType) {
		Integer count = unitByType.get(typeID);
		if (count == null) {
			unitByType.put(typeID, 1);
		} else {
			unitByType.put(typeID, count + 1);
		}
	}
	
	private Unit getHatchery() {
		Set<Unit> hatcheryUnits = setOfUnitsByType.get(UnitTypes.Zerg_Hatchery);
		
		if (hatcheryUnits==null || hatcheryUnits.isEmpty()) {
			return null;
		}
		Unit hatchery = hatcheryUnits.toArray(new Unit[0])[0];
		return hatchery;
	}
	
	private boolean isEnemyNearbyHatchery() {
		Map<Integer, List<Unit>> listOfEnemyUnitsByType = getListOfVisibleEnemyUnitsByType();
		if (listOfEnemyUnitsByType.isEmpty()) {
			System.out.println("no enemies visible");
			return false;
		}
		
		Unit hatchery = getHatchery();
		for (Map.Entry<Integer, List<Unit>> e : listOfEnemyUnitsByType.entrySet()) {
			List<Unit> enemyUnits = e.getValue();
			for (Unit enemy : enemyUnits) {
				if (closeBy(enemy, hatchery)) {
					return true;
				}
			}
		}
		
		System.out.println("no nearby enemies");
		return false;
	}
	
	private boolean closeBy(Unit a, Unit b) {
		double distance = Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(),2));
		System.out.println(String.format("distance:%d", distance));
		return distance < MIN_RADIUS;
	}

	public static void main(String[] args) {
		Perception p = new Perception(null);
		p.unitAvailableCountByType.put(UnitTypes.Zerg_Drone.ordinal(), 2);
		System.out.println(p.unitAvailableCountByType.get(UnitTypes.Zerg_Drone.ordinal()));
	}

	public Set<Unit> getDrones() {
		Set<Unit> drones = Perception.instance.setOfIdleUnitsByType.get(UnitTypes.Zerg_Drone.ordinal());
		if (drones==null || drones.isEmpty()) {
			return new HashSet<Unit>();
		}
//		System.out.println("drones "+drones.size()+" size");
		for (Unit drone : drones) {
			if (drone.getID() == Perception.instance.poolDrone) {
				drones.remove(drone);
			}
		}
		
		return drones;
	}

	public Set<Unit> getUnclaimedMinerals() {
		Set<Unit> minerals = setOfNeutralUnitsByType.get(UnitTypes.Resource_Mineral_Field.ordinal());
		if (minerals==null || minerals.isEmpty()) {
//			System.out.println("No Minerals Found!!");
			return new HashSet<Unit>();
		}

//		System.out.println("minerals "+minerals.size()+" size");
		minerals.removeAll(claimed);
//		System.out.println("after remove, minerals "+minerals.size()+" size");
		return minerals;
	}
	
	public Set<Unit> getCompletedZerglingSpawingPool() {
		Set<Unit> spawningPools = Perception.instance.setOfUnitsByType.get(UnitTypes.Zerg_Spawning_Pool.ordinal());

		
		for (Unit spawningPool : spawningPools) {
			if (!spawningPool.isCompleted()) {
				spawningPools.remove(spawningPool);
			}
		}
		
		return spawningPools;
	}

	

}
