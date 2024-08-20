package me.resurrectajax.nationslegacy.persistency;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.nationslegacy.chunkgain.ChunkGainManager;
import me.resurrectajax.nationslegacy.ranking.Rank;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.scoreboard.ScoreboardManager;
import me.resurrectajax.nationslegacy.sql.Database;

public class MappingRepository implements me.resurrectajax.ajaxplugin.interfaces.MappingRepository{
	private Set<AllianceMapping> alliances = new HashSet<AllianceMapping>();
	private Set<NationMapping> nations = new HashSet<NationMapping>();
	private Set<PlayerMapping> players = new HashSet<PlayerMapping>();
	private Set<WarMapping> wars = new HashSet<WarMapping>();
	
	private Set<UUID> isClaiming = new HashSet<UUID>();
	private Set<UUID> isUnclaiming = new HashSet<UUID>();
	
	private HashMap<UUID, Set<Integer>> playerInvites = new HashMap<UUID, Set<Integer>>();
	
	private ChunkGainManager chunkManager;
	private ScoreboardManager scoreboardManager;
	private Database db;
	private Nations main;
	
	public MappingRepository(Nations main) {
		this.main = main;
		load();
	}
	
	public Database getDatabase() {return db;}
	
	
	
	public Set<AllianceMapping> getAlliances() {
		return alliances;
	}
	public Set<AllianceMapping> getAlliancesByNationID(int ID) {
		Set<AllianceMapping> alliances = new HashSet<AllianceMapping>();
		for(AllianceMapping alliance : this.alliances) {
			if(alliance.getNationID() == ID || alliance.getAllyID() == ID) alliances.add(alliance);
		}
		return alliances;
	}
	public Set<AllianceMapping> getAlliancesByNationName(String name) {
		NationMapping nation = getNationByName(name);
		
		Set<AllianceMapping> alliances = new HashSet<AllianceMapping>();
		for(AllianceMapping alliance : this.alliances) {
			if(alliance.getNationID() == nation.getNationID() || alliance.getAllyID() == nation.getNationID()) alliances.add(alliance);
		}
		return alliances;
	}
	public AllianceMapping getAllianceByNationIDs(Integer nationID, Integer allyID) {
		if(nationID == null || allyID == null) return null;
		for(AllianceMapping alliance : this.alliances) {
			if((alliance.getNationID() == nationID && alliance.getAllyID() == allyID) || (alliance.getAllyID() == nationID && alliance.getNationID() == allyID)) return alliance;
		}
		return null;
	}
	public void createAlliance(int nationID, int allyID) {
		if((this.alliances.stream().anyMatch(al -> al.getNationID() == nationID) && this.alliances.stream().anyMatch(al -> al.getAllyID() == allyID)) || 
				(this.alliances.stream().anyMatch(al -> al.getNationID() == allyID) && this.alliances.stream().anyMatch(al -> al.getAllyID() == nationID))) return;
		
		this.alliances.add(db.insertAlliance(nationID, allyID));
		
		Bukkit.getOnlinePlayers().forEach(el -> {
			getScoreboardManager().updateScoreboard(el);
		});
	}
	public void removeAlliance(int nationID, int allyID) {
		this.alliances.remove(getAllianceByNationIDs(nationID, allyID));
		this.db.deleteAlliance(nationID, allyID);
		
		Bukkit.getOnlinePlayers().forEach(el -> {
			getScoreboardManager().updateScoreboard(el);
		});
	}
	public Set<NationMapping> getAllianceNationsByNationID(int nationID) {
		return getAlliancesByNationID(nationID).stream()
				.map(el -> el.getNationID() != nationID ? getNationByID(el.getNationID()) : getNationByID(el.getAllyID()))
				.collect(Collectors.toSet());
	}

	

	public Set<NationMapping> getNations() {
		return nations;
	}
	public NationMapping getNationByName(String name) {
		for(NationMapping nation : nations) {
			if(nation.getName().equalsIgnoreCase(name)) return nation;
		}
		return null;
	}
	public NationMapping getNationByID(Integer ID) {
		if(ID == null) return null;
		for(NationMapping nation : nations) {
			if(nation.getNationID() == ID) return nation;
		}
		return null;
	}
	/**
	 * Add a nation to the repository and database
	 * @param name - {@link String} nation name
	 * @param leader - {@link PlayerMapping} leader of the nation
	 * @param maxChunks - {@link Integer} maximum amount of chunks
	 * */
	public NationMapping createNation(String name, PlayerMapping leader) {
		FileConfiguration config = main.getConfig();
		if(getNationByName(name) != null || !leader.getRank().equals(Rank.getNationless())) return null;
		NationMapping nation = db.insertNation(name, leader, config.getInt("Nations.Claiming.MaxChunks"));
		this.nations.add(nation);
		
		Bukkit.getOnlinePlayers().forEach(el -> {
			getScoreboardManager().updateScoreboard(el);
		});
		return nation;
	}
	/**
	 * Disband a nation
	 * */
	public void disbandNation(NationMapping nation) {
		if(!this.nations.stream().anyMatch(nat -> nat.getNationID() == nation.getNationID())) return;
		this.alliances.removeIf(el -> el.getAllyID() == nation.getNationID() || el.getNationID() == nation.getNationID());
		this.wars.removeIf(el -> el.getEnemy().getNationID() == nation.getNationID() || el.getNation().getNationID() == nation.getNationID());
		this.nations.remove(nation);
		nation.disband();
		
		Bukkit.getOnlinePlayers().forEach(el -> {
			getScoreboardManager().updateScoreboard(el);
		});
	}



	public Set<PlayerMapping> getPlayers() {
		return players;
	}
	public PlayerMapping getPlayerByUUID(UUID uuid) {
		for(PlayerMapping player : players) {
			if(player.getUUID().equals(uuid)) return player;
		}
		return null;
	}
	public PlayerMapping getPlayerByName(String name) {
		for(PlayerMapping player : players) {
			OfflinePlayer offline = Bukkit.getOfflinePlayer(player.getUUID());
			if(offline == null || offline.getName() == null) return null;
			if(offline.getName().equalsIgnoreCase(name)) return player;
		}
		return null;
	}
	public NationMapping getNationByPlayer(PlayerMapping player) {
		if(player == null || player.getNationID() == null) return null;
		return getNationByID(player.getNationID());
	}
	public Set<PlayerMapping> getPlayersByNationID(int nationID) {
		Set<PlayerMapping> players = new HashSet<PlayerMapping>();
		for(PlayerMapping player : players) {
			if(player.getNationID() == nationID) players.add(player);
		}
		return players;
	}
	/**
	 * Add a player to the repository and database
	 * @param player - {@link Player} to add
	 * */
	public void addPlayer(Player player) {
		if(players.stream().anyMatch(play -> play.getUUID().equals(player.getUniqueId()))) return;
		PlayerMapping playermap = db.insertPlayer(player.getUniqueId(), 0, Rank.getNationless());
		this.players.add(playermap);
	}

	

	public Set<WarMapping> getWars() {
		return wars;
	}
	public Set<WarMapping> getWarsByNationID(int ID) {
		Set<WarMapping> wars = new HashSet<WarMapping>();
		for(WarMapping war : this.wars) {
			if(war.getNation().getNationID() == ID || war.getEnemy().getNationID() == ID) wars.add(war);
		}
		return wars;
	}
	public Set<WarMapping> getWarsByNationName(String name) {
		NationMapping nation = getNationByName(name);
		
		Set<WarMapping> wars = new HashSet<WarMapping>();
		for(WarMapping war : this.wars) {
			if(war.getNation().getNationID() == nation.getNationID() || war.getEnemy().getNationID() == nation.getNationID()) wars.add(war);
		}
		return wars;
	}
	public WarMapping getWarByNationIDs(Integer nationID, Integer warID) {
		if(nationID == null || warID == null) return null;
		for(WarMapping war : this.wars) {
			if((war.getNation().getNationID() == nationID && war.getEnemy().getNationID() == warID) || (war.getNation().getNationID() == warID && war.getEnemy().getNationID() == nationID)) return war;
		}
		return null;
	}
	public void startWar(int nationID, int enemyID) {
		if((this.wars.stream().anyMatch(wa -> wa.getNation().getNationID() == nationID) && this.wars.stream().anyMatch(wa -> wa.getEnemy().getNationID() == enemyID)) || 
				(this.wars.stream().anyMatch(wa -> wa.getNation().getNationID() == enemyID) && this.wars.stream().anyMatch(wa -> wa.getEnemy().getNationID() == nationID))) return;
		
		this.wars.add(db.insertWar(nationID, enemyID));
		
		Bukkit.getOnlinePlayers().forEach(el -> {
			getScoreboardManager().updateScoreboard(el);
		});
	}
	public void removeWar(int nationID, int enemyID) {
		this.wars.remove(getWarByNationIDs(nationID, enemyID));
		this.db.deleteWar(nationID, enemyID);
		
		Bukkit.getOnlinePlayers().forEach(el -> {
			getScoreboardManager().updateScoreboard(el);
		});
	}
	public Set<NationMapping> getWarNationsByNationID(int nationID) {
		return getWarsByNationID(nationID).stream()
				.map(el -> el.getNation().getNationID() != nationID ? el.getNation() : el.getEnemy())
				.collect(Collectors.toSet());
	}
	public void updateNationWars(int nationID) {
		this.db.updateWars(getWarsByNationID(nationID));
	}
	
	
	public NationMapping getNationByChunk(Chunk chunk) {
		NationMapping nation = nations.stream().filter(el -> el.getClaimedChunks().stream().anyMatch(ch -> chunk.getX() == ch.getX() && chunk.getZ() == ch.getZ())).findFirst().orElse(null);
		return nation;
	}
	
	public Set<UUID> getClaimingSet() {
		return isClaiming;
	}

	public void setIsClaiming(UUID player) {
		this.isClaiming.add(player);
	}

	public Set<UUID> getUnclaimingSet() {
		return isUnclaiming;
	}

	public void setIsUnclaiming(UUID player) {
		this.isUnclaiming.add(player);
	}

	public HashMap<UUID, Set<Integer>> getPlayerInvites() {
		return playerInvites;
	}

	public void addPlayerInvite(Integer nationID, UUID receiver) {
		Set<Integer> invites = new HashSet<Integer>();
		if(this.playerInvites.containsKey(receiver)) invites.addAll(this.playerInvites.get(receiver));
		invites.add(nationID);
		
		this.playerInvites.put(receiver, invites);
	}
	public void removePlayerInvite(Integer nationID, UUID receiver) {
		if(!this.playerInvites.containsKey(receiver)) return;
		this.playerInvites.get(receiver).remove(nationID);
	}

	
	public ChunkGainManager getChunkGainManager() {
		return this.chunkManager;
	}


	public ScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}

	
	public void load() {
		this.chunkManager = new ChunkGainManager(main);
		this.scoreboardManager = new ScoreboardManager(main);
		
		//load database
		this.db = new Database(main, this);
		this.db.load();
		//database
		
		players.addAll(db.getAllPlayers());
		nations.addAll(db.getAllNations());
		alliances.addAll(db.getAllAlliances());
		wars.addAll(db.getAllWars());
	}
}
