package events.nation.create;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import events.nation.NationEvent;
import main.Main;
import general.GeneralMethods;
import persistency.MappingRepository;

public class CreateNationEvent extends NationEvent{

	private String nationName;
	
	public CreateNationEvent(CommandSender sender, String name) {
		super(null, sender);
		
		setNationName(name);
		
		Main main = Main.getInstance();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				MappingRepository mappingRepo = main.getMappingRepo();
				FileConfiguration lang = main.getLanguage();
				OfflinePlayer player = (OfflinePlayer) sender;
				nation = mappingRepo.createNation(nationName, mappingRepo.getPlayerByUUID(player.getUniqueId()));
				sender.sendMessage(GeneralMethods.format(player, lang.getString("Command.Nations.Create.Created.Message"), nationName));
			}
		}, 1L);
	}

	public String getNationName() {
		return nationName;
	}

	public void setNationName(String nationName) {
		this.nationName = nationName;
	}

}
