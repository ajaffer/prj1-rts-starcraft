package edu.drexel.cs680.prj1.perception;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public static Perception instance;

	public Perception(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
		unitAvailableCountByType = new HashMap<Integer, Integer>();
		enemyUnitCountsByType = new HashMap<Integer, Integer>();
		listOfUnitsIdleByType = new HashMap<Integer, List<Unit>>();
		listOfEnemyUnitsByType = new HashMap<Integer, List<Unit>>();
		lastCommandByUnit = new HashMap<Unit, Integer>();
	}
	
	public void collectData() {
		updateAvailableUnitCountsByType();
		updateEnemyUnitCountsByType();
		updateListOfIdleUnitsByType();
		updateListOfEnemyUnitsByType();
		updateLastCommandsByUnit();
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
		System.out
				.println(p.unitAvailableCountByType.get(UnitTypes.Zerg_Drone.ordinal()));
	}

}
