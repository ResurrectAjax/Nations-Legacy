package me.resurrectajax.nationslegacy.ranking;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.resurrectajax.nationslegacy.main.Nations;

public class Rank {
	
	private String name;
	private int power, worth;
	private List<String> permissions;
	public Rank(String name, int power, int worth, List<String> permissions) {
		this.name = name;
		this.power = power;
		this.worth = worth;
		this.permissions = permissions;
	}
	
	private static Set<Rank> ranks = initializeRanks();
	private static Set<Rank> initializeRanks() {
		Nations main = Nations.getInstance();
		FileConfiguration config = main.getConfig();
		ConfigurationSection section = config.getConfigurationSection("Nations.Nation.Ranks");
		
		Set<Rank> rankingSet = new HashSet<>();
		for(String key : section.getKeys(false)) {
			int power = section.getInt(String.format("%s.Power", key));
			int worth = section.getInt(String.format("%s.Worth", key));
			List<String> permissions = section.getStringList("Permissions");
			rankingSet.add(new Rank(key, power, worth, permissions));
		}
		
		return rankingSet;
	}
	
	public static Rank getRankByPower(int power) {
		return ranks.stream().filter(el -> el.power == power).findFirst().orElse(null);
	}
	public static Rank getRankByName(String name) {
		return ranks.stream().filter(el -> el.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public static void reloadRanks() {
		ranks = initializeRanks();
	}
	
	public static Set<Rank> getRanks() {
		return ranks;
	}
	
	public String getName() {
		return name;
	}
	
	public int getPower() {
		return power;
	}
	
	public int getWorth() {
		return worth;
	}
	
	public List<String> getPermissions() {
		return permissions;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, power, worth);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rank other = (Rank) obj;
		return Objects.equals(name, other.name) && power == other.power && worth == other.worth;
	}
	
}