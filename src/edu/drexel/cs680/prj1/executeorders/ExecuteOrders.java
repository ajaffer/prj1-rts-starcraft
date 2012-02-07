package edu.drexel.cs680.prj1.executeorders;

import java.util.List;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;

public class ExecuteOrders {

	private JNIBWAPI bwapi;
	public static ExecuteOrders instance;

	public ExecuteOrders(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
	}

	/** 
	 * 1. Finds the enemy and player's centroid units.
	 * 2. Move player's centroid unit close to the enemy's centroid
	 * 3. Moves player's other units on the same path as the centroid unit*/
	public void moveCloseToEnemy(List<Unit> allIdleZerglings,
			List<Unit> allEnemyUnits) {

		Unit enemyCentroidUnit = getCentroidUnit(allEnemyUnits);
		Unit zerglingCentroidUnit = getCentroidUnit(allIdleZerglings);

		List<Node> pathToEnemyCentroidUnit = PathFinding.instance.findPath(
				zerglingCentroidUnit.getX(), zerglingCentroidUnit.getY(),
				enemyCentroidUnit.getX(), enemyCentroidUnit.getY());

		System.out.println(String.format("Move close to enemy Unit# >>>%d<<<", enemyCentroidUnit.getID()));
		moveAlongPath(zerglingCentroidUnit, pathToEnemyCentroidUnit);

		allIdleZerglings.remove(zerglingCentroidUnit);

		moveToLeaderThanPath(zerglingCentroidUnit, pathToEnemyCentroidUnit,
				allIdleZerglings);
	}

	private void moveToLeaderThanPath(Unit zerglingCentroidUnit,
			List<Node> pathToEnemyCentroidUnit, List<Unit> allZerglings) {
		for (Unit zergling : allZerglings) {
			List<Node> pathToZerglingCentroidUnit = PathFinding.instance
					.findPath(zergling.getX(), zergling.getY(),
							zerglingCentroidUnit.getX(),
							zerglingCentroidUnit.getY());
			moveAlongPath(zergling, pathToZerglingCentroidUnit);
			moveAlongPath(zergling, pathToEnemyCentroidUnit);
		}
	}

	private void moveAlongPath(Unit zerglings,
			List<Node> pathToEnemyCentroidUnit) {
		for (Node moveTo : pathToEnemyCentroidUnit) {
			bwapi.move(zerglings.getID(), (int) moveTo.self.x,
					(int) moveTo.self.y);
		}
	}

	private Unit getCentroidUnit(List<Unit> allEnemyUnits) {
		// TODO implement a proper centroid unit
		return allEnemyUnits.get(allEnemyUnits.size() / 2);
	}
}
