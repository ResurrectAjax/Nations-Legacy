package enumeration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import main.Main;

public enum Rank {
	Leader,
	Officer,
	Member,
	Nationless;
	
	public static Integer getRankWorth(Rank rank) {
		Main main = Main.getInstance();
		FileConfiguration config = main.getConfig();
		ConfigurationSection section = config.getConfigurationSection("Nations.War.KillPoints");
		
		return section.getInt(rank.toString());
	}
}
