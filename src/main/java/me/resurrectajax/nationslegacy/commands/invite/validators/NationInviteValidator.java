package me.resurrectajax.nationslegacy.commands.invite.validators;

import org.bukkit.Bukkit;
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

public class NationInviteValidator extends CommandValidator {

	public NationInviteValidator(CommandSender sender, String[] args, ParentCommand command) {
		super((Nations)command.getMain(), sender, args, command);
	}

	@Override
	public boolean validate() {
		FileConfiguration language = main.getLanguage();
		Player player = (Player) sender;
		
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		PlayerMapping receiver = mappingRepo.getPlayerByName(args.length > 1 ? args[1] : "");
		
		CommandSender receive = (CommandSender)Bukkit.getPlayer(receiver.getUUID());
		NationMapping nation = mappingRepo.getNationByPlayer(playerMap);
		
		if(args.length != 2) player.sendMessage(GeneralMethods.getBadSyntaxMessage(main, command.getSyntax()));
		else if(nation == null) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), args[1]));
		else if(!sender.hasPermission(command.getPermissionNode())) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotALeaderOrOfficer.Message"), nation.getName()));
		else if(Bukkit.getPlayer(args[1]) == null) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotExist.Message"), args[1]));
		else if(player.getUniqueId().equals(Bukkit.getPlayer(args[1]).getUniqueId())) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.Invite.Send.SelfInvite.Message"), args[1]));
		else if(Bukkit.getPlayer(receiver.getUUID()) == null) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotExist.Message"), args[1]));
		else if(receiver.getNationID() != null) player.sendMessage(GeneralMethods.relFormat(sender, receive, language.getString("Command.Player.Invite.Send.AlreadyInNation.Message"), args[1]));
		else return true;
		return false;
	}

}
