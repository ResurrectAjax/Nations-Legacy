package me.resurrectajax.nationslegacy.commands.claim.validators;

import java.util.Arrays;

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

public class ClaimChunkValidator extends CommandValidator {

	public ClaimChunkValidator(CommandSender sender, String[] args, ParentCommand command) {
		super((Nations) command.getMain(), sender, args, command);
	}

	@Override
	public boolean validate() {
		String arg = args.length < 2 ? "" : args[1];
		
		FileConfiguration language = main.getLanguage();
		
		Player player = (Player) sender;
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		NationMapping nation = mappingRepo.getNationByPlayer(playerMap);
		
		if(nation == null) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), ""));
		else if(args.length < 2 || !Arrays.asList(command.getArguments(player.getUniqueId())).contains(arg.toLowerCase())) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, command.getSyntax()));
		else if(!sender.hasPermission(command.getPermissionNode())) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotALeaderOrOfficer.Message"), nation.getName()));
		else return true;
		return false;
	}

}
