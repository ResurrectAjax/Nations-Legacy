package events.nation.create;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import events.nation.NationEvent;
import general.GeneralMethods;
import main.Main;
import persistency.MappingRepository;
import persistency.NationMapping;

public class CreateNationEvent extends NationEvent{
	
	public CreateNationEvent(NationMapping nation, CommandSender sender) {
		super(nation, sender);
		
		Main main = Main.getInstance();
		FileConfiguration language = main.getLanguage();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				MappingRepository mappingRepo = main.getMappingRepo();
				Player player = (Player) sender;
				
				if(isCancelled) mappingRepo.disbandNation(nation);
				else {
					mappingRepo.getChunkGainManager().addChunkGain(nation);
					sender.sendMessage(GeneralMethods.format((OfflinePlayer)player, language.getString("Command.Nations.Create.Created.Message"), nation.getName()));
					GeneralMethods.updatePlayerTab(player);
				}
			}
		}, 1L);
	}
	
	/**
	 * Returns the created nation, removed when the event is cancelled
	 * @return {@link NationMapping} created nation
	 * */
	public NationMapping getNation() {
		return nation;
	}

}
