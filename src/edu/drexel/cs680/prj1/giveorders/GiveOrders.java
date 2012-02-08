package edu.drexel.cs680.prj1.giveorders;

import java.util.ArrayList;
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
	
	public boolean gatheringGas;
	public boolean gatheringMinerals;
	public boolean foundGas;
	public boolean foundMinerals;

	public GiveOrders(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
		
		gatheringMinerals = false;
		gatheringGas = false;
		foundGas = false;
		foundMinerals = false;
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
		if(!foundMinerals)
		// TODO make a Zerg Drone gather minerals.  Use half of what's idle

	    System.out.println("Gathering minerals...");
	}
	
	private void gatherGas()
	{
		if(!foundGas)
			return;
		
		// TODO make a Zerg Drone gather minerals.  Use half of what's idle
		System.out.println("Gathering gas...");
	}
	
	private void buildExtractor()
	{
		// TODO make a Zerg Drone create an extractor.  Needs to find a gas source, though
		System.out.println("Attempting to create extractor...");
	}
	
	private void searchForGas()
	{
		// TODO makes a Zerg Drone (that's idle) look for an available gas chamber
		int close_gas_id = -1;
		
		if(foundGas)
			return;
		
		//Unit searchingUnit;
		//if(Perception.instance.listOfUnitsIdleByType.get(UnitTypes.Zerg_Drone).size()==0)
		//	return;
		
		// if no available units, return
		// otherwise have an available drone look for gas...
		for(Unit everyUnit: Perception.instance.listOfUnitsIdleByType.get(UnitTypes.Zerg_Drone))
		{
			close_gas_id = Perception.instance.getNearestGasChamber(everyUnit);
			if(close_gas_id==-1)
			{
				continue;
			}
			else
			{
				bwapi.rightClick(everyUnit.getID(), close_gas_id);
				System.out.println("Found Gas!");
				foundGas=true;
				
				break;
			}
				
		}
			
	}
	
	private void searchForMinerals()
	{
		// TODO makes a Zerg Drone (that's idle) look for an available MineralField
		int close_mineral_id = -1;
		
		System.out.println("SEarching.....");
		
		//if(foundMinerals)
		//	return;
		
		Unit searchingUnit;
		//if(Perception.instance.listOfUnitsIdleByType.get(UnitTypes.Zerg_Drone).size()==0)
		//	return;
		
		// if no available units, return
		// otherwise have an available drone look for gas...
		//for(Unit everyUnit: Perception.instance.listOfUnitsIdleByType.get(UnitTypes.Zerg_Drone))
		for(Unit everyUnit: bwapi.getMyUnits())
		{
			if(!(everyUnit.getTypeID()==UnitTypes.Zerg_Drone.ordinal()))
				continue;
			
			System.out.println("Checking id: " + everyUnit.getID());
			close_mineral_id = Perception.instance.getNearestMineralField(everyUnit);
			System.out.println("Closest is " + close_mineral_id);
			if(close_mineral_id==-1)
			{
				continue;
			}
			else
			{
				bwapi.rightClick(everyUnit.getID(), close_mineral_id);
				System.out.println("Found Minerals!");
				foundGas=true;
				break;
			}
				
		}
			
	}

	private void build() {
		// TODO Auto-generated method stub
		
		/**
		 * This is the build state, the AI is in this state because of the following
		 * factors that may not be present
		 */
		
		// check if there are enough resources
		if(Perception.instance.totalMinerals < 100)			// this is from the Strategy "enoughResourcesAvailable" method
		{
			if(gatheringMinerals)
				gatherMinerals();
			else
				searchForMinerals();
			
		}

		//TODO move logic to Strategy
//		if(Perception.instance.buildingExtractor < 1)
//			buildExtractor();
		
		if(Perception.instance.totalGas< 100)
		{
			if(gatheringGas)
				gatherGas();
			else
				searchForGas();
		}
		
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
