package me.resurrectajax.nationslegacy.events.nation.home;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.resurrectajax.nationslegacy.events.nation.NationEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.NationMapping;

public class SetHomeEvent extends NationEvent{

	private String homeName;
	private Location homeLocation;
	
	public SetHomeEvent(NationMapping nation, CommandSender sender, String home, Location loc) {
		super(nation, sender);
		
		setHomeName(home);
		setHomeLocation(loc);
		
		Nations main = Nations.getInstance();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				if(homeName == null || homeName.isBlank() || homeName.isEmpty()) nation.setHome(homeLocation);
				else nation.setHome(homeName, homeLocation);
				
				FileConfiguration language = main.getLanguage();
				sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Home.SetHome.Set.Message"), nation.getName()));
			}
		}, 1L);
	}

	public String getHomeName() {
		return homeName;
	}

	public Location getHomeLocation() {
		return homeLocation;
	}

	public void setHomeName(String homeName) {
		this.homeName = homeName;
	}

	public void setHomeLocation(Location homeLocation) {
		this.homeLocation = homeLocation;
	}

}
