package me.resurrectajax.nationslegacy.enumeration;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.resurrectajax.nationslegacy.main.Nations;

public enum Rank {
	Leader,
	Officer,
	Member,
	Nationless;
	
	public static Integer getRankWorth(Rank rank) {
		Nations main = Nations.getInstance();
		FileConfiguration config = main.getConfig();
		ConfigurationSection section = config.getConfigurationSection("Nations.Nation.Ranks." + rank.toString());
		
		return section.getInt("Worth");
	}
	
	public static List<String> getPermissionsByRank(Rank rank) {
		Nations main = Nations.getInstance();
		FileConfiguration config = main.getConfig();
		ConfigurationSection section = config.getConfigurationSection("Nations.Nation.Ranks." + rank.toString());
		
		return section.getStringList("Permissions");
	}
}
