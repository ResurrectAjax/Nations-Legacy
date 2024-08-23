package me.resurrectajax.nationslegacy.commands.description.set.validators;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.nationslegacy.general.CommandValidator;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class SetDescriptionValidator extends CommandValidator {

	public SetDescriptionValidator(CommandSender sender, String[] args, ParentCommand command) {
		super((Nations) command.getMain(), sender, args, command);
	}

	@Override
	public boolean validate() {
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(((Player) sender).getUniqueId());
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		
		if(args.length < 3) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, command.getSyntax()));
		else if(nation == null) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), ""));
		else if(!sender.hasPermission(command.getPermissionNode())) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotALeader.Message"), nation.getName()));
		else return true;
		return false;
	}

}
