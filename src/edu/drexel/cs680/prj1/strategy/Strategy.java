package edu.drexel.cs680.prj1.strategy;

import edu.drexel.cs680.prj1.giveorders.GiveOrders;
import edu.drexel.cs680.prj1.perception.AgentState;
import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType.UnitTypes;

public class Strategy {
	
	public static JNIBWAPI bwapi;
	
	public static void makeDecision() {
		for (Unit unit : bwapi.getMyUnits()) {
			if (unit.getTypeID() == UnitTypes.Zerg_Larva.ordinal()) {
				if (bwapi.getSelf().getMinerals() >= 50 && !AgentState.morphedDrone) {
					GiveOrders.morphDrone(unit.getID(), UnitTypes.Zerg_Drone.ordinal());
				}
			}
		}
	}

}
