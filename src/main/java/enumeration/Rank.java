package enumeration;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import main.Main;

public enum Rank {
	Leader,
	Officer,
	Member,
	Nationless;
	
	private static final Map<Rank, Integer> rankWorth = Collections.unmodifiableMap(initializeMap());
	
	private static Map<Rank, Integer> initializeMap() {
		Main main = Main.getInstance();
		FileConfiguration config = main.getConfig();
		ConfigurationSection section = config.getConfigurationSection("Nations.War.KillPoints");
		Map<Rank, Integer> rankWorth = new HashMap<>();
		
		for(String key : section.getKeys(false)) {
			if(!Arrays.asList(Rank.values()).stream().map(el -> el.toString()).collect(Collectors.toSet()).contains(key)) continue;
			rankWorth.put(Rank.valueOf(key), section.getInt(key));
		}
		return rankWorth;
	}
	
	public static Integer getRankWorth(Rank rank) {
		return rankWorth.get(rank);
	}
}
