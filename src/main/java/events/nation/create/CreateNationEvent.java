package events.nation.create;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import events.nation.NationEvent;
import general.GeneralMethods;
import main.Main;
import persistency.MappingRepository;

public class CreateNationEvent extends NationEvent{

	public CreateNationEvent(CommandSender sender, String nation) {
		super(null);
		
		if(super.isCancelled) return;
		
		MappingRepository mappingRepo = Main.getInstance().getMappingRepo();
		FileConfiguration lang = Main.getInstance().getLanguage();
		OfflinePlayer player = (OfflinePlayer) sender;
		
		mappingRepo.createNation(nation, mappingRepo.getPlayerByUUID(player.getUniqueId()));
		sender.sendMessage(GeneralMethods.format(player, lang.getString("Command.Nations.Create.Created.Message"), nation));
	}

}
