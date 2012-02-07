package edu.drexel.cs680.prj1.perception;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.corba.se.spi.extension.ZeroPortPolicy;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType.UnitTypes;

public class Perception {

	public JNIBWAPI bwapi;

	public Map<Integer, Integer> unitAvailableCountByType;
	public Map<Integer, Integer> enemyUnitCountsByType;
	public Map<Integer, List<Unit>> listOfUnitsIdleByType;
	public Map<Integer, List<Unit>> listOfEnemyUnitsByType;
	
	public Map<Unit, Integer> lastCommandByUnit;
	
	// used in Strategy
	public int totalEnemyUnits;
	public int totalMilitary;
	public int totalMinerals;
	public int totalExtractors;
	public int totalGas;
	public int armyDrone;
	public int armyZergling;
	public int buildingExtractor;
	
	
	public static Perception instance;

	public Perception(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
		
		unitAvailableCountByType = new HashMap<Integer, Integer>();
		enemyUnitCountsByType = new HashMap<Integer, Integer>();
		listOfUnitsIdleByType = new HashMap<Integer, List<Unit>>();
		listOfEnemyUnitsByType = new HashMap<Integer, List<Unit>>();
		lastCommandByUnit = new HashMap<Unit, Integer>();
		
		totalMinerals = 0;
		totalGas = 0;
		totalExtractors = 0;
		armyDrone = 0;		// drone army is 5 Zerg Drones for gathering		
		armyZergling = 0;   // Zergling army is 20 Zerglings for attacking
		buildingExtractor = 0;
		

		enemyUnitCountsByType = new HashMap<Integer, Integer>();
		//updateEnemyUnitCount();

	}
	
	public void collectData() {
	
		updateAvailableMinerals();
		updateAvailableGas();
		updateAvailableUnitCountsByType();
		updateEnemyUnitCountsByType();
		updateListOfIdleUnitsByType();
		updateListOfEnemyUnitsByType();
		updateLastCommandsByUnit();
		updateEnemyUnitCount();
	}

	private void updateAvailableMinerals()
	{
		totalMinerals = bwapi.getSelf().getCumulativeMinerals();
	}
	
	private void updateAvailableGas()
	{
		totalMinerals = bwapi.getSelf().getCumulativeGas();
	}
	
	private void updateEnemyUnitCount()
	{
		int count = 0;
		for(Unit eachUnit: bwapi.getEnemyUnits())
		{
			if(eachUnit.isVisible())
				count++;
		}
		System.out.println(String.format("totalEnemyUnits:%d", totalEnemyUnits));
		totalEnemyUnits = count;
	}
			

	public List<Unit> allEnemyUnits() {
		List<Unit> allEnemyUnits = new ArrayList<Unit>();
		Collection<List<Unit>> metaListOfEnemyUnits = listOfEnemyUnitsByType.values();
		for( List<Unit> enemyUnitList : metaListOfEnemyUnits) {
			allEnemyUnits.addAll(enemyUnitList);
		}
		
		return allEnemyUnits;
	}

	private void updateListOfEnemyUnitsByType() {
		for (Unit enemy : bwapi.getEnemyUnits()) {
			addUnitByType(enemy, listOfEnemyUnitsByType);
		}
		
	}

	private void updateEnemyUnitCountsByType() {
		for (Unit enemy : bwapi.getEnemyUnits()) {
			incrementUnitType(enemy.getTypeID(), enemyUnitCountsByType);
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
		
		System.out.println(String.format("# of Zerglings: %d", listOfUnitsIdleByType.get(UnitTypes.Zerg_Zergling).size()));
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
	
	public static void main(String[] args) {
		Perception p = new Perception(null);
		p.unitAvailableCountByType.put(UnitTypes.Zerg_Drone.ordinal(), 2);
		System.out.println(p.unitAvailableCountByType.get(UnitTypes.Zerg_Drone.ordinal()));
	}

}
