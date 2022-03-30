package Persistency;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;

import Enumeration.Flag;
import Enumeration.Rank;
import Main.Main;
import SQL.Database;

public class NationMapping {
	private int nationID;
	private int maxChunks;
	private String name;
	private List<PlayerMapping> leaders = new ArrayList<PlayerMapping>();
	private List<PlayerMapping> officers = new ArrayList<PlayerMapping>();
	private List<PlayerMapping> members = new ArrayList<PlayerMapping>();
	private List<Chunk> claimedChunks = new ArrayList<Chunk>();
	private List<Chunk> newChunks = new ArrayList<Chunk>();
	private List<Chunk> deletedChunks = new ArrayList<Chunk>();
	private List<Flag> flags = new ArrayList<Flag>();
	
	private Database db;

	public NationMapping(int nationID, String name, PlayerMapping leader, int maxChunks, Database db) {
		this.maxChunks = maxChunks;
		this.nationID = nationID;
		setName(name);
		addLeader(leader);
		this.db = db;
	}
	
	
	
	public NationMapping(int nationID, String name, int maxChunks, List<PlayerMapping> leaders, List<PlayerMapping> officers, List<PlayerMapping> members, List<Chunk> claimedChunks, List<Flag> flags, Database db) {
		this.maxChunks = maxChunks;
		this.nationID = nationID;
		setName(name);
		addLeaders(leaders);
		addOfficers(officers);
		addMembers(members);
		addClaimedChunks(claimedChunks);
		addFlags(flags);
		this.db = db;
	}
	
	
	
	public int getMaxChunks() {
		return maxChunks;
	}



	public void setMaxChunks(int maxChunks) {
		this.maxChunks = maxChunks;
	}



	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	public List<PlayerMapping> getLeaders() {
		return leaders;
	}
	public boolean addLeaders(List<PlayerMapping> leaders) {
		for(PlayerMapping player : leaders) {
			if(this.leaders.stream().anyMatch(play -> play.getUUID().equals(player.getUUID()))) return false;
		}
		for(PlayerMapping leader : leaders) {
			this.leaders.add(leader);
			leader.setRank(Rank.Leader);
		}
		return true;
	}
	public boolean addLeader(PlayerMapping leader) {
		if(this.leaders.stream().anyMatch(play -> play.getUUID().equals(leader.getUUID()))) return false;
		this.leaders.add(leader);
		leader.setRank(Rank.Leader);
		db.updatePlayer(leader);
		return true;
	}
	
	
	
	public List<PlayerMapping> getOfficers() {
		return officers;
	}
	public boolean addOfficers(List<PlayerMapping> officers) {
		for(PlayerMapping player : officers) {
			if(this.officers.stream().anyMatch(play -> play.getUUID().equals(player.getUUID()))) return false;
		}
		for(PlayerMapping officer : officers) {
			this.officers.add(officer);
			officer.setRank(Rank.Officer);
		}
		return true;
	}
	private boolean demoteOfficer(PlayerMapping officer) {
		if(!this.officers.stream().anyMatch(play -> play.getUUID().equals(play.getUUID()))) return false;
		this.officers.remove(officer);
		this.members.add(officer);
		officer.setRank(Rank.Member);
		return true;
	}
	private boolean promoteOfficer(PlayerMapping officer) {
		if(!this.members.stream().anyMatch(play -> play.getUUID().equals(officer.getUUID()))) return false;
		this.members.remove(officer);
		this.officers.add(officer);
		officer.setRank(Rank.Officer);
		return true;
	}
	
	
	public List<PlayerMapping> getMembers() {
		return members;
	}
	public boolean addMembers(List<PlayerMapping> members) {
		for(PlayerMapping player : members) {
			if(this.members.stream().anyMatch(play -> play.getUUID().equals(player.getUUID()))) return false;
		}
		for(PlayerMapping member : members) {
			this.members.add(member);
			member.setRank(Rank.Member);
		}
		return true;
	}
	public boolean addMember(PlayerMapping member) {
		boolean isMember = this.members.stream().anyMatch(play -> play.getUUID().equals(member.getUUID())) || 
				this.officers.stream().anyMatch(play -> play.getUUID().equals(member.getUUID())) ||
				this.leaders.stream().anyMatch(play -> play.getUUID().equals(member.getUUID()));
		
		if(isMember) return false;
		this.members.add(member);
		member.setRank(Rank.Member);
		return true;
	}
	private boolean demoteMember(PlayerMapping member) {
		if(!this.members.stream().anyMatch(play -> play.getUUID().equals(member.getUUID()))) return false;
		kickPlayer(member);
		return true;
	}
	private boolean promoteMember(PlayerMapping member) {
		if(!this.members.stream().anyMatch(play -> play.getUUID().equals(member.getUUID()))) return false;
		this.members.remove(member);
		this.officers.add(member);
		member.setRank(Rank.Officer);
		return true;
	}
	
	
	
	public boolean kickPlayer(PlayerMapping player) {
		if(this.leaders.stream().anyMatch(play -> play.getUUID().equals(player.getUUID()))) return false;
		player.setNationID(null);
		player.setRank(Rank.Nationless);
		db.updatePlayer(player);
		return true;
	}
	public String[] demotePlayer(PlayerMapping player) {
		if(demoteOfficer(player) || demoteMember(player)) {
			db.updatePlayer(player);
			return new String[] {"true", player.getRank().toString()};
		}
		
		return new String[] {"false", player.getRank().toString()};
	}
	public String[] promotePlayer(PlayerMapping player) {
		if(promoteMember(player) || promoteOfficer(player)) {
			db.updatePlayer(player);
			return new String[] {"true", player.getRank().toString()};
		}
		
		return new String[] {"false", player.getRank().toString()};
	}
	
	
	
	public int getNationID() {
		return nationID;
	}
	
	
	
	public List<Chunk> getClaimedChunks() {
		return claimedChunks;
	}
	public boolean addClaimedChunks(List<Chunk> claimedChunks) {
		for(Chunk chunk : claimedChunks) {
			if(this.claimedChunks.contains(chunk)) return false;
		}
		this.claimedChunks.addAll(claimedChunks);
		return true;
	}
	public boolean addClaimedChunk(Chunk chunk) {
		MappingRepository mappingRepo = Main.getInstance().getMappingRepo();
		
		if(mappingRepo.getAllClaimedChunks().contains(chunk)) return false;
		this.claimedChunks.add(chunk);
		this.newChunks.add(chunk);
		return true;
	}
	public boolean unclaimChunk(Chunk chunk) {
		if(!claimedChunks.contains(chunk)) return false;
		claimedChunks.remove(chunk);
		this.deletedChunks.add(chunk);
		return true;
	}
	public void saveChunks() {
		if(!deletedChunks.isEmpty()) db.deleteClaimedChunks(deletedChunks, nationID);
		if(!newChunks.isEmpty()) db.addClaimedChunks(newChunks, nationID);
	}
	
	public List<Flag> getFlags() {
		return flags;
	}
	public boolean addFlags(List<Flag> flags) {
		for(Flag flag : this.flags) {
			if(flags.contains(flag)) return false;
		}
		this.flags.addAll(flags);
		return true;
	}
	public boolean addFlag(Flag flag) {
		if(this.flags.contains(flag)) return false;
		this.flags.add(flag);
		return true;
	}

	
	
	public void disband() {
		List<PlayerMapping> players = new ArrayList<PlayerMapping>();
		players.addAll(leaders);
		players.addAll(officers);
		players.addAll(members);
		
		for(PlayerMapping player : players) {
			player.setNationID(null);
			player.setRank(Rank.Nationless);
		}
		db.deleteNation(nationID);
	}
}
