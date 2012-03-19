package edu.drexel.cs680.prj1.executeorders;

import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.drexel.cs680.prj1.logistics.Logistics;
import edu.drexel.cs680.prj1.logistics.Logistics.Squadron;
import edu.drexel.cs680.prj1.pathfinding.PathFindingUtil;
import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType.UnitTypes;

public class ExecuteOrders {

	private JNIBWAPI bwapi;
	private Random r;
	public static ExecuteOrders instance;

	
	
	public ExecuteOrders(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
		r = new Random();
	}

	/**
	 * 1. Finds the enemy and player's centroid units. 2. Move player's centroid
	 * unit close to the enemy's centroid 3. Moves player's other units on the
	 * same path as the centroid unit
	 */
	public void moveCloseToEnemy(Logistics.Squadron squad,
			Set<Unit> allEnemyUnits) {

		Unit enemyUnit = getRandomUnit(allEnemyUnits);
		Unit zerglingUnit = getRandomUnit(squad.units);

		System.out.println(String.format("Move Zerg Unit# %d at %d:%d",
				zerglingUnit.getID(), zerglingUnit.getTileX(), zerglingUnit.getTileY()));
		
		List<Node> pathToEnemyUnit = null;
//		int count = 0;
		
//		while (pathToEnemyUnit == null && count++ < 50) {
		try {
			pathToEnemyUnit = PathFindingUtil.instance.findPath(
					zerglingUnit.getTileX(), zerglingUnit.getTileY(),
					enemyUnit.getTileX(), enemyUnit.getTileY(), PathFindingUtil.ALGO.LRTASTAR, squad);
		} catch (IllegalStateException e) {
			System.out.println("Path wasn't found.");
		}
			
//			enemyUnit = getRandomUnit(allEnemyUnits);
//			zerglingUnit = getRandomUnit(allIdleZerglings);
//		}

		if (pathToEnemyUnit == null) {
			System.out.println("path returned by Path Finding Algo is NULL");
			return;
		}
		
		System.out.println(String.format("Move close to enemy Unit# %d, type: %d at %d:%d",
				enemyUnit.getID(), enemyUnit.getTypeID(),  enemyUnit.getTileX(), enemyUnit.getTileY()));
//		moveAlongPath(zerglingUnit, pathToEnemyUnit);
//		allIdleZerglings.remove(zerglingUnit);
		
		for (Unit zergling : squad.units) {
			moveAlongPath(zergling, pathToEnemyUnit);
		}
	}

	public void moveAlongPath(Unit zerglings,
			List<Node> pathToEnemyCentroidUnit) {
		System.out.println(String.format("Unit# %d has %d steps to take", zerglings.getID(), pathToEnemyCentroidUnit.size()));
		for (Node moveTo : pathToEnemyCentroidUnit) {			
			bwapi.move(zerglings.getID(), (int) moveTo.x,
					(int) moveTo.y);
		}
	}

	private Unit getRandomUnit(Set<Unit> allUnits) {
		return allUnits.toArray(new Unit[0])[r.nextInt(allUnits.size())];
	}
	
	public void morphToDrone(Unit larva) {
		bwapi.morph(larva.getID(), UnitTypes.Zerg_Drone.ordinal());
	}

	public void buildSpawinPool(Integer poolDrone, Unit overlord) {
		bwapi.build(poolDrone, overlord.getTileX(), overlord.getTileY(), UnitTypes.Zerg_Spawning_Pool.ordinal());		
	}

	public void morphToOverlord(Unit larva) {
		bwapi.morph(larva.getID(), UnitTypes.Zerg_Overlord.ordinal());		
	}
	
	public void patrolTile(int unitID,int tileX,int tileY)
	{
		bwapi.patrol(unitID, tileX, tileY);
	}

	public void moveSquad(Squadron squad, Node loc) {
		for (Unit n : squad.units) {			
			bwapi.move(n.getID(), loc.x, loc.y);
		}		
	}
}
