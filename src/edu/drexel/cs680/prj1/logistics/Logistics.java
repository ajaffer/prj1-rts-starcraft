package edu.drexel.cs680.prj1.logistics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;

public class Logistics {
/**
 * Used to determine whether something can be built with necessary resources
 */
	public static Logistics instance;
	public JNIBWAPI bwapi;
	public Set<Unit> idlePatrollers;
	public Set<Unit> runningPatrollers;
	
	private int currentlyAttackingSquad;
	private List<Squadron> sqauadrons;
//	private List<Squadron> patrollers;
	private Squadron currentSqaud;
	private Set<Unit> observerdUnits;
//	enum SqaudType {PATROL, ATTACK};
	
	public class Squadron {
		public static final int MIN_SIZE = 3;
		public Set<Unit> units;
//		public SqaudType sqaudType;
		
		public Squadron(Set<Unit> units){
			this.units = units;
//			this.sqaudType = sqaudType;
		}
	}
	
	public Logistics(JNIBWAPI bwapi) {
		instance = this;
		this.bwapi = bwapi;
		sqauadrons = new ArrayList<Squadron>();
//		patrollers = new ArrayList<Squadron>();
		currentSqaud = new Squadron(new HashSet<Unit>());
		observerdUnits = new HashSet<Unit>();
		idlePatrollers = new HashSet<Unit>();
		runningPatrollers = new HashSet<Unit>();
		currentlyAttackingSquad = 0;
	}
	
	public void addUnits(Set<Unit> units) {
		if (currentSqaud.units.size() < Squadron.MIN_SIZE){
			units.removeAll(observerdUnits);
			currentSqaud.units.addAll(units);
			observerdUnits.addAll(units);
		}
		
		if (currentSqaud.units.size() >= Squadron.MIN_SIZE){
			sqauadrons.add(currentSqaud);
			currentSqaud = new Squadron(new HashSet<Unit>());
			System.out.println(String.format("Added Squad, total: %d", noOfSquads()));
		}
	}
	
	public Squadron getSquad(){
		return sqauadrons.remove(0);
//		if (currentlyAttackingSquad == sqauadrons.size()) {
//			currentlyAttackingSquad = 0;
//		}
//		return sqauadrons.get(currentlyAttackingSquad++);
	}
	
	public int noOfSquads() {
		return sqauadrons.size();
	}
	
}
