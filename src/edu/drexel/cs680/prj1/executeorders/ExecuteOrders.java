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

	public void moveCloseToEnemy(List<Unit> allIdleZerglings,
			List<Unit> allEnemyUnits) {

		Unit enemyCentroidUnit = getCentroidUnit(allEnemyUnits);
		Unit zerglingCentroidUnit = getCentroidUnit(allIdleZerglings);

		List<Node> pathToEnemyCentroidUnit = PathFinding.instance.findPath(
				zerglingCentroidUnit.getX(), zerglingCentroidUnit.getY(),
				enemyCentroidUnit.getX(), enemyCentroidUnit.getY());
		
		for (Unit zergling : allIdleZerglings) {
			moveAlongPath(zergling, pathToEnemyCentroidUnit);
		}

	}

	private void moveAlongPath(Unit zerglings,
			List<Node> pathToEnemyCentroidUnit) {
		for (Node moveTo : pathToEnemyCentroidUnit) {
			bwapi.move(zerglings.getID(), (int)moveTo.self.x, (int)moveTo.self.y);
		}
	}

	private Unit getCentroidUnit(List<Unit> allEnemyUnits) {
		// TODO implement a proper centroid unit
		return allEnemyUnits.get(allEnemyUnits.size() / 2);
	}
}
