package edu.drexel.cs680.prj1.perception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType.UnitTypes;

public class Perception {

	public JNIBWAPI bwapi;

	public Map<Integer, Integer> unitAvailableCountByType;
	public Map<Integer, List<Unit>> listOfUnitsIdleByType;
	public Map<Unit, Integer> lastCommandByUnit;
	public Map<Integer, Integer> enemyUnitCountsByType;
	
	public static Perception instance;

	public Perception(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
		unitAvailableCountByType = new HashMap<Integer, Integer>();
		listOfUnitsIdleByType = new HashMap<Integer, List<Unit>>();
		lastCommandByUnit = new HashMap<Unit, Integer>();
		enemyUnitCountsByType = new HashMap<Integer, Integer>();
	}
	
	public void collectData() {
		updateAvailableUnitCountsByType();
		updateEnemyUnitCountsByType();
		updateListOfIdleUnitsByType();
		updateLastCommandsByUnit();
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
				addUnitByType(u);
			}
		}
	}

	private void addUnitByType(Unit u) {
		List<Unit> availableUnits = listOfUnitsIdleByType.get(u.getTypeID());
		if (availableUnits == null) {
			availableUnits = new ArrayList<Unit>();
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
