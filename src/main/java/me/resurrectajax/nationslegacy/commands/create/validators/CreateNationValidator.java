package me.resurrectajax.nationslegacy.commands.create.validators;

import java.util.regex.Pattern;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.nationslegacy.general.CommandValidator;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;

public class CreateNationValidator extends CommandValidator{

	public CreateNationValidator(CommandSender sender, String[] args, ParentCommand command) {
		super((Nations) command.getMain(), sender, args, command);
	}

	@Override
	public boolean validate() {
		FileConfiguration lang = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		OfflinePlayer player = (OfflinePlayer) sender;
		
		if(args.length != 2) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, command.getSyntax()));
		else if(!Pattern.matches("[a-zA-Z]+", args[1])) sender.sendMessage(GeneralMethods.format(player, lang.getString("Command.Error.SpecialCharacters.Message"), args[1]));
		else if(mappingRepo.getPlayerByUUID(player.getUniqueId()).getNationID() != null) sender.sendMessage(GeneralMethods.format(player, lang.getString("Command.Nations.Create.AlreadyInNation.Message"), args[1]));
		else if(mappingRepo.getNationByName(args[1]) != null) sender.sendMessage(GeneralMethods.format(player, lang.getString("Command.Nations.Create.AlreadyExists.Message"), args[1]));
		else return true;
		return false;
	}

}
