package me.resurrectajax.nationslegacy.commands.invite.validators;

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

public class NationInviteAcceptValidator extends CommandValidator {

	public NationInviteAcceptValidator(CommandSender sender, String[] args, ParentCommand command) {
		super((Nations)command.getMain(), sender, args, command);
	}

	@Override
	public boolean validate() {
		FileConfiguration language = main.getLanguage();
		Player player = (Player) sender;
		
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping nation = mappingRepo.getNationByName(args.length < 2 ? "" : args[1]);
		
		if(args.length < 2) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, command.getSyntax()));
		else if(!mappingRepo.getPlayerInvites().containsKey(player.getUniqueId()) || 
				nation == null || 
				!mappingRepo.getPlayerInvites().get(player.getUniqueId()).contains(nation.getNationID())) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.Invite.Receive.NoInvite.Message"), args[1]));
		else return true;
		return false;
	}

}
