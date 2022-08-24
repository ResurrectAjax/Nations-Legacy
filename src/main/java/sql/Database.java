package sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import enumeration.Flag;
import enumeration.Rank;
import main.Main;
import persistency.AllianceMapping;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;
import persistency.WarMapping;

/**
 * Class for handling the database
 * 
 * @author ResurrectAjax
 * */
public abstract class Database {
    private final Main plugin;
    private MappingRepository mappingRepo;
    Connection connection;
    /**
	 * Constructor<br>
	 * @param instance instance of the {@link Main.Main} class
	 * */
    public Database(Main instance, MappingRepository mappingRepo){
        plugin = instance;
        this.mappingRepo = mappingRepo;
    }

    
    /**
     * Get the SQL connection
     * @return {@link Connection} to the database
     * */
    public abstract Connection getSQLConnection();

    /**
     * load database and execute table creation statements
     * */
    public abstract void load();

    /**
     * Create the connection with the database and check if the connection is stable
     * */
    public void initialize(){
        connection = getSQLConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM Nations WHERE NationID = 1");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
   
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
                PlayerMapping player = new PlayerMapping(UUID.fromString(rs.getString(1)), rs.getInt(2), Rank.valueOf(rs.getString(4)), this);
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
    
    private String getStringFromList(Collection<PlayerMapping> playerList) {
    	return playerList.stream().map(play -> play.getUUID().toString()).collect(Collectors.joining(","));
    }
    
    private Set<PlayerMapping> getPlayerListFromString(String list) {
    	if(list == null) return new HashSet<PlayerMapping>();
    	String[] itemSplit = list.split(",");
    	Set<PlayerMapping> items = new HashSet<PlayerMapping>();
    	
    	for(String item : itemSplit) {
    		if(item.isBlank() || item.isEmpty()) continue;
    		items.add(mappingRepo.getPlayerByUUID(UUID.fromString(item)));
    	}
    	return items;
    }
    
    public Set<NationMapping> getAllNations() {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        Set<NationMapping> nations = new HashSet<NationMapping>();
        HashMap<Integer, Set<Chunk>> chunkMap = getAllClaimedChunks();
        HashMap<Integer, Set<Flag>> flagMap = getAllNationFlags();
        
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM Nations;");
   
            rs = ps.executeQuery();
            while(rs.next()){
            	int nationID = rs.getInt(1), maxChunks = rs.getInt(7);
            	String name = rs.getString(2), description = rs.getString(3) == null ? "" : rs.getString(3);
            	
            	Set<PlayerMapping> leaders = getPlayerListFromString(rs.getString(4)), 
            			officers = getPlayerListFromString(rs.getString(5)), 
            			members = getPlayerListFromString(rs.getString(6));
            	Set<Chunk> chunks = chunkMap.get(nationID) == null ? new HashSet<Chunk>() : chunkMap.get(nationID);
            	Set<Flag> flags = flagMap.get(nationID) == null ? new HashSet<Flag>() : flagMap.get(nationID);
            	
            	NationMapping nation = new NationMapping(nationID, name, description, maxChunks, leaders, officers, members, chunks, flags, this);
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
    
    
    
    public HashMap<Integer, Set<Flag>> getAllNationFlags() {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        HashMap<Integer, Set<Flag>> map = new HashMap<Integer, Set<Flag>>();
        Set<Flag> flags = new HashSet<Flag>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM FlagLines");
            
            rs = ps.executeQuery();
            
            Integer nationID = null;
            while(rs.next()){
            	if(nationID != rs.getInt(2)) flags = new HashSet<Flag>();
            	nationID = rs.getInt(2);
            	
            	flags.add(Flag.valueOf(rs.getString(3)));
            	map.put(nationID, new HashSet<Flag>(flags));
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
            
            if(flagStrings.isEmpty()) {
            	ps = conn.prepareStatement(SQLiteInsertFlags());
                ps.executeUpdate();
            	return;
            }
            
            List<String> flags = new ArrayList<String>();
            for(Flag flag : Flag.values()) {
            	flags.add(flag.toString());
            }
            
            List<String> difference = new ArrayList<String>(flagStrings);
            if(flagStrings.retainAll(flags)) difference.removeAll(flagStrings);
            
            if(difference.isEmpty()) return;
            
        	String stmt = "DELETE FROM Flags WHERE Flag = ?";
        	for(int i = 1; i <= difference.size(); i++) {
        		if(i != 1) stmt += " OR Flag = ?";
        	}
        	
        	ps = conn.prepareStatement(stmt);

        	for(int i = 1; i <= difference.size(); i++) {
        		ps.setString(i, difference.get(i-1));
        	}
            ps.executeUpdate();
            ps.close();
            
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
            while(rs.next()){
            	
            	NationMapping nation = mappingRepo.getNationByID(rs.getInt(1)), 
            			enemy = mappingRepo.getNationByID(rs.getInt(2));
            	
                WarMapping war = new WarMapping(nation, enemy, rs.getInt(3), rs.getInt(4), this);
                wars.add(war);
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
            ps.setInt(2, player.getNationID()); 
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
        
        try {
        	Integer nationID = null;
        	
            conn = getSQLConnection();
            ps = conn.prepareStatement("INSERT INTO Nations(Name, Leaders, MaxChunks) values(?,?,?);", Statement.RETURN_GENERATED_KEYS);
            
            ps.setString(1, name);
            ps.setString(2, leader.getUUID().toString());
            ps.setInt(3, maxChunks);
            
            ps.executeUpdate();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
            	nationID = rs.getInt(1);
            	leader.setNationID(nationID);
            	
            	return new NationMapping(nationID, name, leader, maxChunks, this);
            }
            
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

    public void updateNation(NationMapping nation) {
    	Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("UPDATE Nations SET Name = ?, Description = ?, Leaders = ?, Officers = ?, Members = ?, MaxChunks = ? WHERE NationID = ?");
                     
            String leaders = getStringFromList(nation.getLeaders()), 
            		officers = getStringFromList(nation.getOfficers()), 
            		members = getStringFromList(nation.getMembers());
            
            ps.setString(1, nation.getName()); 
            ps.setString(2, nation.getDescription());
            ps.setString(3, leaders);
            ps.setString(4, officers); 
            ps.setString(5, members);
            ps.setInt(6, nation.getMaxChunks());
            ps.setInt(7, nation.getNationID());
            
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
    
    public void deleteNation(int nationID) {
    	Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            
            ps = conn.prepareStatement("DELETE FROM Nations WHERE NationID = ?");
            
            ps.setInt(1, nationID);
            
            ps.executeUpdate();
            ps.close();
            
            ps = conn.prepareStatement("UPDATE Players SET NationID = NULL, Rank = ?");
            ps.setString(1, Rank.Nationless.toString());
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
    
    public void addNationFlag(Flag flag, int nationID) {
    	Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("INSERT INTO FlagLines(NationID, Flag) values(?,?);");
            
            ps.setInt(1, nationID);
            ps.setString(2, flag.toString());
            
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
    
    public void removeNationFlag(Flag flag, int nationID) {
    	Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM FlagLines WHERE NationID = ? AND Flag = ?");
            
            ps.setInt(1, nationID);
            ps.setString(2, flag.toString());
            
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
