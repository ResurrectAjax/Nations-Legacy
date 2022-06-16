package persistency;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import enumeration.Rank;
import main.Main;
import sql.Database;
import sql.MysqlMain;

public class MappingRepository {
	private List<AllianceMapping> alliances = new ArrayList<AllianceMapping>();
	private List<NationMapping> nations = new ArrayList<NationMapping>();
	private List<PlayerMapping> players = new ArrayList<PlayerMapping>();
	private List<WarMapping> wars = new ArrayList<WarMapping>();
	
	private List<Chunk> claimedChunks = new ArrayList<Chunk>();
	
	private Database db;
	private Main main;
	
	public MappingRepository(Main main) {
		this.main = main;
		load();
	}
	
	public Database getDatabase() {return db;}
	
	
	
	public List<AllianceMapping> getAlliances() {
		return alliances;
	}
	public List<AllianceMapping> getAlliancesByNationID(int ID) {
		List<AllianceMapping> alliances = new ArrayList<AllianceMapping>();
		for(AllianceMapping alliance : this.alliances) {
			if(alliance.getNationID() == ID || alliance.getAllyID() == ID) alliances.add(alliance);
		}
		return alliances;
	}
	public List<AllianceMapping> getAlliancesByNationName(String name) {
		NationMapping nation = getNationByName(name);
		
		List<AllianceMapping> alliances = new ArrayList<AllianceMapping>();
		for(AllianceMapping alliance : this.alliances) {
			if(alliance.getNationID() == nation.getNationID() || alliance.getAllyID() == nation.getNationID()) alliances.add(alliance);
		}
		return alliances;
	}
	public AllianceMapping getAllianceByNationIDs(int nationID, int allyID) {
		for(AllianceMapping alliance : this.alliances) {
			if((alliance.getNationID() == nationID && alliance.getAllyID() == allyID) || (alliance.getAllyID() == nationID && alliance.getNationID() == allyID)) return alliance;
		}
		return null;
	}
	public void createAlliance(int nationID, int allyID) {
		if((this.alliances.stream().anyMatch(al -> al.getNationID() == nationID) && this.alliances.stream().anyMatch(al -> al.getAllyID() == allyID)) || 
				(this.alliances.stream().anyMatch(al -> al.getNationID() == allyID) && this.alliances.stream().anyMatch(al -> al.getAllyID() == nationID))) return;
		
		this.alliances.add(db.insertAlliance(nationID, allyID));
	}
	public List<NationMapping> getAllianceNationsByNationID(int nationID) {
		return getAlliancesByNationID(nationID).stream()
				.map(el -> getNationByID(el.getAllyID()) != null ? getNationByID(el.getAllyID()) : getNationByID(el.getNationID()))
				.filter(el -> el.getNationID() != nationID)
				.collect(Collectors.toList());
	}

	

	public List<NationMapping> getNations() {
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
	public void createNation(String name, PlayerMapping leader) {
		FileConfiguration config = main.getConfig();
		if(getNationByName(name) != null || !leader.getRank().equals(Rank.Nationless)) return;
		NationMapping nation = db.insertNation(name, leader, config.getInt("nations.maxchunks"));
		this.nations.add(nation);
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
	}



	public List<PlayerMapping> getPlayers() {
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
			if(Bukkit.getOfflinePlayer(player.getUUID()).getName().equalsIgnoreCase(name)) return player;
		}
		return null;
	}
	public NationMapping getNationByPlayer(PlayerMapping player) {
		if(player == null || player.getNationID() == null) return null;
		return getNationByID(player.getNationID());
	}
	public List<PlayerMapping> getPlayersByNationID(int nationID) {
		List<PlayerMapping> players = new ArrayList<PlayerMapping>();
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
		PlayerMapping playermap = db.insertPlayer(player.getUniqueId(), 0, Rank.Nationless);
		this.players.add(playermap);
	}

	

	public List<WarMapping> getWars() {
		return wars;
	}
	public List<WarMapping> getWarsByNationID(int ID) {
		List<WarMapping> wars = new ArrayList<WarMapping>();
		for(WarMapping war : this.wars) {
			if(war.getNation().getNationID() == ID || war.getEnemy().getNationID() == ID) wars.add(war);
		}
		return wars;
	}
	public List<WarMapping> getWarsByNationName(String name) {
		NationMapping nation = getNationByName(name);
		
		List<WarMapping> wars = new ArrayList<WarMapping>();
		for(WarMapping war : this.wars) {
			if(war.getNation().getNationID() == nation.getNationID() || war.getEnemy().getNationID() == nation.getNationID()) wars.add(war);
		}
		return wars;
	}
	public WarMapping getWarByNationIDs(int nationID, int warID) {
		for(WarMapping war : this.wars) {
			if((war.getNation().getNationID() == nationID && war.getEnemy().getNationID() == warID) || (war.getNation().getNationID() == warID && war.getEnemy().getNationID() == nationID)) return war;
		}
		return null;
	}
	public void startWar(int nationID, int enemyID) {
		if((this.wars.stream().anyMatch(wa -> wa.getNation().getNationID() == nationID) && this.wars.stream().anyMatch(wa -> wa.getEnemy().getNationID() == enemyID)) || 
				(this.wars.stream().anyMatch(wa -> wa.getNation().getNationID() == enemyID) && this.wars.stream().anyMatch(wa -> wa.getEnemy().getNationID() == nationID))) return;
		
		this.wars.add(db.insertWar(nationID, enemyID));
	}
	public List<NationMapping> getWarNationsByNationID(int nationID) {
		return getWarsByNationID(nationID).stream()
				.map(el -> el.getEnemy() != null ? el.getEnemy() : el.getNation())
				.filter(el -> el.getNationID() != nationID)
				.collect(Collectors.toList());
	}
	

	
	public List<Chunk> getAllClaimedChunks() {
		return claimedChunks;
	}
	



	private void load() {
		//load database
		this.db = new MysqlMain(main, this);
		this.db.load();
		//database
		
		players.addAll(db.getAllPlayers());
		nations.addAll(db.getAllNations());
		alliances.addAll(db.getAllAlliances());
		wars.addAll(db.getAllWars());
		
		for(NationMapping nation : nations) {
			claimedChunks.addAll(nation.getClaimedChunks());
		}
	}
}
