package edu.drexel.cs680.prj1.executeorders;

import java.util.List;
import java.util.Set;

import edu.drexel.cs680.prj1.pathfinding.PathFindingUtil;
import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType.UnitTypes;

public class ExecuteOrders {

	private JNIBWAPI bwapi;
	public static ExecuteOrders instance;

	public ExecuteOrders(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
	}

	/**
	 * 1. Finds the enemy and player's centroid units. 2. Move player's centroid
	 * unit close to the enemy's centroid 3. Moves player's other units on the
	 * same path as the centroid unit
	 */
	public void moveCloseToEnemy(Set<Unit> allIdleZerglings,
			Set<Unit> allEnemyUnits) {

		Unit enemyCentroidUnit = getCentroidUnit(allEnemyUnits);
		Unit zerglingCentroidUnit = getCentroidUnit(allIdleZerglings);

		System.out.println(String.format("Move Zerg Unit# %d at %d:%d",
				zerglingCentroidUnit.getID(), zerglingCentroidUnit.getX(), zerglingCentroidUnit.getY()));

		List<Node> pathToEnemyCentroidUnit = PathFindingUtil.instance.findPath(
				zerglingCentroidUnit.getX(), zerglingCentroidUnit.getY(),
				enemyCentroidUnit.getX(), enemyCentroidUnit.getY());

		System.out.println(String.format("Move close to enemy Unit# %d at %d:%d",
				enemyCentroidUnit.getID(), enemyCentroidUnit.getX(), enemyCentroidUnit.getY()));
		moveAlongPath(zerglingCentroidUnit, pathToEnemyCentroidUnit);

		allIdleZerglings.remove(zerglingCentroidUnit);

		moveToLeaderThanPath(zerglingCentroidUnit, pathToEnemyCentroidUnit,
				allIdleZerglings);
	}

	private void moveToLeaderThanPath(Unit zerglingCentroidUnit,
			List<Node> pathToEnemyCentroidUnit, Set<Unit> allZerglings) {
		for (Unit zergling : allZerglings) {
			List<Node> pathToZerglingCentroidUnit = PathFindingUtil.instance
					.findPath(zergling.getX(), zergling.getY(),
							zerglingCentroidUnit.getX(),
							zerglingCentroidUnit.getY());
			moveAlongPath(zergling, pathToZerglingCentroidUnit);
			moveAlongPath(zergling, pathToEnemyCentroidUnit);
		}
	}

	private void moveAlongPath(Unit zerglings,
			List<Node> pathToEnemyCentroidUnit) {
		System.out.println(String.format("Unit# %d has %d steps to take", zerglings.getID(), pathToEnemyCentroidUnit.size()));
		for (Node moveTo : pathToEnemyCentroidUnit) {
			bwapi.move(zerglings.getID(), (int) moveTo.self.x,
					(int) moveTo.self.y);
		}
	}

	private Unit getCentroidUnit(Set<Unit> allEnemyUnits) {
		// TODO implement a proper centroid unit
		return allEnemyUnits.toArray(new Unit[0])[allEnemyUnits.size() / 2];
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
}
