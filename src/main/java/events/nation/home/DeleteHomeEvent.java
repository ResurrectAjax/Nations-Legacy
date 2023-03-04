package events.nation.home;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import events.nation.NationEvent;
import general.GeneralMethods;
import main.Main;
import persistency.NationMapping;

public class DeleteHomeEvent extends NationEvent{

	private String nationHome;
	
	public DeleteHomeEvent(NationMapping nation, CommandSender sender, String name) {
		super(nation, sender);
		
		if(name != null && !name.isEmpty() && !name.isBlank()) this.nationHome = name;
		else this.nationHome = "home";
		
		Main main = Main.getInstance();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				nation.deleteHome(nationHome);
				FileConfiguration language = main.getLanguage();
				sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Home.DelHome.Delete.Message"), nation.getName()));
			}
		}, 1L);
	}

	public String getNationHome() {
		return nationHome;
	}

}
