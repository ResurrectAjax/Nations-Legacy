package me.resurrectajax.nationslegacy.commands.flag.info.validators;

import java.util.regex.Pattern;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.nationslegacy.general.CommandValidator;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;

public class FlagInfoValidator extends CommandValidator {

	public FlagInfoValidator(CommandSender sender, String[] args, ParentCommand command) {
		super((Nations)command.getMain(), sender, args, command);
	}

	@Override
	public boolean validate() {
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		if(args.length == 2) {
			if(!(sender instanceof OfflinePlayer)) {
				sender.sendMessage(GeneralMethods.format(language.getString("Command.Error.ByConsole.Message")));
				return false;
			}
			
			String nation = "";
			NationMapping nationMap = mappingRepo
					.getNationByPlayer(mappingRepo
					.getPlayerByUUID(((OfflinePlayer) sender)
					.getUniqueId()));
			nation = nationMap == null ? null : nationMap.getName();
			if(nation != null) return true;
			sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), ""));
		}
		else if(args.length > 3) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, command.getSyntax()));
		else if(!Pattern.matches("[a-zA-Z]+", args[2])) GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Error.SpecialCharacters.Message"), args[2]);
		else if(mappingRepo.getNationByName(args[2]) == null) sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.NotExist.Message"), args[2]));
		else return true;
		return false;
	}

}
