package edu.drexel.cs680.prj1.giveorders;

import edu.drexel.cs680.prj1.perception.AgentState;
import edu.drexel.cs680.prj1.strategy.Strategy;
import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType.UnitTypes;

public class GiveOrders {

	private JNIBWAPI bwapi;
	private static GiveOrders instance;
	
	public GiveOrders(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
	}

	//TODO remove this
	public static void morphDrone(int id, int ordinal) {
		instance.bwapi.morph(id, ordinal);
		AgentState.morphedDrone = true;
	}

	public void sendOrders() {
		if (Strategy.instance.currentState == Strategy.States.Attack) {
			attackEnemy();
		} else if (Strategy.instance.currentState == Strategy.States.Defend) {
			defend();
		} else if (Strategy.instance.currentState == Strategy.States.Build) {
			build();
		} else if (Strategy.instance.currentState == Strategy.States.Explore) {
			explore();
		}
	}

	private void explore() {
		// TODO Auto-generated method stub
		
	}

	private void build() {
		// TODO Auto-generated method stub
		
	}

	private void defend() {
		// TODO Auto-generated method stub
		
	}

	private void attackEnemy() {
		// TODO Auto-generated method stub
		
	}

}
