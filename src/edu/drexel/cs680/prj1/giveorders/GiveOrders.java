package edu.drexel.cs680.prj1.giveorders;

import java.util.List;

import edu.drexel.cs680.prj1.executeorders.ExecuteOrders;
import edu.drexel.cs680.prj1.perception.AgentState;
import edu.drexel.cs680.prj1.perception.Perception;
import edu.drexel.cs680.prj1.strategy.Strategy;
import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType.UnitTypes;

public class GiveOrders {

	private JNIBWAPI bwapi;
	public static GiveOrders instance;

	public GiveOrders(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
	}
	
	public void sendOrders() {
		if (Strategy.instance.currentState.equals(Strategy.States.Attack)) {
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
	
	private void gatherMinerals()
	{
		// TODO make a Zerg Drone gather minerals.  Use half of what's idle
		System.out.println("Gathering minerals...");
	}
	
	private void gatherGas()
	{
		// TODO make a Zerg Drone gather minerals.  Use half of what's idle
		System.out.println("Gathering gas...");
	}
	
	private void buildExtractor()
	{
		// TODO make a Zerg Drone create an extractor.  Needs to find a gas source, though
		System.out.println("Attempting to create extractor...");
	}

	private void build() {
		// TODO Auto-generated method stub
		
		/**
		 * This is the build state, the AI is in this state because of the following
		 * factors that may not be present
		 */
		
		// check if there are enough resources
		if(Perception.instance.totalMinerals < 100)			// this is from the Strategy "enoughResourcesAvailable" method
			gatherMinerals();

		//TODO move logic to Strategy
//		if(Perception.instance.buildingExtractor < 1)
//			buildExtractor();
		
		if(Perception.instance.totalGas< 100)
			gatherGas();
		
	}

	private void defend() {
		// TODO Auto-generated method stub

	}

	private void attackEnemy() {
		List<Unit> allEnemyUnits = Perception.instance.allVisibleEnemyUnits();
		List<Unit> allIdleZerglings = Perception.instance.listOfUnitsIdleByType
				.get(UnitTypes.Zerg_Zergling);

		System.out.println(String.format("Enemy/Player:%d/%d", allEnemyUnits.size(), allIdleZerglings.size()));
		ExecuteOrders.instance.moveCloseToEnemy(allIdleZerglings, allEnemyUnits);
		attack(allIdleZerglings, allEnemyUnits);
	}

	private void attack(List<Unit> allIdleZerglings, List<Unit> allEnemyUnits) {
		for (Unit unit : allIdleZerglings) {
			for (Unit enemy : allEnemyUnits) {
				System.out.println(String.format("Attack Enemy Unit#>>>%d<<<", unit.getID()));
				bwapi.attack(unit.getID(), enemy.getX(), enemy.getY());
				break;
			}
		}
	}

}
