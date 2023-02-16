package events.nation.create;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import events.nation.NationEvent;
import main.Main;
import general.GeneralMethods;
import persistency.MappingRepository;

public class CreateNationEvent extends NationEvent{

	public CreateNationEvent(CommandSender sender, String nation) {
		super(null, sender);
		
		if(super.isCancelled) return;
		
		MappingRepository mappingRepo = Main.getInstance().getMappingRepo();
		FileConfiguration lang = Main.getInstance().getLanguage();
		OfflinePlayer player = (OfflinePlayer) sender;
		super.nation = mappingRepo.createNation(nation, mappingRepo.getPlayerByUUID(player.getUniqueId()));
		sender.sendMessage(GeneralMethods.format(player, lang.getString("Command.Nations.Create.Created.Message"), nation));
	}

}
