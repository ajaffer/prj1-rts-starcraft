package edu.drexel.cs680.prj1.giveorders;

import edu.drexel.cs680.prj1.perception.AgentState;
import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType.UnitTypes;

public class GiveOrders {

	public static JNIBWAPI bwapi;

	public static void morphDrone(int id, int ordinal) {
		bwapi.morph(id, ordinal);
		AgentState.morphedDrone = true;
	}

}
