package me.resurrectajax.nationslegacy.events.nation.description;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.resurrectajax.nationslegacy.events.nation.NationEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.NationMapping;

public class SetNationDescriptionEvent extends NationEvent{
	private String description;
	
	public SetNationDescriptionEvent(NationMapping nation, CommandSender sender, String descriptionA) {
		super(nation, sender);
		setDescription(descriptionA);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Nations.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				FileConfiguration language = Nations.getInstance().getLanguage();
				
				if(description.isBlank()) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Description.Remove.Message"), nation.getDescription()));
				else sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Description.Set.Message"), description));
					
				nation.setDescription(description);
				nation.update();
			}
		}, 1L);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
