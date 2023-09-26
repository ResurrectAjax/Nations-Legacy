package me.resurrectajax.nationslegacy.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.flywaydb.core.Flyway;

import db.migration.V1_0_1__remove_ranks_foreign_key;
import me.resurrectajax.ajaxplugin.sql.Errors;
import me.resurrectajax.nationslegacy.enumeration.Flag;
import me.resurrectajax.nationslegacy.ranking.Rank;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.AllianceMapping;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;
import me.resurrectajax.nationslegacy.persistency.WarMapping;

/**
 * Class for handling the database
 * 
 * @author ResurrectAjax
 * */
public class Database extends me.resurrectajax.ajaxplugin.sql.Database{
    private MappingRepository mappingRepo;
    /**
	 * Constructor<br>
	 * @param instance instance of the {@link Nations.Main} class
	 * */
    public Database(Nations instance, MappingRepository mappingRepo){
    	super(instance);
        this.mappingRepo = mappingRepo;
    }
    
    public void loadMigrations() {
    	File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
    	
    	Flyway flyway = Flyway.configure()
                .dataSource("jdbc:sqlite:" + dataFolder, "", "")
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .javaMigrations(
                		new V1_0_1__remove_ranks_foreign_key()
                		)
                .load();

        // Run the database migrations
        flyway.migrate();
    }
    
    private String SQLiteCreateRanksTable = "CREATE TABLE IF NOT EXISTS Ranks (" + 
    		"`Rank` varchar(32) PRIMARY KEY," +
    		"`Power` int" +
            ");"; 
    
    private String SQLiteInsertRanks() {
    	String stmt = "INSERT OR REPLACE INTO Ranks(Rank, Power) values"; 
    	List<me.resurrectajax.nationslegacy.ranking.Rank> ranks = new ArrayList<>(me.resurrectajax.nationslegacy.ranking.Rank.getRanks());
    	for(me.resurrectajax.nationslegacy.ranking.Rank rank : ranks) {
    		if(rank.equals(ranks.get(ranks.size()-1))) stmt += String.format("('%s', %d)", rank.getName(), rank.getPower());
    		else stmt += String.format("('%s', %d),", rank.getName(), rank.getPower());
    	}
    	return stmt;
    }
    
    private String SQLiteCreateFlagsTable = "CREATE TABLE IF NOT EXISTS Flags (" + 
    		"`Flag` varchar(32) PRIMARY KEY" +
            ");";
    
    private String SQLiteCreatePlayersTable = "CREATE TABLE IF NOT EXISTS Players (" + 
    		"`UUID` varchar(36) PRIMARY KEY, " +
            "`Killpoints` int NOT NULL, " +
            "`NationID` int, " +
            "`Rank` varchar(32) not null, " +
            "foreign key(NationID) references Nations(NationID) on delete set null" +
            ");"; 
    
    private String SQLiteCreateNationsTable = "CREATE TABLE IF NOT EXISTS Nations (" + 
    		"`NationID` INTEGER PRIMARY KEY AUTOINCREMENT, " +
    		"`Name` varchar(32) NOT NULL, " + 
    		"`Description` varchar(32), " +
            "`MaxChunks` int NOT NULL," +
    		"`Gained_Chunks` int NOT NULL DEFAULT 0" +
            ");"; 
    
    private String SQLiteCreateNationPlayersTable = "CREATE TABLE IF NOT EXISTS Nation_Players (" + 
    		"`NationID` INTEGER NOT NULL, " +
    		"`UUID` varchar(36) NOT NULL, " +
    		"primary key(NationID, UUID), " +
    		"foreign key(NationID) references Nations(NationID) on delete cascade, " +
    		"foreign key(UUID) references Players(UUID) on delete cascade" +
            ");"; 
    
    private String SQLiteCreateWarsTable = "CREATE TABLE IF NOT EXISTS Wars (" + 
    		"`NationID` int NOT NULL," +
    		"`EnemyID` int NOT NULL, " +
    		"`NationKillpoints` int NOT NULL, " +
    		"`EnemyKillpoints` int NOT NULL, " +
    		"`KillpointGoal` int NOT NULL, " +
    		"primary key(NationID, EnemyID), " +
    		"foreign key(NationID) references Nations(NationID) on delete cascade, " +
    		"foreign key(EnemyID) references Nations(NationID) on delete cascade" +
            ");";
    
    private String SQLiteCreateAlliancesTable = "CREATE TABLE IF NOT EXISTS Alliances (" + 
    		"`NationID` int NOT NULL, " +
    		"`AllyID` int NOT NULL, " +
    		"primary key(NationID, AllyID), " +
    		"foreign key(NationID) references Nations(NationID) on delete cascade, " +
    		"foreign key(AllyID) references Nations(NationID) on delete cascade" +
            ");";
    
    private String SQLiteCreateClaimedChunksTable = "CREATE TABLE IF NOT EXISTS ClaimedChunks (" + 
    		"`NationID` int NOT NULL, " +
    		"`World` varchar(32) NOT NULL, " +
    		"`Xcoord` int NOT NULL, " +
    		"`Zcoord` int NOT NULL, " +
    		"primary key(World, Xcoord, Zcoord), " +
    		"foreign key(NationID) references Nations(NationID) on delete cascade" +
            ");";
    
    private String SQLiteCreateFlagLinesTable = "CREATE TABLE IF NOT EXISTS FlagLines (" + 
    		"`ID` INTEGER PRIMARY KEY AUTOINCREMENT, " +
    		"`NationID` int NOT NULL, " +
    		"`Flag` varchar(32) NOT NULL, " +
    		"`Allow` TINYINT(1) NOT NULL DEFAULT 0," +
    		"foreign key(NationID) references Nations(NationID) on delete cascade, " +
    		"foreign key(Flag) references Flags(Flag) on delete cascade" +
            ");";
    
    private String SQLiteCreateHomesTable = "CREATE TABLE IF NOT EXISTS Homes (" + 
    		"`NationID` int NOT NULL, " +
    		"`Name` varchar(32) NOT NULL, " +
    		"`World` varchar(64) NOT NULL, " +
    		"`Xcoord` double NOT NULL, " +
    		"`Ycoord` double NOT NULL, " +
    		"`Zcoord` double NOT NULL, " +
    		"primary key(NationID, Name), " +
    		"foreign key(NationID) references Nations(NationID) on delete cascade" +
            ");";
    
    /**
     * load database and execute table creation statements
     * */
    public void load() {
    	File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
    	boolean exists = dataFolder.exists();
    	super.setConnection(getSQLConnection());
    	
    	if (exists) loadMigrations();
        try{
            Statement s = connection.createStatement();
            
            s.executeUpdate(SQLiteCreateRanksTable);
            s.executeUpdate(SQLiteInsertRanks());
            s.executeUpdate(SQLiteCreateFlagsTable);
            s.executeUpdate(SQLiteCreateNationsTable);
            s.executeUpdate(SQLiteCreatePlayersTable);
            s.executeUpdate(SQLiteCreateNationPlayersTable);
            s.executeUpdate(SQLiteCreateAlliancesTable);
            s.executeUpdate(SQLiteCreateWarsTable);
            s.executeUpdate(SQLiteCreateClaimedChunksTable);
            s.executeUpdate(SQLiteCreateHomesTable);
            s.executeUpdate(SQLiteCreateFlagLinesTable);
            updateFlags();
            s.close();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }
    
    /**
     * Load all the players' data
     * @return {@link List} playerdata
     * */
    public List<PlayerMapping> getAllPlayers() {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        List<PlayerMapping> players = new ArrayList<PlayerMapping>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM Players;");
   
            rs = ps.executeQuery();
            while(rs.next()){
                PlayerMapping player = new PlayerMapping(UUID.fromString(rs.getString(1)), rs.getInt(2), Rank.getRankByName(rs.getString(4)), this);
                if(rs.getObject(3) != null) player.setNationID(rs.getInt(3));
                players.add(player);
            }
        	return players;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return players;
    }
    
    public Set<NationMapping> getAllNations() {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        FileConfiguration config = plugin.getConfig();
        Set<NationMapping> nations = new HashSet<NationMapping>();
        HashMap<Integer, Set<Chunk>> chunkMap = getAllClaimedChunks();
        HashMap<Integer, HashMap<Flag, Boolean>> flagMap = getAllNationFlags();
        
        Set<NationHome> nationHomes = getHomes();
        
        try {
        	Set<PlayerMapping> players = getAllNationMembers();
        	
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM Nations;");
   
            rs = ps.executeQuery();
            while(rs.next()){
            	int nationID = rs.getInt(1), maxChunks = rs.getInt(4), gainedChunks = rs.getInt(5);
            	String name = rs.getString(2), description = rs.getString(3) == null ? "" : rs.getString(3);
            	
            	Set<Chunk> chunks = chunkMap.get(nationID) == null ? new HashSet<Chunk>() : chunkMap.get(nationID);
            	HashMap<Flag, Boolean> flags = flagMap.get(nationID) == null ? new HashMap<>() : flagMap.get(nationID);
            	
            	HashMap<String, Location> homes = new HashMap<>(nationHomes.stream().filter(el -> el.getNationID() == nationID).collect(Collectors.toMap(NationHome::getName, NationHome::getLocation)));
            	
            	Set<PlayerMapping> nationMembers = players.stream().filter(el -> el.getNationID() == nationID).collect(Collectors.toSet());
            	NationMapping nation = new NationMapping(nationID, name, description, maxChunks, config.getInt("Nations.Claiming.MaxChunks"), gainedChunks, nationMembers, chunks, flags, homes, this);
            	
            	nations.add(nation);
            }
        	return nations;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return nations;
    }
    
    public Set<PlayerMapping> getAllMembersOfNation(int nationID) {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        Set<PlayerMapping> players = new HashSet<>();
        
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM Nation_Players WHERE NationID = ?;");
            ps.setInt(1, nationID);
            
            rs = ps.executeQuery();
            while(rs.next()){
            	PlayerMapping player = mappingRepo.getPlayerByUUID(UUID.fromString(rs.getString("UUID")));
            	players.add(player);
            }
        	return players;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return players;
    }
    
    public Set<PlayerMapping> getAllNationMembers() {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        Set<PlayerMapping> players = new HashSet<>();
        
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM Nation_Players;");
   
            rs = ps.executeQuery();
            while(rs.next()){
            	PlayerMapping player = mappingRepo.getPlayerByUUID(UUID.fromString(rs.getString("UUID")));
            	players.add(player);
            }
        	return players;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return players;
    }
    
    private Set<NationHome> getHomes() {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        Set<NationHome> nationHomes = new HashSet<>();
        
        try {
        	
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM Homes;");
   
            rs = ps.executeQuery();
            while(rs.next()){            	
            	int nationID = rs.getInt(1);
            	String name = rs.getString(2), world = rs.getString(3);
            	double x = rs.getDouble(4), y = rs.getDouble(5), z = rs.getDouble(6);
            	
            	nationHomes.add(new NationHome(nationID, name, new Location(Bukkit.getWorld(world), x, y, z)));
            }
        	return nationHomes;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return nationHomes;
    }
    private class NationHome {
    	private int nationID;
    	private String name = null;
    	private Location location = null;
    	
    	public NationHome(int id, String name, Location loc) {
			this.nationID = id;
			this.name = name;
			this.location = loc;
		}

		public int getNationID() {
			return nationID;
		}

		public String getName() {
			return name;
		}

		public Location getLocation() {
			return location;
		}
    }
    
    public HashMap<Integer, Set<Chunk>> getAllClaimedChunks() {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        HashMap<Integer, Set<Chunk>> map = new HashMap<Integer, Set<Chunk>>();
        List<Chunk> chunks = new ArrayList<Chunk>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM ClaimedChunks");
            
            rs = ps.executeQuery();
            
            Integer nationID = null;
            while(rs.next()){
            	if((nationID == null) || (nationID != rs.getInt(1))) chunks = new ArrayList<Chunk>();
            	nationID = rs.getInt(1);
            	
            	Chunk chunk = Bukkit.getWorld(rs.getString(2)).getChunkAt(rs.getInt(3), rs.getInt(4));
            	chunks.add(chunk);
            	
            	map.put(nationID, new HashSet<Chunk>(chunks));
            }
        	return map;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return map;
    }
    
    
    
    public HashMap<Integer, HashMap<Flag, Boolean>> getAllNationFlags() {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        HashMap<Integer, HashMap<Flag, Boolean>> map = new HashMap<Integer, HashMap<Flag, Boolean>>();
        HashMap<Flag, Boolean> flags = new HashMap<>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM FlagLines ORDER BY NationID, Flag");
            
            rs = ps.executeQuery();
            
            Integer nationID = null;
            while(rs.next()){
            	nationID = rs.getInt(2);
            	if(map.containsKey(nationID)) flags = new HashMap<>(map.get(nationID));
            	
            	flags.put(Flag.getFromString(rs.getString(3)), rs.getInt(4) == 0 ? false : true);
            	map.put(nationID, flags);
            }
        	return map;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return map;
    }
    
    
    protected void updateFlags() {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            List<String> flagStrings = new ArrayList<String>();
            
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM Flags");
            
            rs = ps.executeQuery();

            while(rs.next()){
            	flagStrings.add(rs.getString(1));
            }
            ps.close();
            
            if(!flagStrings.isEmpty()) return;
            
            ps = conn.prepareStatement(SQLiteInsertFlags());
            ps.executeUpdate();
        	return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }
    
    private String SQLiteInsertFlags() {
    	String stmt = "INSERT OR IGNORE INTO Flags(Flag) values";
    	for(Flag flag : Flag.values()) {
    		if(flag.equals(Flag.values()[Flag.values().length-1])) stmt += "('" + flag.toString() + "')";
    		else stmt += "('" + flag.toString() + "'),";
    	}
    	return stmt;
    }
    
    public List<WarMapping> getAllWars() {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        List<WarMapping> wars = new ArrayList<WarMapping>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM Wars;");
   
            rs = ps.executeQuery();
            try {
            	while(rs.next()){
                	NationMapping nation = mappingRepo.getNationByID(rs.getInt(1)), 
                			enemy = mappingRepo.getNationByID(rs.getInt(2));
                	
                    WarMapping war = new WarMapping(nation, enemy, rs.getInt(3), rs.getInt(4), this);
                    wars.add(war);
                }
            }
            catch(SQLException ex) {
            	//exception is thrown for no reason -> silence it
            }
        	return wars;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return wars;
    }
    
    public List<AllianceMapping> getAllAlliances() {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        List<AllianceMapping> alliances = new ArrayList<AllianceMapping>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM Alliances;");
   
            rs = ps.executeQuery();
            while(rs.next()){
            	AllianceMapping alliance = new AllianceMapping(rs.getInt(1), rs.getInt(2));
            	alliances.add(alliance);
            }
        	return alliances;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return alliances;
    }

    
    
    public PlayerMapping insertPlayer(UUID uuid, int killpoints, Rank rank) {
    	Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("INSERT INTO Players(UUID, Killpoints, Rank) values(?,?,?);");
            
            ps.setString(1, uuid.toString());
            ps.setInt(2, killpoints);
            ps.setString(3, rank.toString());
            
            ps.executeUpdate();
            return new PlayerMapping(uuid, killpoints, rank, this);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
    }

    public void updatePlayer(PlayerMapping player) {
    	Connection conn = null;
        PreparedStatement ps = null;
        try { 	
            conn = getSQLConnection();
            ps = conn.prepareStatement("UPDATE Players SET Killpoints = ?, NationID = ?, Rank = ? WHERE UUID = ?");
                     
            ps.setInt(1, player.getKillpoints());
            if(player.getNationID() == null) ps.setNull(2, java.sql.Types.NULL);
            else ps.setInt(2, player.getNationID()); 
            ps.setString(3, player.getRank().toString());
            ps.setString(4, player.getUUID().toString());
            
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }
    
    public NationMapping insertNation(String name, PlayerMapping leader, int maxChunks) {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer nationID = null;
        NationMapping nation = null;
        
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("INSERT INTO Nations(Name, MaxChunks) values(?,?);", Statement.RETURN_GENERATED_KEYS);
            
            ps.setString(1, name);
            ps.setInt(2, maxChunks);
            
            ps.executeUpdate();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
            	nationID = rs.getInt(1);
            	leader.setNationID(nationID);
            	
            	nation = new NationMapping(nationID, name, leader, maxChunks, this);
            }
            return nation;
            
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
            if(nationID != null) addNationFlags(nationID);
        }
        return null;
    }
    
    public void insertPlayerIntoNation(int nationID, UUID uuid) {
    	Connection conn = null;
        PreparedStatement ps = null;
		try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("INSERT INTO Nation_Players(NationID, UUID) values(?,?);");
            
            ps.setInt(1, nationID);
            ps.setString(2, uuid.toString());
            
            ps.executeUpdate();
            
            PlayerMapping player = mappingRepo.getPlayerByUUID(uuid);
            player.setNationID(nationID);
        	
        	player.update();
            
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }
    
    public void removePlayerFromNation(UUID uuid) {
    	Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM Nation_Players WHERE UUID = ?;");
            
            ps.setString(1, uuid.toString());
            
            ps.executeUpdate();
            
            PlayerMapping player = mappingRepo.getPlayerByUUID(uuid);
            player.setNationID(null);
            player.setRank(Rank.getNationless());
        	
        	player.update();
            
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }

    public void updateNation(NationMapping nation) {
    	Connection conn = null;
        PreparedStatement ps = null;
        try {        	
            conn = getSQLConnection();
            ps = conn.prepareStatement("UPDATE Nations SET Name = ?, Description = ?, MaxChunks = ?, Gained_Chunks = ? WHERE NationID = ?");
            
            ps.setString(1, nation.getName()); 
            ps.setString(2, nation.getDescription());
            ps.setInt(3, nation.getMaxChunks());
            ps.setInt(4, nation.getGainedChunks());
            ps.setInt(5, nation.getNationID());
            
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }
    
    public void insertHome(int nationID, String name, Location loc) {
    	Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("INSERT OR REPLACE INTO Homes(NationID, Name, World, Xcoord, Ycoord, Zcoord) VALUES(?,?,?,?,?,?)");
            
            ps.setInt(1, nationID); 
            ps.setString(2, name);
            ps.setString(3, loc.getWorld().getName());
            ps.setDouble(4, loc.getX()); 
            ps.setDouble(5, loc.getY());
            ps.setDouble(6, loc.getZ());
            
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    	
    }
    
    public HashMap<String, Location> getHomes(int nationID) {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        HashMap<String, Location> map = new HashMap<>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM Homes WHERE NationID = ?");
            ps.setInt(1, nationID);
            
            rs = ps.executeQuery();
            
            while(rs.next()){
            	map.put(rs.getString(2), new Location(Bukkit.getWorld(rs.getString(3)), rs.getDouble(4), rs.getDouble(5), rs.getDouble(6)));
            }
        	return map;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return map;
    }
    
    public void deleteHome(int nationID, String name) {
		Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            
            ps = conn.prepareStatement("DELETE FROM Homes WHERE NationID = ? AND Name = ?");
            
            ps.setInt(1, nationID);
            ps.setString(2, name);
            
            ps.executeUpdate();
            ps.close();
            
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }
    
    public void deleteNation(int nationID) {
    	Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            
            
            ps = conn.prepareStatement("UPDATE Players SET Rank = ? WHERE NationID = ?");
            ps.setString(1, Rank.getNationless().toString());
            ps.setInt(2, nationID);
            ps.executeUpdate();
            ps.close();
            
            ps = conn.prepareStatement("DELETE FROM Nations WHERE NationID = ?");
            ps.setInt(1, nationID);
            ps.executeUpdate();
            ps.close();
            
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }
    
    public AllianceMapping insertAlliance(int nationID, int allyID) {
    	Connection conn = null;
        PreparedStatement ps = null;
        
        try {
        	
            conn = getSQLConnection();
            ps = conn.prepareStatement("INSERT INTO Alliances(NationID, AllyID) values(?,?);", Statement.RETURN_GENERATED_KEYS);
            
            ps.setInt(1, nationID);
            ps.setInt(2, allyID);
            
            ps.executeUpdate();
            
            return new AllianceMapping(nationID, allyID);
            
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
    }
    
    public void deleteAlliance(int nationID, int allyID) {
    	Connection conn = null;
        PreparedStatement ps = null;
        
        try {
        	
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM Alliances WHERE (NationID = ? AND AllyID = ?) OR (NationID = ? AND AllyID = ?);");
            
            ps.setInt(1, nationID);
            ps.setInt(2, allyID);
            ps.setInt(3, allyID);
            ps.setInt(4, nationID);
            
            ps.executeUpdate();
            
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;
    }
    
    public WarMapping insertWar(int nationID, int enemyID) {
    	Connection conn = null;
        PreparedStatement ps = null;
        
        try {
        	
        	NationMapping nation = mappingRepo.getNationByID(nationID), 
        			enemy = mappingRepo.getNationByID(enemyID);
        	
        	WarMapping war = new WarMapping(nation, enemy, this);
        	
            conn = getSQLConnection();
            ps = conn.prepareStatement("INSERT INTO Wars(NationID, EnemyID, NationKillpoints, EnemyKillpoints, KillpointGoal) values(?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            
            ps.setInt(1, nationID);
            ps.setInt(2, enemyID);
            ps.setInt(3, war.getNationKillpoints());
            ps.setInt(4, war.getEnemyKillpoints());
            ps.setInt(5, war.getKillpointGoal());
            
            ps.executeUpdate();
            return war;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
    }
    
    public void updateWar(WarMapping war) {
    	Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("UPDATE Wars SET NationKillpoints = ?, EnemyKillpoints = ?, KillpointGoal = ? WHERE NationID = ? AND EnemyID = ?");
            
            ps.setInt(1, war.getNationKillpoints()); 
            ps.setInt(2, war.getEnemyKillpoints());
            ps.setInt(3, war.getKillpointGoal()); 
            ps.setInt(4, war.getNation().getNationID());
            ps.setInt(5, war.getEnemy().getNationID());
            
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }
    
    public void updateWars(Set<WarMapping> wars) {
    	Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(
            		"WITH Tmp(NationID, EnemyID, NationKillpoints, EnemyKillpoints, KillpointGoal) as (VALUES" + 
            		wars.stream().map(el -> {
            			return String.format("(%d, %d, %d, %d, %d)", el.getNation().getNationID(), el.getEnemy().getNationID(), el.getNationKillpoints(), el.getEnemyKillpoints(), el.getKillpointGoal());
            		}).collect(Collectors.joining(", ")) +
            		") " +
            		"UPDATE Wars SET NationKillpoints = (SELECT NationKillpoints FROM Tmp WHERE Wars.NationID = Tmp.NationID AND Wars.EnemyID = Tmp.EnemyID), " + 
            		"EnemyKillpoints = (SELECT EnemyKillpoints FROM Tmp WHERE Wars.NationID = Tmp.NationID AND Wars.EnemyID = Tmp.EnemyID), " + 
            		"KillpointGoal = (SELECT KillpointGoal FROM Tmp WHERE Wars.NationID = Tmp.NationID AND Wars.EnemyID = Tmp.EnemyID) " + 
            		"WHERE NationID IN (SELECT NationID FROM Tmp) AND EnemyID IN (SELECT EnemyID FROM Tmp)"
            		);
            
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }
    
    public void deleteWar(int nationID, int warID) {
    	Connection conn = null;
        PreparedStatement ps = null;
        
        try {
        	
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM Wars WHERE (NationID = ? AND EnemyID = ?) OR (NationID = ? AND EnemyID = ?);");
            
            ps.setInt(1, nationID);
            ps.setInt(2, warID);
            ps.setInt(3, warID);
            ps.setInt(4, nationID);
            
            ps.executeUpdate();
            
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;
    }
    
    public void addClaimedChunks(List<Chunk> chunks, int nationID) {
    	Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = getSQLConnection();
            
            String stmt = "INSERT INTO ClaimedChunks(NationID, World, Xcoord, Zcoord) values";
            for(Chunk chunk : chunks) {
            	if(chunk.equals(chunks.get(chunks.size()-1))) stmt += "(?,?,?,?)";
            	else stmt += "(?,?,?,?),";
            }
            
            ps = conn.prepareStatement(stmt);
            
            int count = 1;
            for(Chunk chunk : chunks) {
                ps.setInt(count++, nationID);
                ps.setString(count++, chunk.getWorld().getName());
                ps.setInt(count++, chunk.getX());
                ps.setInt(count++, chunk.getZ());	
            }
            
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }
    
    public void deleteAllClaimedChunks(int nationID) {
    	Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM ClaimedChunks WHERE NationID = ?");
            
            ps.setInt(1, nationID); 
            
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }

    public void deleteClaimedChunks(List<Chunk> chunks, int nationID) {
    	Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            
            String stmt = "DELETE FROM ClaimedChunks WHERE ";
            for(Chunk chunk : chunks) {
            	if(chunk.equals(chunks.get(chunks.size()-1))) stmt += "(NationID = ? AND World = ? AND Xcoord = ? AND Zcoord = ?)";
            	else stmt += "(NationID = ? AND World = ? AND Xcoord = ? AND Zcoord = ?) OR ";
            }
            ps = conn.prepareStatement(stmt);
            
            int count = 1;
            for(Chunk chunk : chunks) {
            	ps.setInt(count++, nationID);
            	ps.setString(count++, chunk.getWorld().getName());
                ps.setInt(count++, chunk.getX());
                ps.setInt(count++, chunk.getZ());	
            }
            
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }
    
    public void updateChunksNationID(int original, int newNation) {
    	Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("UPDATE ClaimedChunks SET NationID = ? WHERE NationID = ?");
            
            ps.setInt(1, newNation); 
            ps.setInt(2, original);
            
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }
    
    public void addNationFlags(int nationID) {
    	Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = getSQLConnection();
            
            String stmt = "INSERT INTO FlagLines(NationID, Flag, Allow) values";
            for(Flag flag : Flag.values()) {
            	String allow = Flag.getDefault(flag);
            	
            	int index = Arrays.asList(Flag.values()).indexOf(flag);
            	if(index == Flag.values().length-1) stmt += String.format("(%d,'%s',%d);", nationID, flag.toString(), allow.equalsIgnoreCase("allow") ? 1 : 0);
            	else  stmt += String.format("(%d,'%s',%d),", nationID, flag.toString(), allow.equalsIgnoreCase("allow") ? 1 : 0);
            	
            }
            ps = conn.prepareStatement(stmt);
            
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }
    
    public void updateNationFlag(Flag flag, int nationID, boolean allow) {
    	Connection conn = null;
        PreparedStatement ps = null;
		try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("UPDATE FlagLines SET Allow = ? WHERE NationID = ? AND Flag = ?");
            
            ps.setInt(1, allow ? 1 : 0);
            ps.setInt(2, nationID);
            ps.setString(3, flag.toString());
            
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }
    
    /*
    public void updateUser(UUID uuid, String channel, boolean profanityFilter) {
    	Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("UPDATE Users SET channel = ?, profanityFilter = ? WHERE uuid = ?");
                     
            ps.setString(1, channel); 
            
            ps.setBoolean(2, profanityFilter);
            
            ps.setString(3, uuid.toString()); 
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;  
    }
    */
    
    /* DELETE EXAMPLE
    public String deleteValues(String table, String query, String string) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM " + table + " WHERE " + query + " = '"+string+"';");
   
            try {
                ps.executeUpdate();
            }
            catch(Exception e) {
            	
            }
            return "";
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "";
    }
    */
    

    /**
     * Close database connection
     * @param ps {@link PreparedStatement}
     * @param rs {@link ResultSet}
     * */
    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Errors.close(plugin, ex);
        }
    }
}
