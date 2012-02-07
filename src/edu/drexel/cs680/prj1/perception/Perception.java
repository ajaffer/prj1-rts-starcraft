package edu.drexel.cs680.prj1.perception;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.drexel.cs680.prj1.util.Util;
import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType.UnitTypes;

public class Perception {

	private static final double MIN_RADIUS = 50;

	public JNIBWAPI bwapi;

	public Map<Integer, Integer> unitAvailableCountByType;
	public Map<Integer, Integer> enemyUnitVisibleCountsByType;
	public Map<Integer, List<Unit>> listOfUnitsIdleByType;
//	public List<Unit> listOfAttackers;
	public Map<Unit, Integer> lastCommandByUnit;
	
	// used in Strategy
//	public int totalEnemyUnits;
//	public int totalMilitary;
	public int totalMinerals;
//	public int totalExtractors;
	public int totalGas;

//	public int armyDrone;
//	public int armyZergling;
//	public int buildingExtractor;
	
	
	public static Perception instance;

	public Perception(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
		
		unitAvailableCountByType = new HashMap<Integer, Integer>();
		enemyUnitVisibleCountsByType = new HashMap<Integer, Integer>();
		listOfUnitsIdleByType = new HashMap<Integer, List<Unit>>();
//		listOfAttackers = new ArrayList<Unit>();
		lastCommandByUnit = new HashMap<Unit, Integer>();
		
		totalMinerals = 0;
		totalGas = 0;
//		totalExtractors = 0;
//		armyDrone = 0;		// drone army is 5 Zerg Drones for gathering		
//		armyZergling = 0;   // Zergling army is 20 Zerglings for attacking
//		buildingExtractor = 0;
		

//		enemyUnitCountsByType = new HashMap<Integer, Integer>();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		List<Unit> zerglings = listOfUnitsIdleByType.get(UnitTypes.Zerg_Zergling.ordinal());
		if (zerglings!=null && !zerglings.isEmpty()) {
			builder.append(String.format("zerglings:%s, ", zerglings.size()));
		}
		builder.append(String.format("visisble enemies:%s, ", allVisibleEnemyUnits().size()));
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
		updateListOfIdleUnitsByType();
//		updateLastCommandsByUnit();
//////		updateEnemyUnitCount();
	}

	private void updateAvailableMinerals()
	{
		totalMinerals = bwapi.getSelf().getCumulativeMinerals();
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
			

	public List<Unit> allVisibleEnemyUnits() {
		List<Unit> enemyUnits = new ArrayList<Unit>();
		
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

	private void updateListOfIdleUnitsByType() {
		for (Unit u : bwapi.getMyUnits()) {
			if (u.isIdle()) {
				addUnitByType(u, listOfUnitsIdleByType);
			}
		}
	}

	private void addUnitByType(Unit u, Map<Integer, List<Unit>> listOfUnitsByType) {
		List<Unit> availableUnits = listOfUnitsByType.get(u.getTypeID());
		if (availableUnits == null) {
			availableUnits = new ArrayList<Unit>();
			listOfUnitsByType.put(u.getTypeID(), availableUnits);
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
		List<Unit> hatcheryUnits = listOfUnitsIdleByType.get(UnitTypes.Zerg_Hatchery);
		
		if (hatcheryUnits==null || hatcheryUnits.isEmpty()) {
			return null;
		}
		
		Unit hatchery = hatcheryUnits.get(0);
		
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

}
