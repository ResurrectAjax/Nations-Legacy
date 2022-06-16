package persistency;

import java.util.UUID;

import enumeration.Rank;

public class PlayerMapping {
	private UUID uuid;
	private int killpoints = 0;
	private Integer nationID;
	private Rank rank;
	
	public PlayerMapping(UUID uuid, int killpoints, Rank rank) {
		setKillpoints(killpoints);
		setRank(rank);
		this.uuid = uuid;
	}
	
	public int getKillpoints() {
		return killpoints;
	}

	public void setKillpoints(int killpoints) {
		this.killpoints = killpoints;
	}

	public UUID getUUID() {
		return uuid;
	}

	public Integer getNationID() {
		return nationID;
	}
	
	public void setNationID(Integer nationID) {
		this.nationID = nationID;
	}

	public Rank getRank() {
		return rank;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}
	
}
