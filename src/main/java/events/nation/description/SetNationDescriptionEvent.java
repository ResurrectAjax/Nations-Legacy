package events.nation.description;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import events.nation.NationEvent;
import general.GeneralMethods;
import main.Main;
import persistency.NationMapping;

public class SetNationDescriptionEvent extends NationEvent{
	private String description;
	
	public SetNationDescriptionEvent(NationMapping nation, CommandSender sender, String descriptionA) {
		super(nation, sender);
		setDescription(descriptionA);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				FileConfiguration language = Main.getInstance().getLanguage();
				
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
