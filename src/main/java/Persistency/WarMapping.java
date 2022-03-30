package Persistency;

import SQL.Database;

public class WarMapping {
	private NationMapping nation, enemy;
	private int nationKillpoints = 0;
	private int enemyKillpoints = 0;
	private int killpointGoal = 0;
	private Database db;
	
	public WarMapping(NationMapping nation, NationMapping enemy, Database db) {
		this.db = db;
		this.nation = nation;
		this.enemy = enemy;
		
		this.killpointGoal = calculateGoal(nation, enemy);
	}
	
	public WarMapping(NationMapping nation, NationMapping enemy, int nationKillpoints, int enemyKillpoints, Database db) {
		this.db = db;
		this.nation = nation;
		this.enemy = enemy;
		
		setNationKillpoints(enemyKillpoints);
		setEnemyKillpoints(enemyKillpoints);
		
		this.killpointGoal = calculateGoal(nation, enemy);
	}
	
	private int calculateGoal(NationMapping nation, NationMapping enemy) {
		int goal = 0;
		
		goal += nation.getLeaders().size();
		goal += nation.getOfficers().size();
		goal += nation.getMembers().size();
		
		goal += enemy.getLeaders().size();
		goal += enemy.getOfficers().size();
		goal += enemy.getMembers().size();
		
		return goal;
	}
	
	public int getNationKillpoints() {
		return nationKillpoints;
	}
	public void setNationKillpoints(int killpoints) {
		this.nationKillpoints = killpoints;
		db.updateWar(this);
		
	}
	public int getEnemyKillpoints() {
		return enemyKillpoints;
	}
	public void setEnemyKillpoints(int killpoints) {
		this.enemyKillpoints = killpoints;
		db.updateWar(this);
	}
	public NationMapping getNation() {
		return nation;
	}
	public NationMapping getEnemy() {
		return enemy;
	}
	public int getKillpointGoal() {
		return killpointGoal;
	}
}
