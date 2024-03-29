package me.resurrectajax.nationslegacy.persistency;

import java.util.Objects;
import java.util.UUID;

import me.resurrectajax.nationslegacy.ranking.Rank;
import me.resurrectajax.nationslegacy.sql.Database;

public class PlayerMapping {
	private UUID uuid;
	private int killpoints = 0;
	private Integer nationID;
	private Rank rank;
	private Database db;
	
	public PlayerMapping(UUID uuid, int killpoints, Rank rank, Database db) {
		setKillpoints(killpoints);
		setRank(rank);
		this.uuid = uuid;
		this.db = db;
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
	
	public void update() {
		db.updatePlayer(this);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(uuid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlayerMapping other = (PlayerMapping) obj;
		return Objects.equals(uuid, other.uuid);
	}
	
}
