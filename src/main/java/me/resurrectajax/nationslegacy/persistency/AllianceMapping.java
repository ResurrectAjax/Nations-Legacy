package me.resurrectajax.nationslegacy.persistency;

public class AllianceMapping {
	private int nationID;
	private int allyID;
	
	public AllianceMapping(int nationID, int allyID) {
		this.nationID = nationID;
		this.allyID = allyID;
	}
	
	public int getNationID() {
		return nationID;
	}
	public int getAllyID() {
		return allyID;
	}
}
