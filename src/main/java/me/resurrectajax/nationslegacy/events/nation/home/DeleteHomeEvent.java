package me.resurrectajax.nationslegacy.events.nation.home;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.resurrectajax.nationslegacy.events.nation.NationEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.NationMapping;

public class DeleteHomeEvent extends NationEvent{

	private String nationHome;
	
	public DeleteHomeEvent(NationMapping nation, CommandSender sender, String name) {
		super(nation, sender);
		
		if(name != null && !name.isEmpty() && !name.isBlank()) this.nationHome = name;
		else this.nationHome = "home";
		
		Nations main = Nations.getInstance();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				nation.deleteHome(nationHome);
				FileConfiguration language = main.getLanguage();
				sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Home.DelHome.Delete.Message"), nation.getName()));
			}
		}, 1L);
	}

	public String getNationHome() {
		return nationHome;
	}

}
