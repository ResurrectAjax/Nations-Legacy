package sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;

import enumeration.Rank;
import main.Main;
import persistency.MappingRepository;

/**
 * Class for creating tables and creating the database connection
 * 
 * @author ResurrectAjax
 * */
public class MysqlMain extends Database{
	
	private Main plugin = Main.getPlugin(Main.class);
	
	private String dbname;
	/**
	 * Constructor<br>
	 * @param instance instance of the {@link Main.Main} class
	 * */
    public MysqlMain(Main instance, MappingRepository mappingRepo){
        super(instance, mappingRepo);
        dbname = plugin.getConfig().getString("SQLite.Nations-Legacy", "Nations-Legacy"); // Set the database name here e.g player_kills
    }
    
    private String SQLiteCreateRanksTable = "CREATE TABLE IF NOT EXISTS Ranks (" + 
    		"`Rank` varchar(32) PRIMARY KEY" +
            ");"; 
    
    private String SQLiteInsertRanks() {
    	String stmt = "INSERT OR IGNORE INTO Ranks(Rank) values";
    	for(Rank rank : Rank.values()) {
    		if(rank.equals(Rank.values()[Rank.values().length-1])) stmt += "('" + rank.toString() + "')";
    		else stmt += "('" + rank.toString() + "'),";
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
            "foreign key(Rank) references Ranks(Rank), " +
            "foreign key(NationID) references Nations(NationID) on delete set null" +
            ");"; 
    
    private String SQLiteCreateNationsTable = "CREATE TABLE IF NOT EXISTS Nations (" + 
    		"`NationID` INTEGER PRIMARY KEY AUTOINCREMENT, " +
    		"`Name` varchar(32) NOT NULL, " + 
    		"`Description` varchar(32), " +
            "`Leaders` varchar(32) NOT NULL, " +
            "`Officers` varchar(32), " +
            "`Members` varchar(32), " +
            "`MaxChunks` int not null" +
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
    		"foreign key(NationID) references Nations(NationID) on delete cascade, " +
    		"foreign key(Flag) references Flags(Flag) on delete cascade" +
            ");";
    /* EXAMPLES
    
    private String SQLiteCreateBlocksTable = 
    		"create table if not exists Blocks("
    		+ "blockID INTEGER PRIMARY KEY AUTOINCREMENT, "
    		+ "raidID int not null, "
    		+ "type varchar(32) not null, "
    		+ "amount int not null, "
    		+ "isContainer boolean not null check(isContainer in (0, 1)), "
    		+ "foreign key(raidID) references Raids(raidID) on delete cascade"
    		+ ");";
	*/
    
    
	@Override
	public Connection getSQLConnection() {
		File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
            	PreparedStatement ps = connection.prepareStatement("PRAGMA foreign_keys = ON;");
                ps.execute();
                ps.close();
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            Properties properties = new Properties();
            properties.setProperty("PRAGMA foreign_keys", "ON");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder, properties);
            PreparedStatement ps = connection.prepareStatement("PRAGMA foreign_keys = ON;");
            ps.execute();
            ps.close();
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
	}
	@Override
	public void load() {
		connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateRanksTable);
            s.executeUpdate(SQLiteInsertRanks());
            s.executeUpdate(SQLiteCreateFlagsTable);
            s.executeUpdate(SQLiteCreateNationsTable);
            s.executeUpdate(SQLiteCreatePlayersTable);
            s.executeUpdate(SQLiteCreateAlliancesTable);
            s.executeUpdate(SQLiteCreateWarsTable);
            s.executeUpdate(SQLiteCreateClaimedChunksTable);
            s.executeUpdate(SQLiteCreateFlagLinesTable);
            updateFlags();
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
		
}
