package SQL;

import java.util.logging.Level;

import Main.Main;

/**
 * Class containing database error messages
 * 
 * @author ResurrectAjax
 * */
public class Errors {
	
	/**
	 * Error message when executing statement
	 * @return error message
	 * */
    public static String sqlConnectionExecute(){
        return "Couldn't execute MySQL statement: ";
    }
    
    /**
	 * Error message when closing connection
	 * @return error message
	 * */
    public static String sqlConnectionClose(){
        return "Failed to close MySQL connection: ";
    }
    
    /**
	 * Error message when there is no connection
	 * @return error message
	 * */
    public static String noSQLConnection(){
        return "Unable to retreive MYSQL connection: ";
    }
    
    /**
	 * Error message when table is not found
	 * @return error message
	 * */
    public static String noTableFound(){
        return "Database Error: No Table Found";
    }
    
    /**
	 * Error when closing connection
	 * @param plugin instance of the {@link Main.Main} class
	 * @param ex exception occurring during execution
	 * */
    public static void close(Main plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}
