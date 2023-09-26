package me.resurrectajax.nationslegacy.persistency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import me.resurrectajax.nationslegacy.enumeration.Flag;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.ranking.Rank;
import me.resurrectajax.nationslegacy.sql.Database;

public class NationMapping {
	private Integer nationID;
	private int chunkIncrement;
	private int maxChunks;
	private int gainedChunks = 0;
	private String name, description = "";
	private HashMap<String, Location> homes = new HashMap<>();
	private Set<PlayerMapping> players = new HashSet<>();
	private Set<Chunk> claimedChunks = new HashSet<Chunk>();
	private Set<Chunk> newChunks = new HashSet<Chunk>();
	private Set<Chunk> deletedChunks = new HashSet<Chunk>();
	private HashMap<Flag, Boolean> flags = new HashMap<Flag, Boolean>();
	
	private Database db;

	public NationMapping(int nationID, String name, PlayerMapping leader, int maxChunks, Database db) {
		this.db = db;
		this.maxChunks = maxChunks;
		this.chunkIncrement = maxChunks;
		this.nationID = nationID;
		this.name = name;
		addPlayerWithRank(leader, Rank.getHighest());
		Arrays.asList(Flag.values()).forEach(flag -> this.flags.put(flag, Flag.getDefault(flag).equalsIgnoreCase("ALLOW")));
	}
	
	
	
	public NationMapping(int nationID, String name, String description, int maxChunks, int chunkIncrement, int gainedChunks, Collection<PlayerMapping> players, Collection<Chunk> claimedChunks, HashMap<Flag, Boolean> flags, HashMap<String, Location> homes, Database db) {
		this.db = db;
		this.maxChunks = maxChunks;
		this.gainedChunks = gainedChunks;
		this.chunkIncrement = chunkIncrement;
		this.nationID = nationID;
		this.name = name;
		this.description = description;
		loadPlayers(players);
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
	
	public int getGainedChunks() {
		return gainedChunks;
	}
	
	public void setGainedChunks(int gain) {
		this.gainedChunks = gain;
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

	public void addAllPlayers(Collection<PlayerMapping> members, int power) {
		for(PlayerMapping player : players) {
			if(this.players.stream().anyMatch(play -> play.getUUID().equals(player.getUUID()))) continue;
		}
		for(PlayerMapping leader : players) {
			this.players.add(leader);
			leader.setRank(Rank.getRankByPower(power));
		}
	}
	
	
	public Set<PlayerMapping> getPlayers() {
		return players;
	}



	public Set<PlayerMapping> getPlayersByRank(Rank rank) {
		return players.stream().filter(el -> el.getRank().equals(rank)).collect(Collectors.toSet());
	}
	public boolean loadPlayers(Collection<PlayerMapping> players) {
		for(PlayerMapping player : players) {
			if(this.players.stream().anyMatch(play -> play.getUUID().equals(player.getUUID()))) return false;
		}
		this.players = new HashSet<>(players);
		return true;
	}
	public boolean addPlayerWithRank(PlayerMapping leader, Rank rank) {
		if(this.players.stream().filter(el -> el.getRank().equals(rank)).anyMatch(play -> play.getUUID().equals(leader.getUUID()))) return false;
		this.players.add(leader);
		leader.setRank(rank);
		leader.setNationID(this.nationID);
		leader.update();
		this.db.insertPlayerIntoNation(this.nationID, leader.getUUID());
		return true;
	}
	
	public boolean addMember(PlayerMapping member) {
		boolean isMember = this.players.stream().anyMatch(play -> play.getUUID().equals(member.getUUID()));
		
		if(isMember) return false;
		this.players.add(member);
		member.setNationID(this.nationID);
		member.setRank(Rank.getLowest());
		member.update();
		this.maxChunks += chunkIncrement;
		this.db.insertPlayerIntoNation(this.nationID, member.getUUID());
		
		Bukkit.getOnlinePlayers().forEach(el -> {
			Nations.getInstance().getMappingRepo().getScoreboardManager().updateScoreboard(el);
		});
		return true;
	}
	
	
	
	public void kickPlayer(PlayerMapping player) {
		player.setNationID(null);
		player.setRank(Rank.getNationless());
		player.update();
		this.players.removeIf(el -> el.equals(player));
		this.maxChunks -= chunkIncrement;
		this.db.removePlayerFromNation(player.getUUID());
		
		Bukkit.getOnlinePlayers().forEach(el -> {
			Nations.getInstance().getMappingRepo().getScoreboardManager().updateScoreboard(el);
		});
	}
	public boolean demotePlayer(PlayerMapping player) {
		ListIterator<me.resurrectajax.nationslegacy.ranking.Rank> rankIterator = me.resurrectajax.nationslegacy.ranking.Rank.getRankIterator();
		
		me.resurrectajax.nationslegacy.ranking.Rank newRank = null;
		do {
			me.resurrectajax.nationslegacy.ranking.Rank rank = rankIterator.next();
			if(player.getRank().toString().equals(rank.toString())) {
				if(rankIterator.hasPrevious()) newRank = rankIterator.previous();
				break;
			}
		}
		while(rankIterator.hasNext());
		
		if(!this.players.stream().anyMatch(play -> play.getUUID().equals(player.getUUID()))) return false;
		if(player.getRank().equals(Rank.getLowest())) {
			kickPlayer(player);
			return true;
		}
		player.setRank(newRank);
		player.update();
		
		return true;
	}
	public boolean promotePlayer(PlayerMapping player) {
		ListIterator<me.resurrectajax.nationslegacy.ranking.Rank> rankIterator = me.resurrectajax.nationslegacy.ranking.Rank.getRankIterator();
		
		me.resurrectajax.nationslegacy.ranking.Rank newRank = null;
		do {
			me.resurrectajax.nationslegacy.ranking.Rank rank = rankIterator.next();
			if(player.getRank().toString().equals(rank.toString())) {
				if(rankIterator.hasNext()) newRank = rankIterator.next();
				break;
			}
		}
		while(rankIterator.hasNext());
		
		if(!this.players.stream().anyMatch(play -> play.getUUID().equals(player.getUUID()))) return false;
		if(player.getRank().equals(Rank.getHighest())) return false;
		player.setRank(newRank);
		player.update();
		
		return true;
	}
	
	
	
	public Integer getNationID() {
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
		MappingRepository mappingRepo = Nations.getInstance().getMappingRepo();
		
		
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
	
	public HashMap<Flag, Boolean> getFlags() {
		return flags;
	}
	public void addFlags(HashMap<Flag, Boolean> flags) {
		this.flags.putAll(flags);
	}
	public void setFlag(Flag flag, boolean allow) {
		this.flags.put(flag, allow);
		this.db.updateNationFlag(flag, this.nationID, allow);
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

	public int getBaseChunkLimit() {
		return players.size()*chunkIncrement;
	}
	
	

	public void update() {
		db.updateNation(this);
	}

	
	/**
	 * Remove all members from the nation and remove the nation from the database
	 * */
	public void disband() {
		
		for(PlayerMapping player : players) {
			player.setNationID(null);
			player.setRank(Rank.getNationless());
		}
		removeInvites();
		
		db.deleteNation(nationID);
		this.nationID = null;
	}
	
	public int countKillPoints() {
		return players.stream().map(el -> el.getKillpoints()).reduce(0, (t, u) -> t + u);
	}
	
	private void removeInvites() {
		MappingRepository mappingRepo = Nations.getInstance().getMappingRepo();
		HashMap<UUID, Set<Integer>> invites = mappingRepo.getPlayerInvites();
		invites.keySet().stream().forEach(el -> invites.get(el).removeIf(val -> mappingRepo.getNationByID(val).getName().equalsIgnoreCase(getName())));
	}



	@Override
	public int hashCode() {
		return Objects.hash(nationID);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NationMapping other = (NationMapping) obj;
		return nationID == other.nationID;
	}
}
