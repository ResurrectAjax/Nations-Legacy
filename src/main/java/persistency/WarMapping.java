package persistency;

import sql.Database;

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
		
		int leaderPoints = (nation.getLeaders().size() * 3) + (enemy.getLeaders().size() * 3);
		int officerPoints = (nation.getOfficers().size() * 2) + (enemy.getOfficers().size() * 2);
		int memberPoints = (nation.getMembers().size() * 1) + (enemy.getMembers().size() * 1);
		goal += leaderPoints + officerPoints + memberPoints;
		goal *= 2;
		
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
