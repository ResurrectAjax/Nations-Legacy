package me.resurrectajax.nationslegacy.commands.war.truce.validators;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.nationslegacy.commands.war.WarCommand;
import me.resurrectajax.nationslegacy.general.CommandValidator;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;

public class TruceCancelValidator extends CommandValidator {

	public TruceCancelValidator(CommandSender sender, String[] args, ParentCommand command) {
		super((Nations)command.getMain(), sender, args, command);
	}

	@Override
	public boolean validate() {
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		NationMapping senderNation = mappingRepo.getNationByPlayer(mappingRepo.getPlayerByUUID(((Player) sender).getUniqueId()));
		NationMapping nation = mappingRepo.getNationByName(args.length < 3 ? "" : args[2]);
		
		if(args.length < 3) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, command.getSyntax()));
		else if(senderNation == null) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), ""));
		else if(nation == null ||
				!((WarCommand)command.getParentCommand()).getTruceRequests().containsKey(nation.getNationID()) ||  
				!((WarCommand)command.getParentCommand()).getTruceRequests().get(nation.getNationID()).contains(senderNation.getNationID())) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.War.Truce.Receive.NoRequest.Message"), args[2]));
		else return true;
		return false;
	}

}
