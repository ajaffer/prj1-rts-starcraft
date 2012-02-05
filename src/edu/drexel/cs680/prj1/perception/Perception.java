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

	public Map<Integer, Integer> unitsAvailableByType;
	public Map<Integer, List<Unit>> listOfUnitsIdleByType;
	public Map<Unit, Integer> lastCommandByUnit;

	public Perception(JNIBWAPI bwapi) {
		this.bwapi = bwapi;
		unitsAvailableByType = new HashMap<Integer, Integer>();
		listOfUnitsIdleByType = new HashMap<Integer, List<Unit>>();
		lastCommandByUnit = new HashMap<Unit, Integer>();
	}

	public void collectData() {
		updateAvailableUnitsByType();
		updateListOfIdleUnitsByType();
		updateLastCommandsByUnit();
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

	private void updateAvailableUnitsByType() {
		for (Unit u : bwapi.getMyUnits()) {
			incrementUnitType(u.getTypeID());
		}
	}

	private void incrementUnitType(int typeID) {
		Integer count = unitsAvailableByType.get(typeID);
		if (count == null) {
			unitsAvailableByType.put(typeID, 1);
		} else {
			unitsAvailableByType.put(typeID, count + 1);
		}
	}

	public static void main(String[] args) {
		Perception p = new Perception(null);
		p.unitsAvailableByType.put(UnitTypes.Zerg_Drone.ordinal(), 2);
		System.out
				.println(p.unitsAvailableByType.get(UnitTypes.Zerg_Drone.ordinal()));
	}

}
