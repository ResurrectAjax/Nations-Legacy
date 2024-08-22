package me.resurrectajax.nationslegacy.ranking;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.resurrectajax.nationslegacy.main.Nations;

public class Rank implements Comparable<Rank>{
	
	private String name;
	private int power, worth;
	private List<String> permissions;
	public Rank(String name, int power, int worth, List<String> permissions) {
		this.name = name;
		this.power = power;
		this.worth = worth;
		this.permissions = permissions;
	}
	
	private static List<Rank> ranks = initializeRanks();
	
	/**
	 * Loads all the ranks from the config.yml file
	 * @return a list of all the available ranks
	 * */
	private static List<Rank> initializeRanks() {
		Nations main = Nations.getInstance();
		FileConfiguration config = main.getConfig();
		ConfigurationSection section = config.getConfigurationSection("Nations.Nation.Ranks");
		
		List<Rank> rankingSet = new ArrayList<>();
		for(String key : section.getKeys(false)) {
			int power = section.getInt(String.format("%s.Power", key));
			int worth = section.getInt(String.format("%s.Worth", key));
			List<String> permissions = section.getStringList(String.format("%s.Permissions", key));
			
			rankingSet.add(new Rank(key, power, worth, permissions));
		}
		
		rankingSet.sort((o1, o2) -> Integer.compare(o1.getPower(), o2.getPower()));
		return rankingSet;
	}
	
	/**
	 * @return the rank associated with a certain power level
	 * */
	public static Rank getRankByPower(int power) {
		return ranks.stream().filter(el -> el.power == power).findFirst().orElse(null);
	}
	/**
	 * @return the rank associated with the given displayname
	 * */
	public static Rank getRankByName(String name) {
		Rank rank = ranks.stream().filter(el -> el.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
		if(rank == null) getRankByPower(0);
		
		return ranks.stream().filter(el -> el.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	/**
	 * @return the rank with a power of 0, which should always be nationless
	 * */
	public static Rank getNationless() {
		return ranks.get(0);
	}
	
	/**
	 * @return the lowest rank possible whithin a nation
	 * */
	public static Rank getLowest() {
		return ranks.get(1);
	}
	/**
	 * @return the heighest rank possible whithin a nation
	 * */
	public static Rank getHighest() {
		return ranks.get(ranks.size()-1);
	}
	
	/**
	 * Reloads all the ranks from config
	 * */
	public static void reloadRanks() {
		ranks = initializeRanks();
	}
	
	/**
	 * @return a list of all the ranks
	 * */
	public static List<Rank> getRanks() {
		return ranks;
	}
	
	/**
	 * @return an iterator of the rank list
	 * */
	public static ListIterator<Rank> getRankIterator() {
		return getRanks().listIterator();
	}
	
	/**
	 * @return the displayname of a rank
	 * */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the power of a rank
	 * */
	public int getPower() {
		return power;
	}
	
	/**
	 * @return the worth of a rank, i.e. how much points a player will get for killing someone of that rank
	 * */
	public int getWorth() {
		return worth;
	}
	
	/**
	 * @return a list of permissions associated with the rank
	 * */
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

	@Override
	public int compareTo(Rank o) {
		// TODO Auto-generated method stub
		return Integer.compare(power, o.getPower());
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}
	
}