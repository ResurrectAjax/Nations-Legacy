package me.resurrectajax.nationslegacy.commands.flag.set.validators;

import java.util.Arrays;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.nationslegacy.flags.Flag;
import me.resurrectajax.nationslegacy.general.CommandValidator;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class FlagSetValidator extends CommandValidator {

	public FlagSetValidator(CommandSender sender, String[] args, ParentCommand command) {
		super((Nations)command.getMain(), sender, args, command);
	}

	@Override
	public boolean validate() {
		FileConfiguration language = main.getLanguage(), config = main.getConfig();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(((Player) sender).getUniqueId());
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		
		if(args.length < 4) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, command.getSyntax()));
		else if(nation == null) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), ""));
		else if(!sender.hasPermission(command.getPermissionNode())) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotALeader.Message"), nation.getName()));
		else if(Flag.getFromString(args[2]) == null) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Flag.Set.NotExist.Message"), args[2]));
		else if(!config.getBoolean(String.format("Nations.Flags.%s.Adjustable", Flag.getFromString(args[2]).toString()))) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Flag.Set.NotAdjustable.Message"), args[2]));
		else if(!Arrays.asList(command.getArguments(playerMap.getUUID())).contains(args[3].toUpperCase())) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, command.getSyntax()));
		else return true;
		return false;
	}

}
