package managers;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import main.Main;

/**
 * Class for managing files
 * 
 * @author ResurrectAjax
 * */
public class FileManager {

    private final Main main;
    private final Map<String, FileConfiguration> loadedConfigs = new HashMap<String, FileConfiguration>();
    private final List<String> nullableSections = new ArrayList<String>(Arrays.asList(
    		"enchantments"
    		));
    

    /**
	 * Constructor<br>
	 * @param plugin instance of the {@link Main.Main} class
	 * */
    public FileManager(Main plugin) {
        this.main = plugin;
    }
    
    /**
     * load the files into the plugin
     * */
    public void loadFiles() {
    	List<String> files = new ArrayList<String>(Arrays.asList(
    			"config.yml",
    			"language.yml"
    			));
    	
    	for(String file : files) {
    		createFile(file);
    		updateFile(file);
    	}
    	saveFiles();
    }
    
    /**
     * Create the file if it doesn't exist
     * @param file {@link String} filename, for example: 'config.yml'
     * */
    public void createFile(String file) {
    	File customFile = new File(main.getDataFolder(), file);
        if (!customFile.exists()) {
        	customFile.getParentFile().mkdirs();
            main.saveResource(file, false);
        }
        
        FileConfiguration customConfig = new YamlConfiguration();
        try {
            customConfig.load(customFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        loadedConfigs.put(file, customConfig);
    }
    
    private boolean containsNullable(String section) {
    	for(String nullable : nullableSections) {
    		if(section.contains(nullable)) return true;
    	}
    	return false;
    }
    
    public void updateFile(String file) {
    	FileConfiguration defaultConfig = null, config = null;
    	Reader defaultStream = null;
    	try {
    	    defaultStream = new InputStreamReader(main.getResource(file), "UTF8");
    	} catch (UnsupportedEncodingException ex) {}

    	if (defaultStream != null) {
    	    defaultConfig = YamlConfiguration.loadConfiguration(defaultStream);
    	    config = loadedConfigs.get(file);
    	    
        	for(String sect : defaultConfig.getKeys(true)) {
        		if(config.contains(sect) || containsNullable(sect)) continue;
        		config.set(sect, defaultConfig.get(sect));
        	}
    	}
    	
    }

    /**
     * Get the file configuration of a file
     * @param file {@link String} filename, for example: 'config.yml'
     * @return {@link FileConfiguration} of given filename
     * */
	public FileConfiguration getConfig(String file) {
		return loadedConfigs.get(file);
	}
	
	/**
	 * Save a file with filename
	 * @param file {@link String} filename, for example: 'config.yml'
	 * */
	public void saveFile(String file) {
		if(!loadedConfigs.containsKey(file)) return;
		try {
			loadedConfigs.get(file).save(new File(main.getDataFolder(), file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Save all files
	 * */
	public void saveFiles() {
		for(String key : loadedConfigs.keySet()) {
			try {
				loadedConfigs.get(key).save(new File(main.getDataFolder(), key));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
    
   
}
