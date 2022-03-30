package Persistency;

import java.util.UUID;

import Enumeration.Rank;

public class PlayerMapping {
	private UUID uuid;
	private String name;
	private int killpoints = 0;
	private Integer nationID;
	private Rank rank;
	
	public PlayerMapping(UUID uuid, String name, int killpoints, Rank rank) {
		setKillpoints(killpoints);
		setRank(rank);
		this.uuid = uuid;
		this.name = name;
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

	public String getName() {
		return name;
	}

	public int getNationID() {
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
