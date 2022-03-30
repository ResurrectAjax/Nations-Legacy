package Persistency;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import Enumeration.Rank;
import Main.Main;
import SQL.Database;
import SQL.MysqlMain;

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

	

	public List<NationMapping> getNations() {
		return nations;
	}
	public NationMapping getNationByName(String name) {
		for(NationMapping nation : nations) {
			if(nation.getName().equalsIgnoreCase(name)) return nation;
		}
		return null;
	}
	public NationMapping getNationByID(int ID) {
		for(NationMapping nation : nations) {
			if(nation.getNationID() == ID) return nation;
		}
		return null;
	}
	public void createNation(String name, PlayerMapping leader, int maxChunks) {
		if(getNationByName(name) != null || !leader.getRank().equals(Rank.Nationless)) return;
		this.nations.add(db.insertNation(name, leader, maxChunks));
	}
	public void disbandNation(NationMapping nation) {
		if(!this.nations.stream().anyMatch(nat -> nat.getNationID() == nation.getNationID())) return;
		nation.disband();
		this.nations.remove(nation);
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
	public NationMapping getNationByPlayer(PlayerMapping player) {
		return getNationByID(player.getNationID());
	}
	public List<PlayerMapping> getPlayersByNationID(int nationID) {
		List<PlayerMapping> players = new ArrayList<PlayerMapping>();
		for(PlayerMapping player : players) {
			if(player.getNationID() == nationID) players.add(player);
		}
		return players;
	}
	public void addPlayer(Player player) {
		if(players.stream().anyMatch(play -> play.getUUID().equals(player.getUniqueId()))) return;
		PlayerMapping playermap = db.insertPlayer(player.getUniqueId(), player.getName(), 0, Rank.Nationless);
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
