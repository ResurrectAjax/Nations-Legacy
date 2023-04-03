package enumeration;

import java.util.Arrays;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import main.Main;

public enum Flag {
	FriendlyFire,
	TerrainEdit,
	StorageAccess,
	Interact;
	
	public static Boolean isAdjustable(Flag flag) {
		Main main = Main.getInstance();
		FileConfiguration config = main.getConfig();
		ConfigurationSection section = config.getConfigurationSection("Nations.Flags");
		
		return section.getBoolean(flag.toString() + ".Adjustable");
	}
	
	public static String getDefault(Flag flag) {
		Main main = Main.getInstance();
		FileConfiguration config = main.getConfig();
		ConfigurationSection section = config.getConfigurationSection("Nations.Flags");
		
		return section.getString(flag.toString() + ".Default");
	}
	
	public static Flag getFromString(String string) {
		for(Flag flag : Arrays.asList(Flag.values())) {
			if(flag.toString().toLowerCase().equals(string.toLowerCase())) return flag;
		}
		return null;
	}
}
