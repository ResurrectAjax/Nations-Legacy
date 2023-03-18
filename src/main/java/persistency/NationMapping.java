package persistency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Chunk;
import org.bukkit.Location;

import enumeration.Flag;
import enumeration.Rank;
import main.Main;
import sql.Database;

public class NationMapping {
	private int nationID;
	private int maxChunks;
	private String name, description = "";
	private HashMap<String, Location> homes = new HashMap<>();
	private Set<PlayerMapping> leaders = new HashSet<PlayerMapping>();
	private Set<PlayerMapping> officers = new HashSet<PlayerMapping>();
	private Set<PlayerMapping> members = new HashSet<PlayerMapping>();
	private Set<Chunk> claimedChunks = new HashSet<Chunk>();
	private Set<Chunk> newChunks = new HashSet<Chunk>();
	private Set<Chunk> deletedChunks = new HashSet<Chunk>();
	private List<Flag> flags = new ArrayList<Flag>();
	
	private Database db;

	public NationMapping(int nationID, String name, PlayerMapping leader, int maxChunks, Database db) {
		this.db = db;
		this.maxChunks = maxChunks;
		this.nationID = nationID;
		setName(name);
		addLeader(leader);
	}
	
	
	
	public NationMapping(int nationID, String name, String description, int maxChunks, Collection<PlayerMapping> players, Collection<Chunk> claimedChunks, Collection<Flag> flags, HashMap<String, Location> homes, Database db) {
		this.db = db;
		this.maxChunks = maxChunks;
		this.nationID = nationID;
		this.name = name;
		this.description = description;
		addLeaders(players.stream().filter(el -> el.getRank().equals(Rank.Leader)).collect(Collectors.toSet()));
		addOfficers(players.stream().filter(el -> el.getRank().equals(Rank.Officer)).collect(Collectors.toSet()));
		addMembers(players.stream().filter(el -> el.getRank().equals(Rank.Member)).collect(Collectors.toSet()));
		addClaimedChunks(claimedChunks);
		addFlags(flags);
		this.homes.putAll(homes);
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
		this.update();
	}
	
	
	
	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
		this.update();
	}



	public Set<PlayerMapping> getLeaders() {
		return leaders;
	}
	public boolean addLeaders(Collection<PlayerMapping> leaders) {
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
		leader.setNationID(this.nationID);
		leader.update();
		return true;
	}
	
	
	
	public Set<PlayerMapping> getOfficers() {
		return officers;
	}
	public boolean addOfficers(Collection<PlayerMapping> officers) {
		for(PlayerMapping player : officers) {
			if(this.officers.stream().anyMatch(play -> play.getUUID().equals(player.getUUID()))) return false;
		}
		for(PlayerMapping officer : officers) {
			this.officers.add(officer);
			officer.setRank(Rank.Officer);
		}
		return true;
	}
	public boolean addOfficer(PlayerMapping officer) {
		if(this.leaders.stream().anyMatch(play -> play.getUUID().equals(officer.getUUID()))) return false;
		this.leaders.add(officer);
		officer.setRank(Rank.Officer);
		officer.setNationID(this.nationID);
		officer.update();
		return true;
	}
	private boolean demoteOfficer(PlayerMapping officer) {
		if(!this.officers.stream().anyMatch(play -> play.getUUID().equals(play.getUUID()))) return false;
		this.officers.remove(officer);
		this.members.add(officer);
		officer.setRank(Rank.Member);
		officer.update();
		return true;
	}
	private boolean promoteOfficer(PlayerMapping officer) {
		if(!this.officers.stream().anyMatch(play -> play.getUUID().equals(officer.getUUID()))) return false;
		this.officers.remove(officer);
		this.leaders.add(officer);
		officer.setRank(Rank.Leader);
		officer.update();
		return true;
	}
	
	
	public Set<PlayerMapping> getMembers() {
		return members;
	}
	public boolean addMembers(Collection<PlayerMapping> members) {
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
		member.setNationID(this.nationID);
		member.setRank(Rank.Member);
		member.update();
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
		member.update();
		return true;
	}
	
	public Set<PlayerMapping> getAllMembers() {
		Set<PlayerMapping> members = new HashSet<PlayerMapping>();
		members.addAll(this.members);
		members.addAll(this.officers);
		members.addAll(this.leaders);
		return members;
	}
	
	
	
	public boolean kickPlayer(PlayerMapping player) {
		player.setNationID(null);
		player.setRank(Rank.Nationless);
		player.update();
		db.removePlayerFromNation(player.getUUID());
		return true;
	}
	public Object[] demotePlayer(PlayerMapping player) {
		if(demoteOfficer(player) || demoteMember(player)) return new Object[] {true, player.getRank()};
		return new Object[] {false, player.getRank().toString()};
	}
	public Object[] promotePlayer(PlayerMapping player) {
		if(promoteMember(player) || promoteOfficer(player)) return new Object[] {true, player.getRank()};
		return new Object[] {false, player.getRank()};
	}
	
	
	
	public int getNationID() {
		return nationID;
	}
	
	
	
	public Set<Chunk> getClaimedChunks() {
		return claimedChunks;
	}
	public boolean addClaimedChunks(Collection<Chunk> claimedChunks) {
		for(Chunk chunk : claimedChunks) {
			if(this.claimedChunks.contains(chunk)) return false;
		}
		this.claimedChunks.addAll(claimedChunks);
		return true;
	}
	public boolean addClaimedChunk(Chunk chunk) {
		MappingRepository mappingRepo = Main.getInstance().getMappingRepo();
		
		
		if(mappingRepo.getNations().stream().map(el -> el.getClaimedChunks()).flatMap(Collection::stream).collect(Collectors.toSet()).contains(chunk)) return false;
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
	public boolean unclaimAll() {
		if(claimedChunks.isEmpty()) return false;
		claimedChunks.clear();
		db.deleteAllClaimedChunks(nationID);
		return true;
	}
	public void saveChunks() {
		if(!deletedChunks.isEmpty()) {
			db.deleteClaimedChunks(new ArrayList<Chunk>(deletedChunks), nationID);
			deletedChunks.clear();
		}
		if(!newChunks.isEmpty()) {
			db.addClaimedChunks(new ArrayList<Chunk>(newChunks), nationID);
			newChunks.clear();
		}
	}
	
	public List<Flag> getFlags() {
		return flags;
	}
	public boolean addFlags(Collection<Flag> flags) {
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
	
	public void setHome(Location home) {
		homes.put("home", home);
		db.insertHome(nationID, "home", home);
	}
	public void setHome(String name, Location home) {
		homes.put(name, home);
		db.insertHome(nationID, name, home);
	}
	
	public void deleteHome(String name) {
		homes.remove(name);
		db.deleteHome(nationID, name);
	}
	
	public HashMap<String, Location> getHomes() {
		return homes;
	}



	public void update() {
		db.updateNation(this);
	}

	
	/**
	 * Remove all members from the nation and remove the nation from the database
	 * */
	public void disband() {
		List<PlayerMapping> players = new ArrayList<PlayerMapping>();
		players.addAll(leaders);
		players.addAll(officers);
		players.addAll(members);
		
		for(PlayerMapping player : players) {
			player.setNationID(null);
			player.setRank(Rank.Nationless);
		}
		removeInvites();
		
		db.deleteNation(nationID);
	}
	
	public int countKillPoints() {
		List<PlayerMapping> players = new ArrayList<>();
		players.addAll(members);
		players.addAll(officers);
		players.addAll(leaders);
		
		return players.stream().map(el -> el.getKillpoints()).reduce(0, (t, u) -> t + u);
	}
	
	private void removeInvites() {
		MappingRepository mappingRepo = Main.getInstance().getMappingRepo();
		HashMap<UUID, Set<Integer>> invites = mappingRepo.getPlayerInvites();
		invites.keySet().stream().forEach(el -> invites.get(el).removeIf(val -> mappingRepo.getNationByID(val).getName().equalsIgnoreCase(getName())));
	}
}
